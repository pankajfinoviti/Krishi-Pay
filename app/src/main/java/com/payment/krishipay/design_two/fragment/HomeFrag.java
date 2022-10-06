package com.payment.krishipay.design_two.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.payment.krishipay.R;
import com.payment.krishipay.adapter.HeaderDesignTwoAdapter;
import com.payment.krishipay.adapter.HomeAdapter;
import com.payment.krishipay.app.AppManager;
import com.payment.krishipay.app.Constants;
import com.payment.krishipay.dataModel.MainLocalData;
import com.payment.krishipay.model.ActivityListModel;
import com.payment.krishipay.network.RequestResponseLis;
import com.payment.krishipay.network.VolleyNetworkCall;
import com.payment.krishipay.utill.AppHandler;
import com.payment.krishipay.utill.MarqueeTextView;
import com.payment.krishipay.utill.MyUtil;
import com.payment.krishipay.utill.RecyclerTouchListener;
import com.payment.krishipay.utill.SharedPrefs;
import com.payment.krishipay.views.AllServices;
import com.payment.krishipay.views.billpayment.RechargeAndBillPayment;
import com.payment.krishipay.views.mhagram_aeps_matm.HandlerActivity;
import com.payment.krishipay.views.moneytransfer.DMTSearchAccount;
import com.payment.krishipay.views.moneytransfer.mintra.MintraDMTSearchAccount;
import com.payment.krishipay.views.walletsection.AepsMatmWalletReqest;
import com.payment.krishipay.views.walletsection.WalletFundRequest;
import com.payment.krishipay.views.walletsection.WalletOptions;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFrag extends Fragment implements RequestResponseLis {
    private RecyclerView rvHome, headerList;
    private Activity context;
    private LinearLayout secAddMoney, tvWalletTitleCon, secAepsWallet, secMatm;
    private TextView tvMatmBalance, tvMainBalance, tvAepsBalance, tvWalletSec;
    private RelativeLayout shimmer;
    private CarouselView rvSlider;

    public HomeFrag() { }

    private void init(View view) {
        rvSlider = view.findViewById(R.id.rvSlider);

        rvHome = view.findViewById(R.id.rvHome);
        headerList = view.findViewById(R.id.headerList);
        context = getActivity();
        shimmer = view.findViewById(R.id.shimmer);
        tvMatmBalance = view.findViewById(R.id.tvMatmBalance);
        tvMainBalance = view.findViewById(R.id.tvMainBalance);
        tvAepsBalance = view.findViewById(R.id.tvAepsBalance);
        tvWalletSec = view.findViewById(R.id.tvWalletSec);

        tvWalletTitleCon = view.findViewById(R.id.tvWalletTitleCon);
        secAepsWallet = view.findViewById(R.id.secAepsWallet);
        secMatm = view.findViewById(R.id.secMatm);
        secAddMoney = view.findViewById(R.id.secReload);

        secAddMoney.setOnClickListener(v -> {
            shimmer.setVisibility(View.VISIBLE);
            networkCallUsingVolleyApi(Constants.URL.BALANCE_UPDATE, false);
        });

        tvWalletSec.setOnClickListener(v -> startActivity(new Intent(context, WalletOptions.class)));
        tvWalletTitleCon.setOnClickListener(v -> {
            Intent i = new Intent(context, WalletFundRequest.class);
            startActivity(i);
        });
        secAepsWallet.setOnClickListener(v -> {
            Intent i = new Intent(context, AepsMatmWalletReqest.class);
            i.putExtra("activity_type", "aeps");
            startActivity(i);
        });
        secMatm.setOnClickListener(v -> {
            Intent i = new Intent(context, AepsMatmWalletReqest.class);
            i.putExtra("activity_type", "matm");
            startActivity(i);
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for context fragment
        View view = inflater.inflate(R.layout.home_fragment_design_two, container, false);

        init(view);
        gridInit();
        topHeaderList();
        gridInitSlider();
        initMarquee(view);
        return view;
    }

    private void gridInit() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 4);
        rvHome.setLayoutManager(layoutManager);
        rvHome.setItemAnimator(new DefaultItemAnimator());
        HomeAdapter homeAdapter = new HomeAdapter(context, MainLocalData.getHomeGridRecord());
        rvHome.setAdapter(homeAdapter);
        rvHome.addOnItemTouchListener(new RecyclerTouchListener(context,
                rvHome, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i;
                if (position == 7) {
                    i = new Intent(context, AllServices.class);
                } else {
                    i = new Intent(context, RechargeAndBillPayment.class);
                    i.putExtra("position", String.valueOf(position));
                }
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void topHeaderList() {
        LinearLayoutManager nearLayoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false);
        headerList.setLayoutManager(nearLayoutManager);
        headerList.setItemAnimator(new DefaultItemAnimator());
        HeaderDesignTwoAdapter homeAdapter = new HeaderDesignTwoAdapter(context, MainLocalData.getMoneyTransferGrid());
        headerList.setAdapter(homeAdapter);
        headerList.addOnItemTouchListener(new RecyclerTouchListener(context,
                headerList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i;
                ActivityListModel model = MainLocalData.getMoneyTransferGrid().get(position);
                switch (model.getTxt1()) {
                    case "AEPS":
                        i = new Intent(context, HandlerActivity.class);
                        i.putExtra("appUserId", SharedPrefs.getValue(context, SharedPrefs.USER_ID));
                        i.putExtra("type", "aeps");
                        startActivityForResult(i, 5000);
                        break;
                    case "MATM":
                        i = new Intent(context, HandlerActivity.class);
                        i.putExtra("appUserId", SharedPrefs.getValue(context, SharedPrefs.USER_ID));
                        i.putExtra("type", "matm");
                        startActivityForResult(i, 5000);
                        break;
                    case "FAEPS":
                        /*i = new Intent(context, AepsDashboard.class);
                        i.putExtra("userId", SharedPrefs.getValue(context, SharedPrefs.USER_ID));
                        i.putExtra("appToken", SharedPrefs.getValue(context, SharedPrefs.APP_TOKEN));
                        i.putExtra("baseUrl", Constants.URL.BASE_URL);
                        startActivity(i);*/
                        break;
                    case "FMATM":
                        AppHandler.openFingPayMicroAtm(context);
                        break;
                    case "UPI":
                        AppHandler.upiServiceOption(context);
                        break;
                    case "CASH D":
                       /* i = new Intent(context, CashDeposite.class);
                        i.putExtra("userId", SharedPrefs.getValue(context, SharedPrefs.USER_ID));
                        i.putExtra("appToken", SharedPrefs.getValue(context, SharedPrefs.APP_TOKEN));
                        i.putExtra("baseUrl", Constants.URL.BASE_URL);
                        startActivity(i);*/
                        break;
                    case "DMT":
                        if (Constants.isActiveMintraDMT)
                            startActivity(new Intent(context, MintraDMTSearchAccount.class));
                        else
                            startActivity(new Intent(context, DMTSearchAccount.class));
                        break;
                    case "PHONE":
                        i = new Intent(context, RechargeAndBillPayment.class);
                        i.putExtra("position", "0");
                        startActivity(i);
                        break;
                    case "DTH":
                        i = new Intent(context, RechargeAndBillPayment.class);
                        i.putExtra("position", "1");
                        startActivity(i);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void networkCallUsingVolleyApi(String url, boolean isLoad) {
        if (AppManager.isOnline(context)) {
            new VolleyNetworkCall(this, context, url, 1, param(), isLoad).netWorkCall();
        } else {
            Toast.makeText(context, "Network connection error", Toast.LENGTH_LONG).show();
        }
    }

    private Map<String, String> param() {
        Map<String, String> map = new HashMap<>();
        return map;
    }

    @Override
    public void onSuccessRequest(String JSonResponse) {
        shimmer.setVisibility(View.GONE);
        try {
            JSONObject jsonObject = new JSONObject(JSonResponse);
            JSONObject userObject = new JSONObject(jsonObject.getString("data"));
            if (userObject.has("mainwallet"))
                SharedPrefs.setValue(context, SharedPrefs.MAIN_WALLET, userObject.getString("mainwallet"));
            else
                SharedPrefs.setValue(context, SharedPrefs.MAIN_WALLET, userObject.getString("balance"));
            if (userObject.has("microatmbalance")) {
                SharedPrefs.setValue(context, SharedPrefs.MICRO_ATM_BALANCE, userObject.getString("microatmbalance"));
            } else {
                SharedPrefs.setValue(context, SharedPrefs.MICRO_ATM_BALANCE, "NO");
            }
            SharedPrefs.setValue(context, SharedPrefs.APES_BALANCE, userObject.getString("aepsbalance"));
            initBalance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBalance() {
        tvMainBalance.setText(MyUtil.formatWithRupee(context, SharedPrefs.getValue(context, SharedPrefs.MAIN_WALLET)));
        tvAepsBalance.setText(MyUtil.formatWithRupee(context, SharedPrefs.getValue(context, SharedPrefs.APES_BALANCE)));
        tvMatmBalance.setText(MyUtil.formatWithRupee(context, "0"));
        String mBal = SharedPrefs.getValue(context, SharedPrefs.MICRO_ATM_BALANCE);
        if (mBal != null && !mBal.equalsIgnoreCase("NO") && mBal.length() > 0)
            tvMatmBalance.setText(MyUtil.formatWithRupee(context, mBal));
    }

    @Override
    public void onFailRequest(String msg) {
        shimmer.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shimmer != null) {
            shimmer.setVisibility(View.VISIBLE);
            networkCallUsingVolleyApi(Constants.URL.BALANCE_UPDATE, false);
        }
    }

    private List<String> stringList;

    private void gridInitSlider() {
        stringList = new ArrayList<>();
        stringList.addAll(Arrays.asList(Constants.images));
        String is_avail_array = SharedPrefs.getValue(context, SharedPrefs.BANNERARRAY);
        if (MyUtil.isNN(is_avail_array)) {
            try {
                stringList = new ArrayList<>();
                JSONArray bannersJson = new JSONArray(is_avail_array);
                for (int i = 0; i < bannersJson.length(); i++) {
                    JSONObject obj = bannersJson.getJSONObject(i);
                    String url = Constants.URL.BASE_URL + "/public/" + obj.getString("value");
                    stringList.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        rvSlider.setPageCount(stringList.size());
        rvSlider.setImageListener(imageListener);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            MyUtil.setGlideImage(stringList.get(position), imageView, context);
        }
    };

    private void initMarquee(View view) {
        String t = "";
        view.findViewById(R.id.secNews).setVisibility(View.GONE);
        MarqueeTextView tvMarquee = view.findViewById(R.id.tvMarquee);
        t = SharedPrefs.getValue(getActivity(), SharedPrefs.NEWS);
        if (Constants.isDemoNewsSec)
            t = "New horizontal scrolling shows how to use marquee, with a long text";
        if (MyUtil.isNN(t)) {
            view.findViewById(R.id.secNews).setVisibility(View.VISIBLE);
            tvMarquee.setText(t);
        }
    }
}
