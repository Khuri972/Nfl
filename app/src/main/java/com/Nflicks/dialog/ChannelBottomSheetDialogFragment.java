package com.Nflicks.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;

import com.Nflicks.CreateChannelActivity;
import com.Nflicks.GlobalElements;
import com.Nflicks.MainActivity;
import com.Nflicks.ProfileActivity;
import com.Nflicks.QrCodeActivity;
import com.Nflicks.R;
import com.Nflicks.SearchActivity;
import com.Nflicks.adapter.UserChannelListAdapter;
import com.Nflicks.custom.DividerItemDecoration;
import com.Nflicks.custom.RecyclerViewPositionHelper;
import com.Nflicks.custom.Toaster;
import com.Nflicks.model.UserChannelListModel;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CRAFT BOX on 8/18/2017.
 */

public class ChannelBottomSheetDialogFragment extends BottomSheetDialogFragment implements UserChannelListAdapter.Intercommunication {
    ArrayList<UserChannelListModel> channel_data = new ArrayList<>();
    UserChannelListAdapter channelListAdapter;
    RecyclerView recycleview;

    FragmentActivity fragment;
    View contentView;
    MyPreferences myPreferences;

    int firstVisibleItem, visibleItemCount, totalItemCount, count = 0;
    protected int m_PreviousTotalCount;
    RecyclerViewPositionHelper mRecyclerViewHelper;
    boolean temp_flag = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface ChannelDismissEvent {
        public void ChannelDismissEvent();
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();

                try {
                    ChannelDismissEvent channelDismissEvent = (ChannelDismissEvent) fragment;
                    channelDismissEvent.ChannelDismissEvent();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                temp_flag = true;
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            System.out.print("");
        }
    };


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.dialog_modal, null);
        myPreferences = new MyPreferences(getActivity());
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        this.fragment = getActivity();
        recycleview = (RecyclerView) contentView.findViewById(R.id.user_channel_recycleview);
        try {
            temp_flag = false;
            BottomBar bottomBar = (BottomBar) contentView.findViewById(R.id.bottomBar);
            //GlobalElements.setBottomBarListner(getActivity(),"ChannelBottomSheetDialogFragment");
            bottomBar.selectTabAtPosition(2);
            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(@IdRes int tabId) {
                    try {
                        if (tabId == R.id.tab_main) {
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else if (tabId == R.id.tab_search) {
                            Intent i = new Intent(getActivity(), SearchActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_create_channel) {
                            if (temp_flag) {
                                dismiss();
                                ChannelDismissEvent channelDismissEvent = (ChannelDismissEvent) fragment;
                                channelDismissEvent.ChannelDismissEvent();
                            } else {
                                temp_flag = true;
                            }
                        } else if (tabId == R.id.tab_qrcode) {
                            Intent i = new Intent(getActivity(), QrCodeActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_profile) {
                            Intent i = new Intent(getActivity(), ProfileActivity.class);
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
                        if (tabId == R.id.tab_create_channel) {
                            if (tabId == R.id.tab_main) {
                                Intent i = new Intent(getActivity(), MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            } else if (tabId == R.id.tab_search) {
                                Intent i = new Intent(getActivity(), SearchActivity.class);
                                startActivityForResult(i, 0);
                            } else if (tabId == R.id.tab_create_channel) {
                                if (temp_flag) {
                                    dismiss();
                                    ChannelDismissEvent channelDismissEvent = (ChannelDismissEvent) fragment;
                                    channelDismissEvent.ChannelDismissEvent();
                                } else {
                                    Log.e("", "");
                                }
                            } else if (tabId == R.id.tab_qrcode) {
                                Intent i = new Intent(getActivity(), QrCodeActivity.class);
                                startActivityForResult(i, 0);
                            } else if (tabId == R.id.tab_profile) {
                                Intent i = new Intent(getActivity(), ProfileActivity.class);
                                startActivityForResult(i, 0);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        count = 0;
        channel_data.clear();
        try {
            if (GlobalElements.userChannelListModels.isEmpty()) {
                Intent i = new Intent(getActivity(), CreateChannelActivity.class);
                i.putExtra("type", "0");
                ((Activity) getActivity()).startActivityForResult(i, 0);
            } else {
                count = GlobalElements.userChannelListModels.size();
                channel_data.addAll(GlobalElements.userChannelListModels);
                Channel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final View touchOutsideView = getDialog().getWindow().getDecorView().findViewById(android.support.design.R.id.touch_outside);
        touchOutsideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dismiss();

                ChannelDismissEvent channelDismissEvent = (ChannelDismissEvent) fragment;
                channelDismissEvent.ChannelDismissEvent();

            }
        });

        if (GlobalElements.isConnectingToInternet(getActivity())) {
            GetUserChannel();
        } else {
            GlobalElements.showDialog(getActivity());
        }

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;
            ((BottomSheetBehavior) behavior).setPeekHeight((height / 2) + 100);
        }

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog,
                                 int keyCode, android.view.KeyEvent event) {
                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    // To dismiss the fragment when the back-button is pressed.
                    dismiss();
                    ChannelDismissEvent channelDismissEvent = (ChannelDismissEvent) fragment;
                    channelDismissEvent.ChannelDismissEvent();

                    return true;
                }
                // Otherwise, do nothing else
                else return false;
            }
        });

        recycleview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mRecyclerViewHelper.getItemCount();
                firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();

                if (totalItemCount == 0 || channelListAdapter == null)
                    return;
                if (m_PreviousTotalCount == totalItemCount) {
                    return;
                } else {
                    boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                    if (loadMore) {
                        m_PreviousTotalCount = totalItemCount;
                        if (GlobalElements.isConnectingToInternet(getActivity())) {

                            GetUserChannel();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (GlobalElements.userChannelListModels.isEmpty()) {

            try {
                dismiss();
                /*ChannelDismissEvent channelDismissEvent = (ChannelDismissEvent) fragment;
                channelDismissEvent.ChannelDismissEvent();*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 350) {
            count = 0;
            channel_data.clear();
            if (GlobalElements.isConnectingToInternet(getActivity())) {
                GetUserChannel();
            } else {
                GlobalElements.showDialog(getActivity());
            }
        }
    }

    @Override
    public void AddFlickDialogOpen(String channel_id, String channel_name, String channel_image_path, String follower) {
        try {
            dismiss();
            GlobalElements.Flick_bottomSheetDialogFragment = new FlickBottomSheetDialogFragment();
            Bundle b = new Bundle();
            b.putString("channel_id", channel_id);
            b.putString("channel_name", channel_name);
            b.putString("channel_image_path", channel_image_path);
            b.putString("follower", follower);
            b.putString("type", "0");
            GlobalElements.Flick_bottomSheetDialogFragment.setArguments(b);
            GlobalElements.Flick_bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), GlobalElements.Flick_bottomSheetDialogFragment.getTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Channel() {
        channelListAdapter = new UserChannelListAdapter(getActivity(), channel_data, this);
        recycleview.setAdapter(channelListAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity());
        recycleview.addItemDecoration(itemDecoration);
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false));
    }

    private void GetUserChannel() {
        try {
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.GetUserChannel(myPreferences.getPreferences(MyPreferences.ID), count, 15);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);

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
                                channel_data.add(da);
                            }
                            if (count == 0) {
                                GlobalElements.userChannelListModels.clear();
                                GlobalElements.userChannelListModels.addAll(channel_data);
                                count += result.length();
                                Channel();
                            } else {
                                count += result.length();
                                channelListAdapter.notifyDataSetChanged();
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
