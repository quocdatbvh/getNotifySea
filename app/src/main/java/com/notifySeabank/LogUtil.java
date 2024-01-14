package com.notifySeabank;

import android.util.Log;

public class LogUtil {
    public static final String DINO_TOPIC_NAME = "DINO_TOPIC_NAME";
    public static final String DINO_QUESTION_ID = "DINO_QUESTION_ID";
    public static final String DINO_QUESTION_TYPE = "DINO_QUESTION_TYPE";
    public static final String DINO_PASS_TEST = "DINO_PASS_TEST";
    public static final String EVENT_DINO_SKIP_QUESTION = "DINO_SKIP_QUESTION";
    public static final String EVENT_DINO_OPEN_TOPIC = "DINO_OPEN_TOPIC";
    public static final String EVENT_DINO_OPEN_TEST = "DINO_OPEN_TEST";
    public static final String EVENT_DINO_OPEN_GAME = "DINO_OPEN_GAME";
    public static final String EVENT_DINO_CAN_NOT_RECORD_AUDIO = "DINO_CAN_NOT_RECORD_AUDIO";
    public static final String EVENT_DINO_FINISH_LESSON = "DINO_FINISH_LESSON";
    public static final String EVENT_DINO_FINISH_TEST = "DINO_FINISH_TEST";
    public static final String EVENT_DINO_FINISH_VOCAB_GAME = "DINO_FINISH_VOCAB_GAME";
    public static final String EVENT_DINO_OPEN_BILLING = "DINO_OPEN_BILLING";
    public static final String DINO_OPEN_BILLING_FROM = "DINO_OPEN_BILLING_FROM";
    public static final String EVENT_DINO_UPGRADE_PRO = "DINO_UPGRADE_PRO";

    public static final String TAG_ADS = "adssss";
    public static final String TAG_EXCEPTION = "exceptionnn";
    public static final String TAG_LOGIN = "loginnnn";
    public static final String TAG_BACKUP = "backupppp";
    public static final String TAG_TEST = "testtttt";

    public static void d(String tag, String message) {
        Log.d(tag, message);
    }
}
