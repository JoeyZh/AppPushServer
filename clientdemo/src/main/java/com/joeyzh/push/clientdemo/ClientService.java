package com.joeyzh.push.clientdemo;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.joey.base.util.LogUtils;
import com.joeyzh.pushlib.IMessageInterface;

/**
 * Created by Joey on 2018/8/28.
 */

public class ClientService extends Service {

    private static Handler mApiHander;
    private IBinder mStub = new IMessageInterface.Stub() {
        @Override
        public void onListener(String receiveMsg) throws RemoteException {
            Log.e("ClientService", receiveMsg);
            Toast.makeText(ClientService.this, receiveMsg, Toast.LENGTH_LONG).show();
            if (mApiHander != null) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("message", receiveMsg);
                msg.setData(bundle);
                mApiHander.sendMessage(msg);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.a("");
    }

    public static void setHandler(Handler handler) {
        mApiHander = handler;
    }

}
