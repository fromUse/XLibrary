package com.library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;

/**
 * Created by chen on 2016/9/27.
 */

public class Tools {

    private static Context mContext ;
    private static RequestQueue mQueue;


    public static void init(Context context){
        mContext = context;
        mQueue = Volley.newRequestQueue(mContext);
    }

    public static Context getmContext() {
        return mContext;
    }

    public static RequestQueue getmQueue() {
        return mQueue;
    }


    public static void installAPK(Context context, File  file){
        Intent intent=new Intent();
        intent.setAction(intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void installAPK(Context context, String  filePath){
       installAPK(context,new File(filePath));
    }

    /**
     * 格式化文件大小
     * @param fileSize
     * @return
     */
    public static  String getFileSizeStr(long fileSize) {

        String sFileSize = "";
        if (fileSize > 0) {
            double dFileSize = (double) fileSize;

            double kiloByte = dFileSize / 1024;
            if (kiloByte < 1) {
                return sFileSize + "Byte(s)";
            }
            double megaByte = kiloByte / 1024;
            if (megaByte < 1) {
                sFileSize = String.format ("%.2f", kiloByte);
                return sFileSize + "KB";
            }

            double gigaByte = megaByte / 1024;
            if (gigaByte < 1) {
                sFileSize = String.format ("%.2f", megaByte);
                return sFileSize + "MB";
            }

            double teraByte = gigaByte / 1024;
            if (teraByte < 1) {
                sFileSize = String.format ("%.2f", gigaByte);
                return sFileSize + "GB";
            }

            sFileSize = String.format ("%.2f", teraByte);
            return sFileSize + "TB";
        }
        return sFileSize;
    }
}
