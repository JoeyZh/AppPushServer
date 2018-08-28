package com.joeyzh.pushlib.httpserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.joey.base.util.LogUtils;

public class ReliveReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (PushService.RELIVE_ACTION.equals(intent.getAction())
                || Intent.ACTION_USER_PRESENT.equals(intent.getAction())
                || Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            LogUtils.a("收到广播啦" + intent.getAction());
            Intent action = new Intent(context, PushService.class);
            context.startService(action);
        }
    }


}