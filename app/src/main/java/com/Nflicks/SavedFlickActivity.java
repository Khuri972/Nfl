package com.Nflicks;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nflicks.adapter.ChannelDescriptionAdapter;
import com.Nflicks.adapter.SavedFlickAdapter;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.custom.RecyclerViewPositionHelper;
import com.Nflicks.model.ChannelDescriptionModel;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavedFlickActivity extends AppCompatActivity implements SavedFlickAdapter.ChannelDescriptionNotifidata {

    @BindView(R.id.main_title)
    TextView mainTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.channel_desc_main_linear)
    LinearLayout main_linear;
    ImageView channel_detail_img;
    @BindView(R.id.channel_list_back)
    ImageView back;
    @BindView(R.id.channel_desc_recycleview)
    RecyclerView recyclerView;
    @BindView(R.id.channel_desc_empty)
    LinearLayout empty;

    ArrayList<ChannelDescriptionModel> data = new ArrayList<>();
    SavedFlickAdapter adapter;
    MyPreferences myPreferences;
    /* load more */
    int firstVisibleItem, visibleItemCount, totalItemCount, count = 0;
    protected int m_PreviousTotalCount;
    RecyclerViewPositionHelper mRecyclerViewHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_flick);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        ButterKnife.bind(SavedFlickActivity.this);
        setSupportActionBar(toolbar);
        myPreferences = new MyPreferences(SavedFlickActivity.this);

        GlobalElements.overrideFonts_roboto_regular(SavedFlickActivity.this, main_linear);
        mainTitle.setTypeface(FontFamily.process(SavedFlickActivity.this, R.raw.sqaure721), Typeface.BOLD);
        count = 0;
        data.clear();

        if (GlobalElements.isConnectingToInternet(SavedFlickActivity.this)) {
            GetFlick();
        } else {
            GlobalElements.showDialog(SavedFlickActivity.this);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
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
                        if (GlobalElements.isConnectingToInternet(SavedFlickActivity.this)) {
                            GetFlick();
                        } else {
                            //GlobalElements.showDialog(ChannelDescriptionActivity.this);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    private void GetFlick() {
        try {
            final ProgressDialog pd = new ProgressDialog(SavedFlickActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.getSaveFlick(myPreferences.getPreferences(MyPreferences.ID), count, GlobalElements.ll);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
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
                                da.setChannel_owner_id(c.getString("user_id"));
                                da.setCreated_date(c.getString("created_date"));
                                da.setModified_date(c.getString("modified_date"));
                                da.setCreated_date_string(c.getString("created_date_string"));

                                JSONObject channel_detail_obj = c.getJSONObject("channel_detail");

                                da.setChannel_name(channel_detail_obj.getString("channel_name"));
                                da.setC_follower(channel_detail_obj.getString("follower"));
                                da.setChannel_image_path(channel_detail_obj.getString("image_path"));
                                if (c.getInt("isSpamed") == 0 && c.getInt("isInAppropriate") == 0) {
                                    data.add(da);
                                }
                                //data.add(da);
                            }

                            if (count == 0) {
                                if (data.isEmpty()) {
                                    recyclerView.setVisibility(View.GONE);
                                    empty.setVisibility(View.VISIBLE);
                                } else {
                                    count += result.length();
                                    adapter = new SavedFlickAdapter(SavedFlickActivity.this, data);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(SavedFlickActivity.this, LinearLayout.VERTICAL, false));
                                    recyclerView.setVisibility(View.VISIBLE);
                                    empty.setVisibility(View.GONE);
                                }
                            } else {
                                count += result.length();
                                adapter.notifyDataSetChanged();
                            }

                        } else {

                            if (count == 0) {
                                recyclerView.setVisibility(View.GONE);
                                empty.setVisibility(View.VISIBLE);
                            }
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
}
