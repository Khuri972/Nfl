package com.Nflicks.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nflicks.ChannelListActivity;
import com.Nflicks.R;
import com.Nflicks.gravitysnaphelper.GravitySnapHelper;
import com.Nflicks.interfacess.ItemTouchHelperAdapter;
import com.Nflicks.interfacess.ItemTouchHelperViewHolder;
import com.Nflicks.interfacess.OnDashboardChangedListener;
import com.Nflicks.interfacess.OnStartDragListener;
import com.Nflicks.model.FollowingCategoryModel;

import java.util.ArrayList;
import java.util.Collections;

public class SearchVerticalAdapter extends RecyclerView.Adapter<SearchVerticalAdapter.ViewHolder> implements GravitySnapHelper.SnapListener {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    Context context;
    private ArrayList<FollowingCategoryModel> followingCategoryModels;

    public SearchVerticalAdapter(Context context, ArrayList<FollowingCategoryModel> followingCategoryModels) {
        this.followingCategoryModels = followingCategoryModels;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        FollowingCategoryModel snap = followingCategoryModels.get(position);
        switch (snap.getGravity()) {
            case Gravity.CENTER_VERTICAL:
                return VERTICAL;
            case Gravity.CENTER_HORIZONTAL:
                return HORIZONTAL;
            case Gravity.START:
                return HORIZONTAL;
            case Gravity.TOP:
                return VERTICAL;
            case Gravity.END:
                return HORIZONTAL;
            case Gravity.BOTTOM:
                return VERTICAL;
        }
        return HORIZONTAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_following_vertical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FollowingCategoryModel snap = followingCategoryModels.get(position);
        holder.snapTextView.setText(snap.getText());

        if (position == 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(30, 25, 30, 15);
            holder.cardView.setLayoutParams(params);
        } else if (followingCategoryModels.size() == position + 1) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(30, 15, 30, 30);
            holder.cardView.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(30, 15, 30, 15);
            holder.cardView.setLayoutParams(params);
        }

        if (snap.getGravity() == Gravity.CENTER_HORIZONTAL) {
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
                    .recyclerView.getContext(), snap.getGravity() == Gravity.CENTER_HORIZONTAL ?
                    LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL, false));
            //new LinearSnapHelper().attachToRecyclerView(holder.recyclerView);
        }

        holder.recyclerView.setAdapter(new FollowingHorizontalAdapter(context, null, snap.getApps(), position));

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(context, ChannelListActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("following_chnnel", followingCategoryModels.get(position).getApps());
                    i.putExtras(b);
                    i.putExtra("category_title", snap.getText());
                    i.putExtra("fragment", "SearchActivity");
                    i.putExtra("cnid", "" + snap.getId());
                    i.putExtra("position", position);
                    ((Activity) context).startActivityForResult(i, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return followingCategoryModels.size();
    }

    @Override
    public void onSnap(int position) {
        Log.d("Snapped: ", position + "");
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public TextView snapTextView, more;
        public RecyclerView recyclerView;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            snapTextView = (TextView) itemView.findViewById(R.id.snapTextView);
            more = (TextView) itemView.findViewById(R.id.list_more);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}

