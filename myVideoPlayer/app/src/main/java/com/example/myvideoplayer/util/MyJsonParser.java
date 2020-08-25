package com.example.myvideoplayer.util;

import com.example.myvideoplayer.models.MediaObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



public class MyJsonParser {
    final static MyJsonParser myParser = new MyJsonParser();

    public static MyJsonParser getInstance(){
        return myParser;
    }

    public MediaObject[] change_to_MediaObjectArray(String json){
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = (JsonArray) jsonParser.parse(json);

        MediaObject[] data = new MediaObject[jsonArray.size()];
        for(int i=0; i<jsonArray.size(); i++){
            JsonObject object = (JsonObject) jsonArray.get(i);
            // fixing "http://" into "https://" (fixed exoplayer error)
            String fixFeedUrl = object.get("feedurl").getAsString();
            String[] stayFeedUrl = fixFeedUrl.split(":");
            String fixedFeedUrl = "https:"+stayFeedUrl[1];


            String fixAvatarUrl = object.get("avatar").getAsString();
            String[] stayAvatarUrl = fixAvatarUrl.split(":");
            String fixedAvatarUrl = "https:"+stayAvatarUrl[1];

            MediaObject each_data = new MediaObject(
                    -1,
                    object.get("nickname").getAsString(),
                    fixedFeedUrl,
                    fixedAvatarUrl,
                    object.get("description").getAsString(),
                    object.get("likecount").getAsInt(),
                    -1
            );

            data[i] = each_data;
        }

        return data;
    }

}