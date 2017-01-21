package io.github.waka.sevenhack.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.activities.PodcastActivity;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.data.entities.Podcast;
import io.github.waka.sevenhack.data.models.Rss;
import io.github.waka.sevenhack.databinding.FragmentPodcastBinding;
import io.github.waka.sevenhack.events.DownloadedEventProvider;
import io.github.waka.sevenhack.logics.EnclosureCacheLogic;
import io.github.waka.sevenhack.logics.EpisodeLogic;
import io.github.waka.sevenhack.logics.PodcastLogic;
import io.github.waka.sevenhack.logics.SearchPodcastLogic;
import io.github.waka.sevenhack.media.PodcastMadiator;
import io.github.waka.sevenhack.services.EnclosureDownloadService;
import io.github.waka.sevenhack.utils.BundleUtil;
import io.github.waka.sevenhack.utils.SnackbarUtil;
import io.github.waka.sevenhack.views.adapters.DividerItemDecorator;
import io.github.waka.sevenhack.views.adapters.EpisodeAdapter;
import io.github.waka.sevenhack.views.adapters.OnBottomScrollListener;
import io.github.waka.sevenhack.views.dialogs.EnclosureDownloadDialog;
import io.github.waka.sevenhack.views.dialogs.EnclosurePlayingDialog;
import io.github.waka.sevenhack.views.dialogs.EpisodeRemoveDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PodcastFragment extends BaseFragment {

    private static final int LIMIT = 30;

    @Inject
    PodcastLogic podcastLogic;

    @Inject
    EpisodeLogic episodeLogic;

    @Inject
    EnclosureCacheLogic enclosureCacheLogic;

    @Inject
    SearchPodcastLogic searchPodcastLogic;

    private FragmentPodcastBinding binding;
    private EpisodeAdapter adapter;
    private Podcast podcast;
    private PodcastMadiator podcastMadiator;
    private CompositeDisposable compositeDisposable;

    public static PodcastFragment newInstance(Bundle args) {
        PodcastFragment fragment = new PodcastFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPodcastBinding.inflate(inflater, container, false);

        podcastMadiator = new PodcastMadiator();

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                DownloadedEventProvider.getInstance().toObservable()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(o -> {
                            updateEpisode((Episode) o);
                        })
        );

        initRecyclerView();

        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        podcast = BundleUtil.getPodcast(getArguments());

        showEpisodes(LIMIT, 0);
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.clear();
        super.onDestroyView();
    }

    private void initRecyclerView() {
        binding.episodeList.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.episodeList.setLayoutManager(layoutManager);
        binding.episodeList.addItemDecoration(new DividerItemDecorator(getActivity()));
        binding.episodeList.setItemAnimator(new DefaultItemAnimator());

        // adapter
        adapter = new EpisodeAdapter(getActivity(), onClickListener);
        binding.episodeList.addOnScrollListener(new OnBottomScrollListener(layoutManager, LIMIT) {
            @Override
            public void onBottom(int limit, int offset) {
                showEpisodes(limit, offset);
            }
        });
        binding.episodeList.setAdapter(adapter);
    }

    private void showEpisodes(int limit, int offset) {
        episodeLogic.list(podcast, limit, offset)
                .subscribe(episodes -> {
                    if (episodes.size() > 0) {
                        podcast.episodes.addAll(episodes);
                        adapter.add(episodes);
                    }
                });
    }

    public void updateEpisode(Episode episode) {
        adapter.update(episode);
    }

    public void updateFromRss() {
        searchPodcastLogic.fetchRss(podcast.url)
                .subscribe(new Observer<Rss>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onComplete() {}

                    @Override
                    public void onError(Throwable e) {
                        if (isAdded()) {
                            SnackbarUtil.show(binding.contentMain, R.string.fetch_rss_error);
                        }
                    }

                    @Override
                    public void onNext(Rss rss) {
                        if (isAdded()) {
                            List<Episode> episodes = episodeLogic.createOnlyNewers(podcast, rss);
                            if (episodes.size() > 0) {
                                Collections.reverse(episodes);
                                podcast.episodes.addAll(0, episodes);
                                adapter.addReverse(episodes);
                                SnackbarUtil.show(binding.contentMain, R.string.updated_episodes);
                            }
                        }
                    }
                });
    }

    public void removePodcast() {
        AlertDialog dialog = EpisodeRemoveDialog.remove(
                getContext(),
                podcast,
                () -> {
                    if (podcastLogic.remove(getContext(), podcast)) {
                        ((PodcastActivity) getActivity())
                                .backWithDeleted(BundleUtil.createWithPodcast(podcast));
                    } else {
                        SnackbarUtil.show(binding.contentMain, R.string.remove_podcast_error);
                    }
                }
        );
        dialog.show();
    }

    private void handleEpisodeClick(View view, Episode episode) {
        ((PodcastActivity) getActivity()).showEpisode(episode, view.findViewById(R.id.item_title));
    }

    private void handleDownloadClick(final Episode episode) {
        AlertDialog dialog;
        if (EnclosureDownloadService.isDownloading(episode)) {
            dialog = EnclosureDownloadDialog.cancelDownload(
                    getContext(),
                    episode,
                    () -> EnclosureDownloadService.cancel(getContext(), episode));
        } else {
            dialog = EnclosureDownloadDialog.download(
                    getContext(),
                    episode,
                    () -> EnclosureDownloadService.start(getContext(), episode));
        }
        dialog.show();
    }

    private void handleClearClick(final Episode episode) {
        AlertDialog dialog = EnclosureDownloadDialog.clearDownload(
                getContext(),
                episode,
                () -> {
                    if (enclosureCacheLogic.remove(getContext(), episode.enclosureCache)) {
                        episode.enclosureCache = null;
                        updateEpisode(episode);
                    }
                }
        );
        dialog.show();
    }

    private void handlePlayClick(final Episode episode) {
        if (episode.enclosureCache == null) {
            AlertDialog dialog = EnclosurePlayingDialog.streaming(
                    getContext(),
                    episode,
                    () -> podcastMadiator.play(getActivity(), episode)
            );
            dialog.show();
        } else {
            if (podcastMadiator.isPlaying() || podcastMadiator.isPaused()) {
                podcastMadiator.stop(getActivity());
            }
            // immediate play
            podcastMadiator.play(getActivity(), episode);
        }
    }

    private final EpisodeAdapter.OnClickListener onClickListener = new EpisodeAdapter.OnClickListener() {
        @Override
        public void onContentsClick(View view, Episode item) {
            handleEpisodeClick(view, item);
        }

        @Override
        public void onDownloadClick(View view, Episode item) {
            handleDownloadClick(item);
        }

        @Override
        public void onClearClick(View view, Episode item) {
            handleClearClick(item);
        }

        @Override
        public void onPlayClick(View view, Episode item) {
            handlePlayClick(item);
        }
    };
}
