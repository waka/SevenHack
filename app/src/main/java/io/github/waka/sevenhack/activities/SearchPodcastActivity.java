package io.github.waka.sevenhack.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.databinding.ActivitySearchPodcastBinding;
import io.github.waka.sevenhack.internal.constants.RequestCodes;
import io.github.waka.sevenhack.views.fragments.SearchPodcastFragment;

public class SearchPodcastActivity extends BaseActivity {

    private ActivitySearchPodcastBinding binding;

    public static void startTransition(Activity activity) {
        Intent intent = new Intent(activity, SearchPodcastActivity.class);
        ActivityCompat.startActivityForResult(activity, intent, RequestCodes.SEARCH_PODCAST, new Bundle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_podcast);

        getComponent().inject(this);

        initToolbar();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_view, SearchPodcastFragment.newInstance())
                    .commitNow();
        }
    }

    private void initToolbar() {
        setSupportActionBar(binding.searchPodcast.getToolBar());

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishTransition();
        }
        return true;
    }
}
