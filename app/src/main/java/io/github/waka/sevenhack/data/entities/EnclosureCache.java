package io.github.waka.sevenhack.data.entities;

import android.content.ContentValues;

import org.parceler.Parcel;

@Parcel
public final class EnclosureCache {
    public int id;
    public int episodeId;
    public String path;

    public ContentValues asContentValues() {
        ContentValues values = new ContentValues();
        values.put("episode_id", this.episodeId);
        values.put("path", this.path);
        return values;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EnclosureCache && ((EnclosureCache) o).id == id || super.equals(o);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
