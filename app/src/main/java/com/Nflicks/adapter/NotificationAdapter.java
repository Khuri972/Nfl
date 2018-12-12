package com.Nflicks.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nflicks.ChannelDescriptionActivity;
import com.Nflicks.GlobalElements;
import com.Nflicks.R;
import com.Nflicks.SavedFlickActivity;
import com.Nflicks.custom.Toaster;
import com.Nflicks.model.ChannelDescriptionModel;
import com.Nflicks.model.NotificationModel;
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
 * Created by CRAFT BOX on 11/9/2016.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private ArrayList<NotificationModel> data;
    private Context context;
    private DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private   MyPreferences  myPreferences;
    public NotificationAdapter(Context context, ArrayList<NotificationModel> data) {
        this.data = data;
        this.context = context;
        myPreferences = new MyPreferences(context);
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    public interface NotificationIntercommunication{
        public void dataChange();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_notifaction,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {

        if (data.get(i).getNotification_title().equals("")) {
            viewHolder.title.setVisibility(View.GONE);
        } else {
            viewHolder.title.setVisibility(View.VISIBLE);
            viewHolder.title.setText(Html.fromHtml("" + data.get(i).getNotification_title()));
        }

        if (data.get(i).getAdate_string().equals("")) {
            viewHolder.adate.setVisibility(View.GONE);
        } else
        {
            viewHolder.adate.setVisibility(View.VISIBLE);
            viewHolder.adate.setText(""+data.get(i).getAdate_string());
        }

        if(data.get(i).getNotification_description().equals(""))
        {
         viewHolder.desc.setVisibility(View.GONE);
        }
        else {
            viewHolder.desc.setVisibility(View.GONE);
            viewHolder.desc.setText("" + data.get(i).getNotification_description());
        }

        if(data.get(i).getImage_path().equals(""))
        {

        }
        else
        {

            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.default_image)
                    .showImageOnFail(R.drawable.default_image)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            imageLoader.displayImage(data.get(i).getImage_path(), viewHolder.img, options);
        }

        if(data.get(i).getNotification_type().equals("1"))
        {
            viewHolder.request_linear.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.request_linear.setVisibility(View.GONE);
        }

        viewHolder.accept.setBackground(GlobalElements.RoundedButtion_White(context));
        viewHolder.reject.setBackground(GlobalElements.RoundedButtion_Lighter_gray(context));

        viewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GlobalElements.isConnectingToInternet(context))
                {
                    Requist(data.get(i).getRef_id(),"accept");
                }
                else
                {
                    GlobalElements.showDialog(context);
                }
            }
        });

        viewHolder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GlobalElements.isConnectingToInternet(context))
                {
                    Requist(data.get(i).getRef_id(),"reject");
                }
                else
                {
                    GlobalElements.showDialog(context);
                }
            }
        });

        viewHolder.linear_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data.get(i).getNotification_type().equals("0") || data.get(i).getNotification_type().equals("2"))
                {

                }
                else
                {
                    Intent i1 =new Intent(context, ChannelDescriptionActivity.class);
                    Bundle b = new Bundle();
                    b.putString("channel_id",data.get(i).getRef_id());
                    b.putString("activity_type","1");
                    i1.putExtras(b);
                    ((Activity) context).startActivityForResult(i1, 0);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.list_notification_img) CircleImageView img;
        @BindView(R.id.list_notification_title) TextView title;
        @BindView(R.id.list_notification_desc) TextView desc;
        @BindView(R.id.list_notification_adate) TextView adate;
        @BindView(R.id.list_notification_request_linear) LinearLayout request_linear;
        @BindView(R.id.list_notification_accept) TextView accept;
        @BindView(R.id.list_notificationt_reject) TextView reject;
        @BindView(R.id.list_notes_main)
        LinearLayout linear_layout;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }
    }

    private void Requist(String refid,String flag){
        try {
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody>  call = request.acceptORreject(myPreferences.getPreferences(MyPreferences.ID),refid,flag);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        String json_response = response.body().string();
                        JSONObject json=new JSONObject(json_response);
                        if (json.getInt("ack") == 1)
                        {
                            NotificationIntercommunication notificationIntercommunication = (NotificationIntercommunication)context;
                            notificationIntercommunication.dataChange();
                        }
                        else
                        {
                            Toaster.show(context,""+json.getString("ack_msg"),true,Toaster.DANGER);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
