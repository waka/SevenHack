package io.github.waka.sevenhack.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Podcast;

public class PodcastAdapter extends BaseRecyclerAdapter<Podcast, PodcastAdapter.ViewHolder>
        implements View.OnClickListener {

    public PodcastAdapter(@NonNull Context context, OnItemClickListener<Podcast> onItemClickListener) {
        super(context, onItemClickListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_podcast, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Podcast podcast = items.get(i);

        viewHolder.title.setText(podcast.title);
        viewHolder.author.setText(podcast.author);
        viewHolder.loadThumbnail(podcast.imageMedium, context);

        if (onItemClickListener != null) {
            viewHolder.cardView.setOnClickListener(this);
            viewHolder.cardView.setTag(podcast);
        }
    }

    @Override
    public void onClick(View view) {
        if (view instanceof CardView) {
            Podcast podcast = (Podcast) view.getTag();
            dispatchOnItemClick(view, podcast);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final CardView cardView;
        final ImageView thumbnail;
        final TextView title;
        final TextView author;

        ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
            author = (TextView) itemView.findViewById(R.id.author);
            author.setVisibility(View.VISIBLE);
        }

        void loadThumbnail(String url, Context context) {
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.color.semi_transparent)
                    .into(thumbnail);
        }
    }
}
