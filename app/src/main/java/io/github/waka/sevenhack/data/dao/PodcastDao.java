package io.github.waka.sevenhack.data.dao;

import android.database.Cursor;

import com.squareup.sqlbrite.BriteDatabase;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.waka.sevenhack.data.entities.Podcast;

@Singleton
public class PodcastDao {

    private static final String TABLE = "podcasts";
    private static final String LIST_QUERY = "SELECT * from " + TABLE + ";";
    private static final String FIND_QUERY = "SELECT count(*) cnt from " + TABLE + " WHERE url = \"%s\";";

    private final BriteDatabase db;

    @Inject
    public PodcastDao(BriteDatabase db) {
        this.db = db;
    }

    public BriteDatabase.Transaction newTransaction() {
        return db.newTransaction();
    }

    public List<Podcast> findAll() {
        List<Podcast> items = new ArrayList<>();

        Cursor cursor = db.query(LIST_QUERY);
        if (cursor == null) {
            return items;
        }
        try {
            while (cursor.moveToNext()) {
                Podcast podcast = new Podcast();
                podcast.id = Db.getInt(cursor, "id");
                podcast.title = Db.getString(cursor, "title");
                podcast.author = Db.getString(cursor, "author");
                podcast.url = Db.getString(cursor, "url");
                podcast.imageSmall = Db.getString(cursor, "image_small");
                podcast.imageMedium = Db.getString(cursor, "image_medium");
                podcast.imageLarge = Db.getString(cursor, "image_large");
                items.add(podcast);
            }
        } finally {
            cursor.close();
        }

        return items;
    }

    public long countByURL(String url) {
        Formatter formatter = new Formatter(new StringBuilder(), Locale.JAPANESE);
        String sql = formatter.format(FIND_QUERY, url).toString();

        long count = 0;

        Cursor cursor = db.query(sql);
        if (cursor == null) {
            return count;
        }
        try {
            while (cursor.moveToNext()) {
                count = cursor.getLong(cursor.getColumnIndexOrThrow("cnt"));
                if (cursor.moveToNext()) {
                    throw new IllegalStateException("Cursor returned more than 1 row");
                }
            }
        } finally {
            cursor.close();
        }

        return count;
    }

    public long insert(Podcast podcast) {
        return db.insert(TABLE, podcast.asContentValues());
    }

    public long delete(Podcast podcast) {
        return db.delete(TABLE, "id = ?", String.valueOf(podcast.id));
    }
}
