package com.Nflicks.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nflicks.R;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.model.CategoryModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CRAFT BOX on 10/10/2016.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private ArrayList<CategoryModel> data;
    private Context context;
    private DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public CategoryAdapter(Context context, ArrayList<CategoryModel> data) {
        this.data = data;
        this.context = context;
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    public interface intercommunication{
        public void CategorySelect();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_category, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        viewHolder.name.setTypeface(FontFamily.process(context,R.raw.roboto_regular));
        viewHolder.name.setText(""+data.get(i).getName());

        if(data.get(i).getId().equals(""))
        {
            viewHolder.footer_layout.setVisibility(View.VISIBLE);
            viewHolder.frameLayout.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.footer_layout.setVisibility(View.GONE);
            viewHolder.frameLayout.setVisibility(View.VISIBLE);
        }

        if(data.get(i).isFlag())
        {
            viewHolder.select_img.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.select_img.setVisibility(View.INVISIBLE);
        }

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data.get(i).isFlag())
                {
                    CategoryModel da=data.get(i);
                    da.setFlag(false);
                    data.set(i,da);
                    notifyDataSetChanged();
                    CategoryAdapter.intercommunication intercommunication =(CategoryAdapter.intercommunication)context;
                    intercommunication.CategorySelect();
                }
                else
                {
                    CategoryModel da=data.get(i);
                    da.setFlag(true);
                    data.set(i,da);
                    notifyDataSetChanged();
                    CategoryAdapter.intercommunication intercommunication =(CategoryAdapter.intercommunication)context;
                    intercommunication.CategorySelect();
                }
            }
        });

        if(data.get(i).getImage_path().equals(""))
        {
            System.out.print("");
        }
        else
        {
            String im=data.get(i).getImage_path();
            System.out.print(""+im);

            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.icon)
                    .showImageOnFail(R.drawable.icon)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            imageLoader.displayImage(data.get(i).getImage_path(), viewHolder.img, options);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.list_category_name) TextView name;
        @BindView(R.id.list_category_img)
        com.github.siyamed.shapeimageview.RoundedImageView img;
        @BindView(R.id.list_category_select_img) ImageView select_img;
        @BindView(R.id.list_category_fram_layout)
        FrameLayout frameLayout;
        @BindView(R.id.list_category_footer)
        LinearLayout footer_layout;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

}
