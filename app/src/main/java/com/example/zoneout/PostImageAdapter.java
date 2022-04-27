package com.example.zoneout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.zoneout.model.Post;

import java.util.ArrayList;

public class PostImageAdapter extends PagerAdapter {

    private Post post;

    private ArrayList<Integer> images;

    private Context ctx;
    private int[] imgs = new int[] {R.drawable.eiffel, R.drawable.tibete, R.drawable.paris};

    PostImageAdapter(Context ctx) {
        this.ctx = ctx;
        //this.images = new ArrayList<>();
    }

    PostImageAdapter(Context ctx, ArrayList<Integer> images){
        this.ctx = ctx;
        this.images = new ArrayList<>(images);
    }

    @Override
    public int getCount() {
        return imgs.length;
        //return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(ctx);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(imgs[position]);
        container.addView(imageView, 0);
        //imageView.setImageResource(images.get(position));
        //container.addView(imageView,0);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }
}
