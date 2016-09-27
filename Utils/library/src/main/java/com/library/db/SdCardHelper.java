package com.library.db;

import android.os.Environment;

import java.io.File;

/**
 * Created by chen on 2016/9/27.
 */

public class SdCardHelper {


    /**
     *
     * @return 返回内存卡的文件载体
     */
    public static File getSdCardFile(){

        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            // 当手机没有过载外部内存卡时，返回手机内置的缓存文件夹
            return Environment.getDownloadCacheDirectory();
        }
        //默认返回手机外置内存卡根目录
        return Environment.getExternalStorageDirectory();
    }

    /**
     *
     * @return 返回内存卡的目录
     */
    public static String getSdCardPath(){

        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            // 当手机没有过载外部内存卡时，返回手机内置的缓存文件夹
            return Environment.getDownloadCacheDirectory().getAbsolutePath();
        }
        //默认返回手机外置内存卡根目录
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

}
