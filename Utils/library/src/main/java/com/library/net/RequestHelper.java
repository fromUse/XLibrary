package com.library.net;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.library.Tools;

import java.util.Map;

/**
 * Created by chen on 2016/9/27.
 */

public class RequestHelper {

    public static void get(String url, final RequestCallBack callBack){

        JunStringRequest request = new JunStringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (callBack != null) {
                    callBack.onSuccess(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callBack != null) {
                    callBack.onFail(error);
                }
            }
        });
        request.setTag(url);
        Tools.getmQueue().add(request);
        Tools.getmQueue().start();

    }

    public static void post(String url, final Map<String,String> params, final RequestCallBack callBack){

        JunStringRequest request = new JunStringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (callBack != null) {
                    callBack.onSuccess(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callBack != null) {
                    callBack.onFail(error);
                }
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        request.setTag(url);
        Tools.getmQueue().add(request);
        Tools.getmQueue().start();
    }


    public interface RequestCallBack{
        void onSuccess(String string);
        void onFail(Exception e);
    }
}
