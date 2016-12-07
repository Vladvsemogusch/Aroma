package ua.pp.oped.aromateque.fragments.productlist;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import ua.pp.oped.aromateque.FilterAdapter;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.model.FilterParameter;
import ua.pp.oped.aromateque.model.FilterParameterValue;
import ua.pp.oped.aromateque.model.PriceFilterParameterValue;
import ua.pp.oped.aromateque.utility.LinearLayoutManagerSmoothScrollEdition;

import static ua.pp.oped.aromateque.FilterAdapter.ActiveParameterChanged.ADD;
import static ua.pp.oped.aromateque.FilterAdapter.ActiveParameterChanged.DELETE;


public class FilterFragment extends Fragment {
    private static final String TAG = "FilterFragment";
    private RelativeLayout activeValuesLayout;

    public FilterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_drawer, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView parameterListView = (RecyclerView) getView().findViewById(R.id.parameter_list);
        LinearLayoutManagerSmoothScrollEdition layoutManager = new LinearLayoutManagerSmoothScrollEdition(getActivity(), RecyclerView.VERTICAL, false);
        parameterListView.setLayoutManager(layoutManager);
        parameterListView.setItemViewCacheSize(30);
        //parameterListView.setNestedScrollingEnabled(false);
        ArrayList<FilterParameter> parameters = new ArrayList<>();
        fillParameters(parameters);
        parameterListView.setAdapter(new FilterAdapter(getActivity(), parameters, parameterListView));
        layoutManager.setSmoothScroller(new LinearSmoothScroller(getActivity()) {
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 0.8f / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, displayMetrics);
            }
        });
        activeValuesLayout = (RelativeLayout) getView().findViewById(R.id.active_values_layout);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void fillParameters(ArrayList<FilterParameter> parameters) {
        FilterParameter brandName = new FilterParameter("Бренд", 0);
        brandName.addValue("Bottega Profumiera", 0);
        brandName.addValue("Les Liquides Imaginaires", 0);
        brandName.addValue("NU_BE", 0);
        brandName.addValue("Naomi Goodsir Parfums", 0);
        brandName.addValue("Kaloo", 0);
        brandName.addValue("Stephane Humbert Lucas 777", 0);
        brandName.addValue("Pierre Guillaume - Parfumerie Générale", 0);
        parameters.add(brandName);
        FilterParameter color = new FilterParameter("Цвет", 0);
        color.addValue("Light");
        color.addValue("Dark");
        color.addValue("Vitouaneous");
        parameters.add(color);
        FilterParameter price = new FilterParameter("Цена", 10);
        price.addValue(new PriceFilterParameterValue(1, 1, 100000));
        parameters.add(price);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onFilterParameterChanged(FilterAdapter.ActiveParameterChanged event) {
        switch (event.action) {
            case ADD:
                addActiveValueView(event.parameterValue);
                break;
            case DELETE:
                removeActiveValueView(event.parameterValue);
                break;
        }
    }

    private void addActiveValueView(FilterParameterValue newActiveValue) {
        Log.d(TAG, "addActiveValueView");
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.active_filter_value, activeValuesLayout, false);
        TextView txt = new TextView(getActivity());
        txt.setText("ASDASD");
        activeValuesLayout.addView(v);

    }

    private void removeActiveValueView(FilterParameterValue newActiveValue) {

    }
}
