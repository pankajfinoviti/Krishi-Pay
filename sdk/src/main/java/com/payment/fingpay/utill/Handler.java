package com.payment.fingpay.utill;

import android.app.Activity;

import org.json.JSONObject;

public class Handler {

    public static boolean checkStatus(String json, Activity context) {
        boolean flag = false;
        try {
            JSONObject obj = new JSONObject(json);
            String status = "";
            if (obj.has("status")) {
                status = obj.getString("status");
            } else if (obj.has("statuscode")) {
                status = obj.getString("statuscode");
            }
            if (status.equalsIgnoreCase("success") || status.equalsIgnoreCase("TXN") ||
                    status.equalsIgnoreCase("RNF") || status.equalsIgnoreCase("OTP")) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static String getStatus(String json) {
        String status = "";
        try {
            JSONObject obj = new JSONObject(json);
            if (obj.has("status")) {
                status = obj.getString("status");
            } else if (obj.has("statuscode")) {
                status = obj.getString("statuscode");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public static String getMessage(String json) {
        String status = "";
        try {
            JSONObject obj = new JSONObject(json);
            if (obj.has("message")) {
                status = obj.getString("message");
            } else if (obj.has("msg")) {
                status = obj.getString("msg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public static String parseExceptionMsg(Exception e) {
        String m = "Some thing went wrong please try again after some time";
        try {
            if (e.getMessage() != null) {
                m += "\nError : " + e.getMessage();
            }
        } catch (Exception d) {
            d.printStackTrace();
        }
        return m;
    }

}
