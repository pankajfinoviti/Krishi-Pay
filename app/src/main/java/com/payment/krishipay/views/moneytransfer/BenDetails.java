package com.payment.krishipay.views.moneytransfer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.payment.krishipay.R;
import com.payment.krishipay.app.AppManager;
import com.payment.krishipay.app.Constants;
import com.payment.krishipay.network.RequestResponseLis;
import com.payment.krishipay.network.VolleyNetworkCall;
import com.payment.krishipay.utill.AppHandler;
import com.payment.krishipay.utill.MyUtil;
import com.payment.krishipay.utill.Print;
import com.payment.krishipay.views.moneytransfer.adapter.BenListAdapter;
import com.payment.krishipay.views.moneytransfer.mintra.MintraAddBeneficiary;
import com.payment.krishipay.views.moneytransfer.mintra.MintraDMTTransaction;
import com.payment.krishipay.views.moneytransfer.model.BenModel;
import com.payment.krishipay.views.otpview.OTPValidate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BenDetails extends AppCompatActivity implements RequestResponseLis, BenListAdapter.BenItemClick {
    private int REQUEST_TYPE = 0;
    private TextView tvContact, tvName, tvMax, tvMin;
    private Context context;
    private Button btnProceed;
    private RecyclerView listView;
    private List<BenModel> benModelList;
    private BenListAdapter adapter;
    private String BEN_MOBILE, SENDER_MOBILE, BEN_ACCOUNT, SENDER_NAME;
    private String TYPE;
    private Button btnAddBen;

    private void init() {
        context = this;
        benModelList = new ArrayList<>();
        listView = findViewById(R.id.benList);
        tvContact = findViewById(R.id.tvContact);
        tvName = findViewById(R.id.tvName);
        tvMax = findViewById(R.id.tvMax);
        tvMin = findViewById(R.id.tvMin);
        btnAddBen = findViewById(R.id.btnAddBen);

        btnAddBen.setOnClickListener(v -> {
            Intent i;
            if (Constants.isActiveMintraDMT) {
                i = new Intent(BenDetails.this, MintraAddBeneficiary.class);
            } else {
                i = new Intent(BenDetails.this, AddBeneficiary.class);
            }
            i.putExtra("sender_name", SENDER_NAME);
            i.putExtra("sender_number", SENDER_MOBILE);
            startActivity(i);
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUtil.transparentStatusBar(this);
        setContentView(R.layout.dmt_ben_details);
        init();

        Bundle bundle = getIntent().getExtras();
        SENDER_MOBILE = bundle.getString("sender_number");
        SENDER_NAME = bundle.getString("name");
        String available_limit = bundle.getString("available_limit");
        String limit_spend = bundle.getString("total_spend");
        String json = bundle.getString("json");
        String max = "TOTAL : " + MyUtil.formatWithRupee(this, available_limit);
        String min = "USED : " + MyUtil.formatWithRupee(this, limit_spend);
        tvMax.setText(max);
        tvMin.setText(min);
        tvName.setText(SENDER_NAME);
        tvContact.setText(SENDER_MOBILE);
        updateRecord(json);
    }

    private void initBenSlider() {
        adapter = new BenListAdapter(this, benModelList, this);
        LinearLayoutManager mLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(adapter);
    }

    private boolean isValid() {
        return true;
    }

    private void networkCallUsingVolleyApi(String url, boolean isLoad) {
        if (AppManager.isOnline(this)) {
            new VolleyNetworkCall(this, this, url, 1, param(), isLoad).netWorkCall();
        } else {
            Toast.makeText(this, "Network connection error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSuccessRequest(String JSonResponse) {
        try {
            if (TYPE.equalsIgnoreCase("otp")) {
                Intent i = new Intent(BenDetails.this, OTPValidate.class);
                i.putExtra("type", "otp");
                i.putExtra("url", Constants.URL.DMT_TRANSACTION);
                i.putExtra("ben_mobile", BEN_MOBILE);
                i.putExtra("sender_mobile", SENDER_MOBILE);
                i.putExtra("ben_account", BEN_ACCOUNT);
                startActivity(i);
                TYPE = "";
            } else {
                updateRecord(JSonResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailRequest(String msg) {
        Print.P(msg);
    }

    private void updateRecord(String json) {
        try {
            benModelList.clear();
            JSONObject jsonObject = new JSONObject(json);
            String available_limit = jsonObject.getString("totallimit");
            String limit_spend = jsonObject.getString("usedlimit");
            String max = "TOTAL : " + MyUtil.formatWithRupee(this, available_limit);
            String min = "USED : " + MyUtil.formatWithRupee(this, limit_spend);
            tvMax.setText(max);
            tvMin.setText(min);
            JSONArray jsonArray = jsonObject.getJSONArray("beneficiary");
            benModelList.addAll(AppHandler.parseBenList(this, jsonArray));
            initBenSlider();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void benItemLis(BenModel model) {
        BEN_MOBILE = model.getBenemobile();
        BEN_ACCOUNT = model.getBeneaccount();
        if (!model.getBenestatus().equalsIgnoreCase("NV")) {
            Intent i;
            if (Constants.isActiveMintraDMT) {
                i = new Intent(this, MintraDMTTransaction.class);
            } else {
                i = new Intent(this, DMTTransaction.class);
            }
            i.putExtra("data", model);
            i.putExtra("sender_name", SENDER_NAME);
            i.putExtra("sender_number", SENDER_MOBILE);
            startActivity(i);
        } else {
            TYPE = "otp";
            networkCallUsingVolleyApi(Constants.URL.DMT_TRANSACTION, true);
        }
    }

    private Map<String, String> param() {
        Map<String, String> map = new HashMap<>();
        if (TYPE.equalsIgnoreCase("otp") || TYPE.equalsIgnoreCase("verification")) {
            map.put("type", TYPE);
            map.put("mobile", SENDER_MOBILE);
            if (MyUtil.isNN(Constants.DMT_RID))
                map.put("rid", Constants.DMT_RID);
        } else {
            Toast.makeText(context, "Transafer Amount", Toast.LENGTH_SHORT).show();
        }
        return map;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.IS_RELOAD_REQUEST) {
            Constants.IS_RELOAD_REQUEST = false;
            TYPE = "verification";
            networkCallUsingVolleyApi(Constants.URL.DMT_TRANSACTION, true);
        }
    }
}
