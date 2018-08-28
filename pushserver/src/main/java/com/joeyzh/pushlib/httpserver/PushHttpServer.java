package com.joeyzh.pushlib.httpserver;

import android.content.Context;

import com.joey.base.util.LogUtils;
import com.joey.base.util.NetworkUtil;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncServerSocket;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.Multimap;
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

public class PushHttpServer extends AppServerDelegate {

    private static PushHttpServer mInstance;
    private AsyncHttpServer mHttpServer;
    private AsyncServerSocket mServerSocket;
    private final int DEFAULT_PORT = 9995;
    private InetAddress inetAddress;

    private HashMap<String, AppReceiveCallback> callbacks;
    private int port;

    private HttpServerRequestCallback requestCallback = new HttpServerRequestCallback() {
        @Override
        public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
            parseRequest(request, response);
//            handleRequest("/", request, response);
        }
    };

    public static PushHttpServer newInstance() {
        if (mInstance != null) {
            return mInstance;
        }
        synchronized (PushHttpServer.class) {
            if (mInstance != null) {
                return mInstance;
            }
            mInstance = new PushHttpServer();
            return mInstance;
        }

    }

    private PushHttpServer() {
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
        LogUtils.a("Socket init OK：" + getPort());
    }

    @Override
    public void start() {
        LogUtils.i("");
        start(DEFAULT_PORT);
    }

    @Override
    protected void close() {
        if (mServerSocket == null) {
            return;
        }
        mServerSocket.stop();
        AsyncServer.getDefault().stop();
        mHttpServer.stop();
        inetAddress = null;
        mServerSocket = null;
    }

    @Override
    public void register(String appId, AppReceiveCallback callback) {
        callbacks.put(appId, callback);
    }

    public int getPort() {
        return port;
    }

    public String getHost(Context context) {
        // 表示服务还没有初始化
        if (inetAddress == null) {
            return null;
        }
        return String.format("http://%s:%d", NetworkUtil.getLocalIpStr(context), getPort());
    }


    private void handleRequest(Multimap multimap, AsyncHttpServerResponse response) {
        String result = multimap.getString("result");
        String appId = multimap.getString("appId");
        if (null == result) {
            response.send("{\"message\":\"非法格式\",\"errorCode\":0}");
        } else {
            response.send("{\"message\":\"I have received message\",\"errorCode\":1}");
        }
        if (callbacks.containsKey(appId)) {
            callbacks.get(appId).onReceiveBody(result, null);
        }
    }

    private void parseRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        AsyncHttpRequestBody body = request.getBody();
        StringBuffer buffer = new StringBuffer();
        buffer.append("currentThread :" + Thread.currentThread().toString());
        buffer.append("收到消息了 body:" + body.get().toString());
        buffer.append("\n contentType :" + body.getContentType());
        buffer.append("\n heads :" + request.getHeaders().getMultiMap().toString());
        LogUtils.a(buffer.toString());
        if (body == null) {
            handleRequest(new Multimap(), response);
            return;
        }

        handleRequest((Multimap) body.get(), response);

    }

}
