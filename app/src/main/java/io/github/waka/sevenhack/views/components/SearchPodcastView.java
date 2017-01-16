package io.github.waka.sevenhack.views.components;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.databinding.ViewSearchPodcastBinding;

public class SearchPodcastView extends FrameLayout {

    private ViewSearchPodcastBinding binding;

    public SearchPodcastView(Context context) {
        super(context);
        setup();
    }

    public SearchPodcastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public SearchPodcastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.view_search_podcast, this, true);
    }

    public Toolbar getToolBar() {
        return binding.toolbar;
    }
}
