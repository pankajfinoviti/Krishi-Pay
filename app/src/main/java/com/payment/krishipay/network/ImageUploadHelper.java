package com.payment.krishipay.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.payment.krishipay.app.Constants;
import com.payment.krishipay.utill.AndroidMultiPartEntity;
import com.payment.krishipay.utill.Print;
import com.payment.krishipay.utill.SharedPrefs;
import com.payment.krishipay.views.loan.OnboardingModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class ImageUploadHelper {
    private OnboardingModel param;
    private File imgAadhar;
    private File imgPan;
    private Activity context;
    private RequestResponseLis listener;

    public ImageUploadHelper(OnboardingModel param, File imgAadhar, File imgPan, Activity context,
                             RequestResponseLis listener) {
        this.param = param;
        this.imgAadhar = imgAadhar;
        this.imgPan = imgPan;
        this.context = context;
        this.listener = listener;
        new ImageUploadHttp().execute();
    }

    ProgressDialog loaderDialog;
    long totalSize = 0;

    class ImageUploadHttp extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loaderDialog = new ProgressDialog(context);
            loaderDialog.setMessage("Please wait...");
            loaderDialog.setCancelable(false);
            loaderDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.URL.AGRILOAN);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        num -> publishProgress((int) ((num / (float) totalSize) * 100)));
                if (imgAadhar != null && imgAadhar.exists()) {
                    entity.addPart("aadharPics", new FileBody(imgAadhar));
                }

                if (imgPan != null && imgPan.exists()) {
                    entity.addPart("pancardPics", new FileBody(imgPan));
                }

                entity.addPart("merchantName", new StringBody(param.getName()));
                entity.addPart("merchantAddress", new StringBody(param.getAddress()));
                entity.addPart("merchantCityName", new StringBody(param.getCity()));
                entity.addPart("merchantState", new StringBody(param.getState()));
                entity.addPart("merchantPhoneNumber", new StringBody(param.getPhone()));
                //entity.addPart("merchantEmail", new StringBody(param.getEmail()));
                entity.addPart("merchantShopName", new StringBody(param.getShopName()));
                entity.addPart("userPan", new StringBody(param.getPan()));
                entity.addPart("merchantPinCode", new StringBody(param.getPinCode()));
                entity.addPart("merchantAadhar", new StringBody(param.getAadhaar()));

                entity.addPart("purpose", new StringBody(param.getLoanPurpose()));
                entity.addPart("amount", new StringBody(param.getLoanAmount()));
                entity.addPart("duration", new StringBody(param.getLoanDuration()));

                entity.addPart("transactionType", new StringBody("useronboard"));
                entity.addPart("user_id", new StringBody(SharedPrefs.getValue(context, SharedPrefs.USER_ID)));
                entity.addPart("apptoken", new StringBody(SharedPrefs.getValue(context, SharedPrefs.APP_TOKEN)));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Print.P("response: " + result);
            if (loaderDialog != null && loaderDialog.isShowing()) {
                loaderDialog.dismiss();
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = "Getting some error in uploading process please try after some time";
                String status = "";
                if (jsonObject.has("status")) {
                    status = jsonObject.getString("status");
                } else {
                    status = jsonObject.getString("statuscode");
                }
                if (jsonObject.has("message"))
                    message = jsonObject.getString("message");
                alertDialog(context, message, status);
            } catch (Exception e) {
                Toast.makeText(context, "Expected Unhandled response in the uploading process", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public interface RequestResponseLis {
        void onSuccessRequest(String JSonResponse);

        void onFailRequest(String msg);
    }

    public static void alertDialog(final Activity context, String str, String status) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle("Status");
        alertDialogBuilder.setMessage(str);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        (dialog, id) -> {
                            dialog.cancel();
                            if (status.equalsIgnoreCase("TXN")) {
                                Constants.IS_RELOAD_REQUEST = true;
                                context.finish();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
