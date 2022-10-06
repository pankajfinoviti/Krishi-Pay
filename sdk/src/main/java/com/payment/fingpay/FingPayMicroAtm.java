package com.payment.fingpay;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fingpay.microatmsdk.MicroAtmLoginScreen;
import com.fingpay.microatmsdk.data.MiniStatementModel;
import com.fingpay.microatmsdk.utils.Constants;
import com.fingpay.microatmsdk.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payment.fingpay.model.FingPayInitiateModel;
import com.payment.fingpay.model.InvocieModel;
import com.payment.fingpay.network.RequestResponseLis;
import com.payment.fingpay.network.VolleyNetworkCallModule;
import com.payment.fingpay.printer.Invoice;
import com.payment.fingpay.printer.MiniStatementInvoice;
import com.payment.fingpay.utill.FingUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FingPayMicroAtm extends Activity implements RequestResponseLis {
    private Context context;
    private String TYPE;

    private EditText merchIdEt, passwordEt, amountEt, remarksEt;
    private TextView tvMobile;
    private RadioGroup radioGroup;
    private Button fingPayBtn, historyBtn;
    private TextView respTv;
    private static final int CODE = 1;
    public static final String SUPER_MERCHANT_ID = "2";

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{
            Manifest.permission.READ_PHONE_STATE};
    private SharedPreferences permissionStatus;
    String mobile;
    String amount;
    TextView type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fing_pay_activity);
        TYPE = getIntent().getStringExtra("type");

        init();

        checkPermissions();

        switch (TYPE) {
            case "MS":
            case "PR":
            case "CP":
            case "CA":
            case "BE":
                amountEt.setText("0");
                amountEt.setVisibility(View.GONE);
        }

        type.setText("Type : " + TYPE);
        tvMobile.setText("Number : " + SharedPrefs.getValue(this, SharedPrefs.LOGIN_ID));

        fingPayBtn.setOnClickListener(view -> {
            mobile = SharedPrefs.getValue(this, SharedPrefs.LOGIN_ID);
            amount = amountEt.getText().toString().trim();
            if (mobile.length() > 0 && amount.length() > 0) {
                initiateFingPayMATM();
            } else {
                Toast.makeText(context, "Mobile number is required", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        permissionStatus = getSharedPreferences("microatm_sample", 0);
        context = FingPayMicroAtm.this;
        tvMobile = findViewById(R.id.et_mobile);
        amountEt = findViewById(R.id.et_amount);
        fingPayBtn = findViewById(R.id.btn_fingpay);
        type = findViewById(R.id.type);
    }

    public static boolean isValidString(String str) {
        if (str != null) {
            str = str.trim();
            if (str.length() > 0)
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FingConstents.ReceiptMap = new LinkedHashMap<>();
        FingConstents.InvoiceModelList = new ArrayList<>();

        try {
            if (resultCode == RESULT_OK && requestCode == CODE) {
            // (true) {
                //Toast.makeText(context, "res" + data.getExtras().toString(), Toast.LENGTH_SHORT).show();
                //Utils.logD(data.getExtras().toString());

                boolean status = data.getBooleanExtra(Constants.TRANS_STATUS, false);
                String response = data.getStringExtra(Constants.MESSAGE);
                double transAmount = data.getDoubleExtra(Constants.TRANS_AMOUNT, 0);
                double balAmount = data.getDoubleExtra(Constants.BALANCE_AMOUNT, 0);
                String bankRrn = data.getStringExtra(Constants.RRN);
                String transType = data.getStringExtra(Constants.TRANS_TYPE);
                int type = data.getIntExtra(Constants.TYPE, Constants.CASH_WITHDRAWAL);
                String cardNum = data.getStringExtra(Constants.CARD_NUM);
                String bankName = data.getStringExtra(Constants.BANK_NAME);
                String cardType = data.getStringExtra(Constants.CARD_TYPE);
                String terminalId = data.getStringExtra(Constants.TERMINAL_ID);
                String fpId = data.getStringExtra(Constants.FP_TRANS_ID);
                String transId = data.getStringExtra(Constants.TRANS_ID);

//                boolean status = true;
//                String response = "Requested Completed";
//                double transAmount = 10;
//                double balAmount = 500;
//                String bankRrn = "99034kjfkf";
//                String transType = "Balance Enquery";
//                int type = 10;
//                String cardNum = "389340394034094034";
//                String bankName = "State Bank of india";
//                String cardType = "Debit Card";
//                String terminalId = "9938498349";
//                String fpId = "FbId";
//                String transId = "93049043094034093094";

                if (type == Constants.MINI_STATEMENT) {
                    List<MiniStatementModel> l = data.getParcelableArrayListExtra(Constants.LIST);
                    if (Utils.isValidArrayList((ArrayList<?>) l)) {
                        Utils.logD(l.toString());
                    }
                }

                if (isValidString(response)) {

                    if (!Utils.isValidString(bankRrn))
                        bankRrn = "";

                    if (!Utils.isValidString(transType))
                        transType = "";

                    FingConstents.ReceiptMap.put("Trans Id", String.valueOf(transId));
                    FingConstents.InvoiceModelList.add(new InvocieModel(String.valueOf(transId), "transactionId", "TransId"));

                    if (fpId != null && fpId.length() > 0) {
                        FingConstents.ReceiptMap.put("FpId", String.valueOf(fpId));
                        FingConstents.InvoiceModelList.add(new InvocieModel(String.valueOf(fpId), "fpId", "FpId"));
                    }

                    if (terminalId != null && terminalId.length() > 0) {
                        FingConstents.ReceiptMap.put("TerminalId", String.valueOf(terminalId));
                        FingConstents.InvoiceModelList.add(new InvocieModel(String.valueOf(terminalId), "terminalId", "TerminalId"));
                    }

                    FingConstents.ReceiptMap.put("Status", String.valueOf(status));
                    FingConstents.InvoiceModelList.add(new InvocieModel(String.valueOf(status), "status", "Status"));

                    FingConstents.ReceiptMap.put("Message", String.valueOf(response));
                    FingConstents.InvoiceModelList.add(new InvocieModel(String.valueOf(response), "message", "Message"));

                    if (bankRrn != null && bankRrn.length() > 0) {
                        FingConstents.ReceiptMap.put("BankRRN ", String.valueOf(bankRrn));
                        FingConstents.InvoiceModelList.add(new InvocieModel(String.valueOf(bankRrn), "bankRrn", "BankRRN"));
                    }

                    if (transType != null && transType.length() > 0) {
                        FingConstents.ReceiptMap.put("TranType", String.valueOf(transType));
                        FingConstents.InvoiceModelList.add(new InvocieModel(String.valueOf(transType), "transType", "TranType"));
                    }

                    FingConstents.ReceiptMap.put("Type", FingUtil.getTypeName(type));
                    FingConstents.InvoiceModelList.add(new InvocieModel(String.valueOf(type), "type", "Type"));

                    if (cardNum != null && cardNum.length() > 0) {
                        FingConstents.ReceiptMap.put("CardNum", String.valueOf(cardNum));
                        FingConstents.InvoiceModelList.add(new InvocieModel(String.valueOf(cardNum), "cardNum", "CardNum"));
                    }

                    if (cardType != null && cardType.length() > 0) {
                        FingConstents.ReceiptMap.put("CardType", String.valueOf(cardType));
                        FingConstents.InvoiceModelList.add(new InvocieModel(String.valueOf(cardType), "cardType", "CardType"));
                    }

                    if (bankName != null && bankName.length() > 0) {
                        FingConstents.ReceiptMap.put("BankName", String.valueOf(bankName));
                        FingConstents.InvoiceModelList.add(new InvocieModel(String.valueOf(bankName), "bankName", "BankName"));
                    }

                    FingConstents.ReceiptMap.put("TransAmount", String.valueOf(transAmount));
                    FingConstents.InvoiceModelList.add(new InvocieModel(String.valueOf(transAmount), "transAmount", "TransAmount"));

                    FingConstents.ReceiptMap.put("BalanceAmount", String.valueOf(balAmount));
                    FingConstents.InvoiceModelList.add(new InvocieModel(String.valueOf(balAmount), "balAmount", "BalanceAmount"));

                    if (type == Constants.MINI_STATEMENT) {
                        FingUtil.StatementList = data.getParcelableArrayListExtra(Constants.LIST);
                    }

                    Intent i = null;
                    if (type == Constants.MINI_STATEMENT) {
                        i = new Intent(this, MiniStatementInvoice.class);
                    } else {
                        i = new Intent(this, Invoice.class);
                    }
                    startActivity(i);
                }
            } else if (requestCode == REQUEST_PERMISSION_SETTING) {
                if (ActivityCompat.checkSelfPermission(FingPayMicroAtm.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                }
                Toast.makeText(context, "Permission Related Issue Accord", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Getting parsing error in invoice data", Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> getUngrantedPermissions() {
        List<String> permissions = new ArrayList<>();
        for (String s : permissionsRequired) {
            if (ContextCompat.checkSelfPermission(context, s) != PackageManager.PERMISSION_GRANTED)
                permissions.add(s);
        }
        return permissions;
    }

    private void checkPermissions() {
        List<String> permissions = getUngrantedPermissions();
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(FingPayMicroAtm.this,
                    permissions.toArray(new String[permissions.size()]),
                    PERMISSION_CALLBACK_CONSTANT);

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {

            } else if (Utils.isValidArrayList((ArrayList<?>) getUngrantedPermissions())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FingPayMicroAtm.this);
                builder.setTitle(getString(R.string.need_permissions));
                builder.setMessage(getString(R.string.device_permission));
                builder.setPositiveButton(getString(R.string.grant), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(FingPayMicroAtm.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.unable_toget_permission), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initiateFingPayMATM() {
        String url = FingConstents.URL.baseUrl + "api/android/secure/microatm/initiate";
        volleyNetworkCall(url);
    }

    private Map<String, String> param() {
        String userId = SharedPrefs.getValue(this, SharedPrefs.USER_ID);
        String token = SharedPrefs.getValue(this, SharedPrefs.APP_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("apptoken", token);
        map.put("user_id", userId);
        map.put("mobile", mobile);
        map.put("imei", AppManager.getImei(this));
        map.put("transactionType", TYPE);
        map.put("amount", amount);
        map.put("remark", "Test");

        String json = new JSONObject(map).toString();
        //Log.e("PARAM", "PARAM: " + json);
        return map;
    }

    private void volleyNetworkCall(String url) {
       // Log.e("PARAM", "PARAM: " + url);
        if (AppManager.isOnline(this)) {
            showLoader(getString(R.string.loading_text));
            new VolleyGetNetworkCall(this, this, url, 1, param()).netWorkCall();
        } else {
            Toast.makeText(this, "Network connection error", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onSuccessRequest(String JSonResponse) {
        closeLoader();
        Gson gson = new GsonBuilder().create();
       // System.out.println("RES: " + JSonResponse);
        try {
            JSONObject jsonObject = new JSONObject(JSonResponse);
            String status = jsonObject.getString("status");
            String message = jsonObject.getString("message");
            if (status.equalsIgnoreCase("TXN")) {
                FingPayInitiateModel model = gson.fromJson(jsonObject.getJSONObject("data").toString(), FingPayInitiateModel.class);
                Intent intent = new Intent(FingPayMicroAtm.this, MicroAtmLoginScreen.class);
                intent.putExtra(Constants.MERCHANT_USERID, model.getMerchantId());
                intent.putExtra(Constants.MERCHANT_PASSWORD, model.getMerchantPassword());
                intent.putExtra(Constants.REMARKS, "Demo");
                intent.putExtra(Constants.AMOUNT_EDITABLE, false);
                intent.putExtra(Constants.TXN_ID, model.getTxnid());
                intent.putExtra(Constants.SUPER_MERCHANTID, model.getSuperMerchentId());
                intent.putExtra(Constants.LATITUDE, Double.parseDouble(model.getLat()));
                intent.putExtra(Constants.LONGITUDE, Double.parseDouble(model.getLon()));
                intent.putExtra(Constants.IMEI, AppManager.getImei(this));
                intent.putExtra(Constants.AMOUNT, amount);
                intent.putExtra(Constants.MOBILE_NUMBER, mobile);

                FingConstents.FING_TXN_ID = model.getTxnid();

                switch (TYPE) {
                    case "CW":
                        intent.putExtra(Constants.TYPE, Constants.CASH_WITHDRAWAL);
                        break;
                    case "CD":
                        intent.putExtra(Constants.TYPE, Constants.CASH_DEPOSIT);
                        break;
                    case "BE":
                        intent.putExtra(Constants.TYPE, Constants.BALANCE_ENQUIRY);
                        break;
                    case "MS":
                        intent.putExtra(Constants.TYPE, Constants.MINI_STATEMENT);
                        break;
                    case "PR":
                        intent.putExtra(Constants.TYPE, Constants.PIN_RESET);
                        break;
                    case "CP":
                        intent.putExtra(Constants.TYPE, Constants.CHANGE_PIN);
                        break;
                    case "CA":
                        intent.putExtra(Constants.TYPE, Constants.CARD_ACTIVATION);
                        break;
                }
                intent.putExtra(Constants.MICROATM_MANUFACTURER, Constants.MoreFun);
                startActivityForResult(intent, CODE);
            } else {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailRequest(String msg) {
        closeLoader();
        Toast.makeText(context, "Network Request Error : " + msg, Toast.LENGTH_SHORT).show();
    }
}
