package io.github.waka.sevenhack.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.databinding.FragmentEpisodeBinding;
import io.github.waka.sevenhack.utils.BundleUtil;
import io.github.waka.sevenhack.utils.StringUtil;

public class EpisodeFragment extends BaseFragment {

    private FragmentEpisodeBinding binding;

    public static EpisodeFragment newInstance(Bundle args) {
        EpisodeFragment fragment = new EpisodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEpisodeBinding.inflate(inflater, container, false);

        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        Episode episode = BundleUtil.getEpisode(getArguments());

        binding.episodeDescription.setText(
                StringUtil.omitArticle(StringUtil.fromHtml(episode.subTitle)));
        binding.showNotes.addLinkView(episode);
    }
}
