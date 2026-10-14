package com.example.callblock.ui.calllog;

public class CallLogItem {
    private String phoneNumber;
    private String displayDetail;
    private boolean isMissedOrSpam;
    private long timestamp;

    public CallLogItem(String phoneNumber, String displayDetail, boolean isMissedOrSpam, long timestamp) {
        this.phoneNumber = phoneNumber;
        this.displayDetail = displayDetail;
        this.isMissedOrSpam = isMissedOrSpam;
        this.timestamp = timestamp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDisplayDetail() {
        return displayDetail;
    }

    public boolean isMissedOrSpam() {
        return isMissedOrSpam;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
