package com.example.callblock.ui.calllog;

public class SmsLogItem {

    private final String sender;
    private final String body;
    private final long date;

    public SmsLogItem(String sender, String body, long date) {
        this.sender = sender;
        this.body = body;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public String getBody() {
        return body;
    }

    public long getDate() {
        return date;
    }
}
