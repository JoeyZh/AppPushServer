package com.joeyzh.pushlib.httpserver;

/**
 * Created by Joey on 2018/8/11.
 * 用于监听收到的消息
 */

public interface AppReceiveCallback {

    void onReceiveBody(String msg,PushError error);

//    void onReceiveHead(String msg,PushError error);

    void onError(PushError error);
}
