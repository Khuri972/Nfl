package com.Nflicks;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Nflicks.adapter.MyPagerAdapter;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.dialog.ChannelBottomSheetDialogFragment;
import com.Nflicks.dialog.FlickBottomSheetDialogFragment;
import com.Nflicks.fragment.ContactsFragment;
import com.Nflicks.fragment.FollowingFragment;
import com.Nflicks.model.FollowingModel;
import com.Nflicks.model.UserChannelListModel;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;
import com.Nflicks.tutoral.TapIntroHelper;
import com.astuetz.PagerSlidingTabStrip;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        FlickBottomSheetDialogFragment.Intercommunication, ChannelBottomSheetDialogFragment.ChannelDismissEvent, ContactsFragment.ContactFragmentIntercommunication {

    @BindView(R.id.main_title)
    TextView mainTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.main_save_flick)
    ImageView save_flick;
    @BindView(R.id.main_save_notification)
    ImageView notification;
    @BindView(R.id.activity_main)
    CoordinatorLayout activityMain;
    @BindView(R.id.main_sync)
    ImageView contact_sync;
    @BindView(R.id.main_sync_1)
    ImageView contact_sync_1;
    @BindView(R.id.main_more)
    ImageView more;
    MyPagerAdapter adapter;
    BottomBar bottomBar;

    BottomSheetDialogFragment bottomSheetDialogFragment;
    Animation rotate_animation;
    MyPreferences myPreferences;

    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        ButterKnife.bind(this);
        myPreferences = new MyPreferences(this);
        setSupportActionBar(toolbar);
        mainTitle.setTypeface(FontFamily.process(MainActivity.this, R.raw.sqaure721), Typeface.BOLD);
        rotate_animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_indefinitely);
        GlobalElements.userChannelListModels = new ArrayList<>();

        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        pager.setCurrentItem(1);

        save_flick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalElements.isConnectingToInternet(MainActivity.this)) {
                    Intent i = new Intent(MainActivity.this, SavedFlickActivity.class);
                    startActivity(i);
                } else {
                    GlobalElements.showDialog(MainActivity.this);
                }

            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalElements.isConnectingToInternet(MainActivity.this)) {
                    Intent i = new Intent(MainActivity.this, NotificationActivity.class);
                    startActivity(i);
                } else {
                    GlobalElements.showDialog(MainActivity.this);
                }
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, more);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.main_activity, popup.getMenu());
                try {
                    MenuItem menuItem = popup.getMenu().findItem(R.id.menu_version);
                    String versionName = MainActivity.this.getPackageManager()
                            .getPackageInfo(MainActivity.this.getPackageName(), 0).versionName;
                    menuItem.setTitle("App Version " + versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_share) {
                            try {
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("text/plain");
                                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getPackageName());
                                startActivity(Intent.createChooser(share, "Share link!"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (item.getItemId() == R.id.menu_review) {
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
                                startActivity(i);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (item.getItemId() == R.id.menu_tutoral) {
                            try {
                                myPreferences.clearTutoralPreferences();
                                TapIntroHelper.showDashboardIntro(MainActivity.this, getResources().getColor(R.color.colorPrimary));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {
                    if (position == 0 || position == 1) {
                        rotate_animation.cancel();
                        contact_sync.setVisibility(View.GONE);
                        contact_sync_1.setVisibility(View.GONE);
                    } else {
                        contact_sync.setVisibility(View.VISIBLE);
                        Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                        contact_sync.startAnimation(myAnim);
                        contact_sync_1.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        contact_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contact_sync.startAnimation(rotate_animation);
                adapter.ContactFragmentMethod();
            }
        });

        try {

            bottomSheetDialogFragment = new ChannelBottomSheetDialogFragment();
            GlobalElements.Flick_bottomSheetDialogFragment = new FlickBottomSheetDialogFragment();
            bottomBar = (BottomBar) findViewById(R.id.bottomBar);
            bottomBar.selectTabAtPosition(0);

            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(@IdRes int tabId) {
                    try {
                        if (tabId == R.id.tab_main) {

                        } else if (tabId == R.id.tab_search) {
                            Intent i = new Intent(MainActivity.this, SearchActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_create_channel) {
                            /*if (GlobalElements.userChannelListModels.isEmpty()) {
                                Intent i = new Intent(MainActivity.this, CreateChannelActivity.class);
                                i.putExtra("type", "0");
                                startActivityForResult(i, 0);
                            }*/
                            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                        } else if (tabId == R.id.tab_qrcode) {
                            Intent i = new Intent(MainActivity.this, QrCodeActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_profile) {
                            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
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

                        } else if (tabId == R.id.tab_search) {
                            Intent i = new Intent(MainActivity.this, SearchActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_create_channel) {
                            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                        } else if (tabId == R.id.tab_qrcode) {
                            Intent i = new Intent(MainActivity.this, QrCodeActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_profile) {
                            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
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

        /*try {
            TapIntroHelper.showDashboardIntro(MainActivity.this, ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 234) // todo FlickBottomSheetDialogFragment result
        {
            GlobalElements.Flick_bottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == 23) // todo  FlickBottomSheetDialogFragment result
        {
            GlobalElements.Flick_bottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode == 245) // todo channel list activity finish return resultCode
        {
            try {
                MyPagerAdapter fragmentPagerAdapter = (MyPagerAdapter) pager.getAdapter();
                for (int i = 0; i < fragmentPagerAdapter.getCount(); i++) {
                    Fragment viewPagerFragment = (Fragment) pager.getAdapter().instantiateItem(pager, i);
                    if (viewPagerFragment != null && viewPagerFragment.isAdded()) {
                        if (viewPagerFragment instanceof FollowingFragment) {
                            FollowingFragment oneFragment = (FollowingFragment) viewPagerFragment;
                            if (oneFragment != null) {
                                oneFragment.RefereshData(data.getIntExtra("position", 0), (ArrayList<FollowingModel>) data.getSerializableExtra("data")); // your custom method
                            }
                        } else if (viewPagerFragment instanceof ContactsFragment) {
                            ContactsFragment oneFragment = (ContactsFragment) viewPagerFragment;
                            if (oneFragment != null) {
                                oneFragment.RefereshData(data.getIntExtra("position", 0), (ArrayList<FollowingModel>) data.getSerializableExtra("data")); // your custom method
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == 249) {
            try {
                FollowingModel followingModel = (FollowingModel) data.getSerializableExtra("data");
                MyPagerAdapter fragmentPagerAdapter = (MyPagerAdapter) pager.getAdapter();
                for (int i = 0; i < fragmentPagerAdapter.getCount(); i++) {
                    Fragment viewPagerFragment = (Fragment) pager.getAdapter().instantiateItem(pager, i);
                    if (viewPagerFragment != null && viewPagerFragment.isAdded()) {
                        if (viewPagerFragment instanceof FollowingFragment) {
                            FollowingFragment oneFragment = (FollowingFragment) viewPagerFragment;
                            if (oneFragment != null) {
                                oneFragment.RefereshData(data.getIntExtra("section_position", 0), data.getIntExtra("position", 0), followingModel); // your custom method
                            }
                        } else if (viewPagerFragment instanceof ContactsFragment) {
                            ContactsFragment oneFragment = (ContactsFragment) viewPagerFragment;
                            if (oneFragment != null) {
                                oneFragment.RefereshData(data.getIntExtra("section_position", 0), data.getIntExtra("position", 0), followingModel); // your custom method
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == 248) // profile finish activity
        {
            bottomBar.selectTabAtPosition(0);
        } else if (resultCode == 120) // qrcode finish activity
        {
            bottomBar.selectTabAtPosition(0);
        } else if (resultCode == 350) {
            bottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
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
        if (GlobalElements.isConnectingToInternet(MainActivity.this)) {
            GetUserChannel();
        }
        try {
            bottomBar.selectTabAtPosition(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        TapIntroHelper.showDashboardIntro(MainActivity.this, ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.setPriority(1);
            intentFilter.addAction("com.Notiflick.tutoral");
            registerReceiver(receiver, intentFilter);
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

    @Override
    public void ChannelDismissEvent() {
        try {
            bottomBar.selectTabAtPosition(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contactSyncSuccess() {
        rotate_animation.cancel();
        contact_sync.setVisibility(View.GONE);
        contact_sync_1.setVisibility(View.GONE);
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
