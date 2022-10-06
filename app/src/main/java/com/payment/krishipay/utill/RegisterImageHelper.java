package com.payment.krishipay.utill;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.payment.krishipay.app.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterImageHelper {
    ProgressDialog loaderDialog;
    long totalSize = 0;
    private File mFilePart1;
    private File mFilePart2;

    private HashMap<String, String> params;
    private Activity context;
    private MRequestResponseLis listener;

    public RegisterImageHelper(File mFilePart1,
                               File mFilePart2,
                               HashMap<String, String> params,
                               Activity context,
                               MRequestResponseLis listener) {
        this.mFilePart1 = mFilePart1;
        this.mFilePart2 = mFilePart2;

        this.params = params;
        this.context = context;
        this.listener = listener;
        new ImageUploadHttp().execute();
    }

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
            String url = (Constants.URL.REGISTER);
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("User-Agent", "Android");
            Print.P("URL : " + url);
            // private File aadharPics, pancardPics, passports, shoppics;

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        num -> publishProgress((int) ((num / (float) totalSize) * 100)));
                if (mFilePart1 != null && mFilePart1.exists()) {
                    entity.addPart("shoppic", new FileBody(mFilePart1));
                }

                if (mFilePart2 != null && mFilePart2.exists()) {
                    entity.addPart("regcertifictepic", new FileBody(mFilePart2));
                }

                for (Map.Entry<String, String> entry : params.entrySet()) {
                    entity.addPart(entry.getKey(), new StringBody(entry.getValue()));
                }

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
            try {
                Print.P("response: " + result);
                if (loaderDialog != null && loaderDialog.isShowing()) {
                    loaderDialog.dismiss();
                }
                listener.onSuccessRequest(result);
            } catch (Exception ignored) {

            }
        }
    }
}
