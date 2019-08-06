package com.example.teamproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamproject.models.Ad;
import com.parse.ParseException;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private Context mContext;
    List<Ad> mAds;

    public RecyclerViewAdapter(Context context, List<Ad> ads) {
        mContext = context;
        mAds = ads;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ad_user_profile, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.title.setText(mAds.get(i).getTitle());
        //initViewFlipper(viewHolder);
        Ad ad = mAds.get(i);
        ParseFile imageFile;
        if (ad.getImages().isEmpty()) {
            imageFile = ad.getImage();
        } else {
            imageFile = ad.getImages().get(0);
        }
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeByteArray(imageFile.getData(), 0, imageFile.getData().length);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.image.setImageBitmap(bmp);
    }

    @Override
    public int getItemCount() {
        return mAds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
//        ViewFlipper viewFlipper;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvTitle);
            image = itemView.findViewById(R.id.ivImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent i = new Intent(mContext, DetailActivity.class);
                    i.putExtra(Ad.class.getSimpleName(), Parcels.wrap(mAds.get(position)));
                    mContext.startActivity(i);
                }
            });
        }
    }

    public void clear() {
        mAds.clear();
        notifyDataSetChanged();
    }
}
