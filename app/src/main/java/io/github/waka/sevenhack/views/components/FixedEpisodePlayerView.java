package io.github.waka.sevenhack.views.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.databinding.ViewFixedEpisodePlayerBinding;
import io.github.waka.sevenhack.media.PodcastMadiator;
import io.github.waka.sevenhack.views.notifications.PodcastPlayerNotification;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FixedEpisodePlayerView extends FrameLayout {

    private ViewFixedEpisodePlayerBinding binding;
    private Episode episode;
    private PodcastMadiator podcastMadiator;
    private CompositeDisposable compositeDisposable;

    public FixedEpisodePlayerView(Context context) {
        super(context);
        setup();
    }

    public FixedEpisodePlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public FixedEpisodePlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.view_fixed_episode_player, this, true);
        setVisibility(View.GONE);

        podcastMadiator = new PodcastMadiator();

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
                                    handleStop();
                                    break;
                                case TICK:
                                    break;
                            }
                        })
        );
    }

    public void dispose() {
        compositeDisposable.clear();
    }

    public boolean shouldStart() {
        return podcastMadiator.isPlaying() || podcastMadiator.isPaused();
    }

    public void start() {
        episode = podcastMadiator.getPlayingEpisode();

        initViews(episode);
        initActionButtons();

        show();
    }

    public void startWithNotify() {
        start();
        PodcastPlayerNotification.notify(getContext(), episode);
    }

    private void initViews(Episode episode) {
        binding.playerTitle.setText(episode.title);
        binding.playerTitle.setSelected(true);
        binding.playerTitle.requestFocus();
    }

    private void initActionButtons() {
        if (podcastMadiator.isPlaying()) {
            preparePause();
        } else {
            preparePlay();
        }

        binding.playButton.setOnClickListener(view -> podcastMadiator.replay(getContext(), episode));
        binding.pauseButton.setOnClickListener(view -> podcastMadiator.pause(getContext(), episode));
        binding.stopButton.setOnClickListener(view -> podcastMadiator.stop(getContext()));
    }

    private void preparePlay() {
        binding.playButton.setVisibility(View.VISIBLE);
        binding.pauseButton.setVisibility(View.GONE);
    }

    private void preparePause() {
        binding.playButton.setVisibility(View.GONE);
        binding.pauseButton.setVisibility(View.VISIBLE);
    }

    private void handlePlay() {
        preparePause();
    }

    private void handlePause() {
        preparePlay();
    }

    private void handleStop() {
        hide();
    }

    private void show() {
        setVisibility(View.VISIBLE);

        binding.fixedEpisodePlayerContainer.animate()
                .translationYBy(binding.fixedEpisodePlayerContainer.getHeight())
                .translationY(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setVisibility(View.VISIBLE);
                    }
                });
    }

    private void hide() {
        binding.fixedEpisodePlayerContainer.animate()
                .translationYBy(0)
                .translationY(binding.fixedEpisodePlayerContainer.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setVisibility(View.GONE);
                    }
                });
    }
}
