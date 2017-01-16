package io.github.waka.sevenhack.data.models;

import com.google.gson.annotations.SerializedName;

public final class SearchResult {

    @SerializedName("collectionId")
    public int collectionId;

    @SerializedName("collectionName")
    public String title;

    @SerializedName("feedUrl")
    public String url;

    @SerializedName("artworkUrl30")
    public String imageSmall;

    @SerializedName("artworkUrl100")
    public String imageMedium;

    @SerializedName("artworkUrl600")
    public String imageLarge;
}
