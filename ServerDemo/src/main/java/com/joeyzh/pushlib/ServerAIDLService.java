package com.joeyzh.pushlib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.joey.base.util.LogUtils;
import com.joeyzh.pushlib.httpserver.IMessageInterface;
import com.joeyzh.pushlib.httpserver.PushServer;

/**
 * Created by Joey on 2018/8/27.
 */

public class ServerAIDLService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
