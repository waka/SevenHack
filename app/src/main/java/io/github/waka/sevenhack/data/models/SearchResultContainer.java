package io.github.waka.sevenhack.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class SearchResultContainer {
    @SerializedName("resultCount")
    public int count;

    @SerializedName("results")
    public List<SearchResult> searchResults;
}
