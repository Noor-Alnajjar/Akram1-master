package com.joserv.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.Toast;

import com.google.zxing.Result;
import com.joserv.Akram.R;


import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrcodeRaederCamera extends AppCompatActivity implements ZXingScannerView.ResultHandler {



    final int RequestCameraPermissionID = 1001;
    private ZXingScannerView mScannerView;


    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RequestCameraPermissionID) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                mScannerView = new ZXingScannerView(this);
                setContentView(mScannerView);
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();


            } else {

                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();

            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mScannerView.stopCamera();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_raeder_camera);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (checkSelfPermission(android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        RequestCameraPermissionID);
            }else{
                mScannerView = new ZXingScannerView(this);
                setContentView(mScannerView);
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();
            }}
        else{
            mScannerView = new ZXingScannerView(this);
            setContentView(mScannerView);
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        }


    }

    @Override
    public void handleResult(Result result) {
        //Do anything with result here :D
        Log.w("handleResult", result.getText());
        Intent in = new Intent();
        in.putExtra("scannedInfo",result.getText());
        setResult(RESULT_OK, in);
        finish();

        //Resume scanning
        //mScannerView.resumeCameraPreview(this);
    }


}
