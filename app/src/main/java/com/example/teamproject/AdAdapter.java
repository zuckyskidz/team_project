package com.example.teamproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teamproject.models.Ad;

import java.util.List;

public class AdAdapter extends BaseAdapter {
    private Context mContext;
    //private static List<Ad> mAds;
    private static Ad[] mAds;

    public AdAdapter(Context mContext, Ad[] ads) {
        this.mContext = mContext;
        this.mAds = ads;
        Log.d("AdAdapter","Set Adapter");

    }

    @Override
    public int getCount() {
        return mAds.length;
    }

    @Override
    public Object getItem(int position) {
        return mAds[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Ad ad = mAds[position];
        ViewHolder holder;
        Log.d("AdAdapter","getView");

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_ad, parent, false);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvTitle.setText(ad.getTitle());
            convertView.setTag(holder);
            Log.d("AdAdapter","Set Holder");

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
//        TextView tvTitle = new TextView(mContext);
//        tvTitle.setText(ad.getTitle());
//        return tvTitle;
    }

    private class ViewHolder {
        private TextView tvTitle;
    }

}
