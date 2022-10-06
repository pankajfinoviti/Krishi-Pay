package com.payment.aeps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.payment.aeps.activity.CashWithdraw;
import com.payment.aeps.util.ModuleSharedPrefs;
import com.payment.aeps.util.ModuleUtil;

public class AepsDashboard extends ParentActivity implements View.OnClickListener {
    private String userId;
    private String appToken;
    private LinearLayout secCashWithDraw, secAadhaar, secBalanceEnquiry, secStatement;
    private TextView tvDevice;
    private void init() {
        secCashWithDraw = findViewById(R.id.secCashWithDraw);
        secAadhaar = findViewById(R.id.secAadhaar);
        secBalanceEnquiry = findViewById(R.id.secBalanceEnquiry);
        secStatement = findViewById(R.id.secStatement);
        tvDevice = findViewById(R.id.tvDevice);
        String selectedDevice = ModuleSharedPrefs.getValue(this, ModuleSharedPrefs.SELECTED_DEVICE);
        if (selectedDevice != null && selectedDevice.length() > 0) {
            String device = "Selected Device  -  " + selectedDevice;
            tvDevice.setText(device);
        }

        userId = getIntent().getStringExtra("userId");
        appToken = getIntent().getStringExtra("appToken");
        ModuleSharedPrefs.setValue(this, ModuleSharedPrefs.USER_ID, userId);
        ModuleSharedPrefs.setValue(this, ModuleSharedPrefs.APP_TOKEN, appToken);
        setLis();
    }

    private void setLis() {
        tvDevice.setOnClickListener(this);
        secCashWithDraw.setOnClickListener(this);
        secAadhaar.setOnClickListener(this);
        secBalanceEnquiry.setOnClickListener(this);
        secStatement.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_aeps_dashboard);
        init();
        if (!ModuleUtil.isAllValueAvailable(userId, appToken, this)) {
            finish();
        }
    }

    private void deviceSelection() {
        String[] deviceType = new String[]{"Mantra", "Morpho", "Tatvik", "Startek", "SecuGen", "Evolute"};
        int from; //This must be declared as global !
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Please select one option");
        alert.setSingleChoiceItems(deviceType, -1, (dialog, which) -> {
            ModuleSharedPrefs.setValue(AepsDashboard.this, ModuleSharedPrefs.SELECTED_DEVICE, deviceType[which]);
            ModuleSharedPrefs.setValue(AepsDashboard.this, ModuleSharedPrefs.SELECTED_DEVICE_INDEX, String.valueOf(which));
            tvDevice.setText("Selected Device  -  " + deviceType[which]);
        });

        alert.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });
        alert.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvDevice) {
            deviceSelection();
        } else if (view.getId() == R.id.secCashWithDraw) {
            Intent i = new Intent(AepsDashboard.this, CashWithdraw.class);
            i.putExtra("option", "3");
            startActivity(i);
        } else if (view.getId() == R.id.secAadhaar) {
            Intent i = new Intent(AepsDashboard.this, CashWithdraw.class);
            i.putExtra("option", "2");
            startActivity(i);
            //Toast.makeText(this, "Available Soon", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.secBalanceEnquiry) {
            Intent i = new Intent(AepsDashboard.this, CashWithdraw.class);
            i.putExtra("option", "1");
            startActivity(i);
        } else if (view.getId() == R.id.secStatement) {
            Intent i = new Intent(AepsDashboard.this, CashWithdraw.class);
            i.putExtra("option", "4");
            startActivity(i);
        }
    }

    public boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            return false;
        }
        return true;
    }

}