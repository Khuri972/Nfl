package com.Nflicks;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nflicks.adapter.ChannelDescriptionAdapter;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.custom.ImageZoomHelper;
import com.Nflicks.custom.RecyclerViewPositionHelper;
import com.Nflicks.custom.Toaster;
import com.Nflicks.dialog.ChannelBottomSheetDialogFragment;
import com.Nflicks.dialog.FlickBottomSheetDialogFragment;
import com.Nflicks.model.ChannelDescriptionModel;
import com.Nflicks.model.UserChannelListModel;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelDescriptionActivity extends AppCompatActivity implements FlickBottomSheetDialogFragment.FlickUpdateIntercommunication, ChannelDescriptionAdapter.ChannelDescriptionNotifidata,
        FlickBottomSheetDialogFragment.Intercommunication, ChannelBottomSheetDialogFragment.ChannelDismissEvent {

    @BindView(R.id.main_title)
    TextView mainTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.channel_desc_main_linear)
    LinearLayout main_linear;
    @BindView(R.id.channel_desc_channel_detail)
    ImageView channel_detail_img;
    @BindView(R.id.channel_list_back)
    ImageView back;

    @BindView(R.id.channel_desc_recycleview)
    RecyclerView recyclerView;
    @BindView(R.id.channel_desc_empty)
    NestedScrollView empty;
    @BindView(R.id.channel_desc_empty_image)
    ImageView empty_image;
    @BindView(R.id.channel_desc_name)
    TextView channel_name;
    @BindView(R.id.channel_desc_img)
    CircleImageView channel_image;
    @BindView(R.id.channel_desc_follow_status)
    TextView follow_status;
    @BindView(R.id.channel_desc_share)
    ImageView share_url;
    @BindView(R.id.channel_desc_qr)
    ImageView qr_img;
    @BindView(R.id.channel_desc_follwer)
    TextView channel_follwer;
    BottomBar bottomBar;
    @BindView(R.id.update_channel)
    ImageView update_channel;

    ArrayList<ChannelDescriptionModel> data = new ArrayList<>();
    ChannelDescriptionAdapter adapter;

    //FollowingModel followingModel;
    private DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    MyPreferences myPreferences;
    AlertDialog buildInfosDialog;

    String channel_id, channel_detail = "", channel_contact = "", channel_address = "", channel_email = "", channel_website = "", channel_shareable_url = "", channel_qr_code = "", channel_privacy = "";
    String activity_type;
    int isFollowing = 0;
    UserChannelListModel userChannelListModel;

    /* load more */
    int firstVisibleItem, visibleItemCount, totalItemCount, count = 0;
    protected int m_PreviousTotalCount;
    RecyclerViewPositionHelper mRecyclerViewHelper;
    BroadcastReceiver receiver;
    ImageZoomHelper imageZoomHelper;
    ImageZoomHelper.OnZoomListener onZoomListener;
    BottomSheetDialogFragment bottomSheetDialogFragment;
    boolean temp_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_description);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        ButterKnife.bind(ChannelDescriptionActivity.this);
        setSupportActionBar(toolbar);
        myPreferences = new MyPreferences(ChannelDescriptionActivity.this);
        imageZoomHelper = new ImageZoomHelper(this);
        ImageZoomHelper.setZoom(recyclerView, true);

        if (myPreferences.getPreferences(MyPreferences.ID).equals("")) {
            Intent i = new Intent(getApplicationContext(), SplashActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }

        onZoomListener = new ImageZoomHelper.OnZoomListener() {
            @Override
            public void onImageZoomStarted(final View view) {

            }

            @Override
            public void onImageZoomEnded(View view) {

            }
        };

        GlobalElements.overrideFonts_roboto_regular(ChannelDescriptionActivity.this, main_linear);
        channel_name.setTypeface(FontFamily.process(ChannelDescriptionActivity.this, R.raw.roboto_regular), Typeface.BOLD);
        mainTitle.setTypeface(FontFamily.process(ChannelDescriptionActivity.this, R.raw.sqaure721), Typeface.BOLD);
        count = 0;
        m_PreviousTotalCount = 0;
        data.clear();
        try {
            Bundle b = getIntent().getExtras();
            channel_id = b.getString("channel_id");
            activity_type = b.getString("activity_type");
            if (activity_type.equals("0")) {
                channel_name.setText("" + b.getString("channel_name"));
                channel_follwer.setText("" + b.getString("channel_follower") + " " + getResources().getString(R.string.followers));

                channel_detail = b.getString("channel_details");
                channel_shareable_url = b.getString("channel_sharable_url");
                channel_qr_code = b.getString("channel_qr_code");
                channel_privacy = b.getString("channel_privacy");

                if (b.getString("channel_image_path").equals("")) {

                } else {
                    imageLoader.init(ImageLoaderConfiguration.createDefault(ChannelDescriptionActivity.this));
                    options = new DisplayImageOptions.Builder()
                            .showImageForEmptyUri(R.drawable.icon)
                            .showImageOnFail(R.drawable.icon)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();
                    imageLoader.displayImage(b.getString("channel_image_path"), channel_image, options);
                }

                try {
                    String edit_flag = b.getString("edit_flag");
                    if (edit_flag != null && edit_flag.equals("1")) {
                        userChannelListModel = (UserChannelListModel) b.getSerializable("data");
                        update_channel.setVisibility(View.VISIBLE);
                    } else {
                        update_channel.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (activity_type.equals("2")) {
                channel_name.setText("" + b.getString("channel_name"));
                channel_follwer.setText("" + b.getString("channel_follower") + " " + getResources().getString(R.string.followers));

                channel_detail = b.getString("channel_details");
                channel_shareable_url = b.getString("channel_sharable_url");
                channel_qr_code = b.getString("channel_qr_code");
                channel_privacy = b.getString("channel_privacy");

                if (b.getString("channel_image_path").equals("")) {

                } else {
                    imageLoader.init(ImageLoaderConfiguration.createDefault(ChannelDescriptionActivity.this));
                    options = new DisplayImageOptions.Builder()
                            .showImageForEmptyUri(R.drawable.icon)
                            .showImageOnFail(R.drawable.icon)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();
                    imageLoader.displayImage(b.getString("channel_image_path"), channel_image, options);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            activity_type = "0";
            channel_id = "";
        }

        try {
            Uri agent_data = getIntent().getData();
            if (agent_data != null) {
                // String scheme = agent_data.getScheme(); // "http"
                // String host = agent_data.getHost(); // "twitter.com"
                List<String> params = agent_data.getPathSegments();
                String url_name = params.get(0); // "url"
                if (channel_id.equals("")) {
                    if (GlobalElements.isConnectingToInternet(ChannelDescriptionActivity.this)) {
                        GetChainDetail(url_name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        share_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!channel_shareable_url.equals("")) {

                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(ChannelDescriptionActivity.this);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View dialogView = inflater.inflate(R.layout.dialog_share, null);

                    ImageView facebook = (ImageView) dialogView.findViewById(R.id.url_availablity_facebook);
                    ImageView google_plus = (ImageView) dialogView.findViewById(R.id.url_availablity_google_plus);
                    ImageView twitter = (ImageView) dialogView.findViewById(R.id.url_availablity_twitter);
                    ImageView whatsapp = (ImageView) dialogView.findViewById(R.id.url_availablity_whatsapp);
                    ImageView share = (ImageView) dialogView.findViewById(R.id.url_availablity_share);
                    TextView copy_url = (TextView) dialogView.findViewById(R.id.url_availablity_copy);
                    TextView sharable_url = (TextView) dialogView.findViewById(R.id.url_availablity_sharable_url);

                    copy_url.setBackground(GlobalElements.RoundedButtion_White(ChannelDescriptionActivity.this));

                    sharable_url.setText("" + myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url);
                    copy_url.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboard.setText("" + myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url);
                                Toaster.show(ChannelDescriptionActivity.this, "Copy " + myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url, false, Toaster.SUCCESS);
                            } else {
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Copied Text", "" + myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url);
                                clipboard.setPrimaryClip(clip);
                                Toaster.show(ChannelDescriptionActivity.this, "Copy " + myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url, false, Toaster.SUCCESS);
                            }
                        }
                    });

                    try {
                        facebook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
                                    if (intent != null) {
                                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                        shareIntent.setType("text/plain");
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url);
                                        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        shareIntent.setPackage("com.facebook.katana");
                                        startActivityForResult(shareIntent, 10);
                                        buildInfosDialog.dismiss();
                                    } else {
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("text/plain");
                                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                        share.putExtra(Intent.EXTRA_TEXT, "" + myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url);
                                        startActivityForResult(Intent.createChooser(share, "Share link!"), 10);
                                        buildInfosDialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("text/plain");
                                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                    share.putExtra(Intent.EXTRA_TEXT, "" + myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url);
                                    startActivityForResult(Intent.createChooser(share, "Share link!"), 10);
                                    buildInfosDialog.dismiss();
                                }
                            }
                        });

                        google_plus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    Intent shareIntent = ShareCompat.IntentBuilder.from(ChannelDescriptionActivity.this).getIntent();
                                    shareIntent.setType("text/plain");
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url);
                                    shareIntent.setPackage("com.google.android.apps.plus");
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    startActivityForResult(shareIntent, 10);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("text/plain");
                                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                    share.putExtra(Intent.EXTRA_TEXT, "" + myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url);
                                    startActivityForResult(Intent.createChooser(share, "Share link!"), 10);
                                    buildInfosDialog.dismiss();

                                }
                            }
                        });

                        twitter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                                tweetIntent.putExtra(Intent.EXTRA_TEXT, myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url);
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
                                    i.putExtra(Intent.EXTRA_TEXT, myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url);
                                    i.setAction(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + urlEncode(myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url)));
                                    startActivity(i);
                                    buildInfosDialog.dismiss();
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
                                        waIntent.putExtra(Intent.EXTRA_TEXT, myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url);
                                        startActivityForResult(Intent.createChooser(waIntent, "Share with"), 10);
                                        buildInfosDialog.dismiss();
                                    } else {
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("text/plain");
                                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                        share.putExtra(Intent.EXTRA_TEXT, "" + myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url);
                                        startActivityForResult(Intent.createChooser(share, "Share link!"), 10);
                                        buildInfosDialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        share.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("text/plain");
                                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                share.putExtra(Intent.EXTRA_TEXT, "" + myPreferences.getPreferences(MyPreferences.sharable_url) + channel_shareable_url);
                                startActivityForResult(Intent.createChooser(share, "Share link!"), 10);
                                buildInfosDialog.dismiss();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    alertDialog2.setView(dialogView);
                    buildInfosDialog = alertDialog2.create();
                    buildInfosDialog.show();
                }
            }
        });
        qr_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(ChannelDescriptionActivity.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.dialog_qr, null);
                ImageView img = (ImageView) dialogView.findViewById(R.id.dialog_qr_image);
                imageLoader.init(ImageLoaderConfiguration.createDefault(ChannelDescriptionActivity.this));
                options = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.icon)
                        .showImageOnFail(R.drawable.icon)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build();
                imageLoader.displayImage(channel_qr_code, img, options);

                alertDialog2.setView(dialogView);
                buildInfosDialog = alertDialog2.create();
                buildInfosDialog.show();
            }
        });

        update_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i1 = new Intent(ChannelDescriptionActivity.this, CreateChannelActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("data", userChannelListModel);
                    i1.putExtra("type", "1");
                    i1.putExtras(b);
                    startActivityForResult(i1, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /* change follow status button */
        follow_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFollowing == 0) {
                    if (GlobalElements.isConnectingToInternet(ChannelDescriptionActivity.this)) {

                        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(ChannelDescriptionActivity.this);
                        LayoutInflater inflater = (LayoutInflater) ChannelDescriptionActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View dialogView = inflater.inflate(R.layout.dialog_flick_more, null);

                        GradientDrawable gd = new GradientDrawable();
                        gd.setColor(Color.WHITE);
                        gd.setCornerRadius(50);
                        dialogView.setBackground(gd);

                        LinearLayout part_1 = (LinearLayout) dialogView.findViewById(R.id.dialog_flick_part_1);
                        LinearLayout part_2 = (LinearLayout) dialogView.findViewById(R.id.dialog_flick_part_2);
                        LinearLayout urgent = (LinearLayout) dialogView.findViewById(R.id.dialog_flick_urgent_linear);
                        LinearLayout important = (LinearLayout) dialogView.findViewById(R.id.dialog_flick_important_linear);
                        GlobalElements.overrideFonts_roboto_regular(ChannelDescriptionActivity.this, part_2);

                        part_1.setVisibility(View.GONE);
                        part_2.setVisibility(View.VISIBLE);

                        urgent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Channel_Follow_Unfollow("follow", "urgent");
                                buildInfosDialog.dismiss();
                            }
                        });

                        important.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Channel_Follow_Unfollow("follow", "important");
                                buildInfosDialog.dismiss();
                            }
                        });
                        alertDialog2.setView(dialogView);
                        buildInfosDialog = alertDialog2.create();
                        buildInfosDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        buildInfosDialog.show();

                    } else {
                        GlobalElements.showDialog(ChannelDescriptionActivity.this);
                    }
                } else if (isFollowing == 1) {
                    if (GlobalElements.isConnectingToInternet(ChannelDescriptionActivity.this)) {
                        Channel_Follow_Unfollow("unfollow", "");
                    } else {
                        GlobalElements.showDialog(ChannelDescriptionActivity.this);
                    }
                } else if (isFollowing == 2) {
                    if (GlobalElements.isConnectingToInternet(ChannelDescriptionActivity.this)) {
                        Channel_Follow_Unfollow("cancel_request", "");
                    } else {
                        GlobalElements.showDialog(ChannelDescriptionActivity.this);
                    }
                }
            }
        });
        /* */

        channel_detail_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    final Dialog d = new Dialog(ChannelDescriptionActivity.this);
                    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    d.setContentView(R.layout.dialog_channel_detail);
                    WindowManager wm = (WindowManager) ChannelDescriptionActivity.this.getSystemService(Context.WINDOW_SERVICE); // for activity use context instead of getActivity()
                    Display display = wm.getDefaultDisplay(); // getting the screen size of device
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x - 10;  // Set your heights
                    int height = size.y - 90; // set your widths
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(d.getWindow().getAttributes());
                    lp.width = width;
                    lp.height = height;
                    d.getWindow().setAttributes(lp);

                    ImageView back_detail = (ImageView) d.findViewById(R.id.dialog_channel_detail_back);
                    TextView detail = (TextView) d.findViewById(R.id.dialog_channel_detail);
                    TextView contact = (TextView) d.findViewById(R.id.dialog_channel_contact);
                    TextView address = (TextView) d.findViewById(R.id.dialog_channel_address);
                    TextView email = (TextView) d.findViewById(R.id.dialog_channel_email);
                    TextView website = (TextView) d.findViewById(R.id.dialog_channel_website);
                    TextView priviacy = (TextView) d.findViewById(R.id.dialog_channel_priviacy);

                    LinearLayout layout_detail = (LinearLayout) d.findViewById(R.id.layout_detail);
                    LinearLayout layout_contact = (LinearLayout) d.findViewById(R.id.layout_contact);
                    LinearLayout layout_address = (LinearLayout) d.findViewById(R.id.layout_address);
                    LinearLayout layout_email = (LinearLayout) d.findViewById(R.id.layout_email);
                    LinearLayout layout_website = (LinearLayout) d.findViewById(R.id.layout_website);

                    detail.setText("" + channel_detail);

                    if (channel_contact.equals("")) {
                        layout_contact.setVisibility(View.GONE);
                    }

                    if (channel_address.equals("")) {
                        layout_address.setVisibility(View.GONE);
                    }

                    if (channel_email.equals("")) {
                        layout_email.setVisibility(View.GONE);
                    }

                    if (channel_website.equals("")) {
                        layout_website.setVisibility(View.GONE);
                    }

                    contact.setText("" + channel_contact);
                    address.setText("" + channel_address);
                    email.setText("" + channel_email);
                    website.setText("" + channel_website);
                    priviacy.setText("" + channel_privacy);

                    back_detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            d.dismiss();
                        }
                    });

                    d.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (GlobalElements.isConnectingToInternet(ChannelDescriptionActivity.this)) {
            if (!channel_id.equals("")) {
                count = 0;
                m_PreviousTotalCount = 0;
                data.clear();
                GetFlick();
                GetUserChannel();
            }
        } else {
            GlobalElements.showDialog(ChannelDescriptionActivity.this);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity_type.equals("2")) {
                    Intent intent = new Intent();
                    setResult(120, intent);
                    finish();
                } else {
                    Intent intent = new Intent();
                    Bundle b = new Bundle();
                    b.putInt("position", 0);
                    b.putInt("position", isFollowing);
                    intent.putExtras(b);
                    setResult(1, intent);
                    finish();
                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mRecyclerViewHelper.getItemCount();
                firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();

                if (totalItemCount == 0 || adapter == null)
                    return;
                if (m_PreviousTotalCount == totalItemCount) {
                    return;
                } else {
                    boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                    if (loadMore) {
                        m_PreviousTotalCount = totalItemCount;
                        if (GlobalElements.isConnectingToInternet(ChannelDescriptionActivity.this)) {
                            GetFlick();
                        } else {
                            //GlobalElements.showDialog(ChannelDescriptionActivity.this);
                        }
                    }
                }
            }
        });

        try {
            temp_flag = false;
            bottomSheetDialogFragment = new ChannelBottomSheetDialogFragment();
            GlobalElements.Flick_bottomSheetDialogFragment = new FlickBottomSheetDialogFragment();
            bottomBar = (BottomBar) findViewById(R.id.bottomBar);
            bottomBar.selectTabAtPosition(2);

            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(@IdRes int tabId) {
                    try {
                        if (tabId == R.id.tab_main) {
                            Intent i = new Intent(ChannelDescriptionActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else if (tabId == R.id.tab_search) {
                            Intent i = new Intent(ChannelDescriptionActivity.this, SearchActivity.class);
                            startActivityForResult(i, 0);
                        }
                        if (tabId == R.id.tab_create_channel) {
                            if (temp_flag) {
                                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                            } else {
                                temp_flag = true;
                            }
                        } else if (tabId == R.id.tab_qrcode) {
                            Intent i = new Intent(ChannelDescriptionActivity.this, QrCodeActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_profile) {
                            Intent i = new Intent(ChannelDescriptionActivity.this, ProfileActivity.class);
                            startActivityForResult(i, 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
                @Override
                public void onTabReSelected(@IdRes int tabId) {
                    try {
                        if (tabId == R.id.tab_main) {
                            Intent i = new Intent(ChannelDescriptionActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else if (tabId == R.id.tab_search) {
                            Intent i = new Intent(ChannelDescriptionActivity.this, SearchActivity.class);
                            startActivityForResult(i, 0);
                        }
                        if (tabId == R.id.tab_create_channel) {
                            if (temp_flag) {
                                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                            } else {
                                temp_flag = true;
                            }
                        } else if (tabId == R.id.tab_qrcode) {
                            Intent i = new Intent(ChannelDescriptionActivity.this, QrCodeActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_profile) {
                            Intent i = new Intent(ChannelDescriptionActivity.this, ProfileActivity.class);
                            startActivityForResult(i, 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (activity_type.equals("2")) {
            Intent intent = new Intent();
            setResult(120, intent);
            finish();
        } else {
            Intent intent = new Intent();
            Bundle b = new Bundle();
            b.putInt("position", 0);
            b.putInt("position", isFollowing);
            intent.putExtras(b);
            setResult(1, intent);
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle b = intent.getExtras();
                    String sender = b.getString("channel_id");
                    if (sender.equals("" + channel_id)) {
                        if (GlobalElements.isConnectingToInternet(ChannelDescriptionActivity.this)) {
                            count = 0;
                            m_PreviousTotalCount = 0;
                            data.clear();
                            GetFlick();
                        }
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.setPriority(1);
            intentFilter.addAction("com.Notiflick.onMessageReceived");
            registerReceiver(receiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageZoomHelper.addOnZoomListener(onZoomListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 234) // todo FlickBottomSheetDialogFragment file result code
        {
            GlobalElements.Flick_bottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == 23) // todo  FlickBottomSheetDialogFragment image result code
        {
            GlobalElements.Flick_bottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode == 350) {
            bottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode == 120) {  // todo qrcodeActivity finish result code
            temp_flag = false;
            bottomBar.selectTabAtPosition(2);
        } else if (resultCode == 450) {  // todo channel update
            try {
                userChannelListModel = (UserChannelListModel) data.getSerializableExtra("data");
                channel_name.setText("" + userChannelListModel.getName());
                channel_follwer.setText("" + userChannelListModel.getFollower() + " " + getResources().getString(R.string.followers));

                channel_detail = userChannelListModel.getDetail();
                channel_shareable_url = userChannelListModel.getShareable_url();
                channel_qr_code = userChannelListModel.getQr_code_path();
                channel_privacy = userChannelListModel.getChannel_privacy();

                if (userChannelListModel.getImage_path().equals("")) {

                } else {
                    imageLoader.init(ImageLoaderConfiguration.createDefault(ChannelDescriptionActivity.this));
                    options = new DisplayImageOptions.Builder()
                            .showImageForEmptyUri(R.drawable.icon)
                            .showImageOnFail(R.drawable.icon)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();
                    imageLoader.displayImage(userChannelListModel.getImage_path(), channel_image, options);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    private void GetFlick() {
        try {
            final ProgressDialog pd = new ProgressDialog(ChannelDescriptionActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.getFlick(myPreferences.getPreferences(MyPreferences.ID), channel_id, count, GlobalElements.ll);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);

                        JSONObject channel_detail_obj = json.getJSONObject("channel_detail");
                        if (json.getInt("ack") == 1) {

                            try {
                                /* todo get channel user detail */
                                isFollowing = channel_detail_obj.getInt("isFollowing");
                                channel_detail = channel_detail_obj.getString("details");
                                channel_contact = channel_detail_obj.getString("contact_no");
                                channel_address = channel_detail_obj.getString("address");
                                channel_email = channel_detail_obj.getString("email");
                                channel_website = channel_detail_obj.getString("website");
                                channel_shareable_url = channel_detail_obj.getString("shareable_url");
                                channel_qr_code = channel_detail_obj.getString("qr_code_path");
                                channel_privacy = channel_detail_obj.getString("channel_privacy");

                                channel_name.setText("" + channel_detail_obj.getString("channel_name"));
                                channel_follwer.setText("" + channel_detail_obj.getString("follower") + " " + getResources().getString(R.string.followers));
                                if (channel_detail_obj.getString("image_path").equals("")) {

                                } else {
                                    imageLoader.init(ImageLoaderConfiguration.createDefault(ChannelDescriptionActivity.this));
                                    options = new DisplayImageOptions.Builder()
                                            .showImageForEmptyUri(R.drawable.icon)
                                            .showImageOnFail(R.drawable.icon)
                                            .cacheInMemory(true)
                                            .cacheOnDisk(true)
                                            .build();
                                    imageLoader.displayImage(channel_detail_obj.getString("image_path"), channel_image, options);
                                }

                                if (isFollowing == 0) {
                                    follow_status.setText("" + getResources().getString(R.string.follow));
                                    follow_status.setTextColor(ContextCompat.getColor(ChannelDescriptionActivity.this, R.color.white));
                                    follow_status.setBackground(GlobalElements.FollowButtion(ChannelDescriptionActivity.this));
                                } else if (isFollowing == 1) {
                                    follow_status.setText("" + getResources().getString(R.string.unfollow));
                                    follow_status.setTextColor(ContextCompat.getColor(ChannelDescriptionActivity.this, R.color.textcolor));
                                    follow_status.setBackground(GlobalElements.UnFollowButtion(ChannelDescriptionActivity.this));
                                } else if (isFollowing == 2) {
                                    follow_status.setText("" + getResources().getString(R.string.requested));
                                    follow_status.setTextColor(ContextCompat.getColor(ChannelDescriptionActivity.this, R.color.textcolor));
                                    follow_status.setBackground(GlobalElements.UnFollowButtion(ChannelDescriptionActivity.this));
                                }

                                if (!myPreferences.getPreferences(MyPreferences.ID).equals("" + channel_detail_obj.getString("user_id"))) {
                                    follow_status.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            /* todo  start get flick detail*/
                            JSONArray result = json.getJSONArray("result");
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject c = result.getJSONObject(i);
                                ChannelDescriptionModel da = new ChannelDescriptionModel();
                                da.setId(c.getString("id"));
                                da.setTitle(c.getString("flick_name"));
                                da.setImage(c.getString("image_path"));
                                da.setDetail(c.getString("details"));
                                da.setShareable_url(c.getString("shareable_url"));
                                da.setFile_path(c.getString("file_path"));
                                da.setIsBookmark(c.getInt("isBookmarked"));
                                da.setChannel_owner_id(channel_detail_obj.getString("user_id"));
                                da.setIsSpamed(c.getInt("isSpamed"));
                                da.setIsInAppropriate(c.getInt("isInAppropriate"));
                                da.setCreated_date(c.getString("created_date"));
                                da.setModified_date(c.getString("modified_date"));
                                da.setCreated_date_string(c.getString("created_date_string"));
                                da.setChannel_name(channel_detail_obj.getString("channel_name"));
                                da.setC_follower(channel_detail_obj.getString("follower"));
                                da.setChannel_image_path(channel_detail_obj.getString("image_path"));
                                if (c.getInt("isSpamed") == 0 && c.getInt("isInAppropriate") == 0) {
                                    data.add(da);
                                }
                            }

                            if (channel_privacy.equals("private") && isFollowing == 0) {
                                channel_detail_img.setVisibility(View.INVISIBLE);
                            } else if (channel_privacy.equals("private") && isFollowing == 2) {
                                channel_detail_img.setVisibility(View.INVISIBLE);
                            } else {
                                channel_detail_img.setVisibility(View.VISIBLE);
                            }


                            /* todo load more recycleview code */
                            if (count == 0) {
                                count += result.length();
                                if (channel_privacy.equals("private") && isFollowing == 1) {

                                    if (data.isEmpty()) {
                                        recyclerView.setVisibility(View.GONE);
                                        empty.setVisibility(View.VISIBLE);
                                    } else {
                                        adapter = new ChannelDescriptionAdapter(ChannelDescriptionActivity.this, data);
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(ChannelDescriptionActivity.this, LinearLayout.VERTICAL, false));
                                        recyclerView.setVisibility(View.VISIBLE);
                                        empty.setVisibility(View.GONE);
                                    }
                                } else if (channel_privacy.equals("public")) {
                                    if (data.isEmpty()) {
                                        recyclerView.setVisibility(View.GONE);
                                        empty.setVisibility(View.VISIBLE);
                                    } else {
                                        adapter = new ChannelDescriptionAdapter(ChannelDescriptionActivity.this, data);
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(ChannelDescriptionActivity.this, LinearLayout.VERTICAL, false));
                                        recyclerView.setVisibility(View.VISIBLE);
                                        empty.setVisibility(View.GONE);
                                    }
                                } else if (channel_privacy.equals("private") && isFollowing == 0) {
                                    if (channel_detail_obj.getString("user_id").equals(myPreferences.getPreferences(MyPreferences.ID))) {
                                        if (data.isEmpty()) {
                                            recyclerView.setVisibility(View.GONE);
                                            empty.setVisibility(View.VISIBLE);
                                        } else {
                                            adapter = new ChannelDescriptionAdapter(ChannelDescriptionActivity.this, data);
                                            recyclerView.setAdapter(adapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(ChannelDescriptionActivity.this, LinearLayout.VERTICAL, false));
                                            recyclerView.setVisibility(View.VISIBLE);
                                            empty.setVisibility(View.GONE);
                                            empty_image.setImageResource(R.drawable.empty_private);
                                        }
                                    } else {
                                        if (data.isEmpty()) {
                                            recyclerView.setVisibility(View.GONE);
                                            empty.setVisibility(View.VISIBLE);
                                        } else {
                                            adapter = new ChannelDescriptionAdapter(ChannelDescriptionActivity.this, data);
                                            recyclerView.setAdapter(adapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(ChannelDescriptionActivity.this, LinearLayout.VERTICAL, false));
                                            recyclerView.setVisibility(View.GONE);
                                            empty.setVisibility(View.VISIBLE);
                                            empty_image.setImageResource(R.drawable.empty_private);
                                        }
                                    }
                                } else {
                                    recyclerView.setVisibility(View.GONE);
                                    empty.setVisibility(View.VISIBLE);
                                }
                            } else {

                                count += result.length();
                                adapter.notifyDataSetChanged();

                                recyclerView.setVisibility(View.VISIBLE);
                                empty.setVisibility(View.GONE);
                            }

                        } else {

                            /* todo get channel detail*/
                            isFollowing = channel_detail_obj.getInt("isFollowing");
                            channel_detail = channel_detail_obj.getString("details");
                            channel_contact = channel_detail_obj.getString("contact_no");
                            channel_address = channel_detail_obj.getString("address");
                            channel_email = channel_detail_obj.getString("email");
                            channel_website = channel_detail_obj.getString("website");
                            channel_shareable_url = channel_detail_obj.getString("shareable_url");
                            channel_qr_code = channel_detail_obj.getString("qr_code_path");
                            channel_privacy = channel_detail_obj.getString("channel_privacy");

                            channel_name.setText("" + channel_detail_obj.getString("channel_name"));
                            channel_follwer.setText("" + channel_detail_obj.getString("follower") + " " + getResources().getString(R.string.followers));

                            if (channel_detail_obj.getString("image_path").equals("")) {

                            } else {
                                imageLoader.init(ImageLoaderConfiguration.createDefault(ChannelDescriptionActivity.this));
                                options = new DisplayImageOptions.Builder()
                                        .showImageForEmptyUri(R.drawable.icon)
                                        .showImageOnFail(R.drawable.icon)
                                        .cacheInMemory(true)
                                        .cacheOnDisk(true)
                                        .build();
                                imageLoader.displayImage(channel_detail_obj.getString("image_path"), channel_image, options);
                            }

                            if (channel_privacy.equals("private") && isFollowing == 0) {
                                channel_detail_img.setVisibility(View.INVISIBLE);
                            } else if (channel_privacy.equals("private") && isFollowing == 2) {
                                channel_detail_img.setVisibility(View.INVISIBLE);
                            } else {
                                channel_detail_img.setVisibility(View.VISIBLE);
                            }

                            if (isFollowing == 0) {
                                follow_status.setText("" + getResources().getString(R.string.follow));
                                follow_status.setTextColor(ContextCompat.getColor(ChannelDescriptionActivity.this, R.color.white));
                                follow_status.setBackground(GlobalElements.FollowButtion(ChannelDescriptionActivity.this));
                            } else if (isFollowing == 1) {
                                follow_status.setText("" + getResources().getString(R.string.unfollow));
                                follow_status.setTextColor(ContextCompat.getColor(ChannelDescriptionActivity.this, R.color.textcolor));
                                follow_status.setBackground(GlobalElements.UnFollowButtion(ChannelDescriptionActivity.this));
                            } else if (isFollowing == 2) {
                                follow_status.setText("" + getResources().getString(R.string.requested));
                                follow_status.setTextColor(ContextCompat.getColor(ChannelDescriptionActivity.this, R.color.textcolor));
                                follow_status.setBackground(GlobalElements.UnFollowButtion(ChannelDescriptionActivity.this));
                            }

                            if (!myPreferences.getPreferences(MyPreferences.ID).equals("" + channel_detail_obj.getString("user_id"))) {
                                follow_status.setVisibility(View.VISIBLE);
                            }

                            if (count == 0) {
                                if (channel_privacy.equals("private") && isFollowing == 0) {
                                    if (channel_detail_obj.getString("user_id").equals(myPreferences.getPreferences(MyPreferences.ID))) {
                                        if (data.isEmpty()) {
                                            recyclerView.setVisibility(View.GONE);
                                            empty.setVisibility(View.VISIBLE);

                                        } else {
                                            adapter = new ChannelDescriptionAdapter(ChannelDescriptionActivity.this, data);
                                            recyclerView.setAdapter(adapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(ChannelDescriptionActivity.this, LinearLayout.VERTICAL, false));
                                            recyclerView.setVisibility(View.VISIBLE);
                                            empty.setVisibility(View.GONE);
                                            empty_image.setImageResource(R.drawable.empty_private);
                                        }
                                    } else {
                                        if (data.isEmpty()) {
                                            recyclerView.setVisibility(View.GONE);
                                            empty.setVisibility(View.VISIBLE);
                                            empty_image.setImageResource(R.drawable.empty_private);
                                        } else {
                                            adapter = new ChannelDescriptionAdapter(ChannelDescriptionActivity.this, data);
                                            recyclerView.setAdapter(adapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(ChannelDescriptionActivity.this, LinearLayout.VERTICAL, false));
                                            recyclerView.setVisibility(View.GONE);
                                            empty.setVisibility(View.VISIBLE);
                                            empty_image.setImageResource(R.drawable.empty_private);
                                        }
                                    }
                                } else {
                                    if (data.isEmpty()) {
                                        recyclerView.setVisibility(View.GONE);
                                        empty.setVisibility(View.VISIBLE);
                                    } else {
                                        adapter = new ChannelDescriptionAdapter(ChannelDescriptionActivity.this, data);
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(ChannelDescriptionActivity.this, LinearLayout.VERTICAL, false));
                                        recyclerView.setVisibility(View.GONE);
                                        empty.setVisibility(View.VISIBLE);
                                        empty_image.setImageResource(R.drawable.empty_private);
                                    }
                                }
                            } else {
                                if (!data.isEmpty()) {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    empty.setVisibility(View.GONE);
                                }
                            }
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

    private void GetChainDetail(String url) {
        try {
            final ProgressDialog pd = new ProgressDialog(ChannelDescriptionActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.getChannelId(myPreferences.getPreferences(MyPreferences.ID), url);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            JSONObject result = json.getJSONObject("result");
                            channel_id = result.getString("id");
                            count = 0;
                            m_PreviousTotalCount = 0;
                            data.clear();
                            GetFlick();
                        } else {
                            Intent i = new Intent(getApplicationContext(), SplashActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return imageZoomHelper.onDispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    private void Channel_Follow_Unfollow(final String flag, String priority) {
        try {
            final ProgressDialog pd = new ProgressDialog(ChannelDescriptionActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.channel_Follow_Unfollow(myPreferences.getPreferences(MyPreferences.ID), channel_id, flag, priority);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            JSONObject channel_detail = json.getJSONObject("channel_detail");
                            isFollowing = channel_detail.getInt("isFollowing");
                            GlobalElements.on_resume_flag = 102;
                            GlobalElements.follow_unfollow_flag = isFollowing;

                            if (isFollowing == 0) {
                                follow_status.setText("" + getResources().getString(R.string.follow));
                                follow_status.setTextColor(ContextCompat.getColor(ChannelDescriptionActivity.this, R.color.white));
                                follow_status.setBackground(GlobalElements.FollowButtion(ChannelDescriptionActivity.this));
                            } else if (isFollowing == 1) {
                                follow_status.setText("" + getResources().getString(R.string.unfollow));
                                follow_status.setTextColor(ContextCompat.getColor(ChannelDescriptionActivity.this, R.color.textcolor));
                                follow_status.setBackground(GlobalElements.UnFollowButtion(ChannelDescriptionActivity.this));
                            } else if (isFollowing == 2) {
                                follow_status.setText("" + getResources().getString(R.string.requested));
                                follow_status.setTextColor(ContextCompat.getColor(ChannelDescriptionActivity.this, R.color.textcolor));
                                follow_status.setBackground(GlobalElements.UnFollowButtion(ChannelDescriptionActivity.this));
                            }
                            channel_follwer.setText("" + channel_detail.getString("follower") + " " + getResources().getString(R.string.followers));
                        } else {
                            Toaster.show(ChannelDescriptionActivity.this, "" + json.getString("ack_msg"), true, Toaster.DANGER);
                        }

                        if (channel_privacy.equals("private") && isFollowing == 0) {
                            channel_detail_img.setVisibility(View.INVISIBLE);
                        } else if (channel_privacy.equals("private") && isFollowing == 2) {
                            channel_detail_img.setVisibility(View.INVISIBLE);
                        } else {
                            channel_detail_img.setVisibility(View.VISIBLE);
                        }

                        if (channel_privacy.equals("private") && isFollowing == 1) {
                            if (data.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                                empty.setVisibility(View.VISIBLE);
                                empty_image.setImageResource(R.drawable.empty_private);
                            } else {
                                adapter = new ChannelDescriptionAdapter(ChannelDescriptionActivity.this, data);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ChannelDescriptionActivity.this, LinearLayout.VERTICAL, false));
                                recyclerView.setVisibility(View.VISIBLE);
                                empty.setVisibility(View.GONE);
                            }
                        } else if (channel_privacy.equals("public")) {
                            if (data.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                                empty.setVisibility(View.VISIBLE);
                            } else {
                                adapter = new ChannelDescriptionAdapter(ChannelDescriptionActivity.this, data);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ChannelDescriptionActivity.this, LinearLayout.VERTICAL, false));
                                recyclerView.setVisibility(View.VISIBLE);
                                empty.setVisibility(View.GONE);
                            }
                        } else if (channel_privacy.equals("private") && isFollowing == 0) {
                            recyclerView.setVisibility(View.GONE);
                            empty.setVisibility(View.VISIBLE);
                            empty_image.setImageResource(R.drawable.empty_private);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            empty.setVisibility(View.VISIBLE);
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

    @Override
    public void Update_flick(int position, String title, String details) {
        try {
            ChannelDescriptionModel da = data.get(position);
            da.setTitle(title);
            da.setDetail(details);
            data.set(position, da);
            adapter.notifyItemChanged(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifidata(int position) {
        try {
            data.remove(position);
            adapter.notifyDataSetChanged();
            if (data.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ChannelDismissEvent() {
        try {
            bottomSheetDialogFragment.dismiss();
            temp_flag = false;
            bottomBar.selectTabAtPosition(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void FlickBackPress() {
        try {
            bottomSheetDialogFragment = new ChannelBottomSheetDialogFragment();
            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetUserChannel() {
        try {
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.GetUserChannel(myPreferences.getPreferences(MyPreferences.ID), 0, 5);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        GlobalElements.userChannelListModels.clear();
                        if (json.getInt("ack") == 1) {

                            JSONArray result = json.getJSONArray("result");
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject c = result.getJSONObject(i);
                                UserChannelListModel da = new UserChannelListModel();
                                da.setId("" + c.getString("id"));
                                da.setName("" + c.getString("channel_name"));
                                da.setImage_path(c.getString("image_path"));
                                da.setFollower(c.getString("follower"));
                                da.setName(c.getString("channel_name"));
                                da.setChannel_privacy(c.getString("channel_privacy"));
                                da.setDetail(c.getString("details"));
                                da.setContact_no(c.getString("contact_no"));
                                da.setAddress(c.getString("address"));
                                da.setEmail(c.getString("email"));
                                da.setWebsite(c.getString("website"));
                                da.setQr_code_path(c.getString("qr_code_path"));
                                da.setShareable_url(c.getString("shareable_url"));
                                da.setUserActive(c.getInt("UserActive"));
                                GlobalElements.userChannelListModels.add(da);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.print("error" + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
