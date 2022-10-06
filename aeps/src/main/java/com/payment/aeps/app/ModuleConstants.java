package com.payment.aeps.app;

import com.payment.aeps.fingpay_microatm.InvocieModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleConstants {

    //public static final String baseUrl = "http://enterprise1.egram.org/";
    //public static final String baseUrl = "http://login.securedigitalpay.in/";
    public static final String baseUrl = "http://mobomoney.co.in/";
    public static String bankList = baseUrl + "api/android/faeps/getdata";
    public static String AEPS_API = baseUrl + "api/android/faeps/transaction";
    public static String CASH_DEPO_API = baseUrl + "api/android/faeps/cdo/transaction";
    public static String FMATM_UPDATE = baseUrl + "api/android/secure/microatm/update";
    public static String FMATM_INITIATE = baseUrl + "api/android/secure/microatm/initiate";

    public static Map<String, String> LOCAL_Map;
    public static HashMap<String, String> ReceiptMap;
    public static List<InvocieModel> InvoiceModelList;
    public static String FING_TXN_ID = "";
}
