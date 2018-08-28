package com.joeyzh.sample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.joeyzh.ui.IMessage;

/**
 * Created by Joey on 2018/8/28.
 */

public class PushReceiver extends BroadcastReceiver {

    static int id = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("com.joeyzh.push.message".equals(action)) {
            IMessage msg = (IMessage) intent.getSerializableExtra("message");
            sendNotification(context, msg);
        }
    }

    private void sendNotification(Context context, IMessage message) {

        PendingIntent pendingIntent = null;
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        //获取NotificationManager实例
        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //实例化NotificationCompat.Builde并设置相关属性
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(new long[]{0, 300, 400, 400})
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                //设置通知标题
                .setContentTitle(message.getTitle())
                //设置通知内容
                .setContentText(message.getContent())
                //设置通知时间，默认为系统发出通知的时间，通常不用设置
                .setWhen(System.currentTimeMillis());
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
        Notification nofify = builder.build();
        nofify.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
//        notification.se(this, contentTitle, contentText, contentIntent);
        notifyManager.notify(++id, builder.build());
    }

}
