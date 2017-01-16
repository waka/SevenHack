package io.github.waka.sevenhack.views.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class OnBottomScrollListener extends RecyclerView.OnScrollListener {

    private int offset;
    private final int limit;
    private int preTotalCount;
    private boolean loading;
    private final LinearLayoutManager layoutManager;

    protected OnBottomScrollListener(LinearLayoutManager layoutManager, int limit) {
        this.layoutManager = layoutManager;
        this.offset = limit;
        this.limit = limit;
        this.preTotalCount = 0;
        this.loading = false;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dx == 0 && dy == 0) {
            return;
        }

        int totalItemCount = layoutManager.getItemCount();
        int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

        if (totalItemCount != preTotalCount) {
            loading = false;
        }

        preTotalCount = totalItemCount;

        int loadLine = totalItemCount - limit;
        if (lastVisibleItem >= loadLine && !loading) {
            loading = true;
            onBottom(limit, offset);
            offset += limit;
        }
    }

    public abstract void onBottom(int limit, int offset);
}
