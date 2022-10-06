package com.payment.krishipay.ocr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.payment.krishipay.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class ProcessCamera extends AppCompatActivity implements SurfaceHolder.Callback, Detector.Processor {
    private SurfaceView cameraView;
    private TextView txtView;
    private CameraSource cameraSource;
    private EditText txt1;
    Button clear;
    Button send;
    String numbers;
    StringBuilder strBuilder1;
    ScrollView scr;
    Button dial;
    Boolean check = false;
    Button regex;

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_main);
        cameraView = findViewById(R.id.surface_view);
        txtView = findViewById(R.id.txtview);
        txt1 = findViewById(R.id.txt1);
        clear = findViewById(R.id.clear);
        send = findViewById(R.id.send);
        dial = findViewById(R.id.dial);
        regex = findViewById(R.id.regex);
        scr = findViewById(R.id.scroll_view);
        clear.setOnClickListener(v -> txt1.setText(""));
        TextRecognizer txtRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!txtRecognizer.isOperational()) {
            Log.e("Main Activity", "Detector dependencies are not yet available");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), txtRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(this);
            txtRecognizer.setProcessor(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                return;
            }
            cameraSource.start(cameraView.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraSource.stop();
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections detections) {
        SparseArray items = detections.getDetectedItems();
        final StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            TextBlock item = (TextBlock) items.valueAt(i);
            strBuilder.append(item.getValue());
            strBuilder.append("/");
        }

        //Regex Operation
        Log.e("-->", "withNumber : ------------------------------------");
        String str = strBuilder.toString();
        String str2 = str.replace("/", "\n");
        //str2 = str2.replaceAll("[^0-9]", "");
        String str1[] = str2.split("\n");
        //Log.d("-->", "withNumber : "+ str);
        //Log.d("-->", "withNumber : "+ str2);
        strBuilder1 = new StringBuilder();

        for (int i = 0; i < str1.length; i++) {
            strBuilder1.append(str1[i]);
            strBuilder1.append("/");
            Log.e("-->", str1[i]);
        }

        //Log.e("strBuilder", strBuilder.toString());
        //Log.e("strBuilder", "------------------------------------");
    }
}