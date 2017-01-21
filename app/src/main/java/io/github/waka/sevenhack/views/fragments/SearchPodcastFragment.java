package io.github.waka.sevenhack.views.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.activities.SearchPodcastActivity;
import io.github.waka.sevenhack.data.entities.Podcast;
import io.github.waka.sevenhack.data.models.Rss;
import io.github.waka.sevenhack.data.models.SearchResult;
import io.github.waka.sevenhack.databinding.FragmentSearchPodcastBinding;
import io.github.waka.sevenhack.logics.PodcastLogic;
import io.github.waka.sevenhack.logics.SearchPodcastLogic;
import io.github.waka.sevenhack.utils.BundleUtil;
import io.github.waka.sevenhack.utils.DisplayUtil;
import io.github.waka.sevenhack.utils.SnackbarUtil;
import io.github.waka.sevenhack.views.adapters.GridItemDecorator;
import io.github.waka.sevenhack.views.adapters.SearchPodcastAdapter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SearchPodcastFragment extends BaseFragment {

    private FragmentSearchPodcastBinding binding;
    private SearchPodcastAdapter adapter;

    @Inject
    SearchPodcastLogic searchPodcastLogic;

    @Inject
    PodcastLogic podcastLogic;

    public static SearchPodcastFragment newInstance() {
        return new SearchPodcastFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchPodcastBinding.inflate(inflater, container, false);

        initRecyclerView();
        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    private void initRecyclerView() {
        binding.podcastList.setHasFixedSize(true);

        binding.podcastList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        binding.podcastList.addItemDecoration(
                new GridItemDecorator(2, DisplayUtil.dpToPx(getActivity()), true));
        binding.podcastList.setItemAnimator(new DefaultItemAnimator());

        adapter = new SearchPodcastAdapter(getActivity(), this::onPodcastClick);
        binding.podcastList.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        initSearchView(searchView);
    }

    private void initSearchView(SearchView searchView) {
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                searchView.clearFocus();
                searchPodcast(text);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void searchPodcast(String str) {
        showLoadingView();

        searchPodcastLogic.search(str)
                .subscribe(new Observer<List<SearchResult>>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onComplete() {}

                    @Override
                    public void onError(Throwable e) {
                        if (isAdded()) {
                            hideLoadingView();
                            SnackbarUtil.show(binding.contentMain, R.string.fetch_podcasts_error);
                        }
                    }

                    @Override
                    public void onNext(List<SearchResult> searchResults) {
                        if (isAdded()) {
                            hideLoadingView();

                            if (searchResults.size() == 0) {
                                SnackbarUtil.show(binding.contentMain, R.string.no_results);
                            } else {
                                adapter.setItems(searchResults);
                            }
                        }
                    }
                });
    }

    private void onPodcastClick(final SearchResult searchResult) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.dialog_fetch_rss_progress));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        searchPodcastLogic.fetchRss(searchResult.url)
                .subscribe(new Observer<Rss>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onComplete() {}

                    @Override
                    public void onError(Throwable e) {
                        if (isAdded()) {
                            progressDialog.dismiss();
                            SnackbarUtil.show(binding.contentMain, R.string.fetch_rss_error);
                        }
                    }

                    @Override
                    public void onNext(Rss rss) {
                        if (isAdded()) {
                            progressDialog.dismiss();
                            showDialog(searchResult, rss);
                        }
                    }
                });
    }

    private void showDialog(final SearchResult searchResult, final Rss rss) {
        String message = rss.channel.getDescription() + "\n\n" + getString(R.string.dialog_add_podcast_description);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(searchResult.title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.dialog_yes, (dialog, which) -> addPodcast(searchResult, rss));
        builder.setNegativeButton(R.string.dialog_no, null);
        builder.create().show();
    }

    private void addPodcast(final SearchResult searchResult, final Rss rss) {
        podcastLogic.exists(searchResult.url)
                .subscribe(count -> {
                    if (count > 0) {
                        SnackbarUtil.show(binding.contentMain, R.string.podcast_already_exists);
                    } else {
                        Podcast podcast = podcastLogic.create(searchResult, rss);
                        ((SearchPodcastActivity) getActivity())
                                .backWithAdded(BundleUtil.createWithPodcast(podcast));
                    }
                });
    }

    private void showLoadingView() {
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void hideLoadingView() {
        binding.progress.setVisibility(View.GONE);
    }
}
