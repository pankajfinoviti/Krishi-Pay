package com.payment.krishipay.views.loan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.payment.krishipay.R;
import com.payment.krishipay.app.Constants;
import com.payment.krishipay.network.ImageUploadHelper;
import com.payment.krishipay.utill.LoanUtil;
import com.payment.krishipay.utill.Print;

import org.json.JSONObject;

import java.io.File;

public class LoanFormTwo extends AppCompatActivity implements ImageUploadHelper.RequestResponseLis {
    private EditText etPinCode, etAadhaar, etLoanPurpose, etAmount, tvSelectDuration;
    private Button btnProceed;
    private OnboardingModel model;
    private LinearLayout secPan, secAadhaar;
    private ImageView imgPan, imgPanClick;
    private TextView tvLPanLabel;
    private File filePanImage = null;
    private ImageView imgAadhaar, imgAadhaarClick;
    private TextView tvLAadhaarLabel;
    private File fileAadhaarImage = null;
    private int TYPE = 0;

    private void init() {
        model = getIntent().getParcelableExtra("data");
        etPinCode = findViewById(R.id.etPinCode);
        btnProceed = findViewById(R.id.btnProceed);
        etAadhaar = findViewById(R.id.etAadhaar);
        etLoanPurpose = findViewById(R.id.etLoanPurpose);
        etAmount = findViewById(R.id.etAmount);
        tvSelectDuration = findViewById(R.id.tvSelectDuration);

        secPan = findViewById(R.id.secPanImage);
        imgPan = secPan.findViewById(R.id.imgPreview);
        imgPanClick = secPan.findViewById(R.id.imgAttach);
        tvLPanLabel = secPan.findViewById(R.id.tvLabel);
        tvLPanLabel.setText("Pancard");
        secAadhaar = findViewById(R.id.secAadharImage);
        imgAadhaar = secAadhaar.findViewById(R.id.imgPreview);
        imgAadhaarClick = secAadhaar.findViewById(R.id.imgAttach);
        tvLAadhaarLabel = secAadhaar.findViewById(R.id.tvLabel);
        tvLAadhaarLabel.setText("Aadhaar");
        setupToolBar();
        btnProceed.setOnClickListener(view -> {
            if (isValid()) {
                model.setAadhaar(etAadhaar.getText().toString());
                model.setPinCode(etPinCode.getText().toString());
                model.setLoanPurpose(etLoanPurpose.getText().toString());
                model.setLoanAmount(etAmount.getText().toString());
                model.setLoanDuration(tvSelectDuration.getText().toString());
                networkCallUsingVolleyApi(Constants.URL.AGRILOAN);
            }
        });

        imgAadhaarClick.setOnClickListener(v -> {
            TYPE = 0;
            pickGalleryImage();
        });

        imgPanClick.setOnClickListener(v -> {
            TYPE = 1;
            pickGalleryImage();
        });

        tvSelectDuration.setOnClickListener(v -> slugPopup());
    }

    private void setupToolBar() {
        ImageView backImage = findViewById(R.id.imgBack);
        backImage.setOnClickListener(view -> finish());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan_form_next);
        init();

    }

    private boolean isValid() {
        boolean flag = false;
        flag = LoanUtil.isValid(etPinCode, "Name", this);
        if (!flag) return flag;

        flag = LoanUtil.isValid(etAadhaar, "Aadhaar", 12, this);
        if (!flag) return flag;

        flag = LoanUtil.isValid(etLoanPurpose, "Loan Purpose", this);
        if (!flag) return flag;

        flag = LoanUtil.isValid(etAmount, "Amount", this);
        if (!flag) return flag;

        flag = LoanUtil.isValid(tvSelectDuration, "Duration", this);
        if (!flag) return flag;

        if (!LoanUtil.validateAadharNumber(etAadhaar.getText().toString())) {
            Toast.makeText(this, "Aadhaar number is not valid", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (filePanImage == null || !filePanImage.exists()) {
            Toast.makeText(this, "Pancard image is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fileAadhaarImage == null || !fileAadhaarImage.exists()) {
            Toast.makeText(this, "Aadhaar image is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void networkCallUsingVolleyApi(String url) {
        Print.P("URL : " + url);
        if (LoanUtil.isOnline(this)) {
            new ImageUploadHelper(model, fileAadhaarImage, filePanImage, this, this);
        } else {
            Toast.makeText(this, "Network connection error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSuccessRequest(String JSonResponse) {
        try {
            Print.P("API RES : \n" + JSonResponse);
            JSONObject obj = new JSONObject(JSonResponse);
            String status = obj.getString("status");
            if (status.equalsIgnoreCase("TXN")) {
                pickGalleryImage();
            } else {
                String msg = obj.getString("message");
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailRequest(String msg) {
        showMsg(msg);
    }

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void pickGalleryImage() {
        ImagePicker.Companion.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(100)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                if (TYPE == 0) {
                    fileAadhaarImage = new File(uri.getPath());
                    if (fileAadhaarImage != null) {
                        showImage(fileAadhaarImage, imgAadhaar);
                    }
                } else if (TYPE == 1) {
                    filePanImage = new File(uri.getPath());
                    if (filePanImage != null) {
                        showImage(filePanImage, imgPan);
                    }
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, "" + ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showImage(File file, ImageView view) {
        if (file.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            view.setImageBitmap(myBitmap);
        }
    }

    private void slugPopup() {
        final CharSequence[] choice = {"Krishi Short Term (500 - 10000)", "Krishi Long Term(10000 - 50000)"};
        AlertDialog.Builder alert = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog);
        alert.setTitle("Please select loan type");
        alert.setSingleChoiceItems(choice, -1, (dialog, which) -> {
            String txt = choice[which].toString();
            dialog.dismiss();
            tvSelectDuration.setText(txt);
        });
        alert.setCancelable(false);
        alert.show();
    }
}