package io.github.waka.sevenhack.views.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.models.SearchResult;

public class PodcastAdapter extends RecyclerView.Adapter<PodcastAdapter.ViewHolder>
        implements View.OnClickListener {

    private final List<SearchResult> items;
    private final Listener listener;
    private final Context context;

    public interface Listener {
        void onItemClicked(SearchResult SearchResult);
    }

    public PodcastAdapter(Context context, Listener listener) {
        this.context  = context;
        this.items    = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_podcast, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        SearchResult item = items.get(i);

        viewHolder.title.setText(item.title);
        viewHolder.loadThumbnail(item.imageMedium, context);

        if (listener != null) {
            viewHolder.cardView.setOnClickListener(this);
            viewHolder.cardView.setTag(item);
        }
    }

    public void setItems(List<SearchResult> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof CardView) {
            SearchResult item = (SearchResult) v.getTag();
            listener.onItemClicked(item);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final CardView cardView;
        final ImageView thumbnail;
        final TextView title;

        ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
        }

        void loadThumbnail(String url, Context context) {
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.color.semi_transparent)
                    .into(thumbnail);
        }
    }
}
