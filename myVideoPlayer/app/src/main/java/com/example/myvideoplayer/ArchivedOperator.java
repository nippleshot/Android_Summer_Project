package com.example.myvideoplayer;

import com.example.myvideoplayer.models.MediaObject;

public interface ArchivedOperator {
    void deleteVideo(MediaObject mediaObject);

    void updateVideo(MediaObject mediaObject);
}
