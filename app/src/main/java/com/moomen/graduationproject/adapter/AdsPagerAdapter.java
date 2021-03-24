package com.moomen.graduationproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.moomen.graduationproject.R;
import com.moomen.graduationproject.model.Ads;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AdsPagerAdapter extends PagerAdapter {

    private ArrayList<Ads> pagesArrayList;
    private Context context;
    //Auto Image Slider with ViewPager
    private OnAdsClickListener mOnAdsClickListener;

    private int custum_position = 0;


    public AdsPagerAdapter(Context context, ArrayList<Ads> pagesArrayList) {
        this.context = context;
        this.pagesArrayList = pagesArrayList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        if (custum_position >pagesArrayList.size()-1){
            custum_position = 0;
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View slidLayout = inflater.inflate(R.layout.ads_item, null);
        Ads currentAds = pagesArrayList.get(custum_position);
        custum_position++;
        ImageView imageAds = slidLayout.findViewById(R.id.imageView_ads);
        ImageView edit = slidLayout.findViewById(R.id.imageView_edit);
        edit.setVisibility(View.GONE);
        ImageView delete = slidLayout.findViewById(R.id.imageView_delete);
        delete.setVisibility(View.GONE);
        Picasso.get().load(currentAds.getImage()).into(imageAds);
        container.addView(slidLayout);
        return slidLayout;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public void setOnAdsClickListener(OnAdsClickListener onAdsClickListener) {
        this.mOnAdsClickListener = onAdsClickListener;
    }

    public interface OnAdsClickListener {
        void setOnAdsClickListener(ViewGroup view, int position);
    }
}

