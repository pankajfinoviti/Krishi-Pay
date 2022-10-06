package com.payment.krishipay.network;

public interface RequestResponseLis {
    void onSuccessRequest(String JSonResponse);

    void onFailRequest(String msg);
}