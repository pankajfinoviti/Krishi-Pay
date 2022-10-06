package com.payment.krishipay;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.amirarcane.lockscreen.activity.EnterPinActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.payment.krishipay.adapter.HeaderAdapter;
import com.payment.krishipay.adapter.HomeAdapter;
import com.payment.krishipay.adapter.MyCustomPagerAdapter;
import com.payment.krishipay.app.AppManager;
import com.payment.krishipay.app.Constants;
import com.payment.krishipay.dataModel.MainLocalData;
import com.payment.krishipay.model.ActivityListModel;
import com.payment.krishipay.model.SlugModel;
import com.payment.krishipay.network.RequestResponseLis;
import com.payment.krishipay.network.VolleyNetworkCall;
import com.payment.krishipay.permission.AppPermissions;
import com.payment.krishipay.permission.PermissionHandler;
import com.payment.krishipay.utill.AepsAppHelper;
import com.payment.krishipay.utill.AppHandler;
import com.payment.krishipay.utill.MyUtil;
import com.payment.krishipay.utill.Print;
import com.payment.krishipay.utill.RecyclerTouchListener;
import com.payment.krishipay.utill.SharedPrefs;
import com.payment.krishipay.views.AllServices;
import com.payment.krishipay.views.BankAccountPage;
import com.payment.krishipay.views.ProfilePage;
import com.payment.krishipay.views.SplashScreen;
import com.payment.krishipay.views.allservices_search.AllServicesSearch;
import com.payment.krishipay.views.billpayment.RechargeAndBillPayment;
import com.payment.krishipay.views.member.MemberListAll;
import com.payment.krishipay.views.member.model.MemberModel;
import com.payment.krishipay.views.mhagram_aeps_matm.HandlerActivity;
import com.payment.krishipay.views.moneytransfer.DMTSearchAccount;
import com.payment.krishipay.views.moneytransfer.mintra.MintraDMTSearchAccount;
import com.payment.krishipay.views.referall.ReferralActivity;
import com.payment.krishipay.views.reports.AEPSTransaction;
import com.payment.krishipay.views.reports.AllReports;
import com.payment.krishipay.views.reports.BillRechargeTransaction;
import com.payment.krishipay.views.reports.DMTTransactionReport;
import com.payment.krishipay.views.reports.adapter.HorizontalReportListAdapter;
import com.payment.krishipay.views.settings.Settings;
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
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity implements RequestResponseLis, View.OnClickListener {
    private static final int REQUEST_CODE = 101;
    private int REQUEST_FLAG =0;
    private int REQUEST =0;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private RecyclerView rvHome, rvReport, rvPayServicesList, headerList;
    private ViewPager viewPager;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private RelativeLayout shimmer;
    private Context context;
    ArrayList<SlugModel> slugList;
    private TextView tvMatmBalance, tvMainBalance, tvAepsBalance, tvWalletSec;
    private CarouselView rvSlider;
    private LinearLayout secAddMoney, tvWalletTitleCon, secAepsWallet, secMatm;

    private void init() {

        new AepsAppHelper(this).checkForUpdates();
//

        context = this;
        rvSlider = findViewById(R.id.rvSlider);
        tvWalletTitleCon = findViewById(R.id.tvWalletTitleCon);
        secAepsWallet = findViewById(R.id.secAepsWallet);
        secMatm = findViewById(R.id.secMatm);
        secAddMoney = findViewById(R.id.secReload);
        headerList = findViewById(R.id.headerList);
        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        rvHome = findViewById(R.id.rvHome);
        rvPayServicesList = findViewById(R.id.rvPayServicesList);
        rvReport = findViewById(R.id.rvReport);
        shimmer = findViewById(R.id.shimmer);
        tvMatmBalance = findViewById(R.id.tvMatmBalance);
        tvMainBalance = findViewById(R.id.tvMainBalance);
        tvAepsBalance = findViewById(R.id.tvAepsBalance);
        tvWalletSec = findViewById(R.id.tvWalletSec);
        setUpNavigationView();
        imageSliderInit();
        gridInit();
        topHeaderList();
        gridInitReport();
        gridInitSlider();
        initBalance();

        secAddMoney.setOnClickListener(v -> {
            shimmer.setVisibility(View.VISIBLE);
            REQUEST =0;
            networkCallUsingVolleyApi(Constants.URL.BALANCE_UPDATE, false);
        });

        tvWalletSec.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WalletOptions.class)));
        tvWalletTitleCon.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, WalletFundRequest.class);
            startActivity(i);
        });
        secAepsWallet.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AepsMatmWalletReqest.class);
            i.putExtra("activity_type", "aeps");
            startActivity(i);
        });
        secMatm.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AepsMatmWalletReqest.class);
            i.putExtra("activity_type", "matm");
            startActivity(i);
        });

        TextView etSearch = findViewById(R.id.etSearch);
        etSearch.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AllServicesSearch.class)));
        appLockHandler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        init();

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE};
        AppPermissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

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

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawer.closeDrawers();
            if (menuItem.getItemId() == R.id.report) {
                startActivity(new Intent(MainActivity.this, AllReports.class));
            } else if (menuItem.getItemId() == R.id.settle) {
                startActivity(new Intent(MainActivity.this, WalletOptions.class));
            } else  if (menuItem.getItemId() == R.id.member) {
                REQUEST =1;
                networkCallUsingVolleyApi(Constants.URL.GET_ROLE, true);

            } else if (menuItem.getItemId() == R.id.profile) {
                startActivity(new Intent(MainActivity.this, ProfilePage.class));
            } else if (menuItem.getItemId() == R.id.bank) {
                startActivity(new Intent(MainActivity.this, BankAccountPage.class));
            } else if (menuItem.getItemId() == R.id.settings) {
                startActivity(new Intent(MainActivity.this, Settings.class));
            } else if (menuItem.getItemId() == R.id.referral) {
                startActivity(new Intent(MainActivity.this, ReferralActivity.class));
            } else {
                Toast.makeText(context, "Available Soon", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        actionBarDrawerToggle.syncState();
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView imgProfile = headerView.findViewById(R.id.imgProfile);
        TextView tvNavName = headerView.findViewById(R.id.tvNavName);
        TextView tvNavEmail = headerView.findViewById(R.id.tvNavEmail);
        String name = SharedPrefs.getValue(this, SharedPrefs.USER_NAME);
        tvNavName.setText(name);
        String email = SharedPrefs.getValue(this, SharedPrefs.USER_EMAIL);
        String useID = SharedPrefs.getValue(this, SharedPrefs.USER_ID);
        email += "\nuser id : " + useID;
        tvNavEmail.setText(email);
        String bcid = SharedPrefs.getValue(this, SharedPrefs.BC_ID);
        String psaId = SharedPrefs.getValue(this, SharedPrefs.PSA_ID);
        String bbpsId = SharedPrefs.getValue(this, SharedPrefs.BBPS_ID);
        Button btnLogout = headerView.findViewById(R.id.btnLogout);
        imgProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfilePage.class)));
        btnLogout.setOnClickListener(v -> AppManager.getInstance().logoutFromServer(this, false));
    }



    private void gridInitReport() {
        LinearLayoutManager nearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvReport.setLayoutManager(nearLayoutManager);
        rvReport.setItemAnimator(new DefaultItemAnimator());
        HorizontalReportListAdapter reportAdapter = new HorizontalReportListAdapter(MainActivity.this,
                MainLocalData.getReportGridRecord());
        rvReport.setAdapter(reportAdapter);
        rvReport.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                rvReport, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position) {
                    case 0:
                        Intent i = new Intent(MainActivity.this, AEPSTransaction.class);
                        i.putExtra("title", "AEPS Transactions");
                        i.putExtra("type", "aepsstatement");
                        startActivity(i);
                        break;
                    case 1:
                        i = new Intent(MainActivity.this, BillRechargeTransaction.class);
                        i.putExtra("title", "Recharge Statement");
                        i.putExtra("type", "rechargestatement");
                        startActivity(i);
                        break;
                    case 2:
                        i = new Intent(MainActivity.this, BillRechargeTransaction.class);
                        i.putExtra("title", "Bill Payment Statement");
                        i.putExtra("type", "billpaystatement");
                        startActivity(i);
                        break;
                    case 3:
                        i = new Intent(MainActivity.this, DMTTransactionReport.class);
                        i.putExtra("title", "DMT Statement");
                        i.putExtra("type", "dmtstatement");
                        startActivity(i);
                        break;
                    case 4:
                        i = new Intent(MainActivity.this, AllReports.class);
                        i.putExtra("title", "All Reports");
                        startActivity(i);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void gridInit() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 4);
        rvHome.setLayoutManager(layoutManager);
        rvHome.setItemAnimator(new DefaultItemAnimator());
        HomeAdapter homeAdapter = new HomeAdapter(MainActivity.this, MainLocalData.getHomeGridRecord());
        rvHome.setAdapter(homeAdapter);
        rvHome.addOnItemTouchListener(new RecyclerTouchListener(this,
                rvHome, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i;
                if (position == 7) {
                    i = new Intent(MainActivity.this, AllServices.class);
                } else {
                    i = new Intent(MainActivity.this, RechargeAndBillPayment.class);
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
        LinearLayoutManager nearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        headerList.setLayoutManager(nearLayoutManager);
        headerList.setItemAnimator(new DefaultItemAnimator());
        HeaderAdapter homeAdapter = new HeaderAdapter(MainActivity.this, MainLocalData.getMoneyTransferGrid());
        headerList.setAdapter(homeAdapter);
        headerList.addOnItemTouchListener(new RecyclerTouchListener(this,
                headerList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i;
                ActivityListModel model = MainLocalData.getMoneyTransferGrid().get(position);
                switch (model.getTxt1()) {
                    case "AEPS":
                        new AepsAppHelper(MainActivity.this).aepsInitiate();
                        break;
                    case "MATM":
                        i = new Intent(MainActivity.this, HandlerActivity.class);
                        i.putExtra("appUserId", SharedPrefs.getValue(MainActivity.this, SharedPrefs.USER_ID));
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
                        AppHandler.openFingPayMicroAtm(MainActivity.this);
                        break;
                    case "UPI":
                        AppHandler.upiServiceOption(MainActivity.this);
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

    private void imageSliderInit() {
        String[] images = Constants.images;
        List<String> stringList = Arrays.asList(images);
        String is_avail_array = SharedPrefs.getValue(this, SharedPrefs.BANNERARRAY);
        if (MyUtil.isNN(is_avail_array))
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
        viewPager = findViewById(R.id.viewPager);
        MyCustomPagerAdapter myCustomPagerAdapter = new MyCustomPagerAdapter(MainActivity.this, stringList);
        viewPager.setAdapter(myCustomPagerAdapter);
        CircleIndicator circleIndicator = findViewById(R.id.circle);
        circleIndicator.setViewPager(viewPager);
        NUM_PAGES = images.length;
        final Handler handler = new Handler();
        final Runnable Update = () -> {
            if (currentPage == NUM_PAGES) {
                currentPage = 0;
            }
            viewPager.setCurrentItem(currentPage++, true);
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
    }

    private void networkCallUsingVolleyApi(String url, boolean isLoad) {
        if (AppManager.isOnline(this)) {
            new VolleyNetworkCall(this, this, url, 1, param(), isLoad).netWorkCall();
        } else {
            Toast.makeText(this, "Network connection error", Toast.LENGTH_LONG).show();
        }
    }

    private Map<String, String> param() {
        Map<String, String> map = new HashMap<>();
        map.put("type", SharedPrefs.getValue(this, SharedPrefs.ROLE_SLUG));
        return map;
    }

    @Override
    public void onSuccessRequest(String JSonResponse) {
        shimmer.setVisibility(View.GONE);
        try {
            JSONObject jsonObject = new JSONObject(JSonResponse);
            if (REQUEST ==0){

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
            }else{
                JSONArray array = jsonObject.getJSONArray("data");
                intSlug(array);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBalance() {
        tvMainBalance.setText(MyUtil.formatWithRupee(this, SharedPrefs.getValue(context, SharedPrefs.MAIN_WALLET)));
        tvAepsBalance.setText(MyUtil.formatWithRupee(this, SharedPrefs.getValue(context, SharedPrefs.APES_BALANCE)));
        tvMatmBalance.setText(MyUtil.formatWithRupee(this, "0"));
        String mBal = SharedPrefs.getValue(context, SharedPrefs.MICRO_ATM_BALANCE);
        if (mBal != null && !mBal.equalsIgnoreCase("NO") && mBal.length() > 0)
            tvMatmBalance.setText(MyUtil.formatWithRupee(this, mBal));
    }

    @Override
    public void onFailRequest(String msg) {
        shimmer.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shimmer != null) {
            shimmer.setVisibility(View.VISIBLE);
            REQUEST_FLAG =0;
            networkCallUsingVolleyApi(Constants.URL.BALANCE_UPDATE, false);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.secDMT:
                if (Constants.isActiveMintraDMT)
                    startActivity(new Intent(context, MintraDMTSearchAccount.class));
                else
                    startActivity(new Intent(context, DMTSearchAccount.class));
                break;
            case R.id.secPhone:
                Intent i = new Intent(this, RechargeAndBillPayment.class);
                i.putExtra("position", "0");
                startActivity(i);
                break;
        }
    }

    private void gridInitSlider() {
        try {
            rvSlider.setPageCount(Constants.imagesBottom.length);
            rvSlider.setImageListener(imageListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            MyUtil.setGlideImage(Constants.imagesBottom[position], imageView, context);
        }
    };

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



    private void intSlug(JSONArray array) {
        Constants.ROLE_LIST = new ArrayList<>();
        Constants.ROLE_LIST.addAll(AppHandler.parseSlugList(array));
        if (Constants.ROLE_LIST.size() > 0) {
        int x = 0;
        String[] choice = new String[Constants.ROLE_LIST.size()];
        for (SlugModel s : Constants.ROLE_LIST) choice[x++] = s.getName();
        if (Constants.ROLE_LIST.size() == 1) {
            Intent i = new Intent(context, MemberListAll.class);
            i.putExtra("type", Constants.ROLE_LIST.get(0).getSlug());
            startActivity(i);
        } else {
            AlertDialog.Builder alert = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog);
            alert.setTitle("Please select role");
            alert.setSingleChoiceItems(choice, -1, (dialog, which) -> {
                dialog.dismiss();
                Intent i = new Intent(context, MemberListAll.class);
                i.putExtra("type", Constants.ROLE_LIST.get(which).getSlug());
                startActivity(i);
            });
            alert.show();
        }

        } else {
            Toast.makeText(context, "You do not have permission for this option", Toast.LENGTH_SHORT).show();
        }

    }

}