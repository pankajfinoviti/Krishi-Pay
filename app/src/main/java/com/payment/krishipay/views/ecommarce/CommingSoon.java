package com.payment.krishipay.views.ecommarce;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.payment.krishipay.R;

public class CommingSoon extends AppCompatActivity {
    private TextView tvUserName, tvMobile, tvKYC, tvEmail, tvPanCard, tvAadhaarCard, tvShopName;
    private LinearLayout secMsg, secCall;

    private void init() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MyUtil.transparentStatusBar(this);
        setContentView(R.layout.comming_soon);
        init();
    }


}
