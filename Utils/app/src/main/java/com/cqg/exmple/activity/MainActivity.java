package com.cqg.exmple.activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.cqg.exmple.R;
import com.cqg.exmple.comm.WebAPI;
import com.google.gson.Gson;
import com.library.Tools;
import com.library.bean.Version;
import com.library.db.SdCardHelper;
import com.library.net.DownloadHelper;
import com.library.net.RequestHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Notification mNotification;
    private NotificationManager manager;
    private RemoteViews views;
    private int p = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

    }

    public void update(View view) {
        String url = WebAPI.prefix + "CheckVersion?versionID=";
        EventBus.getDefault().post(url);

    }


    @Subscribe
    public void onEventBackgroundThread(String url) {

        InputStream input = getResources().openRawResource(R.raw.version);
        final Gson gson = new Gson();
        InputStreamReader reader = new InputStreamReader(input);
        final Version localVersion = gson.fromJson(reader, Version.class);
        if (localVersion != null) {
            url += localVersion.getVersionID();
            RequestHelper.get(url, new RequestHelper.RequestCallBack() {
                @Override
                public void onSuccess(String result) {
                    Version version = gson.fromJson(result, Version.class);
                    if (version != null) {

                        if (version.getVersionID() > localVersion.getVersionID()) {
                            EventBus.getDefault().post(version);
                        }

                    }
                }


                @Override
                public void onFail(Exception e) {

                }
            });
        }
    }

    @Subscribe
    public void onEventMainThread(final Version version) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("有新版本")

                .setMessage(version.getDescribe())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                        new Thread (new Runnable () {
                            @Override
                            public void run() {
                                onEventBackgroundThread (version);
                            }
                        }).start ();
                    }
                }).setNegativeButton("我不", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void onEventBackgroundThread(final Version version){

        String path = SdCardHelper.getSdCardPath() +"/APK";
        DownloadHelper.downloadFile(version.getDownloadURL(), path, "mukawang.apk", new DownloadHelper.DownloadCallBack() {
            @Override
            public void onSuccess(final File file) {
                runOnUiThread (new Runnable () {
                    @Override
                    public void run() {
                        Tools.installAPK(MainActivity.this,file);
                    }
                });
            }

            @Override
            public void onFail(Exception e) {

            }

            @Override
            public void onExist(final File file) {
                runOnUiThread (new Runnable () {
                    @Override
                    public void run() {
                        Tools.installAPK(MainActivity.this,file);
                    }
                });
            }

            @Override
            public void progress(final int progress, int total) {


                final int progre = (int)((progress / (total *1.0f))*100);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (mNotification == null) {
                            Notification.Builder builder = new Notification.Builder(MainActivity.this);
                            builder.setContentTitle("更新中");
                            builder.setTicker("正在下载新版本...");
                            builder.setSmallIcon(R.mipmap.ic_launcher);
                            mNotification = builder.build();
                            mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                            views = new RemoteViews(getPackageName(),R.layout.download_apk);
                            mNotification.contentView = views;

                            views.setProgressBar(R.id.progressBar,100,progre,false);

                            manager = (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
                        }

                        if (p!=progre){
                            p = progre;
                            views.setProgressBar(R.id.progressBar,100,progre,false);
                            if (p ==100){
                                views.setViewVisibility(R.id.progressBar, View.GONE);
                                views.setViewVisibility(R.id.done,View.VISIBLE);
                                Log.i(TAG, "run: 下载完啦 ....");
                            }
                            Log.i(TAG, "progress: " + progre);
                            manager.notify(R.id.progressBar,mNotification);
                        }

                    }
                });



            }
        });



    }

}
