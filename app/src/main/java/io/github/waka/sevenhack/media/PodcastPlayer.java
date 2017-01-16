package io.github.waka.sevenhack.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.events.PlayerEvent;
import io.github.waka.sevenhack.events.PlayerEventProvider;
import io.github.waka.sevenhack.utils.FileUtil;

public class PodcastPlayer extends MediaPlayer implements MediaPlayer.OnPreparedListener {
    private enum Status {
        PREPARED,
        PLAYING,
        PAUSED,
        STOPPED
    }

    private static PodcastPlayer instance;

    private Status status = Status.STOPPED;
    private Episode playingItem;
    private TickTimer tickTimer;

    public static PodcastPlayer getInstance() {
        if (instance == null) {
            instance = new PodcastPlayer();
        }
        return instance;
    }

    private PodcastPlayer() {
        super();
    }

    public Episode getPlayingEpisode() {
        return playingItem;
    }

    @Override
    public boolean isPlaying() {
        return status == Status.PLAYING;
    }

    public boolean isPaused() {
        return status == Status.PAUSED;
    }

    boolean isPlayingEpisode(Episode episode) {
        return playingItem != null && episode.equals(playingItem);
    }

    public void play(Context context, Episode episode) {
        if (episode.enclosureUrl == null) return;
        playingItem = episode;

        reset();

        try {
            if (episode.enclosureCache != null) {
                File file = FileUtil.getEnclosureCacheFile(context, episode);
                if (file == null) {
                    return;
                }
                setDataSource(file.getAbsolutePath());
            } else {
                setDataSource(context.getApplicationContext(), Uri.parse(episode.enclosureUrl));
            }
            prepareAsync();
            setOnPreparedListener(this);
        } catch (IOException e) {
            // do nothing
        }
    }

    void setTickListener(final TickListener tickListener) {
        tickTimer = new TickTimer();
        tickTimer.start(() -> {
            if (isPlaying() || isPaused()) {
                tickListener.onTick(getCurrentPosition());
            } else {
                tickListener.onTick(0);
            }
        });
    }

    @Override
    public void start() {
        restart();
    }

    void restart() {
        super.start();
        status = Status.PLAYING;

        PlayerEventProvider.getInstance().send(PlayerEvent.PLAY);
    }

    @Override
    public void pause() {
        super.pause();
        status = Status.PAUSED;
    }

    @Override
    public void stop() {
        super.pause();
        super.seekTo(0);
        status = Status.STOPPED;

        playingItem = null;
        stopTick();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        status = Status.PREPARED;
        start();
        status = Status.PLAYING;
    }

    void stopTick() {
        if (tickTimer != null) {
            tickTimer.cancel();
            tickTimer = null;
        }
    }

    private class TickTimer extends Timer {
        private boolean stopped;
        private boolean isRunning = false;

        private void start(Callback callback) {
            stopped = false;
            if (isRunning) return;
            isRunning = true;

            scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!stopped) {
                        callback.tick();
                    }
                }
            }, 1000, 1000);
        }
    }

    private interface Callback {
        void tick();
    }

    public interface TickListener {
        void onTick(int currentPosition);
    }
}