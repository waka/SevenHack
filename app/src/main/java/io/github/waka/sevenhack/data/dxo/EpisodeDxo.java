package io.github.waka.sevenhack.data.dxo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.data.entities.Podcast;
import io.github.waka.sevenhack.data.models.Item;
import io.github.waka.sevenhack.data.models.Rss;
import io.github.waka.sevenhack.utils.DateUtil;

public class EpisodeDxo {

    public static List<Episode> convert(Podcast podcast, Rss rss) {
        List<Episode> episodes = new ArrayList<>();
        for (Item item: rss.channel.items) {
            Episode episode = new Episode();
            episode.podcastId = podcast.id;
            episode.title = item.title;
            episode.subTitle = item.getSubTitle();
            episode.description = item.description;
            episode.pubDate = item.pubDate;
            episode.link = item.link;
            episode.duration = item.getDuration();
            episode.enclosureUrl = item.enclosure.url;

            episodes.add(episode);
        }
        Collections.reverse(episodes);
        return episodes;
    }

    public static List<Episode> convertOnlyDifference(Podcast podcast, Rss rss) {
        List<Episode> episodes = new ArrayList<>();
        Episode latestEpisode = podcast.episodes.get(0);
        if (latestEpisode == null) {
            return episodes;
        }

        for (Item item: rss.channel.items) {
            if (!isAfterThanLatest(latestEpisode, item)) {
                break;
            }

            Episode episode = new Episode();
            episode.podcastId = podcast.id;
            episode.title = item.title;
            episode.subTitle = item.getSubTitle();
            episode.description = item.description;
            episode.pubDate = item.pubDate;
            episode.link = item.link;
            episode.duration = item.getDuration();
            episode.enclosureUrl = item.enclosure.url;

            episodes.add(episode);
        }
        Collections.reverse(episodes);
        return episodes;
    }

    private static boolean isAfterThanLatest(Episode episode, Item item) {
        Date latestPubDate = DateUtil.fromString(episode.pubDate);
        Date pubDate = DateUtil.fromString(item.pubDate);
        return !(latestPubDate == null || pubDate == null) && latestPubDate.before(pubDate);
    }
}
