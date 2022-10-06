package com.payment.krishipay.utill;

public interface MRequestResponseLis {
    void onSuccessRequest(String JSonResponse);

    void onFailRequest(String msg);
}