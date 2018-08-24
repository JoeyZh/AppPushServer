package com.joeyzh.pushlib.httpserver;

import android.os.Bundle;
import android.text.TextUtils;

import com.joey.base.util.LogUtils;
import com.joey.base.util.NetworkUtil;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncServerSocket;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.Headers;
import com.koushikdutta.async.http.body.AsyncHttpRequestBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * Created by Joey on 2018/8/6.
 */

public class PushServer implements AppServerDelegate {

    private static PushServer mInstance;
    private AsyncHttpServer mHttpServer;
    private AsyncServerSocket mServerSocket;
    private final int DEFAULT_PORT = 9995;
    private InetAddress inetAddress;

    private HashMap<String, AppReceiveCallback> callbacks;
    private int port;

    private HttpServerRequestCallback requestCallback = new HttpServerRequestCallback() {
        @Override
        public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
            handleRequest("/", request, response);
        }
    };

    public static PushServer newInstance() {
        if (mInstance != null) {
            return mInstance;
        }
        synchronized (PushServer.class) {
            if (mInstance != null) {
                return mInstance;
            }
            mInstance = new PushServer();
            return mInstance;
        }

    }

    private PushServer() {
        mHttpServer = new AsyncHttpServer();
        callbacks = new HashMap<>();
        mHttpServer.setErrorCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                ex.printStackTrace();
                LogUtils.e(ex.getMessage() + "出错了");
            }
        });
        mHttpServer.post("/Message", requestCallback);
        mHttpServer.get("/Message", requestCallback);
    }

    public void start(int port) {
        close();
        AsyncServerSocket socket = mHttpServer.listen(port);
        while (socket == null) {
            socket = mHttpServer.listen(++port);
        }
        this.port = port;
        mServerSocket = socket;
        inetAddress = NetworkUtil.getLocalInetAddress();
        LogUtils.a("Socket init OK：" + getServerUrl());
    }

    @Override
    public void start() {
        start(DEFAULT_PORT);
    }

    @Override
    public void close() {
        if (mServerSocket == null) {
            return;
        }
        mServerSocket.stop();
        AsyncServer.getDefault().stop();
        mHttpServer.stop();
        inetAddress = null;
        mServerSocket = null;
    }

    public String getHost() {
        // 表示服务还没有初始化
        if (inetAddress == null) {
            return null;
        }
        return inetAddress.getHostAddress().toString();
    }

    public String getServerUrl() {
        if (TextUtils.isEmpty(getHost())) {
            return null;
        }
        return String.format("http://%s:%d/Message", getHost(), port);
    }


    private void handleRequest(String method, AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        AsyncHttpRequestBody body = request.getBody();
        Headers headers = request.getHeaders();
        body.get();
        StringBuffer buffer = new StringBuffer();
        buffer.append("收到消息了 body:" + body.get().toString() );
        buffer.append("\n contentType :" + body.getContentType());
        buffer.append("\n heads :" + headers.getMultiMap().toString());
        LogUtils.a(buffer.toString());
        response.send("hello world");
        if (callbacks.containsKey(method)) {
            callbacks.get(method).onReceiveBody(body.get().toString(), null);
        }
    }

}
