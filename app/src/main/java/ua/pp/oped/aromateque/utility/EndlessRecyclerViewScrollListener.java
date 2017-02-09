package ua.pp.oped.aromateque.utility;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ua.pp.oped.aromateque.AdapterBestsellersView;

public class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private int visibleThreshold = 3;
    private int previousTotalItemCount = 0;
    private boolean loading = true;
    private AdapterBestsellersView adapter;
    private LinearLayoutManager mLayoutManager;
    private int lastVisibleItemPosition;
    private int totalItemCount;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager, AdapterBestsellersView adapter) {
        this.mLayoutManager = layoutManager;
        this.adapter = adapter;
        totalItemCount = mLayoutManager.getItemCount();
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {

        lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
        if (totalItemCount < previousTotalItemCount) {
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
        }

        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount && dx > 0) {
            for (int i = 1; i >= 0; i--) {
                adapter.products.add(adapter.products.remove(i));
            }
            new Handler().post(new Runnable() {
                public void run() {
                    adapter.notifyItemRangeRemoved(0, 2);
                    adapter.notifyItemRangeInserted(adapter.getItemCount(), 2);
                }
            });
        }
        if (!loading && (mLayoutManager.findFirstVisibleItemPosition() - visibleThreshold) < 0 && dx < 0) {
            for (int i = adapter.getItemCount() - 1; i >= adapter.getItemCount() - 2; i--) {
                adapter.products.add(0, adapter.products.remove(i));
            }
            new Handler().post(new Runnable() {
                public void run() {
                    adapter.notifyItemRangeRemoved(adapter.getItemCount() - 2, 2);
                    adapter.notifyItemRangeInserted(0, 2);
                }
            });
        }
    }

}