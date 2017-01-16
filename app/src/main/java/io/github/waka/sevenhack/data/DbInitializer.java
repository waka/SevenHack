package io.github.waka.sevenhack.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class DbInitializer extends SQLiteOpenHelper {

    // if you want to upgrade, bump up this value
    private static final int VERSION = 1;

    private static final String CREATE_PODCASTS = ""
            + "CREATE TABLE podcasts ("
            + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "title TEXT NOT NULL,"
            + "description TEXT,"
            + "author TEXT NOT NULL,"
            + "url TEXT NOT NULL,"
            + "image_small  TEXT NOT NULL,"
            + "image_medium TEXT NOT NULL,"
            + "image_large  TEXT NOT NULL"
            + ");";
    private static final String CREATE_EPISODES = ""
            + "CREATE TABLE episodes ("
            + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "podcast_id INTEGER NOT NULL,"
            + "title TEXT NOT NULL,"
            + "sub_title TEXT NOT NULL,"
            + "description TEXT,"
            + "pub_date TEXT NOT NULL,"
            + "link TEXT,"
            + "duration TEXT NOT NULL,"
            + "enclosure_url TEXT NOT NULL,"
            + "last_played_at TEXT,"
            + "FOREIGN KEY(podcast_id) REFERENCES podcasts(id) ON DELETE CASCADE"
            + ");";
    private static final String CREATE_ENCLOSURE_CACHES = ""
            + "CREATE TABLE enclosure_caches ("
            + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "episode_id INTEGER NOT NULL,"
            + "path TEXT NOT NULL,"
            + "FOREIGN KEY(episode_id) REFERENCES episodes(id) ON DELETE CASCADE"
            + ");";

    public DbInitializer(Context context) {
        super(context, "seven_hack.db", null, VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PODCASTS);
        db.execSQL(CREATE_EPISODES);
        db.execSQL(CREATE_ENCLOSURE_CACHES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // write migrate sql
    }
}
