package com.joeyzh.pushlib.httpserver;

import java.io.IOException;

/**
 * Created by Joey on 2018/8/6.
 * APPserver的工厂模式接口
 */

public interface AppServerDelegate {

    void start();

    void close();

}
