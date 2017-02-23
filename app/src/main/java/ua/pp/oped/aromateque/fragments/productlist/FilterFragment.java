package ua.pp.oped.aromateque.fragments.productlist;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ua.pp.oped.aromateque.AdapterFilter;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.model.EntityIdName;
import ua.pp.oped.aromateque.model.FilterParameter;
import ua.pp.oped.aromateque.model.PriceFilterParameterValue;
import ua.pp.oped.aromateque.utility.LinearLayoutManagerSmoothScrollEdition;


public class FilterFragment extends Fragment {
    private static final String TAG = "FilterFragment";
    private RecyclerView parameterRecyclerView;
    private AdapterFilter adapterFilter;
    LinearLayoutManagerSmoothScrollEdition layoutManager;

    public FilterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.filter_drawer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        ArrayList<EntityIdName> parameters = new ArrayList<>();
        if (savedInstanceState != null) {
            Log.d(TAG, "savedInstanceState!=null");
            parameters = savedInstanceState.getParcelableArrayList("filter_adapter_list");
        } else {
            fillParameters(parameters);
        }
        parameterRecyclerView = (RecyclerView) getView().findViewById(R.id.parameter_list);
        LinearLayoutManagerSmoothScrollEdition layoutManager = new LinearLayoutManagerSmoothScrollEdition(getActivity(), RecyclerView.VERTICAL, false);
        parameterRecyclerView.setLayoutManager(layoutManager);
        parameterRecyclerView.setItemViewCacheSize(30);
        //parameterListView.setNestedScrollingEnabled(false);
        if (adapterFilter == null) {
            adapterFilter = new AdapterFilter(getActivity(), parameters, parameterRecyclerView);
        } else {
            adapterFilter.setRecyclerView(parameterRecyclerView);
        }

        parameterRecyclerView.setAdapter(adapterFilter);
        layoutManager.setSmoothScroller(new LinearSmoothScroller(getActivity()) {
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 0.8f / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, displayMetrics);
            }
        });
    }


    private void fillParameters(ArrayList<EntityIdName> parameters) {
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
        price.addValue(new PriceFilterParameterValue(1, 1, 50000));
        parameters.add(price);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");

    }

    public void prepareForRemoval() {
        ((AdapterFilter) parameterRecyclerView.getAdapter()).clearActiveValuesLayout();
        parameterRecyclerView.setAdapter(null);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        AdapterFilter adapter = (AdapterFilter) parameterRecyclerView.getAdapter();
        outState.putParcelableArrayList("active_filter_parameter_values", adapter.getActiveFilterParameterValues());
        outState.putParcelableArrayList("filter_adapter_list", adapter.getFilterAdapterList());

    }
}
