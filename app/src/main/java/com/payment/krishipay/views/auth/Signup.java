package com.payment.krishipay.views.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.payment.krishipay.R;
import com.payment.krishipay.app.AppManager;
import com.payment.krishipay.app.Constants;
import com.payment.krishipay.model.SlugModel;
import com.payment.krishipay.network.RequestResponseLis;
import com.payment.krishipay.network.VolleyNetworkCall;
import com.payment.krishipay.network.VolleyPostNetworkCall;
import com.payment.krishipay.utill.AppHandler;
import com.payment.krishipay.utill.MRequestResponseLis;
import com.payment.krishipay.utill.Print;
import com.payment.krishipay.utill.RegisterImageHelper;
import com.payment.krishipay.views.select_state_district.SearchWithListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Signup extends AppCompatActivity implements VolleyPostNetworkCall.RequestResponseLis, MRequestResponseLis, RequestResponseLis {

    int PIC_TYPE = 0;
    String userId, stateId="", stateName="", districtId;
    ProgressDialog dialog;
    String slug = "";
    int REQUEST_CODE = 0;
    ArrayList<SlugModel> slugList;
    private Button btnSignup;
    private EditText etName;
    private EditText etMobile;
    private EditText etEmail;
    private EditText etShop;
    private EditText etPan;
    private EditText etAadhar;
    private EditText etState;
    private EditText etReferralCode;
    private EditText etCity;
    private EditText etAddress;
    private EditText etPincode, etRegNo;
    private Spinner spnrSlug;
    private Context context;
    private File shopPic, certifcatePic;
    private String shopPicString, certifcatePicString;
    private View shopView, certicateView;
    private ImageView shopImgAttach, certImgAttach, shopImageView, certImageView;
    private TextView shopTitle, certTitle;
    private AlertDialog loaderDialog;

    private void init() {
        context = Signup.this;
        etName = findViewById(R.id.etName);
        etPincode = findViewById(R.id.etPincode);
        spnrSlug = findViewById(R.id.spnrSlug);
        etMobile = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);
        etShop = findViewById(R.id.etShop);
        etPan = findViewById(R.id.etPan);
        etAadhar = findViewById(R.id.etAadhar);
        etState = findViewById(R.id.etState);
        etReferralCode = findViewById(R.id.etReferralCode);
        etCity = findViewById(R.id.etCity);
        etRegNo = findViewById(R.id.etRegNo);
        etAddress = findViewById(R.id.etAddress);
        btnSignup = findViewById(R.id.btnSubmit);
        shopView = findViewById(R.id.shopView);
        certicateView = findViewById(R.id.certicateView);

        shopImgAttach = shopView.findViewById(R.id.imgAttach);
        shopImageView = shopView.findViewById(R.id.imgPreview);
        shopTitle = shopView.findViewById(R.id.tvLabel);
        shopTitle.setText("Shop Image");


        certImgAttach = certicateView.findViewById(R.id.imgAttach);
        certImageView = certicateView.findViewById(R.id.imgPreview);
        certTitle = certicateView.findViewById(R.id.tvLabel);
        certTitle.setText("Certificate Image");

        shopImgAttach.setOnClickListener(v -> {
            PIC_TYPE = 0;
            showBottomSheetDialog();
        });


        certImgAttach.setOnClickListener(v -> {
            PIC_TYPE = 1;
            showBottomSheetDialog();
        });


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        REQUEST_CODE = 0;
        networkCallUsingVolleyApi(Constants.URL.GET_ROLE);
        etState.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            Intent intent = new Intent(this, SearchWithListActivity.class);
            bundle.putString("type", "state");
            intent.putExtras(bundle);
            startActivityForResult(intent, 100);
        });

        etCity.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            Intent intent = new Intent(this, SearchWithListActivity.class);
            bundle.putString("type", "district");
            bundle.putString("stateId", stateId);
            intent.putExtras(bundle);
            startActivityForResult(intent, 101);
        });

        btnSignup.setOnClickListener(v -> {
            if (AppManager.isOnline(Signup.this)) {
                if (isValid()) {
                    REQUEST_CODE = 1;
                    String url = Constants.URL.BASE_URL + "api/android/auth/user/register";
                    networkCallUsingVolleyApi(param(), url);
                    param();
                }
            } else {
                Toast.makeText(Signup.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void networkCallUsingVolleyApi(String url) {
        if (AppManager.isOnline(this)) {
            new VolleyNetworkCall(this, this, url, 1, param(), true).netWorkCall();
        } else {
            Toast.makeText(this, "Network connection error", Toast.LENGTH_LONG).show();
        }
    }

    private void networkCallUsingVolleyApi(Map<String, String> map, String url) {
        if (AppManager.isOnline(this)) {
            showLoader("Loading ...");
            new RegisterImageHelper(shopPic, certifcatePic, param(), this, this);
        } else {
            Toast.makeText(this, "Network connection error", Toast.LENGTH_LONG).show();
        }
    }

    private void showLoader(String loaderMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.android_loader, null);
        builder.setView(view);
        builder.create();
        loaderDialog = builder.show();
        loaderDialog.setCancelable(false);
    }

    private void closeLoader() {
        if (loaderDialog != null && loaderDialog.isShowing()) {
            loaderDialog.dismiss();
        }
    }

    public HashMap<String, String> param() {
        HashMap<String, String> map = new HashMap<>();
        if (REQUEST_CODE == 1) {
            map.put("name", etName.getText().toString());
            map.put("mobile", etMobile.getText().toString());
            map.put("email", etEmail.getText().toString());
            map.put("shopname", etShop.getText().toString());
            map.put("pancard", etPan.getText().toString());
            map.put("aadharcard", etAadhar.getText().toString());
            map.put("state", stateId);
            map.put("city", districtId);
            map.put("address", etAddress.getText().toString());
            map.put("pincode", etPincode.getText().toString());
            map.put("shopregno", etRegNo.getText().toString());
            map.put("refercode", etReferralCode.getText().toString());
            map.put("slug", slug);
        } else {
            map.put("type", "register");
        }
        String paramString = new JSONObject(map).toString() ;
        System.out.print("JSON Res Param: " + paramString);
        return map;
    }

    private boolean isValid() {

        if (etName.getText().toString().length() == 0) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPincode.getText().toString().length() == 0) {
            Toast.makeText(this, "Pin is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etPincode.getText().toString().length() != 6) {
            Toast.makeText(this, "Pin length should be 6", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etMobile.getText().toString().length() == 0) {
            Toast.makeText(this, "Mobile number is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etMobile.getText().toString().length() != 10) {
            Toast.makeText(this, "Invalid contact", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etEmail.getText().toString().length() == 0) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etShop.getText().toString().length() == 0) {
            Toast.makeText(this, "Shop field is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPan.getText().toString().length() == 0) {
            Toast.makeText(this, "Pancard number is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etAadhar.getText().toString().length() == 0) {
            Toast.makeText(this, "Aadhar number is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etAadhar.getText().toString().length() != 12) {
            Toast.makeText(this, "Value should be 12 digits long", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (stateId.equals("")) {
            Toast.makeText(this, "State is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (districtId.equalsIgnoreCase("")) {
            Toast.makeText(this, "City is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etAddress.getText().toString().length() == 0) {
            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etRegNo.getText().toString().length() == 0) {
            Toast.makeText(this, "Shop Registration Number is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (slug.length() == 0) {
            Toast.makeText(this, "Slug value is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onSuccessRequest(String JSonResponse) {
        Print.P(JSonResponse);
        //{"statuscode":"TXN","message":"Thank you for choosing, your request is successfully submitted for approval"}

        try {
            JSONObject jsonObject = null;
            String msg = "some error accour";
            jsonObject = new JSONObject(JSonResponse);
            if (REQUEST_CODE == 1) {
                closeLoader();

                String status;
                if (jsonObject.has("status")) {
                    status = jsonObject.getString("status");
                } else {
                    status = jsonObject.getString("statuscode");
                }
                if (jsonObject.has("message"))
                    msg = jsonObject.getString("message");

                if (status.equalsIgnoreCase("TXN")) {
                    confirmPopup(msg);
                } else {
                    Toast.makeText(context, "Error : " + msg, Toast.LENGTH_SHORT).show();
                }
            } else {
                JSONArray array = jsonObject.getJSONArray("data");

                intSlug(array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Print.P("Json Parser Exception");
            closeLoader();
        }
    }

    private void intSlug(JSONArray array) {
        slugList = new ArrayList<>();
        slugList.addAll(AppHandler.parseSlugList(array));
        slug = slugList.get(0).getSlug().toLowerCase(Locale.ROOT);
        ArrayList<String> list = new ArrayList();
        for (SlugModel data : slugList) {
            list.add(data.getName());
        }


        ArrayAdapter stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spnrSlug.setAdapter(stateAdapter);

        spnrSlug.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    slug = slugList.get(i).getSlug().toLowerCase(Locale.ROOT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void confirmPopup(String msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(msg);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onFailRequest(String msg) {
        closeLoader();
    }


    private void showBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.custom_dialog_image_selection);
        Button btnSubmit = bottomSheetDialog.findViewById(R.id.btnClose);
        ImageView btnGallery = bottomSheetDialog.findViewById(R.id.btnGallery);
        ImageView btnCamera = bottomSheetDialog.findViewById(R.id.btnCamera);
        btnSubmit.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        btnCamera.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            ImagePicker.Companion.with(this).crop().cameraOnly().maxResultSize(720, 1080).start(2021);
        });

        btnGallery.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            ImagePicker.Companion.with(this).crop().galleryOnly().maxResultSize(720, 1080).start(2021);
        });
        bottomSheetDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case 2021:
                    if (resultCode == Activity.RESULT_OK) {
                        Uri uri = data.getData();
                        File fileImage = new File(uri.getPath());
                        Bitmap mImageBitmap = BitmapFactory.decodeFile(fileImage.getAbsolutePath());
                        switch (PIC_TYPE) {
                            case 0:
                                shopPic = fileImage;
                                shopPicString = fileImage.getName();
                                shopImageView.setImageBitmap(mImageBitmap);
                                break;
                            case 1:
                                certifcatePic = fileImage;
                                certifcatePicString = fileImage.getName();
                                certImageView.setImageBitmap(mImageBitmap);
                                break;

                        }
                    } else if (resultCode == ImagePicker.RESULT_ERROR) {
                        Toast.makeText(this, "" + ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 100:
                    stateName = data.getStringExtra("stateName");
                    stateId = data.getStringExtra("stateId");
                    etState.setText(stateName);
                    etCity.setText("");
                    break;
//                ExtensionFunction.showToast(this,stateId);
                case 101:
                    String districtName = data.getStringExtra("districtname");
                    districtId = data.getStringExtra("districtId");
                    etCity.setText(districtName);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
