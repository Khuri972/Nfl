package com.Nflicks.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.Nflicks.GlobalElements;
import com.Nflicks.R;
import com.Nflicks.adapter.AllFlickAdapter;
import com.Nflicks.custom.RecyclerViewPositionHelper;
import com.Nflicks.model.ChannelDescriptionModel;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CRAFT BOX on 7/29/2017.
 */

public class AllFlickFragment extends Fragment {

    @BindView(R.id.channel_desc_recycleview)
    RecyclerView recyclerView;
    @BindView(R.id.channel_desc_empty)
    NestedScrollView empty;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<ChannelDescriptionModel> data = new ArrayList<>();
    AllFlickAdapter adapter;
    /* load more */
    int firstVisibleItem, visibleItemCount, totalItemCount, count = 0;
    protected int m_PreviousTotalCount;
    RecyclerViewPositionHelper mRecyclerViewHelper;
    MyPreferences myPreferences;
    String last_flick_id = "0";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_all_flick, container, false);
        ButterKnife.bind(this, rootView);
        myPreferences = new MyPreferences(getActivity());

        count = 0;
        data.clear();
        last_flick_id = "0";

        if (GlobalElements.isConnectingToInternet(getActivity())) {
            GetFlick(true);
        } else {
            GlobalElements.showDialog(getActivity());
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (GlobalElements.isConnectingToInternet(getActivity())) {
                    GetFlick(false);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                try {
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
                            if (GlobalElements.isConnectingToInternet(getActivity())) {
                                GetFlick(true);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    private void GetFlick(final boolean refresh_flag) {
        try {
            final ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);

            if (refresh_flag) {
                pd.show();
            }

            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call;

            if (refresh_flag) {
                call = request.getAllFlick(myPreferences.getPreferences(MyPreferences.ID), last_flick_id, count, GlobalElements.ll);
            } else {
                call = request.getAllFlick(myPreferences.getPreferences(MyPreferences.ID), last_flick_id);
            }

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (refresh_flag) {
                            pd.dismiss();
                        } else {
                            try {
                                swipeRefreshLayout.setRefreshing(false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);

                        if (json.getInt("ack") == 1) {
                            JSONArray result = json.getJSONArray("result");
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject c = result.getJSONObject(i);
                                if (refresh_flag) {
                                    if (i == 0) {
                                        last_flick_id = c.getString("id");
                                    }
                                } else {
                                    last_flick_id = c.getString("id");
                                }

                                ChannelDescriptionModel da = new ChannelDescriptionModel();
                                da.setId(c.getString("id"));
                                da.setTitle(c.getString("flick_name"));
                                da.setImage(c.getString("image_path"));
                                da.setDetail(c.getString("details"));
                                da.setShareable_url(c.getString("shareable_url"));
                                da.setFile_path(c.getString("file_path"));
                                da.setIsBookmark(c.getInt("isBookmarked"));
                                da.setChannel_owner_id(c.getString("user_id"));
                                da.setCreated_date(c.getString("created_date"));
                                da.setModified_date(c.getString("modified_date"));
                                da.setCreated_date_string(c.getString("created_date_string"));

                                JSONObject channel_detail_obj = c.getJSONObject("channel_detail");
                                da.setChannel_id(channel_detail_obj.getString("id"));
                                da.setChannel_name(channel_detail_obj.getString("channel_name"));
                                da.setC_follower(channel_detail_obj.getString("follower"));
                                da.setChannel_image_path(channel_detail_obj.getString("image_path"));
                                da.setQr_code_path(channel_detail_obj.getString("qr_code_path"));
                                da.setChannel_privacy(channel_detail_obj.getString("channel_privacy"));

                                if (c.getInt("isSpamed") == 0 && c.getInt("isInAppropriate") == 0) {
                                    if (refresh_flag) {
                                        data.add(da);
                                    } else {
                                        if (data.isEmpty()) {
                                            data.add(da);
                                        } else {
                                            data.add(0, da);
                                            adapter.notifyItemInserted(0);
                                            recyclerView.smoothScrollToPosition(0);
                                        }
                                    }
                                }
                            }

                            if (count == 0) {
                                if (data.isEmpty()) {
                                    recyclerView.setVisibility(View.GONE);
                                    empty.setVisibility(View.VISIBLE);
                                } else {
                                    count += result.length();
                                    adapter = new AllFlickAdapter(getActivity(), data);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false));
                                    recyclerView.setVisibility(View.VISIBLE);
                                    empty.setVisibility(View.GONE);
                                }
                            } else {
                                count += result.length();
                                if (refresh_flag) {
                                    adapter.notifyDataSetChanged();
                                } else {

                                }
                            }
                        } else {
                            if (count == 0) {
                                recyclerView.setVisibility(View.GONE);
                                empty.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (refresh_flag) {
                            pd.dismiss();
                        } else {
                            try {
                                swipeRefreshLayout.setRefreshing(false);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (refresh_flag) {
                        pd.dismiss();
                    } else {
                        try {
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
