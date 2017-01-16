package io.github.waka.sevenhack.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.data.entities.Podcast;
import io.github.waka.sevenhack.databinding.ActivityMainBinding;
import io.github.waka.sevenhack.events.PlayerEvent;
import io.github.waka.sevenhack.events.PlayerEventProvider;
import io.github.waka.sevenhack.internal.constants.RequestCodes;
import io.github.waka.sevenhack.internal.constants.ResultCodes;
import io.github.waka.sevenhack.services.PodcastPlayerService;
import io.github.waka.sevenhack.utils.BundleUtil;
import io.github.waka.sevenhack.utils.SnackbarUtil;
import io.github.waka.sevenhack.views.fragments.DownloadedListFragment;
import io.github.waka.sevenhack.views.fragments.PodcastListFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    private ActivityMainBinding binding;
    private CompositeDisposable compositeDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        DataBindingUtil.bind(binding.navView.getHeaderView(0));

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
        startService(new Intent(getApplicationContext(), PodcastPlayerService.class));

        initToolbar();
        initViews();

        if (savedInstanceState == null) {
            showPodcastList();
        }
        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding.fixedEpisodePlayer.shouldStart()) {
            binding.fixedEpisodePlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        binding.fixedEpisodePlayer.dispose();
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
        super.onDestroy();
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.nav_podcast_list);
        }
    }

    private void initViews() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                binding.drawer, binding.toolbar, R.string.open, R.string.close);
        binding.drawer.setDrawerListener(toggle);
        toggle.syncState();

        binding.fab.setOnClickListener(view -> SearchPodcastActivity.startTransition(this));
        binding.navView.setNavigationItemSelectedListener(this);
        binding.navView.setCheckedItem(R.id.nav_podcast_list);
    }

    private void replaceFragment(Fragment fragment) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
        ft.replace(R.id.content_view, fragment, fragment.getClass().getSimpleName());
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RequestCodes.SEARCH_PODCAST:
                if (resultCode == ResultCodes.ADDED) {
                    Podcast podcast = BundleUtil.getPodcast(data.getBundleExtra(BUNDLE_NAME));
                    onAddedPodcast(podcast);
                }
                break;
            case RequestCodes.PODCAST:
                if (resultCode == ResultCodes.DELETED) {
                    Podcast podcast = BundleUtil.getPodcast(data.getBundleExtra(BUNDLE_NAME));
                    onDeletedPodcast(podcast);
                }
                break;
            case RequestCodes.LISENCE:
                // nothing to do
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment current = fm.findFragmentById(R.id.content_view);
        if (current == null) {
            // no more fragments in the stack. finish.
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        binding.drawer.closeDrawer(GravityCompat.START);

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_podcast_list:
                showPodcastList();
                break;
            case R.id.nav_downloaded_list:
                showDownloadedList();
                break;
            case R.id.nav_manage_license:
                LicenseActivity.startTransition(this);
                break;
        }

        return true;
    }

    private void onAddedPodcast(Podcast podcast) {
        PodcastListFragment fragment = (PodcastListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_view);
        fragment.addPodcast(podcast);
        SnackbarUtil.show(binding.containerMain, R.string.result_add_podcast);
    }

    private void onDeletedPodcast(Podcast podcast) {
        PodcastListFragment fragment = (PodcastListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_view);
        fragment.removePodcast(podcast);
        SnackbarUtil.show(binding.containerMain, R.string.result_remove_podcast);
    }

    private void showPodcastList() {
        replaceFragment(PodcastListFragment.newInstance());
        binding.fab.setVisibility(View.VISIBLE);
        binding.collapsingToolbar.setTitle(getString(R.string.nav_podcast_list));
    }

    private void showDownloadedList() {
        replaceFragment(DownloadedListFragment.newInstance());
        binding.fab.setVisibility(View.GONE);
        binding.collapsingToolbar.setTitle(getString(R.string.nav_downloaded_list));
    }

    private void showPlayer() {
        binding.fixedEpisodePlayer.startWithNotify();
    }

    public void showPodcast(Podcast podcast, View view) {
        PodcastActivity.startTransition(this, podcast, view);
    }

    public void showEpisode(Episode episode, View view) {
        EpisodeActivity.startTransition(this, episode, null, view);
    }
}
