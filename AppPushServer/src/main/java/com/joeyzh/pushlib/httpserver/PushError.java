package com.joeyzh.pushlib.httpserver;

/**
 * Created by Joey on 2018/8/11.
 * 错误
 */

public class PushError {

    private String message;
    private int errorId;
    private String messageDesc;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public String getMessageDesc() {
        return messageDesc;
    }

    public void setMessageDesc(String messageDesc) {
        this.messageDesc = messageDesc;
    }
}
