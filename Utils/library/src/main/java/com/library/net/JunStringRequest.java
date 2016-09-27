package com.library.net;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class JunStringRequest extends StringRequest {
    private String charset = null;

    public JunStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.charset = HTTP.UTF_8;
    }

    public JunStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        this.charset = HTTP.UTF_8;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        //return super.parseNetworkResponse(response);
        String parsed;
        try {
            if(charset != null) {
                parsed = new String(response.data, charset);
            } else {
                parsed = new String(response.data, parseCharset(response.headers,HTTP.UTF_8));
            }
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    public static String parseCharset(Map<String, String> headers, String defaultCharset) {
        String contentType = headers.get(HTTP.CONTENT_TYPE);
        if (contentType != null) {
            String[] params = contentType.split(";");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }

        return defaultCharset;
    }


    /**
     * @return the Parse Charset Encoding
     */
    public String getCharset() {
        return charset;
    }

    /**
     * set the Parse Charset Encoding
     * @param charset
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }


}