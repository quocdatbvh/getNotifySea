package com.notifySeabank;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NotiCatchService extends NotificationListenerService implements GetData.SendNotificationListener {
    public static final String ACTION_RECEIVE_NOTIFICATION = "TDD_ACTION_RECEIVE_NOTIFICATION";
    private long lastNotificationPostTime = 0;
    private GetData getData;

    public NotiCatchService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtil.d("receiveee", "onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        getData = new GetData(this, this);

        LogUtil.d("receiveee", "onListenerConnected");
        StatusBarNotification[] arrActiveNotification = getActiveNotifications();

        if (arrActiveNotification != null) {
            if (arrActiveNotification.length > 0) {
                //  reverse lại array để lấy từ cũ -> mới
                List<StatusBarNotification> listActiveNotification = Arrays.asList(arrActiveNotification);
                Collections.reverse(listActiveNotification);
                for (StatusBarNotification sbn : listActiveNotification) {
                    if (!sbn.isOngoing() && !"android".equals(sbn.getPackageName()) && !getPackageName().equals(sbn.getPackageName())) {
                        //  Kiểm tra xem có thông báo nào chưa được lưu không
                        LogUtil.d("ấccac", sbn.getPackageName() + "-" + sbn.getPostTime());
                        List<String> listSaveNotificationPackageName = Utils.getListSaveNotificationPackageName(this);
                        if (listSaveNotificationPackageName.contains(sbn.getPackageName())) {
                            long postTime = sbn.getPostTime();
                            lastNotificationPostTime = DatabaseManager.getInstance(this).getLastNotificationPostTime();
                            if (postTime > lastNotificationPostTime) {
                                onNewNotification(sbn, postTime);
                            }
                        } else {
                            cancelNotification(sbn.getKey());
                        }
                    } else {
                        cancelNotification(sbn.getKey());
                    }
                }
            }
        }
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        LogUtil.d("receiveee", "onListenerDisconnected");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (!sbn.isOngoing() && !"android".equals(sbn.getPackageName()) && !getPackageName().equals(sbn.getPackageName())) {
            // Chỉ lưu thông báo của các ứng dụng được chọn
            List<String> listSaveNotificationPackageName = Utils.getListSaveNotificationPackageName(this);
            if (listSaveNotificationPackageName.contains(sbn.getPackageName())) {
                long postTime = sbn.getPostTime();
                if (postTime != lastNotificationPostTime) {
                    onNewNotification(sbn, postTime);
                }
            } else {
                cancelNotification(sbn.getKey());
            }
        } else {
            cancelNotification(sbn.getKey());
        }
    }

    private void onNewNotification(StatusBarNotification sbn, long postTime) {
        lastNotificationPostTime = postTime;

        NotificationInfo notificationInfo = getNotificationInfo(sbn);
        if (notificationInfo != null) {
            DatabaseManager.getInstance(this).addNotification(notificationInfo);
            Intent intent = new Intent(ACTION_RECEIVE_NOTIFICATION);
            intent.putExtra(Constants.EXTRA_NOTIFICATION, notificationInfo);
            sendBroadcast(intent);

            //todo Gửi thông báo lên api
            getData.sendNotification(notificationInfo);

            cancelNotification(sbn.getKey());
        }
    }

    private NotificationInfo getNotificationInfo(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        int id = sbn.getId();
        String packageName = sbn.getPackageName();
        String title = null;
        String text = null;
        String sender = null;

        switch (packageName) {
            case Constants.GMAIL_PACKAGE_NAME:
                title = getNormalNotificationTitle(extras, packageName);
                // thông báo gmail có tiêu đề và nội dung nên dùng EXTRA_BIG_TEXT để lấy hết nội dung
                if (extras.getCharSequence(Notification.EXTRA_BIG_TEXT) != null) {
                    text = extras.getCharSequence(Notification.EXTRA_BIG_TEXT).toString();
                } else {
                    text = getNormalNotificationText(extras);
                }
                break;
            case Constants.ZALO_PACKAGE_NAME: {
                // Xử lý Zalo cùng một người gửi nhưng có thêm số tin nhắn VD : Duy (2 tin nhắn)
                String tickerText = notification.tickerText.toString();
                title = getNormalNotificationTitle(extras, packageName);
                text = getNormalNotificationText(extras);
                if (text != null && !tickerText.trim().isEmpty()) {
                    String missedCallNotification = text + ": " + title;
                    if (tickerText.equals(missedCallNotification)) {
                        // Nếu là thông báo cuộc gọi nhỡ thì đổi vị trí title và text
                        String temp = title;
                        title = text;
                        text = temp;
                    } else {
                        // Nếu là thông báo tin nhắn
                        // thì xử lý cùng một người gửi nhưng có thêm số tin nhắn VD : Duy (2 tin nhắn)
                        tickerText = tickerText.replace(text, "");
                        if (tickerText.contains(": ")) {
                            int index = tickerText.lastIndexOf(": ");
                            title = tickerText.substring(0, index);
                        }
                    }
                }
                break;
            }
            case Constants.MESSENGER_PACKAGE_NAME: {
                // Xử lý group chat Messenger
                String conversationTitle = null;
                title = getNormalNotificationTitle(extras, packageName);
                text = getNormalNotificationText(extras);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    if (extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE) != null) {
                        conversationTitle = extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE).toString();
                        String extra = conversationTitle + ": ";
                        if (title.contains(extra)) {
                            sender = title.replace(extra, "");
                            title = conversationTitle;
                        }
                    }
                }

                break;
            }
            default:
                title = getNormalNotificationTitle(extras, packageName);
                text = getNormalNotificationText(extras);
                break;
        }

        // Vẫn có thể có thông báo có title null (Shopee)
        if (text == null) {
            return null;
        }

        long postTime = sbn.getPostTime();
        int postDate = Utils.getFormattedDateByTimeMillis(postTime);
        LogUtil.d("svvsdvsdvsdv", "large icon " + notification.extras.getParcelable(Notification.EXTRA_LARGE_ICON));

        // Lấy large icon (Ảnh đại diện messenger, zalo,...)
        byte[] byteArray = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (notification.getLargeIcon() != null) {
                Icon large = notification.getLargeIcon();
                Drawable drawable = large.loadDrawable(this);
                Bitmap bitmap = Utils.drawableToBitmap(drawable);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
                bitmap.recycle();
            }
        }

        NotificationInfo notificationInfo = new NotificationInfo(id,
                packageName,
                title,
                text,
                postTime,
                postDate,
                byteArray,
                sender);

        return notificationInfo;
    }

    private String getNormalNotificationTitle(Bundle extras, String packageName) {
        String title = null;
        if (extras.getCharSequence(Notification.EXTRA_TITLE) != null) {
            title = extras.getCharSequence(Notification.EXTRA_TITLE).toString();
        } else {
            if (extras.getString(Notification.EXTRA_TITLE) != null) {
                title = extras.getString(Notification.EXTRA_TITLE);
            } else {
                title = Utils.getAppNameFromPackageName(this, packageName);
            }
        }

        return title;
    }

    private String getNormalNotificationText(Bundle extras) {
        String text = null;
        if (extras.getCharSequence(Notification.EXTRA_TEXT) != null) {
            text = extras.getCharSequence(Notification.EXTRA_TEXT).toString();
        } else {
            if (extras.getString(Notification.EXTRA_TEXT) != null) {
                text = extras.getString(Notification.EXTRA_TEXT);
            } else {
                if (extras.getCharSequence(Notification.EXTRA_BIG_TEXT) != null) {
                    text = extras.getCharSequence(Notification.EXTRA_BIG_TEXT).toString();
                }
            }
        }

        return text;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        LogUtil.d("receiveee", "removed");
    }

    @Override
    public void onStartSendNotification() {

    }

    @Override
    public void onSendNotificationSuccessful(SendResult result, NotificationInfo notificationInfo) {
        Log.d("smsss", result.getMsg());
    }

    @Override
    public void onSendNotificationFail() {

    }
}