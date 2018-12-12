package com.Nflicks;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Nflicks.adapter.ChannelListAdapter;
import com.Nflicks.custom.DividerItemDecoration;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.custom.RecyclerViewPositionHelper;
import com.Nflicks.dialog.ChannelBottomSheetDialogFragment;
import com.Nflicks.dialog.FlickBottomSheetDialogFragment;
import com.Nflicks.model.FollowingModel;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;
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

public class ChannelListActivity extends AppCompatActivity implements FlickBottomSheetDialogFragment.Intercommunication, ChannelBottomSheetDialogFragment.ChannelDismissEvent {

    @BindView(R.id.main_title)
    TextView mainTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.channel_list_recycleview)
    RecyclerView recycleview;
    @BindView(R.id.channel_list_back)
    ImageView back;
    @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    @BindView(R.id.channel_list_empty)
    LinearLayout empty;

    ChannelListAdapter listAdapter;
    ArrayList<FollowingModel> followingModels = new ArrayList<>();
    //ProgressDialog pd;
    MyPreferences myPreferences;

    /* load more */
    int firstVisibleItem, visibleItemCount, totalItemCount, count = 0;
    protected int m_PreviousTotalCount;
    RecyclerViewPositionHelper mRecyclerViewHelper;
    Bundle b;
    String fragment_type = "";
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    BottomSheetDialogFragment bottomSheetDialogFragment;
    boolean temp_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        GlobalElements.on_resume_flag = 100;
        mainTitle.setTypeface(FontFamily.process(ChannelListActivity.this, R.raw.sqaure721), Typeface.BOLD);
        myPreferences = new MyPreferences(ChannelListActivity.this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                finish();
            }
        });
        count = 0;
        followingModels.clear();

        try {
            b = getIntent().getExtras();
            mainTitle.setText("" + b.getString("category_title"));
            fragment_type = b.getString("fragment");

            if (GlobalElements.isConnectingToInternet(ChannelListActivity.this)) {
                if (fragment_type.equals("FollowingFragment")) // // TODO: 9/22/2017 search by query
                {
                    GetSearchWithQuery(b.getString("category_title"));
                } else if (fragment_type.equals("ContactsFragment")) {
                    String ch = b.getString("channel_owner_id");
                    GetSearchWithQuery(b.getString("channel_owner_id"));
                } else if (fragment_type.equals("SearchActivity")) {
                    GetSearchWithQuery(b.getString("cnid"));
                }
            } else {
                GlobalElements.showDialog(ChannelListActivity.this);
            }

            recycleview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
                    visibleItemCount = recyclerView.getChildCount();
                    totalItemCount = mRecyclerViewHelper.getItemCount();
                    firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();

                    if (totalItemCount == 0 || listAdapter == null)
                        return;
                    if (m_PreviousTotalCount == totalItemCount) {
                        return;
                    } else {
                        boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                        if (loadMore) {
                            m_PreviousTotalCount = totalItemCount;
                            if (GlobalElements.isConnectingToInternet(ChannelListActivity.this)) {
                                if (fragment_type.equals("FollowingFragment")) // // TODO: 9/22/2017 search by query
                                {
                                    GetSearchWithQuery(b.getString("category_title"));
                                } else if (fragment_type.equals("ContactsFragment")) {
                                    GetSearchWithQuery(b.getString("user_id"));
                                } else if (fragment_type.equals("SearchActivity")) {
                                    GetSearchWithQuery(b.getString("cnid"));
                                }
                            } else {
                                //GlobalElements.showDialog(ChannelDescriptionActivity.this);
                            }
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

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
                            Intent i = new Intent(ChannelListActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else if (tabId == R.id.tab_search) {
                            Intent i = new Intent(ChannelListActivity.this, SearchActivity.class);
                            startActivityForResult(i, 0);
                        }
                        if (tabId == R.id.tab_create_channel) {
                            if (temp_flag) {
                                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                            } else {
                                temp_flag = true;
                            }
                        } else if (tabId == R.id.tab_qrcode) {
                            Intent i = new Intent(ChannelListActivity.this, QrCodeActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_profile) {
                            Intent i = new Intent(ChannelListActivity.this, ProfileActivity.class);
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
                            Intent i = new Intent(ChannelListActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else if (tabId == R.id.tab_search) {
                            Intent i = new Intent(ChannelListActivity.this, SearchActivity.class);
                            startActivityForResult(i, 0);
                        }
                        if (tabId == R.id.tab_create_channel) {
                            if (temp_flag) {
                                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                            } else {
                                temp_flag = true;
                            }
                        } else if (tabId == R.id.tab_qrcode) {
                            Intent i = new Intent(ChannelListActivity.this, QrCodeActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_profile) {
                            Intent i = new Intent(ChannelListActivity.this, ProfileActivity.class);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            System.out.print("1");
        }
        if (requestCode == 234) // todo FlickBottomSheetDialogFragment file result code
        {
            GlobalElements.Flick_bottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == 23) // todo  FlickBottomSheetDialogFragment image result code
        {
            GlobalElements.Flick_bottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode == 350) {
            bottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == 120) {  // todo qrcodeActivity finish result code
            temp_flag = false;
            bottomBar.selectTabAtPosition(2);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (GlobalElements.on_resume_flag == 102) {
                FollowingModel f = followingModels.get(GlobalElements.follow_unfollow_flag_position);
                f.setIsFollowing(GlobalElements.follow_unfollow_flag);
                listAdapter.notifyDataSetChanged();
                GlobalElements.on_resume_flag = 100;
                GlobalElements.follow_unfollow_flag = 0;
                GlobalElements.follow_unfollow_flag_position = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    private void GetSearchWithQuery(String query) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call;

            if (fragment_type.equals("FollowingFragment")) {
                call = request.getSearchWithQuery(myPreferences.getPreferences(MyPreferences.ID), query, "false","1", count, GlobalElements.ll);
            } else if (fragment_type.equals("ContactsFragment")) {
                call = request.GetUserChannel(query,query, count, GlobalElements.ll);
            } else {
                call = request.GetMoreEditerContent(myPreferences.getPreferences(MyPreferences.ID), query, count, GlobalElements.ll);
            }

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {

                            JSONArray result = json.getJSONArray("result");
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject c = result.getJSONObject(i);
                                FollowingModel da = new FollowingModel();
                                da.setId("" + c.getString("id"));
                                da.setName("" + c.getString("channel_name"));
                                da.setImage_path(c.getString("image_path"));
                                da.setFollower(c.getString("follower"));
                                da.setName(c.getString("channel_name"));
                                da.setDetails(c.getString("details"));
                                da.setQr_code_path(c.getString("qr_code_path"));
                                da.setShareable_url(c.getString("shareable_url"));
                                da.setChannel_privacy(c.getString("channel_privacy"));
                                da.setIsFollowing(c.getInt("isFollowing"));
                                da.setChannel_owner_id(c.getString("user_id"));
                                followingModels.add(da);
                            }
                            if (count == 0) {
                                listAdapter = new ChannelListAdapter(ChannelListActivity.this, followingModels);
                                recycleview.setAdapter(listAdapter);
                                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(ChannelListActivity.this);
                                recycleview.addItemDecoration(itemDecoration);
                                recycleview.setLayoutManager(new LinearLayoutManager(ChannelListActivity.this, LinearLayout.VERTICAL, false));
                                empty.setVisibility(View.GONE);
                                recycleview.setVisibility(View.VISIBLE);

                                count += result.length();
                            } else {

                                count += result.length();
                                listAdapter.notifyDataSetChanged();
                            }

                        } else {
                            if (count == 0) {
                                empty.setVisibility(View.VISIBLE);
                                recycleview.setVisibility(View.GONE);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    System.out.print("error" + t.getMessage());
                }
            });
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
            //GlobalElements.redirectFlag_bottomsheet = false;
            bottomSheetDialogFragment = new ChannelBottomSheetDialogFragment();
            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
