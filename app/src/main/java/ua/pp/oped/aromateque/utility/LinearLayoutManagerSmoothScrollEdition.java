package ua.pp.oped.aromateque.utility;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;


public class LinearLayoutManagerSmoothScrollEdition extends LinearLayoutManager {
    private LinearSmoothScroller smoothScroller;

    public LinearLayoutManagerSmoothScrollEdition(Context context) {
        super(context);
    }

    public LinearLayoutManagerSmoothScrollEdition(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }


    public void setSmoothScroller(LinearSmoothScroller smoothScroller) {
        this.smoothScroller = smoothScroller;
    }

    @Override
    public void smoothScrollToPosition(@Nullable RecyclerView recyclerView,
                                       @Nullable RecyclerView.State state,
                                       int position) {
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}
