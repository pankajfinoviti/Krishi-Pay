package com.payment.aeps.fingpay_microatm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import com.payment.aeps.app.ModuleConstants;
import com.payment.aeps.fingpay_microatm.FingSdkUtill.FingUtil;
import com.payment.aeps.fingpay_microatm.printer.Invoice;
import com.payment.aeps.fingpay_microatm.printer.MiniStatementInvoice;
import com.payment.aeps.network.VolleyGetNetworkCall;
import com.payment.aeps.util.ModuleSharedPrefs;
import com.payment.aeps.util.ModuleUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FingPayMicroAtm extends Activity implements VolleyGetNetworkCall.RequestResponseLis {
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
    private String inputMobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fing_pay_activity);
        TYPE = getIntent().getStringExtra("type");
        inputMobileNumber = getIntent().getStringExtra("mobile");
        String userId = getIntent().getStringExtra("userId");
        String appToken = getIntent().getStringExtra("appToken");

        ModuleSharedPrefs.setValue(this, ModuleSharedPrefs.USER_ID, userId);
        ModuleSharedPrefs.setValue(this, ModuleSharedPrefs.APP_TOKEN, appToken);
        ModuleSharedPrefs.setValue(this, ModuleSharedPrefs.LOGIN_ID, inputMobileNumber);

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
                findViewById(R.id.inputCon).setVisibility(View.GONE);
        }

        type.setText(FingUtil.getTypeName(TYPE));
        tvMobile.setText(inputMobileNumber);
        fingPayBtn.setOnClickListener(view -> {
            mobile = inputMobileNumber;
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
            return str.length() > 0;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ModuleConstants.ReceiptMap = new LinkedHashMap<>();
        ModuleConstants.InvoiceModelList = new ArrayList<>();

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

                    ModuleConstants.ReceiptMap.put("TransId", String.valueOf(transId));
                    ModuleConstants.InvoiceModelList.add(new InvocieModel(String.valueOf(transId), "transactionId", "TransId"));

                    if (fpId != null && fpId.length() > 0) {
                        ModuleConstants.ReceiptMap.put("FpId", String.valueOf(fpId));
                        ModuleConstants.InvoiceModelList.add(new InvocieModel(String.valueOf(fpId), "fpId", "FpId"));
                    }

                    if (terminalId != null && terminalId.length() > 0) {
                        ModuleConstants.ReceiptMap.put("TerminalId", String.valueOf(terminalId));
                        ModuleConstants.InvoiceModelList.add(new InvocieModel(String.valueOf(terminalId), "terminalId", "TerminalId"));
                    }

                    ModuleConstants.ReceiptMap.put("Status", String.valueOf(status));
                    ModuleConstants.InvoiceModelList.add(new InvocieModel(String.valueOf(status), "status", "Status"));

                    ModuleConstants.ReceiptMap.put("Message", String.valueOf(response));
                    ModuleConstants.InvoiceModelList.add(new InvocieModel(String.valueOf(response), "message", "Message"));

                    if (bankRrn != null && bankRrn.length() > 0) {
                        ModuleConstants.ReceiptMap.put("BankRRN ", String.valueOf(bankRrn));
                        ModuleConstants.InvoiceModelList.add(new InvocieModel(String.valueOf(bankRrn), "bankRrn", "BankRRN"));
                    }

                    if (transType != null && transType.length() > 0) {
                        ModuleConstants.ReceiptMap.put("TranType", String.valueOf(transType));
                        ModuleConstants.InvoiceModelList.add(new InvocieModel(String.valueOf(transType), "transType", "TranType"));
                    }

                    ModuleConstants.ReceiptMap.put("Type", FingUtil.getTypeName(type));
                    ModuleConstants.InvoiceModelList.add(new InvocieModel(String.valueOf(type), "type", "Type"));

                    if (cardNum != null && cardNum.length() > 0) {
                        ModuleConstants.ReceiptMap.put("CardNum", String.valueOf(cardNum));
                        ModuleConstants.InvoiceModelList.add(new InvocieModel(String.valueOf(cardNum), "cardNum", "CardNum"));
                    }

                    if (cardType != null && cardType.length() > 0) {
                        ModuleConstants.ReceiptMap.put("CardType", String.valueOf(cardType));
                        ModuleConstants.InvoiceModelList.add(new InvocieModel(String.valueOf(cardType), "cardType", "CardType"));
                    }

                    if (bankName != null && bankName.length() > 0) {
                        ModuleConstants.ReceiptMap.put("BankName", String.valueOf(bankName));
                        ModuleConstants.InvoiceModelList.add(new InvocieModel(String.valueOf(bankName), "bankName", "BankName"));
                    }

                    ModuleConstants.ReceiptMap.put("TransAmount", String.valueOf(transAmount));
                    ModuleConstants.InvoiceModelList.add(new InvocieModel(String.valueOf(transAmount), "transAmount", "TransAmount"));

                    ModuleConstants.ReceiptMap.put("BalanceAmount", String.valueOf(balAmount));
                    ModuleConstants.InvoiceModelList.add(new InvocieModel(String.valueOf(balAmount), "balAmount", "BalanceAmount"));

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
        volleyNetworkCall(ModuleConstants.FMATM_INITIATE);
    }

    private Map<String, String> param() {
        String userId = ModuleSharedPrefs.getValue(this, ModuleSharedPrefs.USER_ID);
        String token = ModuleSharedPrefs.getValue(this, ModuleSharedPrefs.APP_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("apptoken", token);
        map.put("user_id", userId);
        map.put("mobile", mobile);
        map.put("imei", FingUtil.getImei(this));
        map.put("transactionType", TYPE);
        map.put("amount", amount);
        map.put("remark", "Test");

        String json = new JSONObject(map).toString();
        //Log.e("PARAM", "PARAM: " + json);
        return map;
    }

    private void volleyNetworkCall(String url) {
        if (ModuleUtil.isOnline(this)) {
            new VolleyGetNetworkCall(this, this, url, 1, param()).netWorkCall();
        } else {
            Toast.makeText(this, "Network connection error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSuccessRequest(String JSonResponse) {
        Gson gson = new GsonBuilder().create();
        //System.out.println("RES: " + JSonResponse);
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
                intent.putExtra(Constants.IMEI, FingUtil.getImei(this));
                intent.putExtra(Constants.AMOUNT, amount);
                intent.putExtra(Constants.MOBILE_NUMBER, mobile);

                ModuleConstants.FING_TXN_ID = model.getTxnid();

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
        Toast.makeText(context, "Network Request Error : " + msg, Toast.LENGTH_SHORT).show();
    }
}
