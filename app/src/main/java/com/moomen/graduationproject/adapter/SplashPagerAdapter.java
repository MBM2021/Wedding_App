package com.moomen.graduationproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.moomen.graduationproject.R;
import com.moomen.graduationproject.model.SplashImage;

import java.util.ArrayList;


public class SplashPagerAdapter extends PagerAdapter {

    private ArrayList<SplashImage> pagesArrayList;
    private Context context;
    //Auto Image Slider with ViewPager

    public SplashPagerAdapter(Context context, ArrayList<SplashImage> pagesArrayList) {
        this.context = context;
        this.pagesArrayList = pagesArrayList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View slidLayout = inflater.inflate(R.layout.splash_item_layout, null);
        SplashImage currentPage = pagesArrayList.get(position);
        TextView title = slidLayout.findViewById(R.id.textView_title_id);
        TextView desc = slidLayout.findViewById(R.id.textView_desc_id);
        ImageView image = slidLayout.findViewById(R.id.imageView);
        title.setText(currentPage.getTitle());
        desc.setText(currentPage.getDisc());
        image.setImageResource(currentPage.getImage());
        /*title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSplashImageClickListener.setOnSplashImageClickListener(container, position);
            }
        });*/
        container.addView(slidLayout);
        return slidLayout;
    }

    @Override
    public int getCount() {
        return pagesArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private OnSplashImageClickListener mOnSplashImageClickListener;

    public interface OnSplashImageClickListener {
        void setOnSplashImageClickListener(ViewGroup view, int position);
    }

    public void setOnSplashImageClickListener(OnSplashImageClickListener onSplashImageClickListener) {
        this.mOnSplashImageClickListener = onSplashImageClickListener;
    }
}

