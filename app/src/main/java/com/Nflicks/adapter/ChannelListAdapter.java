package com.Nflicks.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nflicks.ChannelDescriptionActivity;
import com.Nflicks.ChannelListActivity;
import com.Nflicks.GlobalElements;
import com.Nflicks.R;
import com.Nflicks.SearchActivity;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.custom.Toaster;
import com.Nflicks.model.FollowingModel;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CRAFT BOX on 10/10/2016.
 */
public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ViewHolder> {
    private ArrayList<FollowingModel> data = new ArrayList<>();
    private Context context;
    private DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    MyPreferences myPreferences;
    AlertDialog buildInfosDialog;
    public ChannelListAdapter(Context context, ArrayList<FollowingModel> data) {
        this.data = data;
        this.context = context;
        myPreferences = new MyPreferences(context);
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_channel_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        viewHolder.name.setTypeface(FontFamily.process(context,R.raw.roboto_regular));
        viewHolder.follow_txt.setTypeface(FontFamily.process(context,R.raw.roboto_regular));
        viewHolder.name.setText(""+data.get(i).getName());


        if(data.get(i).getIsFollowing()==0)
        {
            viewHolder.follow_txt.setText(""+context.getResources().getString(R.string.follow));
            viewHolder.follow_txt.setTextColor(ContextCompat.getColor(context,R.color.white));
            viewHolder.follow_txt.setBackground(GlobalElements.FollowButtion(context));
        }
        else if(data.get(i).getIsFollowing()==1)
        {
            viewHolder.follow_txt.setText(""+context.getResources().getString(R.string.unfollow));
            viewHolder.follow_txt.setTextColor(ContextCompat.getColor(context,R.color.textcolor));
            viewHolder.follow_txt.setBackground(GlobalElements.UnFollowButtion(context));
        }
        else if(data.get(i).getIsFollowing()==2)
        {
            viewHolder.follow_txt.setText(""+context.getResources().getString(R.string.requested));
            viewHolder.follow_txt.setTextColor(ContextCompat.getColor(context,R.color.textcolor));
            viewHolder.follow_txt.setBackground(GlobalElements.UnFollowButtion(context));
        }

        if(!myPreferences.getPreferences(MyPreferences.ID).equals(""+data.get(i).getChannel_owner_id())) {
            viewHolder.follow_txt.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.follow_txt.setVisibility(View.GONE);
        }

        viewHolder.follow_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data.get(i).getIsFollowing()==0)
                {
                    if(GlobalElements.isConnectingToInternet(context)) {
                        AlertDialog.Builder alertDialog2 =new AlertDialog.Builder(context);
                        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View dialogView = inflater.inflate(R.layout.dialog_flick_more, null);
                        LinearLayout part_1 = (LinearLayout)dialogView.findViewById(R.id.dialog_flick_part_1);
                        LinearLayout part_2 = (LinearLayout)dialogView.findViewById(R.id.dialog_flick_part_2);
                        LinearLayout urgent = (LinearLayout)dialogView.findViewById(R.id.dialog_flick_urgent_linear);
                        LinearLayout important = (LinearLayout)dialogView.findViewById(R.id.dialog_flick_important_linear);
                        GlobalElements.overrideFonts_roboto_regular(context,part_2);

                        part_1.setVisibility(View.GONE);
                        part_2.setVisibility(View.VISIBLE);

                        urgent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Channel_Follow_Unfollow("follow","urgent",data.get(i).getId(),i);
                                buildInfosDialog.dismiss();
                            }
                        });

                        important.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Channel_Follow_Unfollow("follow","important",data.get(i).getId(),i);
                                buildInfosDialog.dismiss();
                            }
                        });
                        alertDialog2.setView(dialogView);
                        buildInfosDialog = alertDialog2.create();
                        buildInfosDialog.show();
                    }
                    else
                    {
                        GlobalElements.showDialog(context);
                    }
                }
                else if(data.get(i).getIsFollowing()==1)
                {
                    if(GlobalElements.isConnectingToInternet(context)) {
                        Channel_Follow_Unfollow("unfollow","",data.get(i).getId(),i);
                    }
                    else
                    {
                        GlobalElements.showDialog(context);
                    }
                }
                else if(data.get(i).getIsFollowing()==2)
                {
                    if(GlobalElements.isConnectingToInternet(context)) {
                        Channel_Follow_Unfollow("cancel_request","",data.get(i).getId(),i);
                    }
                    else
                    {
                        GlobalElements.showDialog(context);
                    }
                }
            }
        });

        if(data.get(i).getImage_path().equals(""))
        {

        }
        else
        {

            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.icon)
                    .showImageOnFail(R.drawable.icon)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            imageLoader.displayImage(data.get(i).getImage_path(), viewHolder.img, options);
        }

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(context instanceof SearchActivity)
                {
                    UpdateSearchHistory(data.get(i).getId());
                }
                GlobalElements.on_resume_flag=102;
                GlobalElements.follow_unfollow_flag_position=i;
                Intent i1 =new Intent(context, ChannelDescriptionActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("channel_id",data.get(i).getId());
                b.putSerializable("channel_name",data.get(i).getName());
                b.putSerializable("channel_follower",data.get(i).getFollower());
                b.putSerializable("channel_details",data.get(i).getDetails());
                b.putSerializable("channel_image_path",data.get(i).getImage_path());
                b.putSerializable("channel_sharable_url",data.get(i).getShareable_url());
                b.putSerializable("channel_qr_code",data.get(i).getQr_code_path());
                b.putSerializable("channel_privacy",data.get(i).getChannel_privacy());
                b.putString("activity_type","0");
                i1.putExtras(b);
                ((Activity) context).startActivityForResult(i1, 0);
                ((Activity) context).overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.list_channel_title) TextView name;
        @BindView(R.id.list_channel_img)
        CircleImageView img;
        @BindView(R.id.list_channel_follow) TextView follow_txt;
        @BindView(R.id.list_channel_main_linear)
        LinearLayout layout;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    private void UpdateSearchHistory(String cid) {
        try {
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.UpdateSearchHistory(myPreferences.getPreferences(MyPreferences.ID),cid);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            Log.e("a","a");
                        }
                        else
                        {
                            Log.e("a","a");
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

    private void Channel_Follow_Unfollow(final String flag, String priority, String channel_id, final int position) {
        try {

            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.channel_Follow_Unfollow(myPreferences.getPreferences(MyPreferences.ID),channel_id,flag,priority);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            JSONObject channel_detail=json.getJSONObject("channel_detail");
                           int isFollowing = channel_detail.getInt("isFollowing");

                            if(isFollowing==0)
                            {
                                FollowingModel da=data.get(position);
                                da.setIsFollowing(isFollowing);
                                data.set(position,da);
                                notifyDataSetChanged();
                            }
                            else if(isFollowing==1)
                            {
                                FollowingModel da=data.get(position);
                                da.setIsFollowing(isFollowing);
                                data.set(position,da);
                                notifyDataSetChanged();
                            }
                            else if(isFollowing==2)
                            {
                                FollowingModel da=data.get(position);
                                da.setIsFollowing(isFollowing);
                                data.set(position,da);
                                notifyDataSetChanged();
                            }
                        } else {
                            Toaster.show(context, "" + json.getString("ack_msg"), true, Toaster.DANGER);
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
