package com.payment.krishipay.views.loan;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.payment.krishipay.R;
import com.payment.krishipay.app.AppManager;
import com.payment.krishipay.app.Constants;
import com.payment.krishipay.network.RequestResponseLis;
import com.payment.krishipay.network.VolleyNetworkCall;
import com.payment.krishipay.utill.Print;
import com.payment.krishipay.utill.SharedPrefs;
import com.payment.krishipay.views.loan.adapter.StateAdapter;
import com.payment.krishipay.views.loan.model.StateModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateList extends AppCompatActivity implements RequestResponseLis {
    private String url, type;
    private ListView operatorListView;
    private List<StateModel> bankDataList;
    private StateAdapter bankListAdapter;
    private Context context;
    private EditText etSearch;
    private Toolbar toolbar;

    private void init() {
        context = this;
        operatorListView = findViewById(R.id.operatorList);
        etSearch = findViewById(R.id.etSearch);
        bankDataList = new ArrayList<>();
        setupToolBar();
    }

    private void setupToolBar() {
        //TextView tv = findViewById(R.id.tvToolBarTitle);
        //tv.setText("Select State");
        //ImageView backImage = findViewById(R.id.imgBack);
        //backImage.setOnClickListener(view -> finish());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan_bank_list);

        init();

        Print.P(url);

        bankListAdapter = new StateAdapter(this, bankDataList);
        operatorListView.setAdapter(bankListAdapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (bankDataList != null) {
                    List<StateModel> temp = new ArrayList<>();
                    for (StateModel d : bankDataList) {
                        if (d.getStatename().toLowerCase().contains(s.toString().toLowerCase())) {
                            temp.add(d);
                        }
                    }
                    bankListAdapter.UpdateList(temp);
                }
            }
        });

        url = Constants.URL.bankList;
        networkCallUsingVolleyApi(url, true);
    }

    private void networkCallUsingVolleyApi(String url, boolean isLoad) {
        if (AppManager.isOnline(this)) {
            new VolleyNetworkCall(this, this, url, 1, param(), isLoad, false).netWorkCall();
        } else {
            Toast.makeText(this, "Network connection error", Toast.LENGTH_LONG).show();
        }
    }

   /* private void networkCallUsingVolleyApi(String url) {
        Print.P(url);
        if (ModuleUtil.isOnline(this)) {
            new VolleyNetworkCall(this, this, url, 1, param()).netWorkCall();
        } else {
            Toast.makeText(this, "Network connection error", Toast.LENGTH_LONG).show();
        }
    }*/

    private Map<String, String> param() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", SharedPrefs.getValue(this, SharedPrefs.USER_ID));
        map.put("apptoken", SharedPrefs.getValue(this, SharedPrefs.APP_TOKEN));
        Print.P("BANK PARAM" + new JSONObject(map).toString());
        return map;
    }

    @Override
    public void onSuccessRequest(String result) {
        Gson gson = new GsonBuilder().create();
        if (!result.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("mahastate");
                Type type = new TypeToken<List<StateModel>>() {
                }.getType();
                bankDataList.addAll(gson.fromJson(jsonArray.toString(), type));
                bankListAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailRequest(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
