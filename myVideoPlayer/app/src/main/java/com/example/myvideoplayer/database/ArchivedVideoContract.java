package com.example.myvideoplayer.database;

import android.provider.BaseColumns;

public class ArchivedVideoContract {

    private ArchivedVideoContract(){

    }

    public static final String SQL_CREATE_ENTRY =
            "CREATE TABLE " + ArchivedVideoEntry.TABLE_NAME + " (" +
                    ArchivedVideoEntry._ID + " INTEGER PRIMARY KEY," +
                    ArchivedVideoEntry.COLUMN_NAME_NICKNAME + " TEXT," +
                    ArchivedVideoEntry.COLUMN_NAME_MEDIA_URL + " TEXT," +
                    ArchivedVideoEntry.COLUMN_NAME_THUMBNAIL + " TEXT," +
                    ArchivedVideoEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    ArchivedVideoEntry.COLUMN_NAME_LIKE_COUNT + " INTEGER," +
                    ArchivedVideoEntry.COLUMN_NAME_PLAYED_UNTIL + " INTEGER)";

    public static class  ArchivedVideoEntry implements BaseColumns {

        public static final String TABLE_NAME = "archived_video_entry";

        public static final String COLUMN_NAME_NICKNAME = "nickname";

        public static final String COLUMN_NAME_MEDIA_URL = "media_url";

        public static final String COLUMN_NAME_THUMBNAIL = "thumbnail";

        public static final String COLUMN_NAME_DESCRIPTION = "description";

        public static final String COLUMN_NAME_LIKE_COUNT = "like_count";

        public static final String COLUMN_NAME_PLAYED_UNTIL = "played_until";
    }
}
