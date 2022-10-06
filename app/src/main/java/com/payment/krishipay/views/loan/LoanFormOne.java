package com.payment.krishipay.views.loan;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.payment.krishipay.R;
import com.payment.krishipay.utill.LoanUtil;


public class LoanFormOne extends AppCompatActivity {
    private EditText etName, etAddress, etCity, etState, etPhone,
            etShopName, etPan;
    private Button btnProceed;
    private OnboardingModel model;

    private void init() {
        model = new OnboardingModel();
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etPhone = findViewById(R.id.etPhone);
        etShopName = findViewById(R.id.etShopName);
        etPan = findViewById(R.id.etPan);
        btnProceed = findViewById(R.id.btnProceed);

        ImageView backImage = findViewById(R.id.imgBack);
        backImage.setOnClickListener(view -> finish());
        btnProceed.setOnClickListener(view -> {
            if (isValid()) {
                setForm();
                startActivity(new Intent(this, LoanFormTwo.class).putExtra("data", model));
            }
        });

        etState.setOnClickListener(view -> {
            Intent i = new Intent(LoanFormOne.this, StateList.class);
            startActivityForResult(i, 101);
        });

        setupToolBar("Term Loan");
        etPan.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
    }

    private void setupToolBar(String title) {
        TextView tvToolbar = findViewById(R.id.tvToolBarTitle);
        tvToolbar.setText(title);
        ImageView backImage = findViewById(R.id.imgBack);
        backImage.setOnClickListener(view -> finish());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan_form);
        init();
    }

    private boolean isValid() {
        boolean flag = false;
        flag = LoanUtil.isValid(etName, "Name", this);
        if (!flag) return flag;
        flag = LoanUtil.isValid(etAddress, "Address", this);
        if (!flag) return flag;
        flag = LoanUtil.isValid(etCity, "City", this);
        if (!flag) return flag;
        flag = LoanUtil.isValid(etState, "State", this);
        if (!flag) return flag;
        flag = LoanUtil.isValid(etPhone, "Phone", 10, this);
        if (!flag) return flag;
        flag = LoanUtil.isValid(etShopName, "Shop Name", this);
        if (!flag) return flag;
        flag = LoanUtil.isValid(etPan, "Pan", 10, this);
        if (!flag) return flag;

        if (!LoanUtil.isValidPanCardNo(etPan.getText().toString())) {
            Toast.makeText(this, "Please input valid pancard number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setForm() {
        model.setName(etName.getText().toString());
        model.setAddress(etAddress.getText().toString());
        model.setCity(etCity.getText().toString());
        model.setPhone(etPhone.getText().toString());
        model.setShopName(etShopName.getText().toString());
        model.setPan(etPan.getText().toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        //DeviceDataModel dataModel;
        try {
            switch (requestCode) {
                case 101:
                    String id = String.valueOf(data.getStringExtra("id"));
                    String Name = data.getStringExtra("name");
                    etState.setText(Name);
                    model.setState(id);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}