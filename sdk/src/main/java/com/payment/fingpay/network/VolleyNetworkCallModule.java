package com.payment.fingpay.network;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.payment.fingpay.FingConstents;
import com.payment.fingpay.R;
import com.payment.fingpay.utill.FingUtil;
import com.payment.fingpay.utill.Handler;
import com.payment.fingpay.utill.Print;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class VolleyNetworkCallModule {
    Map<String, String> map;
    Dialog dialog;
    private RequestResponseLis listener;
    private Activity context;
    private String apiUrl;
    private int method = 0;
    private boolean isDialog = true;
    private boolean isStatus = true;

    public VolleyNetworkCallModule(Activity context, String apiUrl) {
        this.context = context;
        this.apiUrl = apiUrl;
    }

    public VolleyNetworkCallModule(RequestResponseLis listener, Activity context, String apiUrl, boolean isDialog) {
        this.listener = listener;
        this.context = context;
        this.apiUrl = apiUrl;
        this.isDialog = isDialog;
    }

    public VolleyNetworkCallModule(RequestResponseLis listener, Activity context, String apiUrl, int method, Map<String, String> map, boolean isDialog) {
        this.listener = listener;
        this.context = context;
        this.apiUrl = apiUrl;
        this.method = method;
        this.map = map;
        this.isDialog = isDialog;
    }

    public VolleyNetworkCallModule(RequestResponseLis listener, Activity context, String apiUrl, int method, Map<String, String> map, boolean isDialog, boolean isStatus) {
        this.listener = listener;
        this.context = context;
        this.apiUrl = apiUrl;
        this.method = method;
        this.map = map;
        this.isDialog = isDialog;
        this.isStatus = isStatus;
    }

    public VolleyNetworkCallModule(Activity context, String apiUrl, int method, Map<String, String> map, boolean isDialog) {
        this.listener = listener;
        this.context = context;
        this.apiUrl = apiUrl;
        this.method = method;
        this.map = map;
        this.isDialog = isDialog;
    }

    public VolleyNetworkCallModule(RequestResponseLis listener, Activity context, String apiUrl, int method, Map<String, String> map) {
        this.listener = listener;
        this.context = context;
        this.apiUrl = apiUrl;
        this.method = method;
        this.map = map;
    }

    StringRequest sendRequest;

    public void netWorkCall() {
        if (FingConstents.queue == null)
            FingConstents.queue = Volley.newRequestQueue(context);
        if (isDialog)
            showLoader(context);
        sendRequest = new StringRequest(
                method,
                apiUrl,
                Response -> {
                    Print.P("Get: Volley Response:" + Response);
                    try {
                        if (listener != null) {
                            if (isStatus) {
                                if (Handler.getStatus(Response).equalsIgnoreCase("UA")) {
                                    listener.onFailRequest("Unauthorized Access");
                                    showNetworkError("Your session has been expired please login again to use our services");
                                    showLogOutButton();
                                } else if (Handler.checkStatus(Response, context)) {
                                    closeLoader();
                                    listener.onSuccessRequest(Response);
                                } else {
                                    String msg = "Getting unsuccessful response from the server please try again after some time";
                                    JSONObject obj = new JSONObject(Response);
                                    if (obj.has("message")) {
                                        msg = obj.getString("message");
                                        showNetworkError("" + msg);
                                    } else {
                                        showNetworkError(msg);
                                    }
                                }
                            } else {
                                closeLoader();
                                listener.onSuccessRequest(Response);
                            }
                        } else {
                            closeLoader();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String msg = "";
                        if (e.getMessage() != null)
                            msg = e.getMessage();
                        showNetworkError("Exception Error : " + msg);
                    }
                }
                , volleyError -> {
            try {
                String json = null;
                NetworkResponse response = volleyError.networkResponse;
                if (response != null && response.data != null) {
                    apiUrl = apiUrl.replaceAll(FingConstents.URL.COMMON_PATH, "");
                    switch (response.statusCode) {
                        case 400:
                            String error = "Status Code : Unexpected response code 400 \nPath : " + apiUrl;
                            json = new String(response.data);
                            json = trimMessage(json, "message");
                            if (json != null)
                                error = error + "\n\n" + json;
                            showNetworkError(error);
                            break;
                        case 404:
                            error = "Unexpected response code 404 for \nPath : " + apiUrl;
                            showNetworkError(error);
                            break;
                        default:
                            error = "Unexpected response code " + response.statusCode + " for\nPath : " + apiUrl;
                            showNetworkError(error);
                            break;
                    }
                }
            } catch (Exception error) {
                error.printStackTrace();
                showNetworkError("Some kind of network error encountered");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                String t = FingConstents.TOKEN;
                String u = FingConstents.USER_ID;
                if (FingUtil.isNN(t) && FingUtil.isNN(u)) {
                    map.put("apptoken", t);
                    map.put("user_id", u);
                }
                Print.P("PARAMS: " + new JSONObject(map).toString());
                return map;
            }
        };

        sendRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 3, 2));

        try {
            //Log.e("get - URL ", sendRequest.getUrl());
            Print.P("get - Headers"+ sendRequest.getHeaders().toString());
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        queue.add(sendRequest);
    }

    public String trimMessage(String json, String key) {
        String trimmedString = null;
        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return trimmedString;
    }

    LottieAnimationView lottie;
    TextView tvStatus;
    ImageView imgClose, imgShare, imgMinimise;
    LinearLayout loaderCon;
    private String error = "";
    private AppCompatButton loginButton;

    private void showLoader(Activity context) {
        Log.e("DIALOG", "DIALOG OPEN");
        closeLoader();
        if (context.isDestroyed())
            return;
        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.module_loader_layout);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        loaderCon = dialog.findViewById(R.id.loaderCon);
        imgShare = dialog.findViewById(R.id.imgShare);
        imgClose = dialog.findViewById(R.id.imgClose);
        tvStatus = dialog.findViewById(R.id.bottomTv);
        lottie = dialog.findViewById(R.id.LottieAnim);
        loginButton = dialog.findViewById(R.id.loginButton);
        imgMinimise = dialog.findViewById(R.id.imgMinimise);
        imgClose.setOnClickListener(v -> {
            closeLoader();
            context.finish();
        });
        imgMinimise.setOnClickListener(v -> closeLoader());
        loginButton.setVisibility(View.GONE);
        loginButton.setOnClickListener(v -> {
            closeLoader();
            AppManager.getInstance().logoutFromServer(context, false);
        });
        imgShare.setOnClickListener(v -> MyUtil.shareOnAllApps(context, error));
        dialog.setCancelable(false);
        dialog.show();
    }

    private void closeLoader() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void showNetworkError(String string) {
        error = string;
        Print.P("Demo Error : " + error);
        if (!isDialog)
            showLoader(context);
        if (listener != null) {
            listener.onFailRequest(string);
        }
        try {
            ViewGroup.LayoutParams params = loaderCon.getLayoutParams();
            params.height = 400;
            params.width = 400;
            loaderCon.setLayoutParams(params);
            lottie.setAnimation(R.raw.network_error);
            imgClose.setVisibility(View.VISIBLE);
            imgShare.setVisibility(View.VISIBLE);
            imgMinimise.setVisibility(View.VISIBLE);
            tvStatus.setText(string);
        } catch (Exception e) {
            e.printStackTrace();
            closeLoader();
        }
    }

    private void showLogOutButton() {
        imgClose.setVisibility(View.GONE);
        imgShare.setVisibility(View.GONE);
        imgMinimise.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
    }
}
