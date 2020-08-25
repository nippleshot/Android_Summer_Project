package com.example.myvideoplayer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.myvideoplayer.archivedUi.ArchivedRecyclerViewAdapter;
import com.example.myvideoplayer.database.ArchivedVideoContract;
import com.example.myvideoplayer.database.ArchivedVideoDBHelper;
import com.example.myvideoplayer.models.MediaObject;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FragmentArchived extends Fragment {
    View view;
    private ArchivedVideoDBHelper dbHelper;
    private RecyclerView recyclerView;
    public ArchivedRecyclerViewAdapter archivedAdapter;

    public FragmentArchived(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.archived_fragment, container, false);
        dbHelper = new ArchivedVideoDBHelper(view.getContext());

        recyclerView = view.findViewById(R.id.archived_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        archivedAdapter = new ArchivedRecyclerViewAdapter(initGlide());
        recyclerView.setAdapter(archivedAdapter);
        archivedAdapter.refresh(loadVideoFromDatabase());

        Bundle bundle = getArguments();
        if(bundle!=null){
            updateArchivedList(bundle.getLong("video_id"), bundle.getLong("played_until"));
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    // for communication with FragmentAll
    public void renewArchivedList(String msg){
        if(msg.equals("video added")){
            archivedAdapter.refresh(loadVideoFromDatabase());
        }
    }

    public void updateArchivedList(long video_id, long played_until){
        if(played_until!=0){
            updateVideo(video_id, played_until);
        }
    }

    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

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

    public void deleteVideo(MediaObject mediaObject) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = ArchivedVideoContract.ArchivedVideoEntry._ID + " LIKE ?";
        String[] selectionArgs = {Long.toString(mediaObject.getId())};
        int deletedRows = db.delete(ArchivedVideoContract.ArchivedVideoEntry.TABLE_NAME, selection, selectionArgs);
        Log.i("FragmentArchived", "perform delete data, result:" + deletedRows);
        archivedAdapter.refresh(loadVideoFromDatabase());
    }

    private void updateVideo(long video_id, long played_until) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ArchivedVideoContract.ArchivedVideoEntry.COLUMN_NAME_PLAYED_UNTIL, played_until);

        String selection = ArchivedVideoContract.ArchivedVideoEntry._ID + " LIKE ?";
        String[] selectionArgs = {Long.toString(video_id)};

        int updateRow = db.update(ArchivedVideoContract.ArchivedVideoEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i("FragmentArchived", "perform update data, result:" + updateRow);
        archivedAdapter.refresh(loadVideoFromDatabase());
    }

    MediaObject delete_chosen = null;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            List<MediaObject> nowDatabase = loadVideoFromDatabase();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    delete_chosen = nowDatabase.get(position);
                    deleteVideo(delete_chosen);
                    archivedAdapter.notifyItemRemoved(position);
                    Snackbar.make(recyclerView, "删除了\""+delete_chosen.getNickname()+"\"的视频", Snackbar.LENGTH_LONG).show();
                    break;
            }
        }


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.my_red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}
