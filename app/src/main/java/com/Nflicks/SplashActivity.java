package com.Nflicks;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RuntimePermissionsActivity;
import com.Nflicks.service.GetDetailService;

import java.io.File;

public class SplashActivity extends RuntimePermissionsActivity {

    MyPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            hideSystemUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_splash);
        myPreferences = new MyPreferences(SplashActivity.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashActivity.super.requestAppPermissions(new
                                String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_SMS,
                                android.Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.WRITE_CONTACTS,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.CAMERA
                        }, R.string.runtime_permissions_txt
                        , 20);
            }
        }, 500);
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                File pdfDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "" + GlobalElements.directory);
                if (!pdfDir.exists()) {
                    pdfDir.mkdir();
                }

                if (GlobalElements.isConnectingToInternet(SplashActivity.this)) {
                    String id = myPreferences.getPreferences(MyPreferences.ID);
                    String status = myPreferences.getPreferences(MyPreferences.status);
                    System.out.print("" + id + "" + status);
                    if (myPreferences.getPreferences(MyPreferences.ID).equals("") || myPreferences.getPreferences(MyPreferences.status).equals("")) {
                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    } else if (!myPreferences.getPreferences(MyPreferences.ID).equals("") && myPreferences.getPreferences(MyPreferences.status).equals("0")) {
                        Intent i = new Intent(SplashActivity.this, RegistrationActivity.class);
                        startActivity(i);
                        finish();
                    } else if (!myPreferences.getPreferences(MyPreferences.ID).equals("") && myPreferences.getPreferences(MyPreferences.status).equals("1")) {
                        Intent i = new Intent(SplashActivity.this, CategoryChooesActivity.class);
                        i.putExtra("type", "0");
                        startActivity(i);
                        finish();
                    } else if (!myPreferences.getPreferences(MyPreferences.ID).equals("") && myPreferences.getPreferences(MyPreferences.status).equals("2")) {
                        /*Intent i = new Intent(SplashActivity.this, ContactSyncActvity.class);
                        i.putExtra("type", "0");
                        startActivity(i);
                        finish();*/
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        i.putExtra("type", "0");
                        startActivity(i);
                        finish();
                    } else if (!myPreferences.getPreferences(MyPreferences.ID).equals("") && myPreferences.getPreferences(MyPreferences.status).equals("3")) {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else if (!myPreferences.getPreferences(MyPreferences.ID).equals("") && myPreferences.getPreferences(MyPreferences.status).equals("4")) {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else if (!myPreferences.getPreferences(MyPreferences.ID).equals("") && myPreferences.getPreferences(MyPreferences.status).equals("5")) {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    startService(new Intent(SplashActivity.this, GetDetailService.class));
                } else {
                    GlobalElements.showDialog(SplashActivity.this);
                }

                /*Intent i =new Intent(SplashActivity.this,QrCodeActivity.class);
                startActivity(i);
                finish();*/

            }
        }, 200);
    }


    private void hideSystemUI() {
        try {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
