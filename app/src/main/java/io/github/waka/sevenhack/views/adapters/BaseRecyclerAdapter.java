package io.github.waka.sevenhack.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    final Context context;
    final List<T> items;
    final OnItemClickListener<T> onItemClickListener;

    BaseRecyclerAdapter(@NonNull Context context, OnItemClickListener<T> onItemClickListener) {
        this.context = context;
        this.items = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }

    @UiThread
    public void reset(Collection<T> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @UiThread
    public void add(Collection<T> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @UiThread
    public void add(T item) {
        this.items.add(item);
        notifyItemInserted(this.items.size() - 1);
    }

    @UiThread
    public void addReverse(T item) {
        this.items.add(0, item);
        notifyItemInserted(0);
    }

    @UiThread
    public void remove(T item) {
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

    @Override
    public int getItemCount() {
        return items.size();
    }

    void dispatchOnItemClick(View view, T item) {
        assert onItemClickListener != null;
        onItemClickListener.onItemClick(view, item);
    }

    public interface OnItemClickListener<T> {
        void onItemClick(View view, T item);
    }
}
