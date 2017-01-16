package io.github.waka.sevenhack.views.components;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Podcast;
import io.github.waka.sevenhack.databinding.ViewPodcastHeaderBinding;

public class PodcastHeaderView extends FrameLayout {

    private ViewPodcastHeaderBinding binding;

    public PodcastHeaderView(Context context) {
        super(context);
        setup();
    }

    public PodcastHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public PodcastHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.view_podcast_header, this, true);
    }

    public void setPodcast(Context context, Podcast podcast, ImageLoadCallback callback) {
        Picasso.with(context)
                .load(podcast.imageLarge)
                .placeholder(R.color.semi_transparent)
                .into(binding.podcastImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        callback.onLoaded(binding.podcastImage);
                    }

                    @Override
                    public void onError() {}
                });
    }

    public interface ImageLoadCallback {
        void onLoaded(ImageView imageView);
    }
}
