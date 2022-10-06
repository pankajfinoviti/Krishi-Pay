package com.payment.krishipay.utill;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.payment.krishipay.R;
import com.payment.krishipay.app.Constants;
import com.payment.krishipay.network.RequestResponseLis;
import com.payment.krishipay.network.VolleyNetworkCall;
import com.payment.krishipay.views.on_boarding.OnBoardingActivity;

import org.json.JSONObject;

import java.util.HashMap;


public class AepsAppHelper {
    private final Activity context;
    private final String PACKAGE_NAME = "com.payment.aepsservice";

    public AepsAppHelper(Activity context) {
        this.context = context;
    }

    public void callAeps(String saltKey, String secretKey, String BcId, String userId,
                         String emailId, String phone, String cpid) {
        try{
            callAEPS(getIntent(),  saltKey,  secretKey,  BcId,  userId, emailId,  phone,  cpid);
        }catch(Exception e){
            aepsPopup();
        }
    }

    private Intent getIntent(){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(PACKAGE_NAME, PACKAGE_NAME + ".AEPSManagerActivity"));
        intent.setType("text/plain");
        return intent;
    }

    private void callAEPS(Intent intent, String saltKey, String secretKey, String BcId, String userId,
                          String emailId, String phone, String cpid){
        intent.putExtra("aepsStatus", "success");
        intent.putExtra("ruteType", "mhagram");
        intent.putExtra("saltKey", saltKey);
        intent.putExtra("secretKey", secretKey);
        intent.putExtra("BcId", BcId);
        intent.putExtra("UserId", userId);
        intent.putExtra("bcEmailId", emailId);
        intent.putExtra("Phone1", phone);
        intent.putExtra("cpid", cpid);
        intent.putExtra("baseUrl", Constants.URL.BASE_URL);
        context.startActivityForResult(intent, 1);
    }

    public void callXAEPS(String ruteType){
        Intent intent = getIntent();
        try{
            intent.putExtra("userId", SharedPrefs.getValue(context, SharedPrefs.USER_ID));
            intent.putExtra("appToken",SharedPrefs.getValue(context, SharedPrefs.APP_TOKEN));
            intent.putExtra("ruteType", ruteType);
            intent.putExtra("txnType", "CW");
            intent.putExtra("baseUrl", Constants.URL.BASE_URL);
            intent.putExtra("aepsStatus", "success");
            intent.putExtra("aepsType", "xettle");
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
            aepsPopup();
        }
    }

    private void aepsPopup() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setContentView(R.layout.bottom_update_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button btn = dialog.findViewById(R.id.btnInstall);
        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v-> dialog.cancel());
        btn.setOnClickListener(v -> {
            dialog.cancel();
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+PACKAGE_NAME)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        dialog.show();
    }

    public void checkForUpdates() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);
        Task<AppUpdateInfo> appUpdateInfo = appUpdateManager.getAppUpdateInfo();
        appUpdateInfo.addOnSuccessListener(result -> handleImmediateUpdate(appUpdateManager, appUpdateInfo));
    }

    private void handleImmediateUpdate(AppUpdateManager manager, Task<AppUpdateInfo> info) {
        try {
            if ((info.getResult().updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                    info.getResult().updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) &&
                    info.getResult().isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                manager.startUpdateFlowForResult(info.getResult(), AppUpdateType.IMMEDIATE, context, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void aepsInitiate(){
        new VolleyNetworkCall(new RequestResponseLis() {
            @Override
            public void onSuccessRequest(String Response) {
                try{
                    JSONObject dataRoot = new JSONObject(Response);
                    if (AppHandler.checkStatus(Response, context)) {
                        JSONObject data = dataRoot.getJSONObject("data");
                        String saltKey = data.getString("saltKey");
                        String secretKey = data.getString("secretKey");
                        String BcId = data.getString("BcId");
                        String UserId = data.getString("UserId");
                        String bcEmailId = data.getString("bcEmailId");
                        String Phone1 = data.getString("Phone1");
                        callAeps(saltKey, secretKey, BcId, UserId, bcEmailId, Phone1, SharedPrefs.getValue(context, SharedPrefs.USER_ID));
                    }else{
                        String message = AppHandler.getMessage(Response);
                        if (message.equalsIgnoreCase("Aeps Registration Pending") ||
                                dataRoot.getString("statustype").equalsIgnoreCase("pending") ||
                                dataRoot.getString("statustype").equalsIgnoreCase("failed")) {
                            Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
                            ExtensionFunction.startActivity(context, OnBoardingActivity.class);
                            context.finish();
                        } else {
                            Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailRequest(String msg) {}
        }, context, Constants.URL.AEPS_INITIATE, 1,  new HashMap<>(), true, false).netWorkCall();
    }
}
