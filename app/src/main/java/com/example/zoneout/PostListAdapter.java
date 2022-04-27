package com.example.zoneout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zoneout.model.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {

    private List<Post> data;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;
    private Bundle bundle;


    public PostListAdapter(Context context, List<Post> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        this.bundle = new Bundle();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.post_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = data.get(position);

        holder.cover.setImageResource(R.drawable.tibete);
        //holder.ratingBar.setRating(post.getRating());
        holder.owner.setText(post.getOwner());

        /*StorageReference pathImg = MainActivity.storageReference.child("25deabril1.jpg");

        pathImg.getBytes(1024*1024*10)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        holder.cover.setImageBitmap(bitmap);
                    }
                });*/
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView cover;
        RatingBar ratingBar;
        TextView owner;

        public ViewHolder(View itemView) {
            super(itemView);
            cover = (ImageView) itemView.findViewById(R.id.coverImage);
            owner = (TextView)  itemView.findViewById(R.id.postOwner);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListener != null){

                int position = getAdapterPosition();
                clickListener.onItemClick(v, position);
            }
        }
    }

    public Post getItem(int id){
        return data.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
