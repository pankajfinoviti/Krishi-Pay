package com.payment.krishipay;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amirarcane.lockscreen.activity.EnterPinActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.payment.krishipay.design_two.fragment.HomeFrag;
import com.payment.krishipay.design_two.fragment.UserAccountFrag;
import com.payment.krishipay.permission.AppPermissions;
import com.payment.krishipay.permission.PermissionHandler;
import com.payment.krishipay.utill.MyUtil;
import com.payment.krishipay.utill.Print;
import com.payment.krishipay.utill.SharedPrefs;
import com.payment.krishipay.views.SplashScreen;
import com.payment.krishipay.views.reports.AllReports;
import com.payment.krishipay.views.settings.Settings;

public class MainActivityMintra extends AppCompatActivity {
    private static final int REQUEST_CODE = 101;
    private BottomNavigationView navigation;
    private Context context;
    private int fragPosition = 0;

    private void init() {
        context = this;
        appLockHandler();
        navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(new HomeFrag());
        fragPosition = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_two);
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE};
        AppPermissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                init();
            }
        });
    }

    private void appLockHandler() {
        String isLockEnable = SharedPrefs.getValue(this, SharedPrefs.IS_ALLOWED_APP_LOCK);
        if (MyUtil.isNN(isLockEnable)) {
            Intent intent = new Intent(this, EnterPinActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case REQUEST_CODE:
                    if (resultCode == EnterPinActivity.RESULT_BACK_PRESSED) {
                        finishAffinity();
                        startActivity(new Intent(this, SplashScreen.class));
                    }
                    break;
                case 5000:
                    String msg = data.getStringExtra("msg");
                    if (MyUtil.isNN(msg)) {
                        Print.P(msg);
                        Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    AlertDialog alert;

    private void confirmPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Do you really want to exit?")
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> finish())
                .setNegativeButton(android.R.string.no, (dialog, whichButton) -> alert.dismiss());
        alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        confirmPopup();
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.btnHome:
                fragPosition = 0;
                fragment = new HomeFrag();
                loadFragment(fragment);
                return true;
            case R.id.btnReports:
                fragPosition = 1;
                Intent i = new Intent(this, AllReports.class);
                startActivity(i);
                return true;
            case R.id.btnAeps:
                fragPosition = 2;
                /*i = new Intent(context, AepsDashboard.class);
                i.putExtra("userId", SharedPrefs.getValue(context, SharedPrefs.USER_ID));
                i.putExtra("appToken", SharedPrefs.getValue(context, SharedPrefs.APP_TOKEN));
                i.putExtra("baseUrl", Constants.URL.BASE_URL);
                startActivity(i);*/
                return true;
            case R.id.btnAccount:
                fragPosition = 3;
                fragment = new UserAccountFrag();
                loadFragment(fragment);
                return true;
            case R.id.btnSetting:
                i = new Intent(this, Settings.class);
                startActivity(i);
                return true;
        }
        return false;
    };

}