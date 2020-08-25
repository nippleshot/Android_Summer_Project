package com.example.myvideoplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.myvideoplayer.R;
import com.example.myvideoplayer.archivedUi.ArchivedRecyclerViewAdapter;
import com.example.myvideoplayer.database.ArchivedVideoContract;
import com.example.myvideoplayer.database.ArchivedVideoDBHelper;
import com.example.myvideoplayer.models.MediaObject;
import com.example.myvideoplayer.util.MyJsonParser;
import com.example.myvideoplayer.util.VerticalSpacingItemDecorator;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FragmentAll extends Fragment {

    View view;
    private VideoPlayerRecyclerView mRecyclerView;
    MediaObject[] media_object_array={};
    ArrayList<MediaObject> mediaObjectsList;
    VideoPlayerRecyclerAdapter adapter;
    private ArchivedVideoDBHelper dbHelper;
    private OnFragmentInteractionListener mListener;


    public FragmentAll(){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.all_fragment, container, false);
        dbHelper = new ArchivedVideoDBHelper(view.getContext());

        mRecyclerView = (VideoPlayerRecyclerView) view.findViewById(R.id.all_recycler_view);
        if(savedInstanceState != null){
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
            mRecyclerView.addItemDecoration(itemDecorator);
            //mediaObjectsList = new ArrayList<MediaObject>(Arrays.asList(media_object_array));
            mediaObjectsList = (ArrayList<MediaObject>) savedInstanceState.getSerializable("data");
            mRecyclerView.setMediaObjects(mediaObjectsList);
            adapter = new VideoPlayerRecyclerAdapter(mediaObjectsList, initGlide());
            mRecyclerView.setAdapter(adapter);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(mRecyclerView);
        }else{
            String data_url = "https://beiyou.bytedance.com/api/invoke/video/invoke/video";
            FragmentAll.MyTask downloadTask = new FragmentAll.MyTask();
            downloadTask.execute(data_url);
        }
        return  view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = getDataFromUrl(strings[0]);
            return data;
        }

        @Override
        protected void onPostExecute(String data){
            super.onPostExecute(data);
            media_object_array = MyJsonParser.getInstance().change_to_MediaObjectArray(data);
            initRecyclerView();
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


    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        mRecyclerView.addItemDecoration(itemDecorator);
        mediaObjectsList = new ArrayList<MediaObject>(Arrays.asList(media_object_array));
        mRecyclerView.setMediaObjects(mediaObjectsList);
        adapter = new VideoPlayerRecyclerAdapter(mediaObjectsList, initGlide());
        mRecyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    MediaObject deleteMovie = null;
    List<MediaObject> currentArchived;
    boolean isExist = false;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    deleteMovie = mediaObjectsList.get(position);
                    mediaObjectsList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Snackbar.make(mRecyclerView, "删除\""+deleteMovie.getNickname()+"\"的视频", Snackbar.LENGTH_LONG).setAction("否", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mediaObjectsList.add(position, deleteMovie);
                            adapter.notifyItemInserted(position);
                        }
                    }).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    MediaObject addMovie = mediaObjectsList.get(position);

                    currentArchived = loadVideoFromDatabase();
                    for(int i=0; i<currentArchived.size(); i++){
                        if(currentArchived.get(i).getMedia_url().equals(addMovie.getMedia_url())){
                            isExist= true;
                        }
                    }
                    if(isExist){
                        adapter.notifyDataSetChanged();
                        Snackbar.make(mRecyclerView, "您已经收藏了\""+addMovie.getNickname()+"\"的视频", Snackbar.LENGTH_LONG).show();
                        isExist = false;
                    }else{
                        saveVideo2Database(addMovie);
                        if(mListener!=null){
                            mListener.onFragmentInteraction("video added");
                        }
                        mediaObjectsList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Snackbar.make(mRecyclerView, "收藏了\""+addMovie.getNickname()+"\"的视频", Snackbar.LENGTH_LONG).show();
                    }
                    break;
            }
        }


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.my_red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_archive_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
    public List<MediaObject> loadVideoFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_NICKNAME,
                ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_MEDIA_URL,
                ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_THUMBNAIL,
                ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_DESCRIPTION,
                ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_PLAYED_UNTIL
        };

        List<MediaObject> videosFromDb = new ArrayList<>();
        Cursor cursor = db.query(
                ArchivedVideoContract.ArchivedVideoEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        while (cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(ArchivedVideoContract.ArchivedVideoEntry._ID));
            String nickname = cursor.getString(cursor.getColumnIndex(ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_NICKNAME));
            String media_url = cursor.getString(cursor.getColumnIndex(ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_MEDIA_URL));
            String thumbnail = cursor.getString(cursor.getColumnIndex(ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_THUMBNAIL));
            String description = cursor.getString(cursor.getColumnIndex(ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_DESCRIPTION));
            long played_until = cursor.getLong(cursor.getColumnIndex(ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_PLAYED_UNTIL));
            MediaObject videoFromDb = new MediaObject(id, nickname, media_url, thumbnail, description, -1, played_until);
            videosFromDb.add(videoFromDb);

        }
        cursor.close();
        return videosFromDb;
    }

    private boolean saveVideo2Database(MediaObject mediaObject) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_NICKNAME, mediaObject.getNickname());
        values.put(ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_MEDIA_URL, mediaObject.getMedia_url());
        values.put(ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_THUMBNAIL, mediaObject.getThumbnail());
        values.put(ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_DESCRIPTION, mediaObject.getDescription());
        values.put(ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_PLAYED_UNTIL, 0);

        long newRowId = db.insert(ArchivedVideoContract.ArchivedVideoEntry.TABLE_NAME, null, values);
        Log.i("FragmentAll", "perform add data, result:" + newRowId);


        if(newRowId>0){
            return true;
        }else {
            return false;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String text);
    }



    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelableArrayList("data", mediaObjectsList);
        outState.putSerializable("data", mediaObjectsList);
    }

    @Override
    public void onDestroy() {
        if(mRecyclerView!=null)
            mRecyclerView.releasePlayer();
        super.onDestroy();
    }
}
