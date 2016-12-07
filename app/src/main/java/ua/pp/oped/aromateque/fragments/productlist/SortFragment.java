package ua.pp.oped.aromateque.fragments.productlist;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import ua.pp.oped.aromateque.R;

import static ua.pp.oped.aromateque.activity.ProductListActivity.DEFAULT_SORT_TYPE;
import static ua.pp.oped.aromateque.activity.ProductListActivity.LIST_TYPE_BIG;
import static ua.pp.oped.aromateque.activity.ProductListActivity.LIST_TYPE_GRID;
import static ua.pp.oped.aromateque.activity.ProductListActivity.LIST_TYPE_WIDE;
import static ua.pp.oped.aromateque.activity.ProductListActivity.SORT_TYPE_CHEAP_FIRST;
import static ua.pp.oped.aromateque.activity.ProductListActivity.SORT_TYPE_DISCOUNT;
import static ua.pp.oped.aromateque.activity.ProductListActivity.SORT_TYPE_EXPENSIVE_FIRST;
import static ua.pp.oped.aromateque.activity.ProductListActivity.SORT_TYPE_LATEST;


public class SortFragment extends Fragment {

    // For sort radioButtons cache
    SharedPreferences settings;

    public SortFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sort_drawer, container, false);
        RadioButton rbSortType;
        rbSortType = (RadioButton) v.findViewById(R.id.expensive_first);
        rbSortType.setTag(SORT_TYPE_EXPENSIVE_FIRST);
        rbSortType = (RadioButton) v.findViewById(R.id.cheap_first);
        rbSortType.setTag(SORT_TYPE_CHEAP_FIRST);
        rbSortType = (RadioButton) v.findViewById(R.id.latest);
        rbSortType.setTag(SORT_TYPE_LATEST);
        rbSortType = (RadioButton) v.findViewById(R.id.discount);
        rbSortType.setTag(SORT_TYPE_DISCOUNT);
        ImageView imgListType;
        imgListType = (ImageView) v.findViewById(R.id.wide);
        imgListType.setTag(LIST_TYPE_WIDE);
        imgListType = (ImageView) v.findViewById(R.id.big);
        imgListType.setTag(LIST_TYPE_BIG);
        imgListType = (ImageView) v.findViewById(R.id.grid);
        imgListType.setTag(LIST_TYPE_GRID);
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        final int checkRadioButtonTag = settings.getInt("sort_type", DEFAULT_SORT_TYPE);
        getView().post(new Runnable() {
            @Override
            public void run() {
                View v = getView().findViewById(R.id.sort_types_radiogroup);
                RadioButton r;
                r = (RadioButton) v.findViewWithTag(checkRadioButtonTag);
                r.setChecked(true);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
