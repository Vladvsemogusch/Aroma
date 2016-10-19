package ua.pp.oped.aromateque.utility;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;


public class EmptyRecycleViewAdapter extends RecyclerView.Adapter {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
