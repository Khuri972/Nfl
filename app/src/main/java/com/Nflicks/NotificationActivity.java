package com.Nflicks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nflicks.adapter.NotificationAdapter;
import com.Nflicks.custom.DividerItemDecoration;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.custom.RecyclerViewPositionHelper;
import com.Nflicks.custom.Toaster;
import com.Nflicks.model.NotificationModel;
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

public class NotificationActivity extends AppCompatActivity implements NotificationAdapter.NotificationIntercommunication {
    @BindView(R.id.main_title)
    TextView mainTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.channel_list_back)
    ImageView back;
    @BindView(R.id.notification_recycleview)
    RecyclerView recyclerView;
    @BindView(R.id.notification_empty)
    LinearLayout empty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    MyPreferences myPreferences;
    /* load more */
    int firstVisibleItem, visibleItemCount, totalItemCount, count = 0;
    protected int m_PreviousTotalCount;
    RecyclerViewPositionHelper mRecyclerViewHelper;

    ArrayList<NotificationModel> data = new ArrayList<>();
    NotificationAdapter adapter;
    ProgressDialog pd;

    String last_notiflick_id = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        ButterKnife.bind(NotificationActivity.this);
        setSupportActionBar(toolbar);
        myPreferences = new MyPreferences(NotificationActivity.this);
        mainTitle.setTypeface(FontFamily.process(NotificationActivity.this, R.raw.sqaure721), Typeface.BOLD);

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
                        if (GlobalElements.isConnectingToInternet(NotificationActivity.this)) {
                            GetNotification(true);
                        }
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });

        count = 0;
        data.clear();
        last_notiflick_id = "0";
        GetNotification(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (GlobalElements.isConnectingToInternet(NotificationActivity.this)) {
                    GetNotification(false);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }


    private void GetNotification(final boolean refresh_flag) {
        try {

            if (refresh_flag) {
                pd = new ProgressDialog(NotificationActivity.this);
                pd.setTitle("Please Wait");
                pd.setMessage("Loading");
                pd.setCancelable(true);
                pd.show();
            }

            final RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call;

            if (refresh_flag) {
                call = request.getNotification(myPreferences.getPreferences(MyPreferences.ID), count, GlobalElements.ll);
            } else {
                call = request.getNotification(myPreferences.getPreferences(MyPreferences.ID), last_notiflick_id);
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
                                        last_notiflick_id = c.getString("id");
                                    }
                                } else {
                                    last_notiflick_id = c.getString("id");
                                }

                                NotificationModel da = new NotificationModel(c.getString("id"), c.getString("user_id"), c.getString("ref_id"), c.getString("ref_table"), c.getString("notification_title"),
                                        c.getString("notification_description"), c.getString("notification_type"), c.getString("notification_extra"), c.getString("image_path"),
                                        c.getString("image_path_inside"), c.getString("isDelete"), c.getString("adate"), c.getString("isExpired"), c.getString("isActive"), c.getString("expiry_date"), c.getString("adate_string"));

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

                            if (count == 0) {
                                if (data.isEmpty()) {
                                    recyclerView.setVisibility(View.GONE);
                                    empty.setVisibility(View.VISIBLE);
                                } else {
                                    count += result.length();
                                    adapter = new NotificationAdapter(NotificationActivity.this, data);
                                    recyclerView.setAdapter(adapter);
                                    RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(NotificationActivity.this);
                                    recyclerView.addItemDecoration(itemDecoration);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this, LinearLayout.VERTICAL, false));
                                    recyclerView.setVisibility(View.VISIBLE);
                                    empty.setVisibility(View.GONE);
                                }
                            } else {
                                count += result.length();
                                if (refresh_flag) {
                                    adapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
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

    @Override
    public void dataChange() {
        count = 0;
        data.clear();
        last_notiflick_id = "0";
        GetNotification(true);
    }
}
