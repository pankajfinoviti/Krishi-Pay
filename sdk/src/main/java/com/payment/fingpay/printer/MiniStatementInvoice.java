package com.payment.fingpay.printer;

import android.app.ProgressDialog;
import android.content.Context;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.payment.fingpay.FingConstents;
import com.payment.fingpay.R;
import com.payment.fingpay.model.MicroATMModel;
import com.payment.fingpay.model.PrintDataModel;
import com.payment.fingpay.printer.adapter.InvoiceListStatementAdapter;
import com.payment.fingpay.utill.FingUtil;

import org.json.JSONObject;

import java.util.ArrayList;

public class MiniStatementInvoice extends AppCompatActivity {

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
        setContentView(R.layout.invoice_layout);
        init();
        context = MiniStatementInvoice.this;

        ImageView print = findViewById(R.id.imgPrint);
        print.setVisibility(View.GONE);
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

        if (FingUtil.StatementList != null && !FingUtil.StatementList.isEmpty()) {
            initTransactionList();
        } else {
            Toast.makeText(context, "Invoice records are not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void initList() {
    }

    private void initTransactionList() {
        imgTxnStatus.setVisibility(View.GONE);
        tvTxnStatus.setVisibility(View.GONE);
        findViewById(R.id.transactionLbl).setVisibility(View.INVISIBLE);
        try {
            InvoiceListStatementAdapter adapter = new InvoiceListStatementAdapter(this, FingUtil.StatementList);
            ListView listView = findViewById(R.id.listView);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(context, "Invoice Generation Failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

    public void netWorkCall() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating balance ...");
        dialog.show();
        dialog.setCancelable(false);
        String res = new JSONObject(FingConstents.ReceiptMap).toString();
        String apiUrl = FingConstents.URL.baseUrl + "api/android/secure/microatm/update?apptoken=" +
                SharedPrefs.getValue(this, SharedPrefs.APP_TOKEN) + "&user_id=" +
                SharedPrefs.getValue(this, SharedPrefs.USER_ID) +
                "&response=" + res;

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
                //Log.e("Log", "Response : " + dileveryRes);
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


