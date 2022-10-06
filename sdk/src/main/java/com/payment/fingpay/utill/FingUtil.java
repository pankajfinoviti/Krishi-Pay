package com.payment.fingpay.utill;

import android.widget.EditText;

import com.fingpay.microatmsdk.data.MiniStatementModel;

import java.util.List;

public class FingUtil {

    public static List<MiniStatementModel> StatementList ;

    public static String getTypeName(int type) {
        String typeName = "";
        switch (type) {
            case 2:
                typeName = "CASH WITHDRAWAL";
                break;
            case 3:
                typeName = "CASH DEPOSIT";
                break;
            case 4:
                typeName = "BALANCE ENQUIRY";
                break;
            case 7:
                typeName = "MINI STATEMENT";
                break;
            case 10:
                typeName = "PIN RESET";
                break;
            case 8:
                typeName = "CHANGE PIN";
                break;
            case 9:
                typeName = "CARD ACTIVATION";
                break;
            default:
                typeName = String.valueOf(type);
        }
        return typeName;
    }

    public static boolean isNN(String str) {
        boolean flag = false;
        if (str != null && str.length() > 0 && !str.equalsIgnoreCase("null")) {
            flag = true;
        }
        return flag;
    }

    public static boolean isNN_ET(EditText strView) {
        String str = strView.getText().toString();
        boolean flag = false;
        if (str != null) {
            flag = true;
        }
        return flag;
    }

}
