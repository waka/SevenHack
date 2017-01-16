package io.github.waka.sevenhack.views.components;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.data.models.PaletteColor;
import io.github.waka.sevenhack.databinding.ViewEpisodeHeaderBinding;

public class EpisodeHeaderView extends FrameLayout {

    private ViewEpisodeHeaderBinding binding;

    public EpisodeHeaderView(Context context) {
        super(context);
        setup();
    }

    public EpisodeHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public EpisodeHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.view_episode_header, this, true);
    }

    public void initViews(Episode episode, PaletteColor paletteColor) {
        if (paletteColor != null) {
            binding.headerContainer.setBackgroundColor(paletteColor.getBackGroundColor());
            binding.episodeTitle.setTextColor(paletteColor.getTitleTextColor());
        }
        binding.episodeTitle.setText(episode.title);
    }
}
