package com.joeyzh.pushlib.httpserver;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.joey.base.util.LogUtils;
import com.joeyzh.ui.IMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joey on 2018/8/24.
 */

public class PushService extends Service implements AppReceiveCallback {


    public static final String RELIVE_ACTION = "com.joeyzh.pushlib.RELIVE";
    public static final String NOTICE_INIT = "com.joeyzh.pushlib.NOTICE_INIT";
    private AppServerDelegate delegate;
    private final String TAG = "HttpService";
    private MyBinder binder = new MyBinder();
    private PushStubCallback callback;

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
        filter.addAction(NOTICE_INIT);
        ReliveReceiver receiver = new ReliveReceiver();
        registerReceiver(receiver, filter);
        delegate = PushHttpServer.newInstance();
        delegate.start();
        callback = new PushStubCallback(this);
        delegate.register("sunrui", this);
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

    @Override
    public void onReceiveBody(String msg, PushError error) {
        LogUtils.e(msg);
        IMessage msgObj = new IMessage();
        try {
            JSONObject object = new JSONObject(msg);
            if (object.has("title"))
                msgObj.setTitle(object.getString("title"));
            if (object.has("content"))
                msgObj.setContent(object.getString("content"));
            if (object.has("decs"))
                msgObj.setDecs(object.getString("decs"));
            if (object.has("picUrl"))
                msgObj.setPicUrl(object.getString("picUrl"));
            Intent intent = new Intent("com.joeyzh.push.message");
            intent.putExtra("message", msgObj);
            sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(PushError error) {

    }

    public class MyBinder extends Binder {
        public PushService getService() {
            return PushService.this;
        }
    }

}
