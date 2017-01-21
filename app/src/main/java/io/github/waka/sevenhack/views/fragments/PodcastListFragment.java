package io.github.waka.sevenhack.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;

import javax.inject.Inject;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.activities.MainActivity;
import io.github.waka.sevenhack.data.entities.Podcast;
import io.github.waka.sevenhack.databinding.FragmentPodcastListBinding;
import io.github.waka.sevenhack.logics.PodcastLogic;
import io.github.waka.sevenhack.utils.DisplayUtil;
import io.github.waka.sevenhack.views.adapters.PodcastAdapter;
import io.github.waka.sevenhack.views.adapters.GridItemDecorator;

public class PodcastListFragment extends BaseFragment {

    @Inject
    PodcastLogic podcastLogic;

    private FragmentPodcastListBinding binding;
    private PodcastAdapter adapter;

    public static PodcastListFragment newInstance() {
        PodcastListFragment fragment = new PodcastListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPodcastListBinding.inflate(inflater, container, false);

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
        binding.podcastList.setHasFixedSize(true);

        binding.podcastList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        binding.podcastList.addItemDecoration(
                new GridItemDecorator(2, DisplayUtil.dpToPx(getActivity()), true));
        binding.podcastList.setItemAnimator(new DefaultItemAnimator());

        adapter = new PodcastAdapter(getActivity(), this::onFeedClick);
        binding.podcastList.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        podcastLogic.list()
                .subscribe(feeds -> {
                    if (feeds.size() > 0) {
                        Collections.reverse(feeds);
                        adapter.reset(feeds);
                    } else {
                        adapter.clear();
                    }
                });
    }

    private void onFeedClick(View view, Podcast podcast) {
        ((MainActivity) getActivity()).showPodcast(podcast, view.findViewById(R.id.thumbnail));
    }

    public void addPodcast(Podcast podcast) {
        adapter.addReverse(podcast);
    }

    public void removePodcast(Podcast podcast) {
        adapter.remove(podcast);
    }
}
