package io.github.waka.sevenhack.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.activities.MainActivity;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.databinding.FragmentDownloadedListBinding;
import io.github.waka.sevenhack.logics.EnclosureCacheLogic;
import io.github.waka.sevenhack.logics.EpisodeLogic;
import io.github.waka.sevenhack.media.PodcastMadiator;
import io.github.waka.sevenhack.utils.SnackbarUtil;
import io.github.waka.sevenhack.views.adapters.DividerItemDecorator;
import io.github.waka.sevenhack.views.adapters.EpisodeAdapter;
import io.github.waka.sevenhack.views.adapters.OnBottomScrollListener;
import io.github.waka.sevenhack.views.dialogs.EnclosureDownloadDialog;
import io.github.waka.sevenhack.views.dialogs.EnclosurePlayingDialog;

public class DownloadedListFragment extends BaseFragment {

    private static final int LIMIT = 30;

    @Inject
    EpisodeLogic episodeLogic;

    @Inject
    EnclosureCacheLogic enclosureCacheLogic;

    private FragmentDownloadedListBinding binding;
    private EpisodeAdapter adapter;
    private PodcastMadiator podcastMadiator;

    public static DownloadedListFragment newInstance() {
        DownloadedListFragment fragment = new DownloadedListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDownloadedListBinding.inflate(inflater, container, false);

        podcastMadiator = new PodcastMadiator();

        initRecyclerView();

        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    private void initRecyclerView() {
        binding.episodeList.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.episodeList.setLayoutManager(layoutManager);
        binding.episodeList.addItemDecoration(new DividerItemDecorator(getActivity()));
        binding.episodeList.setItemAnimator(new DefaultItemAnimator());

        // adapter
        adapter = new EpisodeAdapter(getActivity(), new EpisodeAdapter.OnClickListener() {
            @Override
            public void onContentsClick(View view, Episode item) {
                handleEpisodeClick(view, item);
            }

            @Override
            public void onDownloadClick(View view, Episode item) {}

            @Override
            public void onClearClick(View view, Episode item) {
                handleClearClick(item);
            }

            @Override
            public void onPlayClick(View view, Episode item) {
                handlePlayClick(item);
            }
        });
        binding.episodeList.addOnScrollListener(new OnBottomScrollListener(layoutManager, LIMIT) {
            @Override
            public void onBottom(int limit, int offset) {
                showEpisodes(limit, offset);
            }
        });
        binding.episodeList.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.clear();
        showEpisodes(LIMIT, 0);
    }

    private void showEpisodes(int limit, int offset) {
        episodeLogic.downloads(limit, offset)
                .subscribe(episodes -> {
                    if (episodes.size() > 0) {
                        adapter.add(episodes);
                    } else {
                        SnackbarUtil.show(binding.contentMain, R.string.no_downloaded_episode);
                    }
                });
    }

    private void handleEpisodeClick(View view, Episode episode) {
        ((MainActivity) getActivity()).showEpisode(episode, view.findViewById(R.id.item_title));
    }

    private void handleClearClick(final Episode episode) {
        AlertDialog dialog = EnclosureDownloadDialog.clearDownload(
                getContext(),
                episode,
                () -> {
                    if (enclosureCacheLogic.remove(getContext(), episode.enclosureCache)) {
                        adapter.remove(episode);
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
}
