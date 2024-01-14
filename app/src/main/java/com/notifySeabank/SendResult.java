package com.notifySeabank;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendResult {
//    @SerializedName("status")
//    @Expose
//    private Boolean status;
    @SerializedName("msg")
    @Expose
    private String msg;

//    public Boolean getStatus() {
//        return status;
//    }
//
//    public void setStatus(Boolean status) {
//        this.status = status;
//    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String result) {
        this.msg = msg;
    }
}
