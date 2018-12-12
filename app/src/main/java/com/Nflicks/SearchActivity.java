package com.Nflicks;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nflicks.adapter.ChannelListAdapter;
import com.Nflicks.adapter.SearchVerticalAdapter;
import com.Nflicks.custom.CustomEditText;
import com.Nflicks.custom.DividerItemDecoration;
import com.Nflicks.custom.Toaster;
import com.Nflicks.dialog.ChannelBottomSheetDialogFragment;
import com.Nflicks.dialog.FlickBottomSheetDialogFragment;
import com.Nflicks.interfacess.DrawableClickListener;
import com.Nflicks.interfacess.OnDashboardChangedListener;
import com.Nflicks.interfacess.OnStartDragListener;
import com.Nflicks.model.FollowingCategoryModel;
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
import me.ydcool.lib.qrmodule.activity.QrScannerActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements FlickBottomSheetDialogFragment.Intercommunication, ChannelBottomSheetDialogFragment.ChannelDismissEvent, OnDashboardChangedListener,
        OnStartDragListener {

    @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    @BindView(R.id.following_recycleview)
    RecyclerView mRecyclerView;
    @BindView(R.id.following_empty)
    ImageView empty;
    @BindView(R.id.search_edt)
    CustomEditText search_edt;
    SearchVerticalAdapter snapAdapter;
    ArrayList<FollowingModel> section = new ArrayList<>();
    ArrayList<FollowingCategoryModel> followingCategoryModel = new ArrayList<>();
    BottomSheetDialogFragment bottomSheetDialogFragment;

    ArrayList<FollowingModel> followingModels = new ArrayList<>();
    ArrayList<FollowingModel> history_model = new ArrayList<>();
    ChannelListAdapter listAdapter;

    ItemTouchHelper mItemTouchHelper;
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    ProgressDialog pd;
    MyPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        ButterKnife.bind(this);
        myPreferences = new MyPreferences(SearchActivity.this);
        GlobalElements.on_resume_flag = 100;

        if (GlobalElements.isConnectingToInternet(SearchActivity.this)) {
            GetDefaultSearch();
        } else {
            GlobalElements.showDialog(SearchActivity.this);
        }

        search_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.print("");
                if (search_edt.getText().toString().equals("")) {
                    if (!history_model.isEmpty()) {
                        listAdapter = new ChannelListAdapter(SearchActivity.this, history_model);
                        mRecyclerView.setAdapter(listAdapter);
                        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(SearchActivity.this);
                        mRecyclerView.addItemDecoration(itemDecoration);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayout.VERTICAL, false));
                        empty.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        search_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    if (!history_model.isEmpty()) {
                        listAdapter = new ChannelListAdapter(SearchActivity.this, history_model);
                        mRecyclerView.setAdapter(listAdapter);
                        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(SearchActivity.this);
                        mRecyclerView.addItemDecoration(itemDecoration);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayout.VERTICAL, false));
                        empty.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        search_edt.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:
                        //Do something here
                        if (GlobalElements.isConnectingToInternet(SearchActivity.this)) {
                            InputMethodManager imm = (InputMethodManager) SearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(search_edt.getWindowToken(), 0);
                            GetSearchWithQuery(search_edt.getText().toString());
                        } else {
                            GlobalElements.showDialog(SearchActivity.this);
                        }
                        break;
                    case RIGHT:
                        checkPermissionsAndOpenFilePicker();
                        break;
                    default:
                        break;
                }
            }
        });

        search_edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (GlobalElements.isConnectingToInternet(SearchActivity.this)) {
                        InputMethodManager imm = (InputMethodManager) SearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(search_edt.getWindowToken(), 0);
                        GetSearchWithQuery(search_edt.getText().toString());
                    } else {
                        GlobalElements.showDialog(SearchActivity.this);
                    }
                    return true;
                }
                return false;
            }
        });


        try {
            bottomSheetDialogFragment = new ChannelBottomSheetDialogFragment();
            GlobalElements.Flick_bottomSheetDialogFragment = new FlickBottomSheetDialogFragment();
            bottomBar = (BottomBar) findViewById(R.id.bottomBar);
            bottomBar.selectTabAtPosition(1);

            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(@IdRes int tabId) {
                    try {
                        if (tabId == R.id.tab_main) {
                            Intent i = new Intent(SearchActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else if (tabId == R.id.tab_search) {

                        } else if (tabId == R.id.tab_create_channel) {
                            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                        } else if (tabId == R.id.tab_qrcode) {
                            Intent i = new Intent(SearchActivity.this, QrCodeActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_profile) {
                            Intent i = new Intent(SearchActivity.this, ProfileActivity.class);
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
                            Intent i = new Intent(SearchActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else if (tabId == R.id.tab_search) {

                        } else if (tabId == R.id.tab_create_channel) {
                            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                        } else if (tabId == R.id.tab_qrcode) {
                            Intent i = new Intent(SearchActivity.this, QrCodeActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_profile) {
                            Intent i = new Intent(SearchActivity.this, ProfileActivity.class);
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
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(248, intent);
        finish();
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
            followingCategoryModel.get(data.getIntExtra("position", 0)).setFollowingModels((ArrayList<FollowingModel>) data.getSerializableExtra("data"));
            snapAdapter.notifyDataSetChanged();
        } else if (resultCode == 248) // todo profile finish activity
        {
            bottomBar.selectTabAtPosition(1);
        } else if (resultCode == 249) {
            FollowingModel followingModel = (FollowingModel) data.getSerializableExtra("data");
            followingCategoryModel.get(data.getIntExtra("section_position", 0)).getFollowingModels().set(data.getIntExtra("position", 0), followingModel);
            snapAdapter.notifyDataSetChanged();
        } else if (resultCode == 350) {
            bottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode == 120) {
            bottomBar.selectTabAtPosition(1);
        }
        if (requestCode == QrScannerActivity.QR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                SearchWithBarcode("" + data.getExtras().getString(QrScannerActivity.QR_RESULT_STR));
            } else {
            }
        }
    }

    @Override
    public void ChannelDismissEvent() {
        try {
            bottomBar.selectTabAtPosition(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void FlickBackPress() {
        try {
            // GlobalElements.redirectFlag_bottomsheet = false;
            bottomSheetDialogFragment = new ChannelBottomSheetDialogFragment();
            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNoteListChanged(ArrayList<FollowingCategoryModel> customers) {
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        try {
            mItemTouchHelper.startDrag(viewHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPermissionsAndOpenFilePicker() {
        String permission = Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Toaster.show(SearchActivity.this, "Allow external storage reading", false, Toaster.DANGER);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            startActivityForResult(
                    new Intent(SearchActivity.this, QrScannerActivity.class),
                    QrScannerActivity.QR_REQUEST_CODE);
        }
    }

    private void GetDefaultSearch() {
        try {
            pd = new ProgressDialog(SearchActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.getDefaultSearch(myPreferences.getPreferences(MyPreferences.ID));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);

                        if (json.getInt("ack") == 1) {
                            followingCategoryModel.clear();
                            JSONArray result = json.getJSONArray("result");
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject result_obj = result.getJSONObject(i);
                                FollowingCategoryModel da = new FollowingCategoryModel();
                                da.setmGravity(Gravity.CENTER_HORIZONTAL);
                                da.setmText("" + result_obj.getString("title"));
                                da.setId("" + result_obj.getString("id"));

                                JSONArray channel_array = result_obj.getJSONArray("channel");
                                section = new ArrayList<>();
                                for (int j = 0; j < channel_array.length(); j++) {
                                    JSONObject channel_obj = channel_array.getJSONObject(j);
                                    FollowingModel da1 = new FollowingModel();
                                    da1.setId("" + channel_obj.getString("id"));
                                    da1.setName("" + channel_obj.getString("channel_name"));
                                    da1.setImage_path(channel_obj.getString("image_path"));
                                    da1.setDetails(channel_obj.getString("details"));
                                    da1.setQr_code_path(channel_obj.getString("qr_code_path"));
                                    da1.setShareable_url(channel_obj.getString("shareable_url"));
                                    da1.setIsFollowing(channel_obj.getInt("isFollowing"));
                                    da1.setFollower(channel_obj.getString("follower"));
                                    da1.setChannel_privacy(channel_obj.getString("channel_privacy"));
                                    da.setChannel_owner_id(channel_obj.getString("user_id"));
                                    da1.setCount(0);
                                    section.add(da1);
                                }
                                if (channel_array.length() > 0) {
                                    da.setFollowingModels(section);
                                    followingCategoryModel.add(da);
                                }
                            }

                            snapAdapter = new SearchVerticalAdapter(SearchActivity.this, followingCategoryModel);
                            mRecyclerView.setAdapter(snapAdapter);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                            mRecyclerView.setHasFixedSize(true);
                            empty.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            empty.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }

                        GetSearchHistory();
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
    protected void onResume() {
        super.onResume();
        try {
            if (GlobalElements.on_resume_flag == 102) {
                FollowingModel f = history_model.get(GlobalElements.follow_unfollow_flag_position);
                f.setIsFollowing(GlobalElements.follow_unfollow_flag);
                listAdapter.notifyDataSetChanged();
                GlobalElements.on_resume_flag = 100;
                GlobalElements.follow_unfollow_flag_position = 0;
                GlobalElements.follow_unfollow_flag_position = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            bottomBar.selectTabAtPosition(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetSearchWithQuery(String query) {
        try {
            pd = new ProgressDialog(SearchActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.getSearchWithQuery(myPreferences.getPreferences(MyPreferences.ID), query, "false","0");

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        followingModels.clear();
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
                            empty.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            listAdapter = new ChannelListAdapter(SearchActivity.this, followingModels);
                            mRecyclerView.setAdapter(listAdapter);
                            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(SearchActivity.this);
                            mRecyclerView.addItemDecoration(itemDecoration);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayout.VERTICAL, false));
                        } else {
                            empty.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
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

    private void GetSearchHistory() {
        try {
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.getSearch_history(myPreferences.getPreferences(MyPreferences.ID));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        history_model.clear();
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
                                history_model.add(da);
                            }
                        } else {

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

    private void SearchWithBarcode(String barcode_code) {
        try {
            pd = new ProgressDialog(SearchActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.getChannelFromBarcode(myPreferences.getPreferences(MyPreferences.ID), barcode_code);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            JSONObject result_obj = json.getJSONObject("result");
                            Intent i1 = new Intent(SearchActivity.this, ChannelDescriptionActivity.class);
                            Bundle b = new Bundle();
                            b.putSerializable("channel_id", result_obj.getString("id"));
                            b.putSerializable("channel_name", result_obj.getString("channel_name"));
                            b.putSerializable("channel_follower", result_obj.getString("follower"));
                            b.putSerializable("channel_details", result_obj.getString("details"));
                            b.putSerializable("channel_image_path", result_obj.getString("image_path"));
                            b.putSerializable("channel_sharable_url", result_obj.getString("shareable_url"));
                            b.putSerializable("channel_qr_code", result_obj.getString("qr_code_path"));
                            b.putSerializable("channel_privacy", result_obj.getString("channel_privacy"));
                            b.putString("activity_type", "0");
                            i1.putExtras(b);
                            startActivityForResult(i1, 0);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        } else {
                            Toaster.show(SearchActivity.this, "" + json.getString("ack_msg"), false, Toaster.DANGER);
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
