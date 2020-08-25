package com.example.myvideoplayer.util;

import com.example.myvideoplayer.R;
import com.example.myvideoplayer.models.MediaObject;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class Resources {
    public static MediaObject[] MEDIA_OBJECTS;

//    public static final MediaObject[] MEDIA_OBJECTS = {
//            new MediaObject("王火火",
//                    "https://jzvd.nathen.cn/video/1137e480-170bac9c523-0007-1823-c86-de200.mp4",
//                    "https://jzvd.nathen.cn/snapshot/f402a0e012b14d41ad07939746844c5e00005.jpg",
//                    "这是第一条Feed数据",
//                    10000),
//            new MediaObject("LILILI",
//                    "https://jzvd.nathen.cn/video/e0bd348-170bac9c3b8-0007-1823-c86-de200.mp4",
//                    "https://jzvd.nathen.cn/snapshot/8bd6d06878fc4676a62290cbe8b5511f00005.jpg",
//                    "这是一条一起学猫叫的视频",
//                    120000),
//            new MediaObject("新闻启示录",
//                    "https://jzvd.nathen.cn/video/2f03c005-170bac9abac-0007-1823-c86-de200.mp4",
//                    "https://jzvd.nathen.cn/snapshot/371ddcdf7bbe46b682913f3d3353192000005.jpg",
//                    "赶紧把这个转发给你们的女朋友吧，这才是对她们最负责的AI",
//                    45000000),
//            new MediaObject("综艺大咖秀",
//                    "https://jzvd.nathen.cn/video/7bf938c-170bac9c18a-0007-1823-c86-de200.mp4",
//                    "https://jzvd.nathen.cn/snapshot/dabe6ca3c71942fd926a86c8996d750f00005.jpg",
//                    "男明星身高暴击",
//                    98777000),
//            new MediaObject("南翔不爱吃饭",
//                    "https://jzvd.nathen.cn/video/47788f38-170bac9ab8a-0007-1823-c86-de200.mp4",
//                    "https://jzvd.nathen.cn/snapshot/edac56544e2f43bb827bd0e819db381000005.jpg",
//                    "挑战手抓饼的一百种吃法第七天",
//                    500000),
//    };
    public void getData(String url){
        String jsonData = getDataFromUrl(url);
        MediaObject[] media_object_array = MyJsonParser.getInstance().change_to_MediaObjectArray(jsonData);
        MEDIA_OBJECTS = media_object_array;
    }

    private String getDataFromUrl(String myUrl){
        BufferedReader bufferedReader;
        String jsonData = "";

        try{
            String requestUrl = myUrl;
            URL url = new URL(requestUrl);
            URLConnection connection = url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                jsonData = jsonData + inputLine;
            }

        } catch (MalformedURLException urlException) {
            urlException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally{

        }

        return jsonData;
    }
}
