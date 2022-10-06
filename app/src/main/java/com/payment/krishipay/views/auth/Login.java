package com.payment.krishipay.views.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.payment.krishipay.R;
import com.payment.krishipay.app.AppManager;
import com.payment.krishipay.app.Constants;
import com.payment.krishipay.network.RequestResponseLis;
import com.payment.krishipay.network.VolleyNetworkCall;
import com.payment.krishipay.utill.AppHandler;
import com.payment.krishipay.utill.MyUtil;
import com.payment.krishipay.utill.Print;
import com.payment.krishipay.utill.SharedPrefs;
import com.payment.krishipay.views.otpview.OTPValidateAuth;
import com.payment.krishipay.views.otpview.OTPValidateForgetPassword;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements RequestResponseLis {
    private EditText etUser, etPassword;
    private TextView tvForgot,tvRegister;
    private Context context;
    private ImageView  imgPass;
    private Button btnLogin;
    private int REQUEST_TYPE = 0;
    private boolean isPassword = true;

    private void init() {
        context = Login.this;
        etUser = findViewById(R.id.etUser);
        tvForgot = findViewById(R.id.tvForgot);
        btnLogin = findViewById(R.id.btnLogin);
        etPassword = findViewById(R.id.etPassword);
        tvRegister = findViewById(R.id.tvRegister);
        imgPass = findViewById(R.id.imgPass);
    }

    private void passwordVisibility() {
        Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
        if (isPassword) {
            isPassword = false;
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            imgPass.setImageDrawable(getDrawable(R.drawable.password_view));
        } else {
            isPassword = true;
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imgPass.setImageDrawable(getDrawable(R.drawable.password_off));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUtil.transparentStatusBar(this);
        setContentView(R.layout.activity_signin);
        init();
        btnLogin.setOnClickListener(v -> {
            if (isValid()) {
                REQUEST_TYPE = 0;
                networkCallUsingVolleyApi(Constants.URL.LOGIN, true);
            }
        });

        tvForgot.setOnClickListener(v -> {
            if (isValidForgot()) {
                REQUEST_TYPE = 1;
                networkCallUsingVolleyApi(Constants.URL.PASSWORD_RESET_REQ, true);
            }
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this,Signup.class));
        });
/*
        imgPass.setOnClickListener(v -> passwordVisibility());*/
    }

    private void networkCallUsingVolleyApi(String url, boolean isLoad) {
        if (AppManager.isOnline(this)) {
            new VolleyNetworkCall(this, this, url, 1, param(), isLoad).netWorkCall();
        } else {
            Toast.makeText(this, "Network connection error", Toast.LENGTH_LONG).show();
        }
    }

    private Map<String, String> param() {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", etUser.getText().toString());
        String uid = SharedPrefs.getValue(this, SharedPrefs.FIREBASE_TOKEN);
        if (MyUtil.isNN(uid)) map.put("uid", uid);
        if (REQUEST_TYPE == 0)
            map.put("password", etPassword.getText().toString());
        return map;
    }

    private boolean isValid() {
        if (!MyUtil.isNN(etUser.getText().toString())) {
            Toast.makeText(this, "Please input user id", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!MyUtil.isNN(etPassword.getText().toString())) {
            Toast.makeText(this, "Please input password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isValidForgot() {
        if (!MyUtil.isNN(etUser.getText().toString())) {
            Toast.makeText(this, "Please input user id", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onSuccessRequest(String res) {
        try {
            String status = AppHandler.getStatus(res);
            String msg = AppHandler.getMessage(res);
            if (REQUEST_TYPE == 1) {
                Intent i = new Intent(this, OTPValidateForgetPassword.class);
                i.putExtra("USER_ID", etUser.getText().toString());
                startActivity(i);
            } else {
                if (status.equals("TXN")) {
                    AppHandler.parseAuthRes(etUser.getText().toString(), res, this);
                } else if (status.equalsIgnoreCase("OTP")) {
                    Intent i = new Intent(this, OTPValidateAuth.class);
                    i.putExtra("USER_ID", etUser.getText().toString());
                    i.putExtra("PASSWORD", etPassword.getText().toString());
                    i.putExtra("MSG", msg);
                    startActivity(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailRequest(String msg) {
        Print.P("" + msg);
    }
}
