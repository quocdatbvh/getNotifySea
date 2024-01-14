package com.notifySeabank;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Duy on 10/09/2018.
 */

public interface ApiClient {
    @POST("store_notify_sea")
    @FormUrlEncoded
    Call<SendResult> sendNotification(@Field("content") String content,@Header("Authorization") String authHeader);
}
