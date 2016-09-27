package com.library.net;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by chen on 2016/9/27.
 */

public class DownloadHelper {
    private static final String TAG = "DownloadHelper";

    public static void downloadFile(String url, String path, String fileName, DownloadCallBack callBack) {
        URL httpUrl = null;
        InputStream input = null;
        BufferedInputStream bfInput = null;
        FileOutputStream fileOutput = null;
        try {
            httpUrl = new URL(url);
            URLConnection connection = httpUrl.openConnection();
            if (connection != null) {
                //文件总长度
                int length = connection.getContentLength();
                //文件所在目录
                File filePath = new File(path);
                //目录不存在就创建
                if (!filePath.exists()) {
                    filePath.mkdirs();
                }
                //文件的路径
                File file = new File(filePath, fileName);
                //文件存在，判断是否大小一致
                if (file.exists()) {
                    //文件存在，并且大小一致就直接返回
                    if (file.length() == length) {
                        Log.w(TAG, "文件 " + file.getName() + "已存在");
                        if (callBack != null) {
                            callBack.onExist(file);
                        }
                        return;
                    } else {
                        file.delete();
                    }
                }
                input = connection.getInputStream();
                if (input != null) {
                    bfInput = new BufferedInputStream(input, 1024);
                    fileOutput = new FileOutputStream(new File(path, fileName));
                    byte[] data = new byte[1024];
                    int temp = 0;
                    int progress = 0;
                    while ((temp = bfInput.read(data)) != -1) {
                        fileOutput.write(data, 0, temp);
                        progress += temp;
                        if (callBack != null) {
                            //更新下载进度
                            callBack.progress(progress,length);
                        }
                    }
                    if (callBack != null) {
                        callBack.onSuccess (file);
                    }
                }
            }

        } catch (MalformedURLException e) {
            if (callBack != null) {
                callBack.onFail(e);
            }
        } catch (IOException e) {
            if (callBack != null) {
                callBack.onFail(e);
            }
        } finally {
            try {

                if (input != null) {
                    input.close();
                }
                if (bfInput != null) {
                    bfInput.close();
                }
                if (fileOutput != null) {
                    fileOutput.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public interface DownloadCallBack {
        void onSuccess(File file);

        void onFail(Exception e);

        void onExist(File file);

        void progress(int progress, int total);
    }

}
