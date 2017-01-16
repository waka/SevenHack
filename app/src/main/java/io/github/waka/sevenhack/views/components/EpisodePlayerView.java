package io.github.waka.sevenhack.views.components;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.data.models.PaletteColor;
import io.github.waka.sevenhack.databinding.ViewEpisodePlayerBinding;
import io.github.waka.sevenhack.events.PlayerEvent;
import io.github.waka.sevenhack.events.PlayerEventProvider;
import io.github.waka.sevenhack.media.PodcastMadiator;
import io.github.waka.sevenhack.utils.DateUtil;
import io.github.waka.sevenhack.utils.StringUtil;
import io.github.waka.sevenhack.views.dialogs.EnclosurePlayingDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class EpisodePlayerView extends FrameLayout {

    private ViewEpisodePlayerBinding binding;
    private Episode episode;
    private PodcastMadiator podcastMadiator;
    private CompositeDisposable compositeDisposable;

    public EpisodePlayerView(Context context) {
        super(context);
        setup();
    }

    public EpisodePlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public EpisodePlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.view_episode_player, this, true);

        podcastMadiator = new PodcastMadiator(currentPosition -> {
            if (podcastMadiator.isPlaying()) {
                PlayerEventProvider.getInstance().send(PlayerEvent.TICK);
            }
        });

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                podcastMadiator.getObservable()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(appEvent -> {
                            switch (appEvent) {
                                case PLAY:
                                    handlePlay();
                                    break;
                                case PAUSE:
                                    handlePause();
                                    break;
                                case STOP:
                                    break;
                                case TICK:
                                    handleTick();
                                    break;
                            }
                        })
        );
    }

    public void dispose() {
        compositeDisposable.clear();
        podcastMadiator.stopTick();
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }

    public void initViews(Episode episode, PaletteColor paletteColor) {
        setEpisode(episode);

        if (paletteColor != null) {
            binding.playerContainer.setBackgroundColor(paletteColor.getBackGroundColor());
            binding.playerCurrentTime.setTextColor(paletteColor.getTitleTextColor());
            binding.playerDuration.setTextColor(paletteColor.getTitleTextColor());
        }

        initActionButtons();
        initSeekBar();
    }

    private void initActionButtons() {
        if (podcastMadiator.isPlayingEpisode(episode)) {
            if (podcastMadiator.isPlaying()) {
                preparePause();
            } else {
                preparePlay();
            }
        } else {
            preparePlay();
        }

        binding.playButton.setOnClickListener(view -> {
            if (podcastMadiator.isPlayingEpisode(episode)) {
                replay();
            } else {
                if (episode.enclosureCache != null) {
                    play();
                } else {
                    showPlayDialog();
                }
            }
        });

        binding.pauseButton.setOnClickListener(view -> pause());
    }

    private void preparePlay() {
        binding.playButton.setVisibility(View.VISIBLE);
        binding.pauseButton.setVisibility(View.GONE);
    }

    private void preparePause() {
        binding.playButton.setVisibility(View.GONE);
        binding.pauseButton.setVisibility(View.VISIBLE);
    }

    private void showPlayDialog() {
        AlertDialog dialog;

        if (episode.enclosureCache != null) {
            dialog = EnclosurePlayingDialog.play(getContext(), episode, this::play);
        } else {
            dialog = EnclosurePlayingDialog.streaming(getContext(), episode, this::play);
        }
        dialog.show();
    }

    private void play() {
        podcastMadiator.play(getContext(), episode);
        binding.playerSeekbar.setEnabled(true);
    }

    private void replay() {
        podcastMadiator.replay(getContext(), episode);
        binding.playerSeekbar.setEnabled(true);
    }

    private void pause() {
        podcastMadiator.pause(getContext(), episode);
        binding.playerSeekbar.setEnabled(false);
    }

    private void initSeekBar() {
        if (podcastMadiator.isPlayingEpisode(episode)) {
            updateCurrentTime(podcastMadiator.getCurrentPosition());
        } else {
            updateCurrentTime(0);
        }
        binding.playerDuration.setText(episode.duration);

        binding.playerSeekbar.setOnSeekBarChangeListener(new OnPlayerSeekListener());
        binding.playerSeekbar.setMax(DateUtil.durationToInt(episode.duration));

        if (podcastMadiator.isPlayingEpisode(episode)) {
            binding.playerSeekbar.setEnabled(true);
        } else {
            binding.playerSeekbar.setEnabled(false);
        }
    }

    private void updateCurrentTime(int currentPosition) {
        binding.playerCurrentTime.setText(StringUtil.seekPositionToString(currentPosition));
        binding.playerSeekbar.setProgress(currentPosition);
    }

    private void handlePlay() {
        preparePause();
    }

    private void handlePause() {
        preparePlay();
    }

    private void handleTick() {
        if (podcastMadiator.isPlayingEpisode(episode)) {
            int currentPosition = podcastMadiator.getCurrentPosition();
            updateCurrentTime(currentPosition);
            podcastMadiator.notifyUpdate(getContext(), episode, currentPosition);
        } else {
            updateCurrentTime(0);
        }
    }

    private class OnPlayerSeekListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!podcastMadiator.isPlaying()) {
                return;
            }
            podcastMadiator.seek(seekBar.getProgress());
        }
    }
}
