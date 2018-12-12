package com.Nflicks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.TypefaceSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.Nflicks.custom.FontFamily;
import com.Nflicks.custom.Toaster;
import com.Nflicks.custom.Validation;
import com.Nflicks.firabase.MyFirebaseMessagingService;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.typeface;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.LoginText)
    TextView LoginText;
    @BindView(R.id.LoginMobileNumberEdt)
    EditText LoginMobileNumberEdt;
    @BindView(R.id.LoginBtn)
    TextView LoginBtn;
    ProgressDialog pd;

    private FirebaseAnalytics mFirebaseAnalytics;
    MyPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        ButterKnife.bind(this);
        myPreferences = new MyPreferences(LoginActivity.this);

        try {
            if (GlobalElements.isConnectingToInternet(LoginActivity.this)) {
                // Analtics Add Event
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                // Create an ad request. TargetLocationCheck logcat output for the hashed device ID to
                // get test ads on a physical device. e.g.
                // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Jay Acharya");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
                startService(new Intent(this, MyFirebaseMessagingService.class));
                // Firebase Analyticvs end
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        LoginText.setTypeface(FontFamily.process(LoginActivity.this, R.raw.roboto_regular));
        LoginMobileNumberEdt.setTypeface(FontFamily.process(LoginActivity.this, R.raw.roboto_regular));
        LoginBtn.setTypeface(FontFamily.process(LoginActivity.this, R.raw.sqaure721), Typeface.BOLD);

        LoginMobileNumberEdt.setOnKeyListener(new View.OnKeyListener() {
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

        LoginMobileNumberEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    LoginMobileNumberEdt.setTypeface(FontFamily.process(LoginActivity.this, R.raw.roboto_bold));
                } else {
                    LoginMobileNumberEdt.setTypeface(FontFamily.process(LoginActivity.this, R.raw.roboto_regular));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Validation.isValid(Validation.MOBILE, LoginMobileNumberEdt.getText().toString())) {
                    Toaster.show(LoginActivity.this, "" + getResources().getString(R.string.mobile_blank), false, Toaster.DANGER);
                } else {
                    String refreshedtoken = FirebaseInstanceId.getInstance().getToken();
                    myPreferences.setPreferences(MyPreferences.refreshedtoken, refreshedtoken);
                    LoginUser();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
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


    private void LoginUser() {
        try {
            pd = new ProgressDialog(LoginActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.LoginUser(LoginMobileNumberEdt.getText().toString());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {

                            Intent i = new Intent(LoginActivity.this, OtpVerificationActivity.class);
                            i.putExtra("mobile_no", "" + LoginMobileNumberEdt.getText().toString());
                            i.putExtra("status", "" + json.getString("status"));
                            startActivity(i);
                        } else {
                            Toaster.show(LoginActivity.this, "" + json.getString("ack_msg"), true, Toaster.DANGER);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
}
