package com.example.myvideoplayer.archivedUi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.myvideoplayer.ArchivedOperator;
import com.example.myvideoplayer.ArchivedVideoPlayerActivity;
import com.example.myvideoplayer.R;
import com.example.myvideoplayer.models.MediaObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ArchivedViewHolder extends RecyclerView.ViewHolder {
    //private final ArchivedOperator operator;

    RequestManager requestManager;
    private ImageView archived_thumbnail;
    private TextView archived_nickname;
    private TextView archived_time;
    private TextView archived_description;
    private LinearLayout archived_container;

    public ArchivedViewHolder(@NonNull View itemView) {
        super(itemView);

        archived_thumbnail = (ImageView) itemView.findViewById(R.id.archived_thumbnail);
        archived_nickname = (TextView) itemView.findViewById(R.id.archived_nickname);
        archived_time = (TextView) itemView.findViewById(R.id.archived_time);
        archived_description = (TextView) itemView.findViewById(R.id.archived_description);
        archived_container = (LinearLayout) itemView.findViewById(R.id.archived_container);
    }

    public void bind(final MediaObject mediaObject, RequestManager requestManager){
        this.requestManager = requestManager;
        this.requestManager.load(mediaObject.getThumbnail()).into(archived_thumbnail);

        archived_nickname.setText("《"+mediaObject.getNickname()+"》 的视频");

        if(mediaObject.getPlayed_until() == 0){
            String redColor = "#FFFF2243";
            archived_time.setText("还没看过");
            archived_time.setTextColor(Color.parseColor(redColor));
        }else if(mediaObject.getPlayed_until() == -2){
            String yellowColor = "FFFF9800";
            archived_time.setText("已经看完了");
            archived_time.setTextColor(Color.parseColor(yellowColor));
        } else{
            String greenColor = "#FF4CAF50";
            archived_time.setText("从" + timeChanger(mediaObject.getPlayed_until()) + "继续开始");
            archived_time.setTextColor(Color.parseColor(greenColor));
        }

       archived_description.setText(mediaObject.getDescription());

        archived_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ArchivedVideoPlayerActivity.class);
                intent.putExtra("seen_until", mediaObject.getPlayed_until());
                intent.putExtra("database_id", mediaObject.getId());
                intent.putExtra("video_link", mediaObject.getMedia_url());
                view.getContext().startActivity(intent);
            }
        });
    }


    private String timeChanger(long millionSeconds){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        c.setTimeInMillis(millionSeconds);
        return simpleDateFormat.format(c.getTime());
    }



}