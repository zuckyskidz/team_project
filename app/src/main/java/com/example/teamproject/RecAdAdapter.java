package com.example.teamproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.teamproject.models.Ad;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class RecAdAdapter extends RecyclerView.Adapter<RecAdAdapter.MasonryView> {
    private static List<Ad> mAds;
    private Context context;

    class MasonryView extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public MasonryView(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.ivAdImage);
            textView = (TextView) itemView.findViewById(R.id.tvTitle);

        }
    }

    public RecAdAdapter(List<Ad> ads, Context context) {
        this.context = context;
        this.mAds = ads;
    }

    @Override
    public MasonryView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false);
        MasonryView masonryView = new MasonryView(layoutView);
        Log.d("RecAdAdapter","getView");
        return masonryView;
    }

    @Override
    public void onBindViewHolder(MasonryView holder, int position) {
        Ad ad = mAds.get(position);

        ParseFile imageFile = ad.getImage();
        String imageURL = null;
        try {
            imageURL = imageFile.getUrl();
        } catch (NullPointerException e) {

        }

        Glide.with(context)
                .load(imageURL)
                .apply(new RequestOptions()
                        //.override(800,800)
                        .transform(new RoundedCorners(50))
                        .fitCenter()
                        //.centerCrop()
                        .placeholder(R.drawable.dog))
                .into(holder.imageView);

        holder.textView.setText(ad.getTitle());
        Log.d("RecAdAdapter","Set Holder");
    }

    @Override
    public int getItemCount() {
        return mAds.size();
    }
}
