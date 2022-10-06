package com.payment.krishipay.utill;

import android.util.Log;

import com.payment.krishipay.BuildConfig;

import timber.log.Timber;

public class Print {

    static int count = 0;

    Print() { }

    public static void P(String sb) {
        if (BuildConfig.DEBUG){
            Log.e("LOG", "Print => "+sb);
            System.out.println("Print : "+sb);
        }
    }

    private static void print(String sb) {
        Log.e("LOG", "\n\n\n=============== LOG ===================");
        if (sb == null || sb.length() == 0) {
            return;
        }
        String TAG = "LOG";
        if (sb.length() > 4000) {
            int chunkCount = sb.length() / 4000;     // integer division
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= sb.length()) {
                    System.out.println("" + sb.substring(4000 * i));
                } else {
                    System.out.println("" + sb.substring(4000 * i, max));
                }
            }
        } else {
            System.out.println("" + sb);
        }
    }

    public static void printFormUploadParameter(String key, String value) {
        System.out.println(key + " = " + value + " " + count++);
    }
}
