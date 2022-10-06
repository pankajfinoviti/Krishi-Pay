package com.payment.fingpay;

import com.android.volley.RequestQueue;
import com.payment.fingpay.model.InvocieModel;

import java.util.HashMap;
import java.util.List;

public class FingConstents {
    public static HashMap<String, String> ReceiptMap;
    public static List<InvocieModel> InvoiceModelList;
    public static String FING_TXN_ID = "";

    public static class URL {
        public static String BASE_URL = "https://portal.payvenue.in/";
        public static final String COMMON_PATH = BASE_URL + "api/android/";
    }

    public static String USER_ID = "";
    public static String TOKEN = "";
    public static RequestQueue queue;

}
