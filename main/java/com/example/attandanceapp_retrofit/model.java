package com.example.attandanceapp_retrofit;

import com.google.gson.annotations.SerializedName;

public class model {

    @SerializedName("status")
    String status;

    @SerializedName("msg")
    String msg;


    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

}
