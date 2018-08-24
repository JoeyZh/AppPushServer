package com.joeyzh.pushlib.httpserver;

import android.text.TextUtils;

import com.joey.base.util.LogUtils;
import com.joey.base.util.NetworkUtil;
import com.koushikdutta.async.AsyncServerSocket;
import com.koushikdutta.async.callback.CompletedCallback;
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

    private AsyncHttpServer httpServer;
    private HashMap<Integer, AsyncServerSocket> serverSockets;
    private final int DEFAULT_PORT = 9995;
    private InetAddress inetAddress;

    private HashMap<String, AppReceiveCallback> callbacks;

    private HttpServerRequestCallback requestCallback = new HttpServerRequestCallback() {
        @Override
        public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
            handleRequest("/", request, response);
        }
    };
    private AppServerDelegate appServerDelegate;

    public PushServer() {
        httpServer = new AsyncHttpServer();
        callbacks = new HashMap<>();
        serverSockets = new HashMap<>();
        httpServer.setErrorCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                ex.printStackTrace();
                LogUtils.e(ex.getMessage() + "出错了");
            }
        });
        httpServer.post("/", requestCallback);
        httpServer.get("/", requestCallback);
    }

    public void start(int port) {
        AsyncServerSocket socket = httpServer.listen(port);
        while (socket == null) {
            socket = httpServer.listen(++port);
        }
        serverSockets.put(port, socket);
        inetAddress = NetworkUtil.getLocalInetAddress();
        LogUtils.a("Socket init OK：" + getServerUrl(port));
    }

    @Override
    public void start() {
        start(DEFAULT_PORT);
    }

    @Override
    public void close() {
        httpServer.stop();
        inetAddress = null;
    }

    public void stop(int port) {
        if (serverSockets.containsKey(port)) {
            serverSockets.get(port).stop();
        }
        serverSockets.remove(port);
        if (serverSockets.isEmpty()) {
            close();
        }
    }

    public void addServerDir(String dir, AppReceiveCallback callback) {
        callbacks.put(dir, callback);
        httpServer.post(dir, requestCallback);
        httpServer.get(dir, requestCallback);
    }

    public String getHost() {
        // 表示服务还没有初始化
        if (inetAddress == null) {
            return null;
        }
        return inetAddress.getHostAddress().toString();
    }

    public String getServerUrl(int port) {
        if (!serverSockets.containsKey(port)) {
            return null;
        }
        if (TextUtils.isEmpty(getHost())) {
            return null;
        }
        return String.format("http://%s:%d", getHost(), port);
    }


    private void handleRequest(String method, AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        String msg = request.getBody().toString();
        LogUtils.a("收到消息了");
        response.send("hello world");
        if (callbacks.containsKey(method)) {
            callbacks.get(method).onReceiveBody(msg, null);
        }
    }

    private class ServerRequestCallback implements HttpServerRequestCallback {

        String method;

        public ServerRequestCallback(String method) {
            this.method = method;
        }

        @Override
        public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
            handleRequest(method, request, response);
        }
    }

}
