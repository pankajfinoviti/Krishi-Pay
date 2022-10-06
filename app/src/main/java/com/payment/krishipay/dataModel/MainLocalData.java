package com.payment.krishipay.dataModel;

import android.content.Context;

import com.payment.krishipay.R;
import com.payment.krishipay.model.ActivityListModel;

import java.util.ArrayList;

public class MainLocalData {

    public static ArrayList<ActivityListModel> getHomeGridRecord() {
        ArrayList<ActivityListModel> activityListModelArrayList = new ArrayList<>();
        activityListModelArrayList.add(new ActivityListModel("Mobile", R.drawable.ic_phone));
        activityListModelArrayList.add(new ActivityListModel("DTH", R.drawable.ic_dth));
        activityListModelArrayList.add(new ActivityListModel("Electricity", R.drawable.ic_electric));
        activityListModelArrayList.add(new ActivityListModel("Gas Bill", R.drawable.ic_gas));
        activityListModelArrayList.add(new ActivityListModel("Landline", R.drawable.ic_landline));
        activityListModelArrayList.add(new ActivityListModel("Broadband", R.drawable.ic_broadband));
        activityListModelArrayList.add(new ActivityListModel("Water", R.drawable.ic_water));
        activityListModelArrayList.add(new ActivityListModel("View More", R.drawable.more_icon));
        return activityListModelArrayList;
    }

    public static ArrayList<ActivityListModel> getMoneyTransferGrid() {
        ArrayList<ActivityListModel> activityListModelArrayList = new ArrayList<>();
        activityListModelArrayList.add(new ActivityListModel("AEPS", R.drawable.ic_fingerprint_scan_new));
        activityListModelArrayList.add(new ActivityListModel("MATM", R.drawable.ic_printing_icon));
        activityListModelArrayList.add(new ActivityListModel("DMT", R.drawable.dmt_new));
        activityListModelArrayList.add(new ActivityListModel("PHONE", R.drawable.ic_phone_new));
        activityListModelArrayList.add(new ActivityListModel("DTH", R.drawable.ic_satellite_dish));
        // activityListModelArrayList.add(new ActivityListModel("AEPS", R.drawable.ic_fingerprint_scan_new));
        // activityListModelArrayList.add(new ActivityListModel("MATM", R.drawable.ic_printing_icon));
        //activityListModelArrayList.add(new ActivityListModel("PHONE", R.drawable.ic_phone_new));
        return activityListModelArrayList;
    }


    public static ArrayList<ActivityListModel> getHomeGridAllRecord(Context context) {
        ArrayList<ActivityListModel> list = new ArrayList<>();
        list.add(new ActivityListModel(R.drawable.ic_phone, context.getString(R.string.mobile_recharge), "0"));
        list.add(new ActivityListModel(R.drawable.ic_dth, context.getString(R.string.dth), "1"));
        list.add(new ActivityListModel(R.drawable.ic_electric, context.getString(R.string.electricity_bill), "2"));
        list.add(new ActivityListModel(R.drawable.ic_gas, context.getString(R.string.lpg_gas), "3"));
        list.add(new ActivityListModel(R.drawable.ic_landline, context.getString(R.string.landline_bills), "4")); // => Holidays
        list.add(new ActivityListModel(R.drawable.ic_broadband, context.getString(R.string.broadband_bills), "5")); // => Cash back
        list.add(new ActivityListModel(R.drawable.ic_water, context.getString(R.string.water_bills), "6"));
        list.add(new ActivityListModel(R.drawable.ic_loan_repayment, context.getString(R.string.loan_repayment), "7"));
        list.add(new ActivityListModel(R.drawable.ic_life_insurance, context.getString(R.string.life_insurance), "8"));
        list.add(new ActivityListModel(R.drawable.ic_fasttag, context.getString(R.string.fasttag), "9"));
        list.add(new ActivityListModel(R.drawable.ic_loan, context.getString(R.string.loan), "10"));
        list.add(new ActivityListModel(R.drawable.ic_cable_tv, context.getString(R.string.cable_tv), "11"));
        list.add(new ActivityListModel(R.drawable.ic_health_insurance, context.getString(R.string.health_insurance), "12"));
        list.add(new ActivityListModel(R.drawable.ic_education_fee, context.getString(R.string.education_fees), "13"));
        list.add(new ActivityListModel(R.drawable.ic_tax, context.getString(R.string.minciple_taxes), "14"));
        list.add(new ActivityListModel(R.drawable.ic_landline, context.getString(R.string.postpaid), "15"));
        list.add(new ActivityListModel(R.drawable.ic_housing_society, context.getString(R.string.housing_society), "16"));
        return list;
    }


    public static ArrayList<ActivityListModel> getReportGridRecord() {
        ArrayList<ActivityListModel> activityReportArrayList = new ArrayList<>();
        activityReportArrayList.add(new ActivityListModel("AEPS", R.drawable.aeps_report_icon));
        activityReportArrayList.add(new ActivityListModel("Recharge", R.drawable.recharge_statement_icon));
        activityReportArrayList.add(new ActivityListModel("Bill Pay", R.drawable.bill_report_icon));
        activityReportArrayList.add(new ActivityListModel("DMT", R.drawable.dmt_report_icon));
        activityReportArrayList.add(new ActivityListModel("All Reports", R.drawable.ic_folder));
        //activityReportArrayList.add(new ActivityListModel("Wallet", R.drawable.wallet_report_icon));
        return activityReportArrayList;
    }

    public static ArrayList<ActivityListModel> getReportAllGridRecord() {
        ArrayList<ActivityListModel> activityReportArrayList = new ArrayList<>();
        activityReportArrayList.add(new ActivityListModel("AEPS", R.drawable.aeps_report_icon));
        activityReportArrayList.add(new ActivityListModel("AEPS Fund Request Report", R.drawable.aeps_report_icon));
        activityReportArrayList.add(new ActivityListModel("AEPS Wallet Statements.", R.drawable.aeps_report_icon));
        activityReportArrayList.add(new ActivityListModel("Main Wallet Statement", R.drawable.ic_wallet_report));
        activityReportArrayList.add(new ActivityListModel("Recharge", R.drawable.recharge_statement_icon));
        activityReportArrayList.add(new ActivityListModel("Bill Pay", R.drawable.bill_report_icon));
        activityReportArrayList.add(new ActivityListModel("DMT", R.drawable.dmt_report_icon));
        activityReportArrayList.add(new ActivityListModel("MATM Settlement Report", R.drawable.ic_matm_settle_report));
        activityReportArrayList.add(new ActivityListModel("MATM Transaction Report", R.drawable.ic_matm_tran_report));
        //activityReportArrayList.add(new ActivityListModel("UPI Transaction Report", R.drawable.ic_upi_icon));
        return activityReportArrayList;
    }

    public static ArrayList<ActivityListModel> getProfilePageOptions() {
        ArrayList<ActivityListModel> activityReportArrayList = new ArrayList<>();
        activityReportArrayList.add(new ActivityListModel("Profile", R.drawable.ic_user));
        activityReportArrayList.add(new ActivityListModel("Bank Account", R.drawable.ic_settings_user));
        return activityReportArrayList;
    }
}
