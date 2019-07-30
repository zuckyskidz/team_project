package com.example.teamproject;

import android.content.Context;
import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.teamproject.models.Ad;
import com.parse.ParseException;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

import static com.parse.Parse.getApplicationContext;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private Context mContext;
    List<Ad> mAds;

    public RecyclerViewAdapter(Context context, List<Ad> ads){
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
        if( ad.getImages().isEmpty()){
            imageFile = ad.getImage();
        }
        else{
            imageFile = ad.getImages().get(0);
        }
        Bitmap bmp= null;
        try {
            bmp = BitmapFactory.decodeByteArray(imageFile.getData(), 0, imageFile.getData().length);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.image.setImageBitmap(bmp);
    }

//    private void initViewFlipper(ViewHolder viewHolder) {
//        if (viewHolder.viewFlipper != null) {
//            viewHolder.viewFlipper.setInAnimation(mContext, android.R.anim.slide_in_left);
//            viewHolder.viewFlipper.setOutAnimation(mContext, android.R.anim.slide_out_right);
//        }
//
//        if (viewHolder.viewFlipper != null) {
//            viewHolder.viewFlipper.setDisplayedChild(0);
//            for (Ad ad : mAds) {
//                for(ParseFile image : ad.getImages()){
//                    ImageView imageView = new ImageView(mContext);
//                    Bitmap bmp= null;
//                    try {
//                        bmp = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    imageView.setImageBitmap(bmp);
//                    viewHolder.viewFlipper.addView(imageView);
//                    if(ad.getImages().size() > 1){
//                        viewHolder.viewFlipper.startFlipping();
//                    }
//                }
//            }
//        }
//    }

    @Override
    public int getItemCount() {
        return mAds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView image;
//        ViewFlipper viewFlipper;

        public ViewHolder(View itemView){
            super(itemView);

            title = itemView.findViewById(R.id.tvTitle);
            image = itemView.findViewById(R.id.ivImage);

//            viewFlipper = itemView.findViewById(R.id.viewFlipper);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//                    Intent i = new Intent(mContext, DetailActivity.class);
//                    i.putExtra(Ad.class.getSimpleName(), Parcels.wrap(mAds.get(position)));
//                    mContext.startActivity(i);
//                }
//            });

        }
    }

    public void clear() {
        mAds.clear();
        notifyDataSetChanged();
    }
}
