package io.github.waka.sevenhack.data.dxo;

import io.github.waka.sevenhack.data.entities.Podcast;
import io.github.waka.sevenhack.data.models.Channel;
import io.github.waka.sevenhack.data.models.SearchResult;
import io.github.waka.sevenhack.data.models.Rss;

public class PodcastDxo {

    public static Podcast convert(SearchResult searchResult, Rss rss) {
        Channel channel = rss.channel;

        Podcast podcast = new Podcast();
        podcast.title = channel.title;
        podcast.description = channel.getDescription();
        podcast.author = channel.author;
        podcast.url = searchResult.url;
        podcast.imageSmall = searchResult.imageSmall;
        podcast.imageMedium = searchResult.imageMedium;
        podcast.imageLarge = searchResult.imageLarge;

        return podcast;
    }
}
