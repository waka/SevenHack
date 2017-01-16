package io.github.waka.sevenhack.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.data.models.PaletteColor;
import io.github.waka.sevenhack.databinding.ActivityEpisodeBinding;
import io.github.waka.sevenhack.events.DownloadedEventProvider;
import io.github.waka.sevenhack.internal.constants.RequestCodes;
import io.github.waka.sevenhack.logics.EnclosureCacheLogic;
import io.github.waka.sevenhack.services.EnclosureDownloadService;
import io.github.waka.sevenhack.utils.BundleUtil;
import io.github.waka.sevenhack.views.dialogs.EnclosureDownloadDialog;
import io.github.waka.sevenhack.views.fragments.EpisodeFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class EpisodeActivity extends BaseActivity {

    @Inject
    EnclosureCacheLogic enclosureCacheLogic;

    private ActivityEpisodeBinding binding;
    private CompositeDisposable compositeDisposable;
    private Episode episode;

    public static void startTransition(
            Activity activity, Episode episode, PaletteColor paletteColor, View transitionView) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                transitionView,
                activity.getString(R.string.transition_episode));
        Intent intent = new Intent(activity, EpisodeActivity.class);
        intent.putExtra(BUNDLE_NAME, BundleUtil.createWithEpisode(episode, paletteColor));
        ActivityCompat.startActivityForResult(activity, intent, RequestCodes.EPISODE, options.toBundle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_episode);
        episode = BundleUtil.getEpisode(getIntent().getBundleExtra(BUNDLE_NAME));

        getComponent().inject(this);

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                DownloadedEventProvider.getInstance().toObservable()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(o -> {
                            reload();
                        })
        );

        initToolbar();
        initViews();

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getBundleExtra(BUNDLE_NAME);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_view, EpisodeFragment.newInstance(bundle))
                    .commitNow();
        }
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle("");
        }
    }

    private void initViews() {
        PaletteColor paletteColor = BundleUtil.getPaletteColor(getIntent().getBundleExtra(BUNDLE_NAME));
        binding.episodeHeader.initViews(episode, paletteColor);
        binding.episodePlayer.initViews(episode, paletteColor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.episodePlayer.dispose();
        compositeDisposable.clear();
    }

    @Override
    public void onBackPressed() {
        backWithUpdated(BundleUtil.createWithEpisode(episode));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.episode, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (episode.enclosureCache == null) {
            menu.findItem(R.id.action_download).setVisible(true);
            menu.findItem(R.id.action_remove_download).setVisible(false);
        } else {
            menu.findItem(R.id.action_download).setVisible(false);
            menu.findItem(R.id.action_remove_download).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_download:
                onDownload();
                return true;
            case R.id.action_remove_download:
                onRemoveDownload();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onDownload() {
        AlertDialog dialog;
        if (EnclosureDownloadService.isDownloading(episode)) {
            dialog = EnclosureDownloadDialog.cancelDownload(
                    this,
                    episode,
                    () -> EnclosureDownloadService.cancel(this, episode));
        } else {
            dialog = EnclosureDownloadDialog.download(
                    this,
                    episode,
                    () -> EnclosureDownloadService.start(this, episode));
        }
        dialog.show();
    }

    private void onRemoveDownload() {
        AlertDialog dialog = EnclosureDownloadDialog.clearDownload(
                this,
                episode,
                () -> {
                    if (enclosureCacheLogic.remove(this, episode.enclosureCache)) {
                        episode.enclosureCache = null;
                        binding.episodePlayer.setEpisode(episode);
                    }
                }
        );
        dialog.show();
    }

    private void reload() {
        enclosureCacheLogic.get(episode)
                .subscribe(cache -> {
                    episode.enclosureCache = cache;
                    binding.episodePlayer.setEpisode(episode);
                });
    }
}
