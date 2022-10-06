package com.payment.krishipay.views.walletsection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.payment.krishipay.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.payment.krishipay.app.AppManager;
import com.payment.krishipay.app.Constants;
import com.payment.krishipay.network.RequestResponseLis;
import com.payment.krishipay.network.VolleyNetworkCall;
import com.payment.krishipay.utill.MyUtil;
import com.payment.krishipay.utill.Print;
import com.payment.krishipay.utill.SharedPrefs;
import com.payment.krishipay.views.invoice.ReportInvoice;
import com.payment.krishipay.views.invoice.model.InvoiceModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AepsMatmWalletReqest extends AppCompatActivity implements RequestResponseLis {
    private EditText etBankName, etAccount, etIFSC, etMode, etAmount;
    private TextView toolbarTitle;
    private RadioButton rbWallet, rbBank;
    private Context context;
    private String MODE;
    private String FLAG;
    private String ACTIVITY_TYPE;
    private Button btnProceed;

    private void init() {
        context = this;
        ACTIVITY_TYPE = getIntent().getStringExtra("activity_type");
        rbWallet = findViewById(R.id.rbWallet);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        rbBank = findViewById(R.id.rbBank);
        etBankName = findViewById(R.id.etBankName);
        etAccount = findViewById(R.id.etAccount);
        etIFSC = findViewById(R.id.etIFSC);
        etMode = findViewById(R.id.etMode);
        etAmount = findViewById(R.id.etAmount);
        btnProceed = findViewById(R.id.btnProceed);

        String Acc = SharedPrefs.getValue(this, SharedPrefs.ACCOUNT);
        if (Acc != null && Acc.length() > 0 && !Acc.equalsIgnoreCase("null")) {
            etAccount.setEnabled(false);
            etAccount.setText(SharedPrefs.getValue(this, SharedPrefs.ACCOUNT));
        }

        String bankNme = SharedPrefs.getValue(this, SharedPrefs.BANK);
        if (bankNme != null && bankNme.length() > 0 && !bankNme.equalsIgnoreCase("null")) {
            etBankName.setText(SharedPrefs.getValue(this, SharedPrefs.BANK));
            etBankName.setEnabled(false);
        }

        String ifsc = SharedPrefs.getValue(this, SharedPrefs.IFSC);
        if (ifsc != null && ifsc.length() > 0 && !ifsc.equalsIgnoreCase("null")) {
            etIFSC.setText(SharedPrefs.getValue(this, SharedPrefs.IFSC));
            etIFSC.setEnabled(false);
        }

        if (ACTIVITY_TYPE.equalsIgnoreCase("aeps"))
            toolbarTitle.setText(R.string.aeps_fun_request);
        else
            toolbarTitle.setText(R.string.matm_fund_request);
        rbBank.setOnClickListener(v -> {
            if (ACTIVITY_TYPE.equalsIgnoreCase("aeps"))
                FLAG = "bank";
            else
                FLAG = "matmbank";
        });

        rbWallet.setOnClickListener(v -> {
            if (ACTIVITY_TYPE.equalsIgnoreCase("aeps"))
                FLAG = "wallet";
            else
                FLAG = "matmwallet";
        });

        etMode.setOnClickListener(v -> modePopup());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aeps_matm_fund_request);
        init();

        btnProceed.setOnClickListener(v -> {
            if (isValid()) {
                networkCallUsingVolleyApi(Constants.URL.FUND_REQUEST, true);
            }
        });

    }

    private void modePopup() {
        final CharSequence[] choice = {"NEFT", "IMPS", "MANUAL"};
        AlertDialog.Builder alert = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog);
        alert.setTitle("Please select payment mode");
        alert.setSingleChoiceItems(choice, -1, (dialog, which) -> {
            String txt = choice[which].toString();
            MODE = txt;
            etMode.setText(txt);
        });
        alert.setNegativeButton("OK", (dialog, which) -> dialog.dismiss());
        alert.show();
    }


    private boolean isValid() {
        if (FLAG == null || FLAG.length() == 0) {
            Toast.makeText(context, "Please select bank or wallet", Toast.LENGTH_SHORT).show();
            return false;
        } else if (FLAG.contains("bank") && etBankName.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter bank name were amount deposited", Toast.LENGTH_SHORT).show();
            return false;
        } else if (FLAG.contains("bank") && etAccount.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter account number in which amount deposited", Toast.LENGTH_SHORT).show();
            return false;
        } else if (FLAG.contains("bank") && etIFSC.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter bank IFSC", Toast.LENGTH_SHORT).show();
            return false;
        } else if (FLAG.contains("bank") && !MyUtil.isNN(MODE)) {
            Toast.makeText(this, "Mode Selection is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etAmount.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter request amount", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            if (Double.parseDouble(etAmount.getText().toString()) < 10) {
                Toast.makeText(this, "Amount value should be grater than 10 ", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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
        map.put("type", FLAG);
        map.put("amount", etAmount.getText().toString());
        if (MyUtil.isNN(etAccount.getText().toString()))
            map.put("account", etAccount.getText().toString());
        if (MyUtil.isNN(etBankName.getText().toString()))
            map.put("bank", etBankName.getText().toString());
        if (MyUtil.isNN(etIFSC.getText().toString()))
            map.put("ifsc", etIFSC.getText().toString());
        if (MyUtil.isNN(MODE))
            map.put("mode", MODE);
        return map;
    }

    @Override
    public void onSuccessRequest(String JSonResponse) {
        try {
            String msg;
            JSONObject jsonObject = new JSONObject(JSonResponse);
            if (jsonObject.has("message")) {
                msg = jsonObject.getString("message");
            } else {
                msg = "Something went wrong, try again";
            }
            String txnid = jsonObject.getString("txnid");
            Constants.INVOICE_DATA = new ArrayList<>();
            Constants.INVOICE_DATA.add(new InvoiceModel("Txn Id", txnid));
            Constants.INVOICE_DATA.add(new InvoiceModel("Bank", etBankName.getText().toString()));
            Constants.INVOICE_DATA.add(new InvoiceModel("Date", Constants.COMMON_DATE_FORMAT.format(new Date())));
            if (ACTIVITY_TYPE.equalsIgnoreCase("aeps"))
                Constants.INVOICE_DATA.add(new InvoiceModel("Trans Type", "Aeps fund Request"));
            else
                Constants.INVOICE_DATA.add(new InvoiceModel("Trans Type", "Matm fund Request"));
            Constants.INVOICE_DATA.add(new InvoiceModel("Amount", MyUtil.formatWithRupee(this, etAmount.getText().toString())));
            Constants.INVOICE_DATA.add(new InvoiceModel("Status", "success"));
            Intent i = new Intent(this, ReportInvoice.class);
            i.putExtra("status", "success");
            i.putExtra("remark", "" + msg);
            startActivity(i);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailRequest(String msg) {
        Print.P(msg);
    }
}
