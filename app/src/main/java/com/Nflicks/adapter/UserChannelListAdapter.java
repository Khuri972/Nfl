package com.Nflicks.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nflicks.ChannelDescriptionActivity;
import com.Nflicks.CreateChannelActivity;
import com.Nflicks.R;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.model.UserChannelListModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by CRAFT BOX on 10/10/2016.
 */
public class UserChannelListAdapter extends RecyclerView.Adapter<UserChannelListAdapter.ViewHolder> {
    private ArrayList<UserChannelListModel> data;
    private Context context;
    Fragment fragment;
    private DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    AlertDialog buildInfosDialog;
    String type;

    public interface Intercommunication {
        public void AddFlickDialogOpen(String id, String channel_name, String channel_image_path, String follower);
    }

    public UserChannelListAdapter(Context context, ArrayList<UserChannelListModel> data, Fragment fragment) {
        this.data = data;
        this.context = context;
        this.fragment = fragment;
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_user_channel_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        viewHolder.name.setTypeface(FontFamily.process(context, R.raw.roboto_regular));
        viewHolder.name.setText("" + data.get(i).getName());

        if (i == 0) {
            viewHolder.create_layout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.create_layout.setVisibility(View.GONE);
        }


        if (data.get(i).getImage_path().equals("")) {

        } else {

            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.icon)
                    .showImageOnFail(R.drawable.icon)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            imageLoader.displayImage(data.get(i).getImage_path(), viewHolder.img, options);
        }

        viewHolder.add_flick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i1 = new Intent(context, ChannelDescriptionActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("channel_id", data.get(i).getId());
                    b.putSerializable("channel_name", data.get(i).getName());
                    b.putSerializable("channel_follower", data.get(i).getFollower());
                    b.putSerializable("channel_details", data.get(i).getDetail());
                    b.putSerializable("channel_image_path", data.get(i).getImage_path());
                    b.putSerializable("channel_sharable_url", data.get(i).getShareable_url());
                    b.putSerializable("channel_qr_code", data.get(i).getQr_code_path());
                    b.putSerializable("channel_privacy", data.get(i).getChannel_privacy());
                    b.putSerializable("edit_flag", "1");
                    b.putSerializable("data", data.get(i));
                    b.putString("activity_type", "0");
                    i1.putExtras(b);
                    ((Activity) context).startActivityForResult(i1, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*UserChannelListAdapter.Intercommunication intercommunication = (UserChannelListAdapter.Intercommunication)fragment;
                intercommunication.AddFlickDialogOpen(data.get(i).getId(),data.get(i).getName(),data.get(i).getImage_path(),data.get(i).getFollower());*/
            }
        });

        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(context, CreateChannelActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("data", data.get(i));
                i1.putExtra("type", "1");
                i1.putExtras(b);
                ((Activity) context).startActivityForResult(i1, 0);
            }
        });

        viewHolder.create_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, CreateChannelActivity.class);
                i.putExtra("type", "0");
                ((Activity) context).startActivityForResult(i, 0);
            }
        });

        viewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*try {
                    Intent i1 =new Intent(context, ChannelDescriptionActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("channel_id",data.get(i).getId());
                    b.putSerializable("channel_name",data.get(i).getName());
                    b.putSerializable("channel_follower",data.get(i).getFollower());
                    b.putSerializable("channel_details",data.get(i).getDetail());
                    b.putSerializable("channel_image_path",data.get(i).getImage_path());
                    b.putSerializable("channel_sharable_url",data.get(i).getShareable_url());
                    b.putSerializable("channel_qr_code",data.get(i).getQr_code_path());
                    b.putSerializable("channel_privacy",data.get(i).getChannel_privacy());
                    b.putString("activity_type","0");
                    i1.putExtras(b);
                    ((Activity) context).startActivityForResult(i1, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                try {
                    Intercommunication intercommunication = (Intercommunication) fragment;
                    intercommunication.AddFlickDialogOpen(data.get(i).getId(), data.get(i).getName(), data.get(i).getImage_path(), data.get(i).getFollower());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        viewHolder.qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View dialogView = inflater.inflate(R.layout.dialog_qr, null);
                    ImageView img = (ImageView) dialogView.findViewById(R.id.dialog_qr_image);

                    options = new DisplayImageOptions.Builder()
                            .showImageForEmptyUri(R.drawable.icon)
                            .showImageOnFail(R.drawable.icon)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();
                    imageLoader.displayImage(data.get(i).getQr_code_path(), img, options);
                    alertDialog2.setView(dialogView);
                    buildInfosDialog = alertDialog2.create();
                    buildInfosDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_channel_title)
        TextView name;
        @BindView(R.id.list_channel_img)
        CircleImageView img;
        @BindView(R.id.list_user_channel_create_channel)
        CircleImageView create_flick_img;
        @BindView(R.id.list_channel_linear)
        LinearLayout layout;
        @BindView(R.id.list_channel_linear_create)
        LinearLayout create_layout;
        @BindView(R.id.list_user_channel_add_flick)
        ImageView add_flick;
        @BindView(R.id.list_user_channel_edit)
        ImageView edit;
        @BindView(R.id.list_user_channel_qr_code)
        ImageView qr_code;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
