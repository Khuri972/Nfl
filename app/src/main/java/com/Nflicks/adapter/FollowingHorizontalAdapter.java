package com.Nflicks.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.Nflicks.ChannelDescriptionActivity;
import com.Nflicks.GlobalElements;
import com.Nflicks.R;
import com.Nflicks.fragment.ContactsFragment;
import com.Nflicks.model.FollowingModel;
import com.github.pavlospt.CircleView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingHorizontalAdapter extends RecyclerView.Adapter<FollowingHorizontalAdapter.ViewHolder> {

    private ArrayList<FollowingModel> followingModels = new ArrayList<>();
    Context context;
    private DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    public int section_position;
    Fragment fragment;

    public FollowingHorizontalAdapter(Context context, Fragment fragment, ArrayList<FollowingModel> apps, int section_position) {
        this.context = context;
        this.fragment = fragment;
        followingModels = apps;
        this.section_position = section_position;
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_following_horizontal, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {
            if (fragment instanceof ContactsFragment) {
                if (followingModels.get(position).getImage_path().equals("")) {
                    holder.imageView.setImageResource(R.drawable.contact_default_image);
                } else {

                    options = new DisplayImageOptions.Builder()
                            .showImageForEmptyUri(R.drawable.contact_default_image)
                            .showImageOnFail(R.drawable.contact_default_image)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();
                    imageLoader.displayImage(followingModels.get(position).getImage_path(), holder.imageView, options);
                }
            } else {
                if (followingModels.get(position).getImage_path().equals("")) {

                } else {

                    options = new DisplayImageOptions.Builder()
                            .showImageForEmptyUri(R.drawable.icon)
                            .showImageOnFail(R.drawable.icon)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();
                    imageLoader.displayImage(followingModels.get(position).getImage_path(), holder.imageView, options);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (followingModels.get(position).getCount() == 0) {
            holder.count_img.setVisibility(View.GONE);
        } else {
            holder.count_img.setTitleText("" + followingModels.get(position).getCount());
            holder.count_img.setSubtitleText("");
            holder.count_img.setVisibility(View.VISIBLE);
        }

        holder.nameTextView.setText(followingModels.get(position).getName());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    try {
                        FollowingModel f = followingModels.get(position);
                        f.setCount(0);
                        notifyItemChanged(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    GlobalElements.channel_id = followingModels.get(position).getId();
                    GlobalElements.on_resume_flag = 101;
                    Intent i1 = new Intent(context, ChannelDescriptionActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("channel_id", followingModels.get(position).getId());
                    b.putSerializable("channel_name", followingModels.get(position).getName());
                    b.putSerializable("channel_follower", followingModels.get(position).getFollower());
                    b.putSerializable("channel_details", followingModels.get(position).getDetails());
                    b.putSerializable("channel_image_path", followingModels.get(position).getImage_path());
                    b.putSerializable("channel_sharable_url", followingModels.get(position).getShareable_url());
                    b.putSerializable("channel_qr_code", followingModels.get(position).getQr_code_path());
                    b.putSerializable("channel_privacy", followingModels.get(position).getChannel_privacy());
                    b.putString("activity_type", "0");
                    i1.putExtras(b);
                    ((Activity) context).startActivityForResult(i1, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return followingModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageView;
        public TextView nameTextView;
        CircleView count_img;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (CircleImageView) itemView.findViewById(R.id.imageView);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            count_img = (CircleView) itemView.findViewById(R.id.list_following_new_count);
        }
    }
}

