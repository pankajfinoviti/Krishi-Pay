package com.payment.krishipay.utill;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashSet;
import java.util.Set;

public class SharedPrefsBackup {

    public static final String PREFS_NAME = "PREFS";
    public static String ABOUT = "appAbout";
    public static String LANGUAGE_TYPE = "set_lang";
    public static String Mobile = "set_lang";
    public static String USER_ID = "user_id";
    public static String USER_NAME = "user_name";
    public static String USER_CONTACT = "user_contact";
    public static String USER_EMAIL = "user_email";
    public static String OTP_VERIFY = "otp_verify";
    public static String MAIN_WALLET = "main_wallet";
    public static String MICRO_ATM_BALANCE = "micro_atm_balance";
    public static String NSDL_WALLET = "nsdl_wallet";
    public static String APES_BALANCE = "apes_balance";
    public static String LOCKED_AMOUNT = "locked_amount";
    public static String ROLE_ID = "role_id";
    public static String PARENT_ID = "parent_id";
    public static String COMPANY_ID = "company_id";
    public static String STATUS = "status";
    public static String ADDRESS = "address";
    public static String SHOP_NAME = "shop_name";
    public static String GSTIN = "gstin";
    public static String CITY = "city";
    public static String STATE = "state";
    public static String PINCODE = "pincode";
    public static String AADHAR_CARD = "aadhar_card";
    public static String PANCARD = "pan_card";
    public static String KYC = "kyc";
    public static String ACCOUNT = "account";
    public static String BANK = "bank";
    public static String IFSC = "ifsc";
    public static String APP_TOKEN = "app_token";
    public static String TOKEN_AMOUNT = "token_amount";
    public static String UTI_ID = "uti_id";
    // ROLE OBJECT
    public static String ROLE_NAME = "role_name";
    public static String ROLE_SLUG = "role_slug";
    // COMPANY OBJECT
    public static String COMPANY_NAME = "company_name";
    public static String SHORT_NAME = "short_name";
    public static String WEBSITE = "website";
    public static String LOGO = "logo";
    public static String LOGIN_ID = "login_id";
    public static String PASSWORD = "password";
    public static String NEWS = "news";
    public static String SUPPORT_EMAIL = "support_email";
    public static String SUPPORT_NUMBER = "support_number";
    public static String SUPPORT_ADDRESS = "support_address";
    // Filter Strings
    public static String FILTER_DATE_FROM = "from_filter_date";
    public static String FILTER_DATE_TO = "to_filter_date";
    public static String REPORT_SEARCH_TEXT = "search_text";
    public static String FILTER_STATUS = "filter_status";
    private static SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.M)
    /*static MasterKey getMasterKey(Context context) {
        try {
            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                    "MASTER_KEY_ALIAS",
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build();
            return new MasterKey.Builder(context)
                    .setKeyGenParameterSpec(spec)
                    .build();
        } catch (Exception e) {
            Log.e("Pref", "Error on getting master key", e);
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String getEncryptedValue(Context context, String name) {
        try {
            sharedPreferences =  EncryptedSharedPreferences.create(
                    context,
                    "your-app-preferences-name",
                    getMasterKey(context), // calling the method above for creating MasterKey
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e("Pref", "Error on getting encrypted shared preferences", e);
        }
        return sharedPreferences.getString(name, null);
    }*/

    public SharedPrefsBackup() {
        super();
    }

    public static void setValue(Context context, String PREF_KEY, String PREF_VALUE) {
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = sharedPreferences.edit(); //2
        editor.putString(PREF_KEY, PREF_VALUE); //3
        editor.apply(); //4
    }

    public static void setIntValue(Context context, String PREF_KEY, int PREF_VALUE) {
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = sharedPreferences.edit(); //2
        editor.putInt(PREF_KEY, PREF_VALUE); //3
        editor.apply(); //4
    }

    public static String getValue(Context context, String PREF_KEY) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_KEY, null);
    }

    public static void setValue(Context context, String PREF_KEY, Set<String> PREF_VALUE) {
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = sharedPreferences.edit(); //2
        Set<String> set = new HashSet<>();
        set.addAll(PREF_VALUE);
        editor.putStringSet(PREF_KEY, set); //3
        editor.apply(); //4
    }

    public static void clearAllPreferences(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }

}
