package com.chen_gui.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.chen_gui.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class BService extends Service {
    private static final String TAG = "BService";
    private boolean flag = true;
    private Notification mNotification;
    public BService() {
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

        Notification.Builder builder = new Notification.Builder (BService.this);
        builder.setContentTitle ("keep-connect");
        builder.setContentText ("正在运行守护进程");
        builder.setSmallIcon (android.R.drawable.ic_menu_preferences);
        mNotification = builder.build ();

        Thread thread = new Thread (new BWorkThread ("com.chen_gui.service.AService"));
        EventBus.getDefault ().register (this);
        thread.start ();
        startForeground (R.id.progress_horizontal,mNotification);
        return super.onStartCommand (intent, flags, startId);
    }


    /**
     * 无论服务启动多少次,都只会调一次这个方法
     * 除非进程被杀死了
     */
    @Override
    public void onCreate() {
        super.onCreate ();
        Log.i (TAG, "************ onCreate B服务被创建**************");

    }

    @Override
    public void onDestroy() {
        super.onDestroy ();
        flag = false;
        EventBus.getDefault ().unregister (this);
        Log.i (TAG, "************ onDestroy B服务被销毁**************");

    }


    class BWorkThread implements Runnable{

        private String AServicePackage;

        public BWorkThread(String AServicePackage) {
            this.AServicePackage = AServicePackage;
        }

        @Override
        public void run() {
            long n = 0;
            while (flag){
                SystemClock.sleep (1000);
                Log.i (TAG, "BService say " + n++);

                if (!isServiceRuning (BService.this, AServicePackage)){
                    EventBus.getDefault ().post ("");

                }

            }
        }
    }

    public static boolean isServiceRuning(Context context, String  packageName){

        ActivityManager manager = (ActivityManager) context.getSystemService (ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = manager.getRunningServices (100);
        for (ActivityManager.RunningServiceInfo info: serviceInfos) {
            if (info.service.getClassName ().equals (packageName)){
                return true;
            }
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startBservice(String str){
        Intent intent = new Intent (BService.this,AService.class);
        startService (intent);
        Log.i (TAG, "************重启服务A************");
    }
}
