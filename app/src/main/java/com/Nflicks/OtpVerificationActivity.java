package com.Nflicks;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Nflicks.custom.FontFamily;
import com.Nflicks.custom.Toaster;
import com.Nflicks.custom.Validation;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;
import com.Nflicks.service.GetDetailService;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    @BindView(R.id.OtpMobileNumber)
    TextView mobile_number;
    @BindView(R.id.otp_title)
    TextView title;
    @BindView(R.id.OtpCodeTitle)
    TextView otp_code_title;
    @BindView(R.id.OptMobileNumberEdt)
    EditText otp_code_edt;
    @BindView(R.id.OtpSubmitBtn)
    TextView otp_submit;
    @BindView(R.id.OtpResendBtn)
    TextView resendOtp;
    @BindView(R.id.OtpBack)
    ImageView back_img;
    @BindView(R.id.otp_terms_check)
    CheckBox otp_terms_check;
    @BindView(R.id.otp_terms_linear)
    LinearLayout otp_terms_linear;
    @BindView(R.id.otp_terms_txt)
    TextView otp_terms_txt;

    String mobile_no, imei, refreshedtoken;
    MyPreferences myPreferences;
    BroadcastReceiver receiver;
    ProgressDialog pd;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        ButterKnife.bind(this);
        myPreferences = new MyPreferences(OtpVerificationActivity.this);
        refreshedtoken = FirebaseInstanceId.getInstance().getToken();
        myPreferences.setPreferences(MyPreferences.refreshedtoken, refreshedtoken);

        hideSystemUI();
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mobile_number.setTypeface(FontFamily.process(OtpVerificationActivity.this, R.raw.roboto_regular), Typeface.BOLD);
        title.setTypeface(FontFamily.process(OtpVerificationActivity.this, R.raw.roboto_regular));
        otp_code_title.setTypeface(FontFamily.process(OtpVerificationActivity.this, R.raw.roboto_regular));
        resendOtp.setTypeface(FontFamily.process(OtpVerificationActivity.this, R.raw.roboto_regular));
        otp_code_edt.setTypeface(FontFamily.process(OtpVerificationActivity.this, R.raw.roboto_regular));
        otp_submit.setTypeface(FontFamily.process(OtpVerificationActivity.this, R.raw.sqaure721), Typeface.BOLD);

        try {
            Intent i = getIntent();
            mobile_no = i.getStringExtra("mobile_no");
            status = i.getStringExtra("status");
            mobile_number.setText("" + mobile_no);

            if (status.equals("0")) {
                otp_terms_linear.setVisibility(View.VISIBLE);
            } else {
                otp_terms_linear.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        otp_terms_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW", Uri.parse("" + getResources().getString(R.string.terms_url)));
                    startActivity(viewIntent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Unable to Connect Try Again...",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        otp_code_edt.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key pressxa
                    hideSystemUI();
                    return true;
                }
                return false;
            }
        });

        otp_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Validation.isValid(Validation.BLANK_CHECK, otp_code_edt.getText().toString())) {
                    Toaster.show(OtpVerificationActivity.this, "" + getResources().getString(R.string.otp_blank), true, Toaster.DANGER);
                } else {
                    try {

                        if (status.equals("0") && !otp_terms_check.isChecked()) {
                            Toaster.show(OtpVerificationActivity.this, "Please check terms and privacy policy", false, Toaster.DANGER);
                            return;
                        }
                        if (refreshedtoken == null || refreshedtoken.equals("")) {

                            refreshedtoken = FirebaseInstanceId.getInstance().getToken();
                            myPreferences.setPreferences(MyPreferences.refreshedtoken, refreshedtoken);

                            AlertDialog buildInfosDialog;
                            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(OtpVerificationActivity.this);
                            alertDialog2.setMessage("We could not get your device information try again");

                            alertDialog2.setPositiveButton("Try again",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Write your code here to execute after dialog
                                            try {
                                                refreshedtoken = FirebaseInstanceId.getInstance().getToken();
                                                myPreferences.setPreferences(MyPreferences.refreshedtoken, refreshedtoken);

                                                if (refreshedtoken == null || refreshedtoken.equals("")) {
                                                    refreshedtoken = FirebaseInstanceId.getInstance().getToken();
                                                    myPreferences.setPreferences(MyPreferences.refreshedtoken, refreshedtoken);
                                                    return;
                                                }

                                                if (GlobalElements.isConnectingToInternet(OtpVerificationActivity.this)) {
                                                    OtpVerify();
                                                } else {
                                                    GlobalElements.showDialog(OtpVerificationActivity.this);
                                                }
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    });

                            buildInfosDialog = alertDialog2.create();
                            buildInfosDialog.show();
                            return;
                        } else {
                            if (GlobalElements.isConnectingToInternet(OtpVerificationActivity.this)) {
                                OtpVerify();
                            } else {
                                GlobalElements.showDialog(OtpVerificationActivity.this);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        refreshedtoken = FirebaseInstanceId.getInstance().getToken();
                    }
                }
            }
        });

        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (GlobalElements.isConnectingToInternet(OtpVerificationActivity.this)) {
                    otp_code_edt.setText("");
                    ResendOtp();
                } else {
                    GlobalElements.showDialog(OtpVerificationActivity.this);
                }

            }
        });

        try {
            TelephonyManager tele = (TelephonyManager) getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = tele.getDeviceId();
            myPreferences.setPreferences(MyPreferences.imei, imei);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle b = intent.getExtras();
                    String sender = b.getString("sender");
                    if (sender.indexOf("" + RetrofitClient.message_pack_name) > 0) {
                        String message = b.getString("message");
                        otp_code_edt.setText("" + message.replaceAll("\\D+", ""));
                        abortBroadcast();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.setPriority(1);
            intentFilter.addAction("com.Nflicks.reciver.onMessageReceived");
            registerReceiver(receiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    private void OtpVerify() {
        try {
            pd = new ProgressDialog(OtpVerificationActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.OtpVerify(mobile_no, otp_code_edt.getText().toString(), imei, myPreferences.getPreferences(MyPreferences.refreshedtoken));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {

                            JSONObject result = json.getJSONObject("result");
                            myPreferences.setPreferences(MyPreferences.ID, result.getString("id"));

                            if (result.getString("status").equals("1")) {
                                Intent i = new Intent(OtpVerificationActivity.this, CategoryChooesActivity.class);
                                i.putExtra("type", "0");
                                startActivity(i);
                                finish();
                            } else if (result.getString("status").equals("0")) {
                                Intent i = new Intent(OtpVerificationActivity.this, RegistrationActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                myPreferences.setPreferences(MyPreferences.status, result.getString("status"));
                                if (!result.getString("status").equals("5")) {
                                    Intent i = new Intent(OtpVerificationActivity.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Intent i = new Intent(OtpVerificationActivity.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                }
                            }
                            startService(new Intent(OtpVerificationActivity.this, GetDetailService.class));
                        } else {
                            Toaster.show(OtpVerificationActivity.this, "" + json.getString("ack_msg"), true, Toaster.DANGER);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        pd.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    pd.dismiss();
                    System.out.print("error" + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ResendOtp() {
        try {
            pd = new ProgressDialog(OtpVerificationActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.ResendOtp(mobile_no);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            Toaster.show(OtpVerificationActivity.this, "" + json.getString("ack_msg"), true, Toaster.SUCCESS);
                        } else {
                            Toaster.show(OtpVerificationActivity.this, "" + json.getString("ack_msg"), true, Toaster.DANGER);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        pd.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    pd.dismiss();
                    System.out.print("error" + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}
