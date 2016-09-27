package com.cqg.exmple.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void onEventBackgroundThread(Version version){

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
            public void progress(int progress, int total) {
                Log.i (TAG, "progress: ");
            }
        });
    }

}
