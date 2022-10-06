package com.payment.krishipay.utill;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.ViewPropertyTransition;
import com.payment.krishipay.R;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoanUtil {
    public static boolean isAllValueAvailable(String userId, String appToken, Context context) {
        boolean flag = true;
        if (userId == null || userId.length() == 0) {
            flag = false;
            Toast.makeText(context, "userId is not correct", Toast.LENGTH_SHORT).show();
        }

        if (appToken == null || appToken.length() == 0) {
            flag = false;
            Toast.makeText(context, "appToken is not correct", Toast.LENGTH_SHORT).show();
        }

        return flag;
    }

    public static boolean isNE(EditText et) {
        return et == null || et.getText().toString().length() == 0;
    }

    public static boolean isNE(EditText et, int ValidSize) {
        return et.getText().toString().length() != ValidSize;
    }

    public static boolean isValid(EditText et, String name, Context context) {
        if (isNE(et)) {
            Toast.makeText(context, name + " is required", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValid(EditText et, String name, int reqLength, Context context) {
        if (isNE(et)) {
            Toast.makeText(context, name + " is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (isNE(et, reqLength)) {
            Toast.makeText(context, "Please input valid " + name, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public static void loadImage(final Context context, ImageView imageView, String url) {
        if (context == null || ((Activity) context).isDestroyed()) return;
        ViewPropertyTransition.Animator animationObject = new ViewPropertyTransition.Animator() {
            @Override
            public void animate(View view) {
                view.setAlpha(0f);
                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
                fadeAnim.setDuration(1000);
                fadeAnim.start();
            }
        };

        RequestOptions options = new RequestOptions()
                .placeholder(context.getResources().getDrawable(R.drawable.ic_loader))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .timeout(60000)
                .error(context.getResources().getDrawable(R.drawable.ic_loader));

        Glide.with(context) //passing context
                .load(url) //passing your url to load image.
                .transition(GenericTransitionOptions.with(animationObject))
                .apply(options)
                .into(imageView);
    }

    public static boolean isNotNull(String s) {
        boolean flag = true;
        if (s == null || s.length() == 0 || s.equalsIgnoreCase("null")) {
            flag = false;
        }
        return flag;
    }

    public static String capitalise(String status) {
        StringBuilder sb = new StringBuilder(status);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static boolean isOnline(Context context) {
        boolean isConnected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(context.CONNECTIVITY_SERVICE);
            isConnected = cm.getActiveNetworkInfo().isConnected();
        } catch (Exception ex) {
            isConnected = false;
        }
        return isConnected;
    }

    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException var3) {
            return false;
        }
    }

    public static String getPreferredPackage(Context context, String deviceId) {
        String data = "";

        try {
            Intent intent = new Intent();
            intent.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
            PackageManager packageManager = context.getPackageManager();
            ResolveInfo resolveInfo = packageManager.resolveActivity(intent, 0);
            String temp = resolveInfo.activityInfo.packageName;
            if (!temp.equalsIgnoreCase("android") && !temp.equalsIgnoreCase(deviceId)) {
                data = temp;
            } else {
                data = "";
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return data;
    }


    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    // Function to validate the PAN Card number.
    public static boolean isValidPanCardNo(String panCardNo) {
        String regex = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
        Pattern p = Pattern.compile(regex);
        if (panCardNo == null) {
            return false;
        }
        Matcher m = p.matcher(panCardNo);
        return m.matches();
    }

    public static boolean validateAadharNumber(String aadharNumber) {
        Pattern aadharPattern = Pattern.compile("\\d{12}");
        boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
        if (isValidAadhar) {
            isValidAadhar = AadharNumberPattern.validateVerhoeff(aadharNumber);
        }
        return isValidAadhar;
    }
}
