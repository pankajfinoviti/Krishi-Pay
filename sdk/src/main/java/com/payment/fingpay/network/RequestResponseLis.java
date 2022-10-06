package com.payment.fingpay.network;

public interface RequestResponseLis {
    void onSuccessRequest(String JSonResponse);

    void onFailRequest(String msg);
}