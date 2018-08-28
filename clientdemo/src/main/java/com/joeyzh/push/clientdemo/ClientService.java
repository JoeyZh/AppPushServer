package com.joeyzh.push.clientdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.joeyzh.pushlib.IMessageInterface;

/**
 * Created by Joey on 2018/8/28.
 */

public class ClientService extends Service {

    private IBinder mIBinder = new IMessageInterface.Stub() {
        @Override
        public void onListener(String receiveMsg) throws RemoteException {

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }


}
