package com.joeyzh.pushlib.httpserver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.joey.base.util.LogUtils;

/**
 * Created by Joey on 2018/8/24.
 */

public class HttpService extends Service {


    public static final String RELIVE_ACTION = "com.joeyzh.pushlib.RELIVE";
    private AppServerDelegate delegate;
    private final String TAG = "HttpService";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RELIVE_ACTION);
        MyReceiver receiver = new MyReceiver();
        registerReceiver(receiver, filter);
        delegate = PushServer.newInstance();
        delegate.start();
        LogUtils.i(PushServer.newInstance().getHost(this));
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
                Intent action = new Intent(context, HttpService.class);
                startService(action);
            }
        }
    }


}
