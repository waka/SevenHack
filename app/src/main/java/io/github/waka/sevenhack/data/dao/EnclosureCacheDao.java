package io.github.waka.sevenhack.data.dao;

import android.database.Cursor;
import android.text.TextUtils;

import com.squareup.sqlbrite.BriteDatabase;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.waka.sevenhack.data.entities.EnclosureCache;
import io.github.waka.sevenhack.data.entities.Episode;

@Singleton
public class EnclosureCacheDao {

    private static final String TABLE = "enclosure_caches";
    private static final String LIST_QUERY = ""
            + "SELECT * from " + TABLE + " "
            + "WHERE episode_id in (%s)"
            + ";";
    private static final String FIND_QUERY = ""
            + "SELECT * from " + TABLE + " "
            + "WHERE episode_id = %d "
            + "LIMIT 1"
            + ";";

    private final BriteDatabase db;

    @Inject
    public EnclosureCacheDao(BriteDatabase db) {
        this.db = db;
    }

    public List<EnclosureCache> findAll(List<Episode> episodes) {
        List<Integer> ids = new ArrayList<>();
        for (Episode episode : episodes) {
            ids.add(episode.id);
        }
        Formatter formatter = new Formatter(new StringBuilder(), Locale.JAPANESE);
        String sql = formatter.format(LIST_QUERY, TextUtils.join(", ", ids)).toString();

        List<EnclosureCache> items = new ArrayList<>();

        Cursor cursor = db.query(sql);
        if (cursor == null) {
            return items;
        }
        try {
            while (cursor.moveToNext()) {
                EnclosureCache cache = createFromCursor(cursor);
                items.add(cache);
            }
        } finally {
            cursor.close();
        }

        return items;
    }

    public EnclosureCache find(Episode episode) {
        Formatter formatter = new Formatter(new StringBuilder(), Locale.JAPANESE);
        String sql = formatter.format(FIND_QUERY, episode.id).toString();

        EnclosureCache cache = null;

        Cursor cursor = db.query(sql);
        if (cursor == null) {
            return null;
        }
        try {
            while (cursor.moveToNext()) {
                cache = createFromCursor(cursor);
                if (cursor.moveToNext()) {
                    throw new IllegalStateException("Cursor returned more than 1 row");
                }
            }
        } finally {
            cursor.close();
        }

        return cache;
    }

    public long insert(EnclosureCache cache) {
        return db.insert(TABLE, cache.asContentValues());
    }

    public long delete(EnclosureCache cache) {
        return db.delete(TABLE, "id = ?", String.valueOf(cache.id));
    }

    private EnclosureCache createFromCursor(Cursor cursor) {
        EnclosureCache cache = new EnclosureCache();
        cache.id = Db.getInt(cursor, "id");
        cache.episodeId = Db.getInt(cursor, "episode_id");
        cache.path = Db.getString(cursor, "path");
        return cache;
    }
}
