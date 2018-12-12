package com.Nflicks;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nflicks.custom.CustomEditText;
import com.Nflicks.custom.FlowLayout;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.custom.Toaster;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelUrlAvailablityActivity extends AppCompatActivity {

    @BindView(R.id.main_title)
    TextView mainTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.channel_desc_main_linear)
    LinearLayout main_linear;
    @BindView(R.id.channel_list_back)
    ImageView back;
    @BindView(R.id.url_availablity_channel_name)
    EditText channel_name_txt;
    @BindView(R.id.url_availablity_option)
    FlowLayout option_layout;
    @BindView(R.id.url_availablity_facebook)
    ImageView facebook;
    @BindView(R.id.url_availablity_google_plus)
    ImageView google_plus;
    @BindView(R.id.url_availablity_twitter)
    ImageView twitter;
    @BindView(R.id.url_availablity_whatsapp)
    ImageView whatsapp;
    @BindView(R.id.url_availablity_share)
    ImageView share;
    @BindView(R.id.url_availablity_update)
    TextView update;
    @BindView(R.id.url_availablity_finish)
    TextView finish_txt;
    @BindView(R.id.url_availablity_sharable_url)
    TextView sharable_url_txt;
    @BindView(R.id.available_url)
    TextView available_url;

    MyPreferences myPreferences;
    String channel_id, sharable_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_url_availablity);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        ButterKnife.bind(ChannelUrlAvailablityActivity.this);
        setSupportActionBar(toolbar);
        myPreferences = new MyPreferences(ChannelUrlAvailablityActivity.this);
        GlobalElements.overrideFonts_roboto_regular(ChannelUrlAvailablityActivity.this, main_linear);
        mainTitle.setTypeface(FontFamily.process(ChannelUrlAvailablityActivity.this, R.raw.sqaure721), Typeface.BOLD);

        try {
            Intent i = getIntent();
            channel_name_txt.setText("" + i.getStringExtra("channel_name"));
            channel_id = i.getStringExtra("channel_id");
            sharable_url_txt.setText("" + myPreferences.getPreferences(MyPreferences.sharable_url));
            sharable_url = myPreferences.getPreferences(MyPreferences.sharable_url) + "" + i.getStringExtra("channel_name");
        } catch (Exception e) {
            e.printStackTrace();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                Intent intent = new Intent();
                intent.putExtra("type", "2");
                intent.putExtra("channel_id", channel_id);
                setResult(250, intent);
                finish();
            }
        });

        update.setBackground(GlobalElements.RoundedButtion_White(ChannelUrlAvailablityActivity.this));
        finish_txt.setBackground(GlobalElements.RoundedButtion_White(ChannelUrlAvailablityActivity.this));

        finish_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("type", "0");
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
                    if (intent != null) {
                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, sharable_url);
                        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        shareIntent.setPackage("com.facebook.katana");
                        startActivityForResult(shareIntent, 10);
                    } else {
                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        share.putExtra(Intent.EXTRA_TEXT, "" + sharable_url);
                        startActivityForResult(Intent.createChooser(share, "Share link!"), 10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET); // other option FLAG_ACTIVITY_NEW_DOCUMENT
                    share.putExtra(Intent.EXTRA_TEXT, "" + sharable_url);
                    startActivityForResult(Intent.createChooser(share, "Share link!"), 10);
                }
            }
        });

        google_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = ShareCompat.IntentBuilder.from(ChannelUrlAvailablityActivity.this).getIntent();
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, sharable_url);
                    shareIntent.setPackage("com.google.android.apps.plus");
                    shareIntent.setAction(Intent.ACTION_SEND);
                    startActivityForResult(shareIntent, 10);
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    share.putExtra(Intent.EXTRA_TEXT, "" + sharable_url);
                    startActivityForResult(Intent.createChooser(share, "Share link!"), 10);
                }
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                tweetIntent.putExtra(Intent.EXTRA_TEXT, sharable_url);
                tweetIntent.setType("text/plain");
                PackageManager packManager = getPackageManager();
                List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);
                boolean resolved = false;
                for (ResolveInfo resolveInfo : resolvedInfoList) {
                    if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                        tweetIntent.setClassName(
                                resolveInfo.activityInfo.packageName,
                                resolveInfo.activityInfo.name);
                        resolved = true;
                        break;
                    }
                }
                if (resolved) {
                    startActivity(tweetIntent);
                } else {
                    Intent i = new Intent();
                    i.putExtra(Intent.EXTRA_TEXT, sharable_url);
                    i.setAction(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + urlEncode(sharable_url)));
                    startActivity(i);
                }
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    waIntent.setPackage("com.whatsapp");
                    if (waIntent != null) {
                        waIntent.putExtra(Intent.EXTRA_TEXT, sharable_url);
                        startActivityForResult(Intent.createChooser(waIntent, "Share with"), 10);
                    } else {
                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        share.putExtra(Intent.EXTRA_TEXT, "" + sharable_url);
                        startActivityForResult(Intent.createChooser(share, "Share link!"), 10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_TEXT, "" + sharable_url);
                startActivityForResult(Intent.createChooser(share, "Share link!"), 10);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalElements.isConnectingToInternet(ChannelUrlAvailablityActivity.this)) {
                    if (channel_name_txt.getText().toString().equals("")) {
                        Toaster.show(ChannelUrlAvailablityActivity.this, "" + getResources().getString(R.string.empty_url), false, Toaster.DANGER);
                    } else {
                        checkUrlAvailability();
                    }
                } else {
                    GlobalElements.showDialog(ChannelUrlAvailablityActivity.this);
                }
            }
        });
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        Intent intent = new Intent();
        intent.putExtra("type", "2");
        setResult(250, intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        Intent intent = new Intent();
        intent.putExtra("type", "2");
        setResult(250, intent);
        finish();
    }

    private void checkUrlAvailability() {
        try {

            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.Updatechannelurl(myPreferences.getPreferences(MyPreferences.ID), channel_id, channel_name_txt.getText().toString());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            Toaster.show(ChannelUrlAvailablityActivity.this, "" + json.getString("ack_msg"), false, Toaster.SUCCESS);
                            option_layout.removeAllViews();
                            option_layout.setVisibility(View.GONE);
                            available_url.setVisibility(View.GONE);
                            sharable_url = myPreferences.getPreferences(MyPreferences.sharable_url) + "" + channel_name_txt.getText().toString();
                        } else {
                            JSONArray suggestion = json.getJSONArray("suggestion");
                            available_url.setVisibility(View.VISIBLE);
                            option_layout.setVisibility(View.VISIBLE);
                            option_layout.removeAllViews();
                            for (int i = 0; i < suggestion.length(); i++) {
                                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final View addView = layoutInflater.inflate(R.layout.tag_view, null);
                                final CustomEditText tag = (CustomEditText) addView.findViewById(R.id.textout);
                                tag.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                tag.setFocusable(false);
                                tag.setClickable(true);
                                tag.setText("" + suggestion.get(i));
                                option_layout.addView(addView);
                                tag.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        channel_name_txt.setText("" + tag.getText().toString());
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
