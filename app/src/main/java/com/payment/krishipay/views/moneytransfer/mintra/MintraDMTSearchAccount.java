package com.payment.krishipay.views.moneytransfer.mintra;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.payment.krishipay.R;
import com.payment.krishipay.app.AppManager;
import com.payment.krishipay.app.Constants;
import com.payment.krishipay.network.RequestResponseLis;
import com.payment.krishipay.network.VolleyNetworkCall;
import com.payment.krishipay.utill.AppHandler;
import com.payment.krishipay.utill.Print;
import com.payment.krishipay.views.moneytransfer.BenDetails;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MintraDMTSearchAccount extends AppCompatActivity implements RequestResponseLis {
    private int REQUEST_TYPE = 0;
    private EditText etMobile, etFirstName, etLastName, etPincode;
    private Context context;
    private Button btnProceed;
    private String rID, otpString;

    private void init() {
        context = this;
        etMobile = findViewById(R.id.etMobile);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPincode = findViewById(R.id.etPincode);
        btnProceed = findViewById(R.id.btnProceed);

        etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 9) {
                    REQUEST_TYPE = 0;
                    networkCallUsingVolleyApi(Constants.URL.DMT_TRANSACTION, true);
                } else {
                    visibilityCon(false);
                }
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mintra_dmt_account_search);
        init();

        btnProceed.setOnClickListener(v -> {
            if (isValid()) {
                REQUEST_TYPE = 1;
                networkCallUsingVolleyApi(Constants.URL.DMT_TRANSACTION, true);
            }
        });
    }

    private boolean isValid() {
        if (etMobile.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (REQUEST_TYPE == 1) {
            if (etFirstName.getText().toString().equals("")) {
                Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show();
                return false;
            } else if (etLastName.getText().toString().equals("")) {
                Toast.makeText(this, "Please enter last name", Toast.LENGTH_SHORT).show();
                return false;
            } else if (etPincode.getText().toString().equals("")) {
                Toast.makeText(this, "Please enter pincode", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
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
        map.put("mobile", etMobile.getText().toString());
        if (REQUEST_TYPE == 0) {
            map.put("type", "verification");
        } else {
            map.put("type", "registration");
            if (REQUEST_TYPE == 2) {
                map.put("type", "registrationValidate");
                map.put("otp", otpString);
                map.put("remitterid", rID);
            }
            map.put("lastname", etLastName.getText().toString());
            map.put("firstname", etFirstName.getText().toString());
            map.put("pincode", etPincode.getText().toString());
        }
        return map;
    }

    @Override
    public void onSuccessRequest(String JSonResponse) {
        try {
            String status = AppHandler.getStatus(JSonResponse);
            JSONObject jsonObject = new JSONObject(JSonResponse);
            if (REQUEST_TYPE == 0) {
                if (status.equalsIgnoreCase("RNF")) {
                    visibilityCon(true);
                } else if (status.equalsIgnoreCase("TXNOTP")) {
                    rID = jsonObject.getString("rid");
                    Toast.makeText(this, "Otp has been send successfully", Toast.LENGTH_SHORT).show();
                    showOtpPopUp();
                } else {
                    String name = jsonObject.getString("name");
                    String totallimit = jsonObject.getString("totallimit");
                    String usedlimit = jsonObject.getString("usedlimit");
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(this, BenDetails.class);
                    bundle.putString("sender_number", etMobile.getText().toString());
                    bundle.putString("name", name);
                    bundle.putString("status", status);
                    bundle.putString("available_limit", totallimit);
                    bundle.putString("total_spend", usedlimit);
                    bundle.putString("json", JSonResponse);
                    bundle.putInt("tab", 1);
                    Constants.DMT_RID = jsonObject.getString("rid");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            } else {
                REQUEST_TYPE = 0;
                networkCallUsingVolleyApi(Constants.URL.DMT_TRANSACTION, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailRequest(String msg) {
        Print.P(msg);
    }

    private void visibilityCon(boolean b) {
        if (b) {
            btnProceed.setVisibility(View.VISIBLE);
            findViewById(R.id.secDetails).setVisibility(View.VISIBLE);
        } else {
            btnProceed.setVisibility(View.GONE);
            findViewById(R.id.secDetails).setVisibility(View.GONE);
        }
    }

    private void showOtpPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.custom_login_otp_dialog, null);
        Button btnSubmit = customView.findViewById(R.id.otp_submit);
        Button btnResend = customView.findViewById(R.id.btnResend);
        final EditText etOtp = customView.findViewById(R.id.otp);
        final TextView tvMsg = customView.findViewById(R.id.tvMsg);
        final ImageView imgClose = customView.findViewById(R.id.imgClose);
        tvMsg.setText("Please eter otp");
        builder.setView(customView);
        builder.create();
        final AlertDialog alertDialog = builder.show();
        builder.setCancelable(false);
        btnSubmit.setOnClickListener(v1 -> {
            String otp = etOtp.getText().toString();
            if (otp.equals("")) {
                Toast.makeText(this, "Please enter otp", Toast.LENGTH_SHORT).show();
            } else {
                alertDialog.dismiss();
                otpString = etOtp.getText().toString();
                REQUEST_TYPE = 2;
                networkCallUsingVolleyApi(Constants.URL.DMT_TRANSACTION, true);
            }
        });

        btnResend.setOnClickListener(v -> {
            REQUEST_TYPE = 0;
            networkCallUsingVolleyApi(Constants.URL.DMT_TRANSACTION, true);
        });

        imgClose.setOnClickListener(v -> alertDialog.dismiss());
    }
}
