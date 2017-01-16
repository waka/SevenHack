package io.github.waka.sevenhack.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.webkit.WebView;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.databinding.ActivityLicenseBinding;
import io.github.waka.sevenhack.internal.constants.RequestCodes;

public class LicenseActivity extends BaseActivity {

    private static final String LICENSES_HTML_PATH = "file:///android_asset/licenses.html";

    private ActivityLicenseBinding binding;

    public static void startTransition(Activity activity) {
        Intent intent = new Intent(activity, LicenseActivity.class);
        ActivityCompat.startActivityForResult(activity, intent, RequestCodes.LISENCE, new Bundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_license);

        initToolbar();

        WebView webView = binding.webView;
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl(LICENSES_HTML_PATH);
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(R.string.nav_manage_license);
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
