package com.notifySeabank;

import android.content.Context;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Duy on 10/09/2018.
 */

public class GetData {
    private Context context;
    private ApiClient apiClient;
    private SendNotificationListener listener;

    public GetData(Context context, SendNotificationListener listener) {
        this.context = context;
        this.listener = listener;
        Retrofit retrofit = (new Retrofit.Builder()).baseUrl("http://testapi.xyz/public/api/sell/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        this.apiClient = retrofit.create(ApiClient.class);
    }

    public void sendNotification(NotificationInfo notificationInfo) {
        listener.onStartSendNotification();
        this.apiClient.sendNotification(notificationInfo.getText(), "Bearer 12|hKDIyMZn2WuOfZ2bYYn4c5lY111HQUhFsQw871lB")
                .enqueue(new Callback<SendResult>() {
                    public void onResponse(Call<SendResult> call, Response<SendResult> response) {
//                        SendResult sendResult = response.body();
//                        Log.d("KET QUA SERVER", sendResult.getMsg());
//
////                        if (sendResult.getResult()) {
////                            listener.onSendNotificationSuccessful(sendResult, notificationInfo);
////                        } else {
////                            listener.onSendNotificationFail();
////                            Log.d("internettt", "unsuccessful");
////                            //todo Gửi thành công nhưng trả về fail vì tin có mã trùng hoặc không tồn tại,
////                            // bản cuối cùng không cần thêm tin vào danh sách chưa gửi ở đây
//////                            Utils.addMessageToUnsentList(context, notificationInfo);
////                        }
                    }

                    public void onFailure(Call<SendResult> call, Throwable t) {
//                        listener.onSendNotificationFail();
//                        Log.d("smsss", t.getMessage());
//
//                        //todo Không thành công thì thêm vào danh sách chờ để gửi lại sau
////                        Utils.addMessageToUnsentList(context, notificationInfo);
                    }
                });
    }

    public interface SendNotificationListener {
        void onStartSendNotification();

        void onSendNotificationSuccessful(SendResult result, NotificationInfo notificationInfo);

        void onSendNotificationFail();
    }

}
