package io.github.waka.sevenhack.data.dao;

import android.database.Cursor;

import com.squareup.sqlbrite.BriteDatabase;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.data.entities.Podcast;

@Singleton
public class EpisodeDao {

    private static final String TABLE = "episodes";
    private static final String LIST_QUERY = ""
            + "SELECT * from " + TABLE + " "
            + "WHERE podcast_id = %d "
            + "ORDER BY id DESC "
            + "LIMIT %d "
            + "OFFSET %d"
            + ";";
    private static final String DOWNLOADED_LIST_QUERY = ""
            + "SELECT " + TABLE + ".* from " + TABLE + " "
            + "INNER JOIN enclosure_caches AS ec ON ec.episode_id = " + TABLE + ".id "
            + "ORDER BY id DESC "
            + "LIMIT %d "
            + "OFFSET %d"
            + ";";

    private final BriteDatabase db;

    @Inject
    public EpisodeDao(BriteDatabase db) {
        this.db = db;
    }

    public BriteDatabase.Transaction newTransaction() {
        return db.newTransaction();
    }

    public List<Episode> findAll(Podcast podcast, int limit, int offset) {
        Formatter formatter = new Formatter(new StringBuilder(), Locale.JAPANESE);
        String sql = formatter.format(LIST_QUERY, podcast.id, limit, offset).toString();

        List<Episode> items = new ArrayList<>();

        Cursor cursor = db.query(sql);
        if (cursor == null) {
            return items;
        }
        try {
            while (cursor.moveToNext()) {
                Episode episode = createFromCursor(cursor);
                items.add(episode);
            }
        } finally {
            cursor.close();
        }

        return items;
    }

    public List<Episode> findAllWithDownloads(int limit, int offset) {
        Formatter formatter = new Formatter(new StringBuilder(), Locale.JAPANESE);
        String sql = formatter.format(DOWNLOADED_LIST_QUERY, limit, offset).toString();

        List<Episode> items = new ArrayList<>();

        Cursor cursor = db.query(sql);
        if (cursor == null) {
            return items;
        }
        try {
            while (cursor.moveToNext()) {
                Episode episode = createFromCursor(cursor);
                items.add(episode);
            }
        } finally {
            cursor.close();
        }

        return items;
    }

    public long insert(Episode episode) {
        return db.insert(TABLE, episode.asContentValues());
    }

    private Episode createFromCursor(Cursor cursor) {
        Episode episode = new Episode();
        episode.id = Db.getInt(cursor, "id");
        episode.podcastId = Db.getInt(cursor, "podcast_id");
        episode.title = Db.getString(cursor, "title");
        episode.subTitle = Db.getString(cursor, "sub_title");
        episode.description = Db.getString(cursor, "description");
        episode.pubDate = Db.getString(cursor, "pub_date");
        episode.link = Db.getString(cursor, "link");
        episode.duration = Db.getString(cursor, "duration");
        episode.enclosureUrl = Db.getString(cursor, "enclosure_url");
        episode.lastPlayedAt = Db.getString(cursor, "last_played_at");
        return episode;
    }
}
