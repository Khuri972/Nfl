package com.Nflicks.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.Nflicks.GlobalElements;
import com.Nflicks.R;
import com.Nflicks.adapter.FollowingVerticalAdapter;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.model.FollowingCategoryModel;
import com.Nflicks.model.FollowingModel;
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

public class FollowingFragment extends Fragment {

    @BindView(R.id.following_recycleview)
    RecyclerView mRecyclerView;
    @BindView(R.id.following_empty)
    TextView mEmpty;
    FollowingVerticalAdapter snapAdapter;
    ArrayList<FollowingModel> followingModels = new ArrayList<>();
    ArrayList<FollowingCategoryModel> followingCategoryModel = new ArrayList<>();

    //ProgressDialog  pd;
    MyPreferences myPreferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_following, container, false);
        ButterKnife.bind(this, rootView);
        myPreferences = new MyPreferences(getActivity());
        mEmpty.setTypeface(FontFamily.process(getActivity(), R.raw.roboto_regular));

        GetUserChannel();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (GlobalElements.on_resume_flag == 101) {
                for (int i = 0; i < followingCategoryModel.size(); i++) {
                    for (int j = 0; j < followingCategoryModel.get(i).getFollowingModels().size(); j++) {
                        if (followingCategoryModel.get(i).getFollowingModels().get(j).getId().equals("" + GlobalElements.channel_id)) {
                            followingCategoryModel.get(i).getFollowingModels().get(j).setCount(0);
                        }
                    }
                }
                if (followingCategoryModel.size() > 0) {
                    snapAdapter.notifyDataSetChanged();
                }
                GlobalElements.channel_id = "0";
                GlobalElements.on_resume_flag = 100;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RefereshData(int position, ArrayList<FollowingModel> da) {
        try {
            if (da == null) {
                System.out.print("empty");
            } else {
                followingCategoryModel.get(position).setFollowingModels(da);
                snapAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RefereshData(int section_position, int position, FollowingModel da) {
        try {
            if (da == null) {
                System.out.print("empty");
            } else {
                followingCategoryModel.get(section_position).getFollowingModels().set(position, da);
                snapAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetUserChannel() {
        try {
            final ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.getAllChannel(myPreferences.getPreferences(MyPreferences.ID));

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
                                da.setmText("" + result_obj.getString("interest"));

                                JSONArray channel_array = result_obj.getJSONArray("channel");
                                followingModels = new ArrayList<>();
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
                                    JSONObject last_seen_detail_obj = channel_obj.getJSONObject("last_seen_detail");
                                    da1.setCount(last_seen_detail_obj.getInt("flickCount"));
                                    followingModels.add(da1);
                                }

                                if (channel_array.length() > 0) {
                                    da.setFollowingModels(followingModels);
                                    followingCategoryModel.add(da);
                                }
                            }

                            snapAdapter = new FollowingVerticalAdapter(getActivity(), followingCategoryModel, FollowingFragment.this);
                            mRecyclerView.setAdapter(snapAdapter);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            mRecyclerView.setHasFixedSize(true);
                            //setupAdapter();

                            if (followingCategoryModel.isEmpty()) {
                                mEmpty.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.GONE);
                            } else {
                                mEmpty.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            mEmpty.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }


                        try {
                            Intent intent = new Intent();
                            intent.setAction("com.Notiflick.tutoral");
                            getActivity().sendOrderedBroadcast(intent, null);
                        } catch (Exception e) {
                            e.printStackTrace();
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
