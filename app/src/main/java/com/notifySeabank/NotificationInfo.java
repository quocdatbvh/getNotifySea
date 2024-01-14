package com.notifySeabank;

import java.io.Serializable;

public class NotificationInfo implements Serializable, Comparable<NotificationInfo> {
    private int id;
    private String packageName;
    private String title;
    private String text;
    private long postTime;
    private int postDate;
    private byte[] largeIcon;
    private String conversationSender;

    public NotificationInfo() {
    }

    public NotificationInfo(int id,
                            String packageName,
                            String title,
                            String text,
                            long postTime,
                            int postDate,
                            byte[] largeIcon,
                            String conversationSender) {
        this.id = id;
        this.packageName = packageName;
        this.title = title;
        this.text = text;
        this.postTime = postTime;
        this.postDate = postDate;
        this.largeIcon = largeIcon;
        this.conversationSender = conversationSender;
    }

    public int getPostDate() {
        return postDate;
    }

    public void setPostDate(int postDate) {
        this.postDate = postDate;
    }

    public String getConversationSender() {
        return conversationSender;
    }

    public void setConversationSender(String conversationSender) {
        this.conversationSender = conversationSender;
    }

    public byte[] getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(byte[] largeIcon) {
        this.largeIcon = largeIcon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    @Override
    public int compareTo(NotificationInfo notificationInfo) {
        if (postTime == notificationInfo.postTime)
            return 0;
        else if (postTime < notificationInfo.postTime)
            return 1;
        else return -1;
    }

}
