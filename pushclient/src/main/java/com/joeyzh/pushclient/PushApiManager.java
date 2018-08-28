package com.joeyzh.pushclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by Joey on 2018/8/27.
 */

public class PushApiManager {

//    private IPushApiInterface mPushApi;
//
//    private ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            //连接后拿到 Binder，转换成 AIDL，在不同进程会返回个代理
//            mPushApi = IPushApiInterface.Stub.asInterface(service);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mPushApi = null;
//        }
//    };

    public void registerById(String appId) {
//        Intent intent1 = new Intent(context, ClientService.class);
//        context.bindService(intent1, mConnection, BIND_AUTO_CREATE);
    }


}
