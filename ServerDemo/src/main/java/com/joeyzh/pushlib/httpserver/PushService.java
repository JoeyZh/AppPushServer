package com.joeyzh.pushlib.httpserver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.joey.base.util.LogUtils;
import com.joeyzh.pushclient.IPushApiInterface;
import com.joeyzh.pushlib.IMessageInterface;

/**
 * Created by Joey on 2018/8/24.
 */

public class PushService extends Service {


    public static final String RELIVE_ACTION = "com.joeyzh.pushlib.RELIVE";
    private AppServerDelegate delegate;
    private final String TAG = "HttpService";
    public IMessageInterface imsgStub;
    private MyBinder binder = new MyBinder();
    private AppReceiveCallback callback = new AppReceiveCallback() {
        @Override
        public void onReceiveBody(String msg, PushError error) {
            if (imsgStub == null) {
                LogUtils.e("connect failed");
                return;
            }
            LogUtils.a("stub listener message:" + msg);
            try {
                imsgStub.onListener(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(PushError error) {

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RELIVE_ACTION);
        MyReceiver receiver = new MyReceiver();
        registerReceiver(receiver, filter);
        delegate = PushHttpServer.newInstance();
        delegate.start();
        LogUtils.i(PushHttpServer.newInstance().getHost(this));
//        Intent intent = new Intent("com.joeyzh.push.clientdemo.ClientService");
//        intent.setPackage("com.joeyzh.push.clientdemo");
//        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(RELIVE_ACTION);
        sendBroadcast(intent);
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RELIVE_ACTION.equals(intent.getAction())
                    || Intent.ACTION_USER_PRESENT.equals(intent.getAction())
                    || Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                Intent action = new Intent(context, PushService.class);
                startService(action);
            }
        }
    }


    public class MyBinder extends IPushApiInterface.Stub {
        public PushService getService() {
            return PushService.this;
        }

        @Override
        public void register(String appId) throws RemoteException {
            delegate.register(appId, callback);
        }
    }

}
