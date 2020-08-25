package com.example.myvideoplayer.archivedUi;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.RequestManager;
import com.example.myvideoplayer.ArchivedOperator;
import com.example.myvideoplayer.R;
import com.example.myvideoplayer.models.MediaObject;

import java.util.ArrayList;
import java.util.List;

public class ArchivedRecyclerViewAdapter extends RecyclerView.Adapter<ArchivedViewHolder> {

    List<MediaObject> archivedData = new ArrayList<>();
    //ArchivedOperator archivedOperator;
    private RequestManager requestManager;


    public ArchivedRecyclerViewAdapter(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    public void refresh(List<MediaObject> newArchivedVideos){
        archivedData.clear();
        if(newArchivedVideos != null){
            archivedData.addAll(newArchivedVideos);
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ArchivedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_archived_video_list, parent, false);
        return new ArchivedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchivedViewHolder holder, int position) {
        holder.bind(archivedData.get(position), requestManager);
    }

    @Override
    public int getItemCount() {
        return archivedData.size();
    }
}
