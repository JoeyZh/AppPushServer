package com.joeyzh.pushlib.httpserver;

/**
 * Created by Joey on 2018/8/6.
 * APPserver的工厂模式接口
 */

public abstract class AppServerDelegate {

    abstract public void start();

    abstract protected void close();

    abstract public void register(String appId, AppReceiveCallback callback);

}
