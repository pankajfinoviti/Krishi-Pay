package com.payment.krishipay.views.payment_gateway;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.payment.krishipay.R;
import com.payment.krishipay.utill.MyUtil;

public class GatewayStatus extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUtil.whiteStatusBar(this);
        setContentView(R.layout.paymet_gateway_initiate);
    }
}
