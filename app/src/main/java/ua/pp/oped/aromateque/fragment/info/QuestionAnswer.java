package ua.pp.oped.aromateque.fragment.info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.pp.oped.aromateque.AdapterQA;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.utility.LinearLayoutManagerSmoothScrollEdition;

public class QuestionAnswer extends Fragment {


    public QuestionAnswer() {
    }


    public static QuestionAnswer newInstance() {
        return new QuestionAnswer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_q_a, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView qaRecyclerView = (RecyclerView) getView().findViewById(R.id.q_a_recyclerview);
        LinearLayoutManagerSmoothScrollEdition layoutManager = new LinearLayoutManagerSmoothScrollEdition(getContext(), RecyclerView.VERTICAL, false);
        layoutManager.setSmoothScroller(new LinearSmoothScroller(getContext()) {
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 0.8f / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, displayMetrics);
            }

            @Override
            protected int getVerticalSnapPreference() {
                return SNAP_TO_START;
            }

        });
        qaRecyclerView.setLayoutManager(layoutManager);
        qaRecyclerView.setAdapter(new AdapterQA(getContext(), qaRecyclerView));

    }

}
