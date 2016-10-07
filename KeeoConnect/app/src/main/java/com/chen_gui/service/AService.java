package com.chen_gui.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.chen_gui.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AService extends Service {
    private static final String TAG = "AService";
    private Notification mNotification;
    private boolean flag = true;
    private Charset charset;
    private SocketChannel socketChannel;
    private Selector selector;
    private PowerManager.WakeLock wakeLock;
    private ExecutorService executorService = Executors.newScheduledThreadPool (5);


    public AService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    /**
     * 每次startService都会调这个方法
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.Builder builder = new Notification.Builder (AService.this);
        builder.setContentTitle ("keep-connect");
        builder.setContentText ("正在运行服务进程");
        builder.setSmallIcon (android.R.drawable.ic_menu_preferences);

        mNotification = builder.build ();
        //Thread connect = new Thread (new SocketChannelThread ("172.30.85.33",1234));
        Thread thread = new Thread (new AWorkThread ("com.chen_gui.service.BService"));
        executorService.execute (new SocketChannelThread ("172.30.85.33",1234));
        //connect.start ();
        thread.start ();
        startForeground (R.id.up,mNotification);
        return super.onStartCommand (intent, flags, startId);
    }


    /**
     * 无论服务启动多少次,都只会调一次这个方法
     * 除非进程被杀死了
     */
    @Override
    public void onCreate() {
        super.onCreate ();
        flag = true;
        Log.i (TAG, "************ onCreate A服务被创建**************");
        acquireWakeLock ();
    }

    @Override
    public void onDestroy() {
        super.onDestroy ();
        flag = false;
        Log.i (TAG, "************ onDestroy A服务被销毁**************");
        releaseWakeLock ();
    }


    class AWorkThread implements Runnable{

        private String BServicePackage;

        public AWorkThread(String BServicePackage) {
            this.BServicePackage = BServicePackage;
        }

        @Override
        public void run() {
            long n = 0;
            while (flag){
                SystemClock.sleep (1000);
                Log.i (TAG, "AService say " + n++);
                //如果服务不存在,就重新启动服务
                if (!isServiceRuning (AService.this,BServicePackage)){
                    Intent intent = new Intent (AService.this,BService.class);
                    startService (intent);
                    Log.i (TAG, "************重启服务B************");
                }

            }
        }
    }


    public static boolean isServiceRuning(Context context,String  packageName){

         ActivityManager manager = (ActivityManager) context.getSystemService (ACTIVITY_SERVICE);
         List<ActivityManager.RunningServiceInfo> serviceInfos = manager.getRunningServices (100);
        for (ActivityManager.RunningServiceInfo info: serviceInfos) {
            if (info.service.getClassName ().equals (packageName)){
                return true;
            }
        }
        return false;
    }


    class SocketChannelThread implements Runnable{
        private String host;
        private int port;

        public SocketChannelThread(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                Log.i (TAG, "-----------------准备连接-------------------");
                socketChannel = SocketChannel.open (new InetSocketAddress (host,port));
                Log.i (TAG, "--------------连接---------------");
                socketChannel.configureBlocking (false);
                selector = Selector.open ();
                socketChannel.register (selector, SelectionKey.OP_READ);
                executorService.execute (new SendThread ());
                ByteBuffer buffer = ByteBuffer.allocate (2048);
                //当服务被杀,连接断开,当再次启动服务时重新连接
                    //阻塞
                    while (selector.select ()>0 && flag){
                        Log.i (TAG, "--------------开始监听---------------");
                        Set<SelectionKey> selectedKeys = selector.selectedKeys ();
                        for (SelectionKey key:selectedKeys) {
                            selectedKeys.remove (key);
                            if (key.isReadable ()) {
                                buffer.clear ();
                               int n = socketChannel.read (buffer);
                                if (n==-1){
                                    Log.i (TAG, "服务器主动断开");
                                    socketChannel.close ();
                                    socketChannel = null;
                                    flag = false;
                                    Thread.sleep (1000*5);
                                    flag = true;
                                    executorService.execute (this);

                                    return;
                                }
                                buffer.flip ();
                                if (charset == null) {
                                    charset = Charset.forName ("utf-8");
                                }
                                Log.i (TAG,  Thread.currentThread ().getId () + " "+flag + "收到服务器的消息 : "+charset.decode (buffer).toString ());
                            }
                        }
                    }
            } catch (Exception e) {
                e.printStackTrace ();
                Log.i (TAG, "---------------连接出错--------------");
                flag = true;
                try {
                    Thread.sleep (1000*5);
                    executorService.execute (this);
                } catch (InterruptedException e1) {
                    e1.printStackTrace ();
                }
            }
        }
    }


    private void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.acquire();
            Log.i (TAG, "acquireWakeLock: 锁定cpu");
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
            Log.i (TAG, "releaseWakeLock: 释放cpu");

        }
    }


    class SendThread implements Runnable{

        @Override
        public void run() {

            while (flag){
                if (socketChannel != null && socketChannel.isConnected ()) {
                    try {
                        socketChannel.write (ByteBuffer.wrap ((Thread.currentThread ().getId () + "你好").getBytes ()));
                        Log.i (TAG, "******************发送消息"+Thread.currentThread ().getId ()+"***************");
                        Thread.sleep (5000);
                    } catch (IOException e) {
                        e.printStackTrace ();
                        Log.i (TAG, "******************发送失败***************");
                        return;
                    } catch (InterruptedException e) {
                        e.printStackTrace ();
                    }
                }
            }
        }
    }
}
