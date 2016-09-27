package com.library;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
}
