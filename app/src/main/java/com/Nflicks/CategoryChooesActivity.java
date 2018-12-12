package com.Nflicks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nflicks.adapter.CategoryAdapter;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.custom.SpacesItemDecoration;
import com.Nflicks.custom.Toaster;
import com.Nflicks.model.CategoryModel;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryChooesActivity extends AppCompatActivity implements CategoryAdapter.intercommunication {


    @BindView(R.id.category_recycleview)
    RecyclerView recycleview;
    @BindView(R.id.category_title)
    TextView title;
    @BindView(R.id.category_sub_title)
    TextView subtitle;

    @BindView(R.id.category_submit_txt)
    TextView submit_txt;
    @BindView(R.id.category_submit_linear)
    LinearLayout submit_linear;
    @BindView(R.id.category_nested)
    NestedScrollView nsv;

    ArrayList<CategoryModel> data = new ArrayList<>();
    ProgressDialog pd;
    MyPreferences myPreferences;
    JSONArray intrest_array = new JSONArray();
    String type = ""; //todo 0 = nothing or 1 = update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_category_chooes);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        ButterKnife.bind(this);
        recycleview.setNestedScrollingEnabled(false);
        myPreferences = new MyPreferences(CategoryChooesActivity.this);

        nsv.post(new Runnable() {
            @Override
            public void run() {
                nsv.scrollTo(0, title.getTop() - 30);
            }
        });
        title.setTypeface(FontFamily.process(CategoryChooesActivity.this, R.raw.roboto_regular), Typeface.BOLD);
        subtitle.setTypeface(FontFamily.process(CategoryChooesActivity.this, R.raw.roboto_regular), Typeface.BOLD);

        /* todo set layout redius programmatically */
        submit_linear.setEnabled(false);
        float radii = CategoryChooesActivity.this.getResources().getDimension(R.dimen.linear_redius_25);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{radii, radii, radii, radii, radii, radii, radii, radii});
        shape.setColor(ContextCompat.getColor(CategoryChooesActivity.this, R.color.dark_red));
        submit_linear.setBackground(shape);


        try {
            Intent i = getIntent();
            type = i.getStringExtra("type");
        } catch (Exception e) {
            e.printStackTrace();
        }


        submit_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalElements.isConnectingToInternet(CategoryChooesActivity.this)) {
                    AddInterest();
                } else {
                    GlobalElements.showDialog(CategoryChooesActivity.this);
                }
            }
        });

        if (GlobalElements.isConnectingToInternet(CategoryChooesActivity.this)) {
            GetInterest();
        } else {
            GlobalElements.showDialog(CategoryChooesActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    public void CategorySelect() {
        int count = 0;
        //category_ids.setLength(0);

        intrest_array = new JSONArray();
        JSONObject interest_obj;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isFlag()) {
                count++;
                try {
                    interest_obj = new JSONObject();
                    interest_obj.put("id", "" + data.get(i).getId());
                    intrest_array.put(interest_obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //category_ids.append(""+data.get(i).getId()).append(",");
            }
        }

        if (count >= 5) {
            submit_txt.setText("" + getResources().getString(R.string.category_submit_txt_done));
            submit_txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.category_finish, 0);
            submit_txt.setGravity(Gravity.CENTER);
            submit_linear.setEnabled(true);

            float radii = CategoryChooesActivity.this.getResources().getDimension(R.dimen.linear_redius_25);
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadii(new float[]{radii, radii, radii, radii, radii, radii, radii, radii});
            shape.setColor(ContextCompat.getColor(CategoryChooesActivity.this, R.color.green));
            submit_linear.setBackground(shape);
        } else {
            submit_txt.setText("" + getResources().getString(R.string.category_submit_txt));
            submit_txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            submit_txt.setGravity(Gravity.CENTER);
            submit_linear.setEnabled(false);

            float radii = CategoryChooesActivity.this.getResources().getDimension(R.dimen.linear_redius_25);
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadii(new float[]{radii, radii, radii, radii, radii, radii, radii, radii});
            shape.setColor(ContextCompat.getColor(CategoryChooesActivity.this, R.color.dark_red));
            submit_linear.setBackground(shape);
        }
    }

    private void GetInterest() {
        try {
            pd = new ProgressDialog(CategoryChooesActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.GetInterest(myPreferences.getPreferences(MyPreferences.ID));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            int count = 0;
                            JSONArray result = json.getJSONArray("result");
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject c = result.getJSONObject(i);
                                CategoryModel da = new CategoryModel();
                                da.setId(c.getString("id"));
                                da.setName(c.getString("interest_name"));
                                da.setImage_path(c.getString("image_path"));

                                if (type.equals("1")) {
                                    try {
                                        boolean isValidProduct = true;
                                        try {
                                            JSONArray demo = new JSONArray("" + myPreferences.getPreferences(MyPreferences.Intrest));
                                            for (int j = 0; j < demo.length(); j++) {
                                                JSONObject a = demo.getJSONObject(j);
                                                if (a.getString("id").equals("" + da.getId())) {
                                                    count++;
                                                    isValidProduct = false;
                                                }
                                            }
                                            if (isValidProduct) {
                                                da.setFlag(false);
                                            } else {
                                                da.setFlag(true);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    da.setFlag(false);
                                }
                                data.add(da);
                            }

                            CategoryModel da = new CategoryModel(); // todo don`t remove this model
                            da.setId("");
                            da.setName("");
                            da.setImage_path("");
                            da.setFlag(false);
                            data.add(da);
                            da = new CategoryModel(); // todo don`t remove this model
                            da.setId("");
                            da.setName("");
                            da.setImage_path("");
                            da.setFlag(false);
                            data.add(da);
                            da = new CategoryModel(); // todo don`t remove this model
                            da.setId("");
                            da.setName("");
                            da.setImage_path("");
                            da.setFlag(false);
                            data.add(da);

                            if (type.equals("1")) {
                                if (count >= 5) {
                                    submit_txt.setText("" + getResources().getString(R.string.category_submit_txt_done));
                                    submit_txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.category_finish, 0);
                                    submit_txt.setGravity(Gravity.CENTER);
                                    submit_linear.setEnabled(true);

                                    float radii = CategoryChooesActivity.this.getResources().getDimension(R.dimen.linear_redius_25);
                                    GradientDrawable shape = new GradientDrawable();
                                    shape.setShape(GradientDrawable.RECTANGLE);
                                    shape.setCornerRadii(new float[]{radii, radii, radii, radii, radii, radii, radii, radii});
                                    shape.setColor(ContextCompat.getColor(CategoryChooesActivity.this, R.color.green));
                                    submit_linear.setBackground(shape);
                                } else {
                                    submit_txt.setText("" + getResources().getString(R.string.category_submit_txt));
                                    submit_txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                    submit_txt.setGravity(Gravity.CENTER);
                                    submit_linear.setEnabled(false);

                                    float radii = CategoryChooesActivity.this.getResources().getDimension(R.dimen.linear_redius_25);
                                    GradientDrawable shape = new GradientDrawable();
                                    shape.setShape(GradientDrawable.RECTANGLE);
                                    shape.setCornerRadii(new float[]{radii, radii, radii, radii, radii, radii, radii, radii});
                                    shape.setColor(ContextCompat.getColor(CategoryChooesActivity.this, R.color.dark_red));
                                    submit_linear.setBackground(shape);
                                }
                            }

                            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(CategoryChooesActivity.this, 3);
                            int spanCount = 3; // 3 columns
                            int spacing = 25; // 50px
                            boolean includeEdge = false;
                            recycleview.addItemDecoration(new SpacesItemDecoration(spanCount, spacing, includeEdge));
                            recycleview.setAdapter(new CategoryAdapter(CategoryChooesActivity.this, data));
                            recycleview.setLayoutManager(layoutManager);

                            nsv.post(new Runnable() {
                                @Override
                                public void run() {
                                    nsv.scrollTo(0, 0);
                                }
                            });
                        } else {
                            Toaster.show(CategoryChooesActivity.this, "" + json.getString("ack_msg"), true, Toaster.DANGER);
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

    private void AddInterest() {
        try {
            pd = new ProgressDialog(CategoryChooesActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.AddInterest(myPreferences.getPreferences(MyPreferences.ID), intrest_array.toString());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            JSONArray result = json.getJSONArray("result");
                            intrest_array = new JSONArray();
                            JSONObject interest_obj;
                            for (int j = 0; j < result.length(); j++) {
                                JSONObject c = result.getJSONObject(j);
                                try {
                                    interest_obj = new JSONObject();
                                    interest_obj.put("id", c.getString("interest_id"));
                                    interest_obj.put("interest_name", c.getString("interest_name"));
                                    intrest_array.put(interest_obj);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            myPreferences.setPreferences(MyPreferences.Intrest, intrest_array.toString());
                            if (type.equals("0")) {
                                myPreferences.setPreferences(MyPreferences.status, json.getString("status"));
                                Intent i = new Intent(CategoryChooesActivity.this, MainActivity.class);
                                i.putExtra("type", "0");
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            } else {
                                Intent intent = new Intent();
                                Bundle b = new Bundle();
                                b.putString("intrest_array", intrest_array.toString());
                                intent.putExtras(b);
                                setResult(250, intent);
                                finish();
                            }
                        } else {
                            Toaster.show(CategoryChooesActivity.this, "" + json.getString("ack_msg"), true, Toaster.DANGER);
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
}
