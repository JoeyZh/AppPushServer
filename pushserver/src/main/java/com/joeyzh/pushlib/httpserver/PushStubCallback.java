package com.joeyzh.pushlib.httpserver;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;

import com.joey.base.util.LogUtils;
import com.joeyzh.pushlib.IMessageInterface;
import com.joeyzh.ui.IMessage;
import com.joeyzh.ui.NoticeCreator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joey on 2018/8/28.
 */

public class PushStubCallback implements AppReceiveCallback {

    private IMessageInterface msgStub;
    private NoticeCreator mCreator;

    public PushStubCallback(Context context) {
        mCreator = new NoticeCreator(context);
    }

    @Override
    public void onReceiveBody(String msg, PushError error) {
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
            if (mCreator != null) {
                mCreator.processMessage(msgObj);
            } else {
                LogUtils.e("mCreator is null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.i(msg);
//        // 跨进程的地方
//        if (msgStub == null) {
//            LogUtils.e("stub is null");
//            return;
//        }
//        try {
//            msgStub.onListener(msg);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onError(PushError error) {

    }

    public void initNoticeCreator(Context context) {
        mCreator = new NoticeCreator(context);
    }

    public void setCreatorInfo(int smallIcon, int icon, String appName) {
        mCreator.setInfo(smallIcon, icon, appName);
    }


    public void setMsgStub(IMessageInterface stub) {
        msgStub = stub;
    }


}
