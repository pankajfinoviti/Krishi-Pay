package com.payment.krishipay.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.payment.krishipay.R;
import com.payment.krishipay.utill.MyUtil;
import com.payment.krishipay.utill.PrefLoginManager;
import com.payment.krishipay.utill.SharedPrefs;
import com.payment.krishipay.utill.ThemeHelper;
import com.payment.krishipay.views.auth.Login;
import com.payment.krishipay.views.loan.AllServicesDash;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
//        setContentView(R.layout.activity_register);
        String ui = SharedPrefs.getValue(this, SharedPrefs.APP_THEAM);
        if (MyUtil.isNN(ui))
            ThemeHelper.applyTheme(ui);
        startLoader(400);
    }

    Thread splashTimer;

    private void startLoader(final int maxTime) {
        splashTimer = new Thread() {
            public void run() {
                try {
                    int splashTime = 0;
                    int selector = 1;
                    while (splashTime < maxTime) {
                        Thread.sleep(700);
                        splashTime = splashTime + 100;
                    }
                    openActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        splashTimer.start();
    }

    private void openActivity() {
        PrefLoginManager pref = new PrefLoginManager(this);
        String status = pref.getFarmerLoginRes();
        if (status != null && status.length() > 0) {
            startActivity(new Intent(this, AllServicesDash.class));
        } else {
            startActivity(new Intent(this, Login.class));
        }
        finish();
    }
}
