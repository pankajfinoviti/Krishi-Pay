package com.payment.aeps.network;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.payment.aeps.app.ModuleConstants;
import com.payment.aeps.util.Print;

import java.util.HashMap;
import java.util.Map;

public class VolleyGetNetworkCall {

    private RequestResponseLis listener;
    private Context context;
    private String apiUrl;
    private int method = 0;
    Map<String, String> map;

    public VolleyGetNetworkCall(RequestResponseLis listener, Context context) {
        this.listener = listener;
        this.context = context;
        apiUrl = ModuleConstants.baseUrl;
    }

    public VolleyGetNetworkCall(RequestResponseLis listener, Context context, String apiUrl) {
        this.listener = listener;
        this.context = context;
        this.apiUrl = apiUrl;
    }

    public VolleyGetNetworkCall(RequestResponseLis listener, Context context, String apiUrl, int method, Map<String, String> map) {
        this.listener = listener;
        this.context = context;
        this.apiUrl = apiUrl;
        this.method = method;
        this.map = map;
    }

    ProgressDialog loaderDialog;

    public void netWorkCall() {
        loaderDialog = new ProgressDialog(context);
        loaderDialog.setMessage("Please wait...");
        loaderDialog.setCancelable(false);
        loaderDialog.show();

        StringRequest sendRequest = new StringRequest(
                method,
                apiUrl,
                Response -> {
                    Print.P(Response);
                    closeLoader();
                    listener.onSuccessRequest(Response);
                }
                , volleyError -> {
            closeLoader();
            volleyError.printStackTrace();
            Print.P("Not able to connect with server");
            try {
                listener.onFailRequest("Network connection error\nStatus Code : " + volleyError.networkResponse.statusCode);
            } catch (Exception e) {
                listener.onFailRequest("Network connection error");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                if (map == null) {
                    map = new HashMap<>();
                }
                return map;
            }
        };

        sendRequest.setRetryPolicy(new
                DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(sendRequest);
    }

    public interface RequestResponseLis {
        void onSuccessRequest(String JSonResponse);

        void onFailRequest(String msg);
    }

    private void closeLoader() {
        if (loaderDialog != null && loaderDialog.isShowing()) {
            loaderDialog.dismiss();
        }
    }

}
