package com.joeyzh.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.joey.base.util.LogUtils;

/**
 * Created by Joey on 2018/8/28.
 */

public class NoticeCreator {

    private static int id = 0;
    private int smallIcon;
    private String appName = "Push Demo";
    private int icon;
    private Class<Activity> gotoCls;
    private Context mContext;

    public NoticeCreator(Context context) {
        this.mContext = context;
    }

    public void setInfo(int smallIcon, int icon, String appName) {
//        gotoCls = cls;
        this.smallIcon = smallIcon;
        this.appName = appName;
        this.icon = icon;
    }

    public void processMessage(IMessage msgObj) {
        LogUtils.a("" + Thread.currentThread().toString());
        sendNotification(mContext, msgObj);
    }

    @SuppressLint("MissingPermission")
    public void processCustomMessage(Context context, IMessage msgObj) {
//        // 判断当前是否有程序在运行
//        if (MyActivityManager.getActivityManager().currentActivity() != null) {
//        Intent intent = new Intent();
//            intent.setAction(FragmentPending.ACTION_RE_LOAD_PENDING);
//            context.sendBroadcast(intent);
//            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//            long[] pattern = new long[]{0, 300, 400, 400};   // 停止 开启 停止 开启
//            vibrator.vibrate(pattern, -1);
//            return;
//        }
        // 显示Notification
        sendNotification(context, msgObj);

    }

    private void sendNotification(Context context, IMessage message) {

        PendingIntent pendingIntent = null;
        if (gotoCls != null) {
            Intent i = new Intent(context, gotoCls);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        }
        String title = message.getTitle() == null ? appName : message.getTitle();
        LogUtils.a("");
        //获取NotificationManager实例
        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //实例化NotificationCompat.Builde并设置相关属性
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                //设置小图标
                .setSmallIcon(smallIcon)
                .setVibrate(new long[]{0, 300, 400, 400})
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                //设置通知标题
                .setContentTitle(title)
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
