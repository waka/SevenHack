package io.github.waka.sevenhack.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.utils.StringUtil;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {

    public interface OnClickListener {
        void onContentsClick(View view, Episode item);
        void onDownloadClick(View view, Episode item);
        void onClearClick(View view, Episode item);
        void onPlayClick(View view, Episode item);
    }

    private final Context context;
    private final List<Episode> items;
    private final OnClickListener listener;

    public EpisodeAdapter(@NonNull Context context, OnClickListener listener) {
        this.context = context;
        this.items = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_episode, viewGroup, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Episode episode = items.get(i);
        viewHolder.bindEpisode(episode);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @UiThread
    public void add(Collection<Episode> items) {
        int currentLastPosition = this.items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(currentLastPosition, items.size());
    }

    @UiThread
    public void addReverse(Collection<Episode> items) {
        this.items.addAll(0, items);
        notifyItemRangeInserted(0, items.size());
    }

    @UiThread
    public void update(Episode item) {
        int position = this.items.indexOf(item);
        if (position > -1) {
            this.items.set(position, item);
            notifyItemChanged(position);
        }
    }

    @UiThread
    public void remove(Episode item) {
        int position = this.items.indexOf(item);
        if (position > -1) {
            this.items.remove(position);
            notifyItemRemoved(position);
        }
    }

    @UiThread
    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout contents;
        private final TextView pubDate;
        private final TextView title;
        private final TextView subTitle;
        private final LinearLayout downloadButton;
        private final LinearLayout clearButton;
        private final LinearLayout playButton;
        private final OnClickListener listener;

        ViewHolder(View itemView, OnClickListener listener) {
            super(itemView);

            contents = (LinearLayout) itemView.findViewById(R.id.contents);
            pubDate = (TextView) itemView.findViewById(R.id.item_pub_date);
            title = (TextView) itemView.findViewById(R.id.item_title);
            subTitle = (TextView) itemView.findViewById(R.id.item_sub_title);
            downloadButton = (LinearLayout) itemView.findViewById(R.id.download_button);
            clearButton = (LinearLayout) itemView.findViewById(R.id.clear_button);
            playButton = (LinearLayout) itemView.findViewById(R.id.play_button);

            this.listener = listener;
        }

        void bindEpisode(final Episode episode) {
            pubDate.setText(StringUtil.fromDateString(episode.pubDate));
            title.setText(episode.title);
            subTitle.setText(episode.subTitle);

            if (episode.enclosureCache == null) {
                showDownload();
            } else {
                showClear();
            }

            if (episode.isNewly) {
                TextView tv = (TextView) itemView.findViewById(R.id.item_newly);
                tv.setVisibility(View.VISIBLE);
            }

            contents.setOnClickListener(view -> listener.onContentsClick(view, episode));
            downloadButton.setOnClickListener(view -> listener.onDownloadClick(view, episode));
            clearButton.setOnClickListener(view -> listener.onClearClick(view, episode));
            playButton.setOnClickListener(view -> listener.onPlayClick(view, episode));
        }

        private void showDownload() {
            clearButton.setVisibility(View.GONE);
            downloadButton.setVisibility(View.VISIBLE);
        }

        private void showClear() {
            downloadButton.setVisibility(View.GONE);
            clearButton.setVisibility(View.VISIBLE);
        }
    }
}
