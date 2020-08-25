package com.example.myvideoplayer;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.myvideoplayer.models.MediaObject;

public class VideoPlayerViewHolder extends RecyclerView.ViewHolder {

    FrameLayout media_container;
    TextView nickname;
    TextView likecount;
    ImageView thumbnail, volumeControl;
    ProgressBar progressBar;
    View parent;
    RequestManager requestManager;

    public VideoPlayerViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        media_container = itemView.findViewById(R.id.media_container);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        thumbnail.setPadding(15,0,15,20);

        nickname = itemView.findViewById(R.id.nickname);
        likecount = itemView.findViewById(R.id.like_count);

        progressBar = itemView.findViewById(R.id.progressBar);
    }

    public void onBind(final MediaObject mediaObject, RequestManager requestManager) {
        this.requestManager = requestManager;
        parent.setTag(this);
        nickname.setText(mediaObject.getNickname());
        if(mediaObject.getLike_count()>=1000000){
            int setLike = mediaObject.getLike_count()/1000000;
            likecount.setText(" "+String.valueOf(setLike) +"M");
        }else if(mediaObject.getLike_count()>=1000){
            int setLike = mediaObject.getLike_count()/1000;
            likecount.setText(" "+String.valueOf(setLike) +"K");
        }else{
            likecount.setText(" "+String.valueOf(mediaObject.getLike_count()));
        }

        this.requestManager
                .load(mediaObject.getThumbnail())
                .into(thumbnail);

        media_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ExoPlayerActivity.class);
                intent.putExtra("video_link", mediaObject.getMedia_url());
                intent.putExtra("nickname", mediaObject.getNickname());
                intent.putExtra("like_count", mediaObject.getLike_count());
                intent.putExtra("description", mediaObject.getDescription());
                view.getContext().startActivity(intent);
            }
        });
    }

}
