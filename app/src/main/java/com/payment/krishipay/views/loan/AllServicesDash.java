package com.payment.krishipay.views.loan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.payment.krishipay.MainActivity;
import com.payment.krishipay.R;
import com.payment.krishipay.app.AppManager;
import com.payment.krishipay.app.Constants;
import com.payment.krishipay.network.RequestResponseLis;
import com.payment.krishipay.network.VolleyNetworkCall;
import com.payment.krishipay.utill.AppHandler;
import com.payment.krishipay.utill.MyUtil;
import com.payment.krishipay.views.ecommarce.CommingSoon;
import com.payment.krishipay.views.onlineservice.OnlineServiceActivity;

import java.util.HashMap;
import java.util.Map;

public class AllServicesDash extends AppCompatActivity implements RequestResponseLis {
    private TextView tvUserName, tvMobile, tvKYC, tvEmail, tvPanCard, tvAadhaarCard, tvShopName;
    private LinearLayout secMarket, secLivestock, secBanking,secFinance,secBima,secBazar;
    private ImageView logout,ivWhatsapp;
    private void init() {
        secMarket = findViewById(R.id.secMarket);
        secBanking = findViewById(R.id.secBanking);
        secLivestock = findViewById(R.id.secLivestock);
        secFinance = findViewById(R.id.secFinance);
        secBima = findViewById(R.id.secBima);
        secBazar = findViewById(R.id.secBazar);
        logout = findViewById(R.id.logout);
        ivWhatsapp = findViewById(R.id.ivWhatsapp);

        logout.setOnClickListener(v -> AppManager.getInstance().logoutFromServer(this, false));
        ivWhatsapp.setOnClickListener(view -> {
            MyUtil.openWhatsApp("6390899948", this);
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_market_ativity);
        init();
        secMarket.setOnClickListener(v -> startActivity(new Intent(AllServicesDash.this, OnlineServiceActivity.class).putExtra("type","Krishi MarketPlace")));
        secBanking.setOnClickListener(v -> startActivity(new Intent(AllServicesDash.this, MainActivity.class)));
        secLivestock.setOnClickListener(v -> startActivity(new Intent(AllServicesDash.this, OnlineServiceActivity.class).putExtra("type","Krishi Livestock")));
        secFinance.setOnClickListener(v -> startActivity(new Intent(AllServicesDash.this, OnlineServiceActivity.class).putExtra("type","Krishi Financing")));
        secBima.setOnClickListener(v -> startActivity(new Intent(AllServicesDash.this, OnlineServiceActivity.class).putExtra("type","Krishi Bima")));
        secBazar.setOnClickListener(v -> startActivity(new Intent(AllServicesDash.this, OnlineServiceActivity.class).putExtra("type","Krishi Bazaar")));
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkCallUsingVolleyApi(Constants.URL.BALANCE_UPDATE, true);
    }

    private void networkCallUsingVolleyApi(String url, boolean isLoad) {
        if (AppManager.isOnline(this)) {
            new VolleyNetworkCall(this, this, url, 1, param(), true).netWorkCall();
        } else {
            Toast.makeText(this, "Network connection error", Toast.LENGTH_LONG).show();
        }
    }

    private Map<String, String> param() {
        Map<String, String> map = new HashMap<>();
        return map;
    }

    @Override
    public void onSuccessRequest(String JSonResponse) {

    }

    @Override
    public void onFailRequest(String msg) {

    }

}
