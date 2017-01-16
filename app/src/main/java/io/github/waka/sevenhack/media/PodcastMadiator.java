package io.github.waka.sevenhack.media;

import android.content.Context;

import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.events.PlayerEventProvider;
import io.github.waka.sevenhack.events.PlayerEvent;
import io.github.waka.sevenhack.views.notifications.PodcastPlayerNotification;
import io.reactivex.Observable;

public class PodcastMadiator {

    private final PodcastPlayer player;

    public PodcastMadiator() {
        player = PodcastPlayer.getInstance();
    }

    public PodcastMadiator(PodcastPlayer.TickListener tickListener) {
        player = PodcastPlayer.getInstance();

        player.setTickListener(tickListener);
    }

    public void play(Context context, Episode episode) {
        player.play(context, episode);
        PodcastPlayerNotification.notify(context, episode);

        // event in PodcastPlayer
    }

    public void replay(Context context, Episode episode) {
        player.restart();
        PodcastPlayerNotification.notify(context, episode);

        // event in PodcastPlayer
    }

    public void pause(Context context, Episode episode) {
        player.pause();
        PodcastPlayerNotification.notify(context, episode, player.getCurrentPosition());

        PlayerEventProvider.getInstance().send(PlayerEvent.PAUSE);
    }

    public void stop(Context context) {
        player.stop();
        PodcastPlayerNotification.cancel(context);

        PlayerEventProvider.getInstance().send(PlayerEvent.STOP);
    }

    public void stopTick() {
        player.stopTick();
    }

    public void seek(int progress) {
        player.seekTo(progress);
    }

    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public void notifyUpdate(Context context, Episode episode, int currentPosition) {
        PodcastPlayerNotification.notify(context, episode, currentPosition);
    }

    public Episode getPlayingEpisode() {
        return player.getPlayingEpisode();
    }

    public boolean isPlayingEpisode(Episode episode) {
        return (isPlaying() || isPaused()) && player.isPlayingEpisode(episode);
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public boolean isPaused() {
        return player.isPaused();
    }

    public Observable<PlayerEvent> getObservable() {
        return PlayerEventProvider.getInstance()
                .toObservable()
                .map(o -> (PlayerEvent) o);
    }
}
