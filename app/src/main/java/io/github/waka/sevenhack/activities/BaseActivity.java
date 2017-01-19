package io.github.waka.sevenhack.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.github.waka.sevenhack.MainApplication;
import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.internal.constants.ResultCodes;
import io.github.waka.sevenhack.internal.di.ActivityComponent;
import io.github.waka.sevenhack.internal.di.ActivityModule;

public class BaseActivity extends AppCompatActivity {

    static final String BUNDLE_NAME = "extra";

    private ActivityComponent activityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById(android.R.id.content).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @NonNull
    public ActivityComponent getComponent() {
        if (activityComponent == null) {
            MainApplication application = (MainApplication) getApplication();
            activityComponent = application.getComponent().plus(new ActivityModule(this));
        }
        return activityComponent;
    }

    // for back scenes

    protected void finishTransition() {
        overridePendingTransition(0, R.anim.activity_fade_exit);
        super.finish();
    }

    public void backWithAdded(Bundle bundle) {
        Intent intent = new Intent();
        intent.putExtra(BUNDLE_NAME, bundle);
        setResult(ResultCodes.ADDED, intent);
        super.onBackPressed();
    }

    public void backWithUpdated(Bundle bundle) {
        Intent intent = new Intent();
        intent.putExtra(BUNDLE_NAME, bundle);
        setResult(ResultCodes.UPDATED, intent);
        super.onBackPressed();
    }

    public void backWithDeleted(Bundle bundle) {
        Intent intent = new Intent();
        intent.putExtra(BUNDLE_NAME, bundle);
        setResult(ResultCodes.DELETED, intent);
        super.onBackPressed();
    }
 }
