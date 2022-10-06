package com.payment.fingpay.printer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payment.fingpay.FingConstents;
import com.payment.payvenue.R;
import com.payment.payvenue.app.AppController;
import com.payment.payvenue.app.Constents;
import com.payment.payvenue.fingPayMicro.InvocieModel;
import com.payment.payvenue.fingPayMicro.printer.adapter.InvoiceListAdapter;
import com.payment.payvenue.model.MicroATMModel;
import com.payment.payvenue.printer.PrintDataModel;
import com.payment.payvenue.utill.SharedPrefs;

import org.json.JSONObject;

import java.util.ArrayList;

public class Invoice extends AppCompatActivity {

    NestedScrollView scrollView;
    TextView titlebar;
    Context context;
    MicroATMModel model;
    private ImageView imgCrossFinish;
    private ImageView imgTxnStatus;
    private TextView tvTxnStatus;
    private String type;

    public void init() {
        imgCrossFinish = findViewById(R.id.imgCrossFinish);
        ImageView imgShare = findViewById(R.id.imgShare);
        scrollView = findViewById(R.id.scrollView);
        View zigzagtop = findViewById(R.id.zigzagtop);
        LinearLayout mainInvoice = findViewById(R.id.mainInvoice);
        ImageView imgPrint = findViewById(R.id.imgPrint);
        imgTxnStatus = findViewById(R.id.imgTxnStatus);
        tvTxnStatus = findViewById(R.id.tvTxnStatus);
        //tvRefStanCode = findViewById(R.id.tvRefStanCode);
        //tvMsgCumStatusCode = findViewById(R.id.tvMsgCumStatusCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fing_invoice_layout);
        init();
        context = Invoice.this;

        ImageView print = findViewById(R.id.imgPrint);
        ImageView share = findViewById(R.id.imgShare);

        type = getIntent().getStringExtra("type");

        imgCrossFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                k();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l();
            }
        });

        if (FingConstents.ReceiptMap != null && !FingConstents.ReceiptMap.isEmpty()) {
            initList();
            if (type != null && type.length() > 0) {
                initTransactionList();
            } else {
                findViewById(R.id.transactionLbl).setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(context, "Invoice records are not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void initList() {
        try {
            InvoiceListAdapter adapter = new InvoiceListAdapter(this, FingConstents.ReceiptMap);
            ListView listView = findViewById(R.id.listView);
            listView.setAdapter(adapter);

            String status = FingConstents.ReceiptMap.get("Status");
            String message = FingConstents.ReceiptMap.get("Message");
            tvTxnStatus.setText(message);
            if (status != null && status.equalsIgnoreCase("true")) {
                imgTxnStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick));
            } else {
                imgTxnStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pending));
            }

            netWorkCall();
        } catch (Exception e) {
            Toast.makeText(context, "Invoice Generation Failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }
    }

    private void initTransactionList() {
        findViewById(R.id.transactionLbl).setVisibility(View.VISIBLE);
        TextView trnLbl = findViewById(R.id.transactionLbl);
        trnLbl.setOnClickListener(view -> {
            Intent i = new Intent(Invoice.this, MiniStatementInvoice.class);
            startActivity(i);
        });
    }

    public void setInvisible(RelativeLayout layout, View view) {
        layout.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
    }

    public final void l() {
        if (Build.VERSION.SDK_INT >= 23 && this.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
                && this.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        } else
            new wd().NUL(this.scrollView, this.context);
    }

    public final void k() {

        final PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        final String string = "E Banker Document";

        String s = "EBanker";
        final ArrayList<PrintDataModel> list2;
        final ArrayList<PrintDataModel> list = list2 = new ArrayList<PrintDataModel>();

        String message = FingConstents.ReceiptMap.get("Message");

        ArrayList<String> keyList = (new ArrayList<String>(FingConstents.ReceiptMap.keySet()));
        for (int i = 0; i < keyList.size(); i++)
            list.add(new PrintDataModel(keyList.get(i), FingConstents.ReceiptMap.get(keyList.get(i))));

        final PrintManager printManager2 = printManager;
        final String s2 = string;
        final ArrayList<PrintDataModel> list5 = list2;
        list5.add(new PrintDataModel("Remark", message));
        printManager2.print(s2, new ud(this, list2, s, "app_name"), null);

    }

    ProgressDialog dialog;
    static int statusCode;
    static String dileveryRes;

    private String getKeyParam() {
        Gson gson = new GsonBuilder().create();
        String res = "";
        if (FingConstents.InvoiceModelList != null && FingConstents.InvoiceModelList.size() > 0) {
            InvocieModel[] mOrderArray = new InvocieModel[FingConstents.InvoiceModelList.size()];
            int i = 0;
            for (InvocieModel model : FingConstents.InvoiceModelList) {
                mOrderArray[i] = model;
                i++;
            }

            res = gson.toJson(mOrderArray);
        }

        return res;
    }

    public void netWorkCall() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating balance ...");
        dialog.show();
        dialog.setCancelable(false);
        String res = new JSONObject(FingConstents.ReceiptMap).toString();
        String apiUrl = FingConstents.URL.baseUrl + "api/android/secure/microatm/update?apptoken=" +
                SharedPrefs.getValue(this, SharedPrefs.APP_TOKEN) + "&user_id=" +
                SharedPrefs.getValue(this, SharedPrefs.USER_ID) +
                "&txn_id=" + FingConstents.FING_TXN_ID +
                "&response=" + res ;
                //"&res=" + getKeyParam();

       // Log.e("URL", "ApiUrl: " + apiUrl);

        StringRequest sendRequest = new StringRequest(0, apiUrl,
                Response -> {
                    closeLoader();
                    Toast.makeText(context, "Records Updated on server", Toast.LENGTH_SHORT).show();
                }, volleyError -> {
            closeLoader();
            onError(volleyError);
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                //Log.e("Log", "Status Code : " + statusCode);
                return super.parseNetworkResponse(response);
            }

            @Override
            protected void deliverResponse(String response) {
                dileveryRes = response;
               // Log.e("Log", "Response : " + dileveryRes);
                super.deliverResponse(response);
            }
        };

        sendRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(sendRequest);
    }

    private void onError(VolleyError volleyError) {
        String errorMsg = "Due to some error these records are not updated on our server.";
        if (volleyError.getMessage() != null) {
            errorMsg += "\nError: " + volleyError.getMessage();
        }

        errorMsg += "\n\nStatus Code: " + volleyError.networkResponse.statusCode;

        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
    }


    private void closeLoader() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}


