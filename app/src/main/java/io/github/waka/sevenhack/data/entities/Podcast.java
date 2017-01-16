package io.github.waka.sevenhack.data.entities;

import android.content.ContentValues;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public final class Podcast {
    public int id;
    public String title;
    public String description;
    public String author;
    public String url;
    public String imageSmall;
    public String imageMedium;
    public String imageLarge;

    public List<Episode> episodes = new ArrayList<>();

    public ContentValues asContentValues() {
        ContentValues values = new ContentValues();
        values.put("title", this.title);
        values.put("description", this.description);
        values.put("author", this.author);
        values.put("url", this.url);
        values.put("image_small", this.imageSmall);
        values.put("image_medium", this.imageMedium);
        values.put("image_large", this.imageLarge);
        return values;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Podcast && ((Podcast) o).id == id || super.equals(o);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
