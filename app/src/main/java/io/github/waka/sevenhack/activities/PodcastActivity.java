package io.github.waka.sevenhack.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.data.entities.Podcast;
import io.github.waka.sevenhack.data.models.PaletteColor;
import io.github.waka.sevenhack.databinding.ActivityPodcastBinding;
import io.github.waka.sevenhack.events.PlayerEvent;
import io.github.waka.sevenhack.events.PlayerEventProvider;
import io.github.waka.sevenhack.internal.constants.RequestCodes;
import io.github.waka.sevenhack.internal.constants.ResultCodes;
import io.github.waka.sevenhack.utils.BundleUtil;
import io.github.waka.sevenhack.views.fragments.PodcastFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PodcastActivity extends BaseActivity {

    private ActivityPodcastBinding binding;
    private CompositeDisposable compositeDisposable;
    private PaletteColor paletteColor;

    public static void startTransition(Activity activity, Podcast podcast, View transitionView) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                transitionView,
                activity.getString(R.string.transition_podcast));
        Intent intent = new Intent(activity, PodcastActivity.class);
        intent.putExtra(BUNDLE_NAME, BundleUtil.createWithPodcast(podcast));
        ActivityCompat.startActivityForResult(activity, intent, RequestCodes.PODCAST, options.toBundle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_podcast);

        getComponent().inject(this);

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                PlayerEventProvider.getInstance().toObservable()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(o -> {
                            if (o == PlayerEvent.PLAY) {
                                showPlayer();
                            }
                        })
        );

        initToolbar();
        initViews();

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getBundleExtra(BUNDLE_NAME);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_view, PodcastFragment.newInstance(bundle))
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
        Podcast podcast = BundleUtil.getPodcast(getIntent().getBundleExtra(BUNDLE_NAME));
        binding.feedHeader.setPodcast(this, podcast, imageView -> {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            new Palette.Builder(bitmap).generate(palette -> {
                if (palette != null) {
                    paletteColor = PaletteColor.buildWithPalette(palette);
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding.fixedEpisodePlayer.shouldStart()) {
            binding.fixedEpisodePlayer.start();
        }
    }

    @Override
    public void onDestroy() {
        binding.fixedEpisodePlayer.dispose();
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RequestCodes.EPISODE:
                if (resultCode == ResultCodes.UPDATED) {
                    Episode episode = BundleUtil.getEpisode(data.getBundleExtra(BUNDLE_NAME));
                    onUpdatedEpisode(episode);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.podcast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh:
                onRefresh();
                return true;
            case R.id.action_remove:
                onRemove();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onUpdatedEpisode(Episode episode) {
        PodcastFragment fragment = (PodcastFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_view);
        fragment.updateEpisode(episode);
    }

    private void onRefresh() {
        PodcastFragment fragment = (PodcastFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_view);
        fragment.updateFromRss();
    }

    private void onRemove() {
        PodcastFragment fragment = (PodcastFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_view);
        fragment.removePodcast();
    }

    private void showPlayer() {
        binding.fixedEpisodePlayer.startWithNotify();
    }

    public void showEpisode(Episode episode, View view) {
        EpisodeActivity.startTransition(this, episode, paletteColor, view);
    }
}
