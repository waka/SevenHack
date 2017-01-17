package io.github.waka.sevenhack.data.entities;

import android.content.ContentValues;

import org.parceler.Parcel;

@Parcel
public class Episode {

    public int id;
    public int podcastId;
    public String title;
    public String subTitle;
    public String description;
    public String pubDate;
    public String link;
    public String duration;
    public String enclosureUrl;
    public String lastPlayedAt;

    public EnclosureCache enclosureCache;
    public boolean isNewly = false;

    public ContentValues asContentValues() {
        ContentValues values = new ContentValues();
        values.put("podcast_id", this.podcastId);
        values.put("title", this.title);
        values.put("sub_title", this.subTitle);
        values.put("description", this.description);
        values.put("pub_date", this.pubDate);
        values.put("link", this.link);
        values.put("duration", this.duration);
        values.put("enclosure_url", this.enclosureUrl);
        values.put("last_played_at", this.lastPlayedAt);
        return values;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Episode && ((Episode) o).id == id || super.equals(o);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
