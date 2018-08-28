package com.joeyzh.pushlib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.joeyzh.pushclient.IPushApiInterface;


/**
 * Created by Joey on 2018/8/27.
 */

public class ServerAIDLService extends Service {


    IBinder mApiBinder = new IPushApiInterface.Stub() {
        @Override
        public void register(String appId) throws RemoteException {

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mApiBinder;
    }


}
