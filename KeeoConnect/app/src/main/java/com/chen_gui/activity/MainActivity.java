package com.chen_gui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.chen_gui.R;
import com.chen_gui.service.AService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Charset charset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
    }


    public void start(View view) {
        if (!AService.isServiceRuning (this, "com.chen_gui.service.AService")) {
            Intent intent = new Intent (this, AService.class);
            startService (intent);
        }

    }


    public void connect(View view) {

       // Thread connect = new Thread (new ConnectThread ("172.30.85.33",1234));
       // connect.start ();
    }

    class ConnectThread implements Runnable {

        private String host;
        private int port;

        public ConnectThread(String host, int port) {
            this.host = host;
            this.port = port;
            if (charset == null) {
                charset = Charset.forName ("utf-8");
            }
        }

        @Override
        public void run() {

            try {

                SocketChannel socketChannel = SocketChannel.open (new InetSocketAddress (host, port));
                socketChannel.configureBlocking (false);
                Selector selector = Selector.open ();
                socketChannel.register (selector, SelectionKey.OP_READ);
                ByteBuffer buffer = ByteBuffer.allocate (2048);
                while (true) {
                    while (selector.select () > 0) {
                        Log.i (TAG, "****************有读事件**************");
                        for (SelectionKey key : selector.selectedKeys ()) {

                            selector.selectedKeys ().remove (key);
                            if (key.isConnectable ()){
                                socketChannel.finishConnect ();
                            }

                            if (key.isReadable ()){
                                buffer.clear ();
                                socketChannel.read (buffer);
                                buffer.flip ();
                                Log.i (TAG, "************* Readable " + charset.decode (buffer).toString ());
                                socketChannel.write (ByteBuffer.wrap ("服务器你好".getBytes ()));
                                buffer.clear ();

                            }


                        }
                    }
                }


            } catch (IOException e) {
                e.printStackTrace ();
            }


        }
    }


}
