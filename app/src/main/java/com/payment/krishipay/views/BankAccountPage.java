package com.payment.krishipay.views;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.payment.krishipay.R;
import com.google.android.material.card.MaterialCardView;
import com.payment.krishipay.utill.MyUtil;
import com.payment.krishipay.utill.SharedPrefs;

public class BankAccountPage extends AppCompatActivity {
    private TextView tvUserName, tvMobile, tvKYC, tvEmail, tvPanCard, tvAadhaarCard, tvShopName;
    private LinearLayout secMsg, secCall;

    private void init() {
        tvUserName = findViewById(R.id.tvUserName);
        tvMobile = findViewById(R.id.tvMobile);
        tvKYC = findViewById(R.id.tvKYC);
        tvEmail = findViewById(R.id.tvEmail);
        tvPanCard = findViewById(R.id.tvPanCard);
        tvAadhaarCard = findViewById(R.id.tvAadhaarCard);
        tvShopName = findViewById(R.id.tvShopName);
        secMsg = findViewById(R.id.secMsg);
        secCall = findViewById(R.id.secCall);

        String userName = SharedPrefs.getValue(this, SharedPrefs.USER_NAME);
        String email = SharedPrefs.getValue(this, SharedPrefs.BANK);
        String mobile = SharedPrefs.getValue(this, SharedPrefs.USER_CONTACT);
        String kyc = SharedPrefs.getValue(this, SharedPrefs.KYC);
        String pancard = SharedPrefs.getValue(this, SharedPrefs.ACCOUNT);
        String aadhaarCard = SharedPrefs.getValue(this, SharedPrefs.AADHAR_CARD);
        String shopName = SharedPrefs.getValue(this, SharedPrefs.SHOP_NAME);
        secMsg.setOnClickListener(v -> openWhatsApp(mobile));
        secCall.setOnClickListener(v -> callOnNum(mobile));
        tvUserName.setText(userName);


        tvMobile.setText(mobile);
        tvKYC.setText(kyc);

        tvEmail.setText("Not Available");
        if (MyUtil.isNN(email))
            tvEmail.setText(email);
        tvPanCard.setText("Not Available");
        if (MyUtil.isNN(email))
            tvPanCard.setText(pancard);
        tvAadhaarCard.setText(aadhaarCard);
        tvShopName.setText(shopName);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUtil.transparentStatusBar(this);
        setContentView(R.layout.activity_bank_page_new);
        init();
        animateRightGrid();
    }

    // animation side panel
    private void animateRightGrid() {
        MaterialCardView iconsGroup = findViewById(R.id.cardView);
        Animation slideAnimation = AnimationUtils.loadAnimation(this, R.anim.left_to_right_rotate);
        iconsGroup.startAnimation(slideAnimation);
        iconsGroup.animate().rotation(-90).setDuration(700).start();
    }

    private void openWhatsApp(String number) {
        try {
            number = number.replace(" ", "").replace("+", "");
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net");
            startActivity(sendIntent);
        } catch (Exception e) {
            Log.e("TAG", "ERROR_OPEN_MESSANGER" + e.toString());
        }
    }

    private void callOnNum(String n) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + n));
        startActivity(intent);
    }
}
