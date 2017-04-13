package ua.pp.oped.aromateque.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;
import ua.pp.oped.aromateque.AromatequeApplication;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.adapter.AdapterCity;
import ua.pp.oped.aromateque.api.NovaPoshtaAPI;
import ua.pp.oped.aromateque.api.SearchSettlementPost;
import ua.pp.oped.aromateque.api.SearchSettlementResponse;
import ua.pp.oped.aromateque.api.SettlementPost;
import ua.pp.oped.aromateque.api.SettlementResponse;
import ua.pp.oped.aromateque.base_activity.CalligraphyActivity;
import ua.pp.oped.aromateque.utility.AnimationHelper;
import ua.pp.oped.aromateque.utility.EditTextBackEvent;
import ua.pp.oped.aromateque.utility.LinearLayoutManagerSmoothScrollEdition;
import ua.pp.oped.aromateque.utility.RetryableCallback;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

public class ActivityCheckoutCity extends CalligraphyActivity {
    View loadingMask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_city_top_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final EditTextBackEvent etCity = (EditTextBackEvent) findViewById(R.id.et_city);
        final RecyclerView recyclerCities = (RecyclerView) findViewById(R.id.recycler_cities);
        loadingMask = findViewById(R.id.loading_mask);
        final InputMethodManager inputMethodManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // Some strange bug
        etCity.getBackground().clearColorFilter();
        etCity.setOnEditTextImeBackListener(new EditTextBackEvent.EditTextImeBackListener() {
            @Override
            public void onImeBack(EditTextBackEvent editText, String text) {
                if (editText.isFocused()) {
                    editText.clearFocus();
                }
            }
        });
        etCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Timber.d("onFocusChange()");
                if (!hasFocus) {
                    Timber.d("!view.isFocused(), closing keyboard");
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        final NovaPoshtaAPI novaPoshtaAPI = AromatequeApplication.getNovaPoshtaAPI();
        final RetryableCallback<SettlementResponse> getDefaultSettlementsCallback = new RetryableCallback<SettlementResponse>() {
            @Override
            public void onFinalResponse(Call<SettlementResponse> call, Response<SettlementResponse> response) {
                List<SearchSettlementResponse.SearchSettlementItem> getSettlementItemsDefault = response.body().getSearchSettlements();
                recyclerCities.setAdapter(new AdapterCity(ActivityCheckoutCity.this, getSettlementItemsDefault));
                loadingMask.clearAnimation();
                loadingMask.setVisibility(View.GONE);
            }

            @Override
            public void onFinalFailure(Call<SettlementResponse> call, Throwable t) {
                loadingMask.clearAnimation();
                loadingMask.setVisibility(View.GONE);
            }
        };
        final RetryableCallback<SearchSettlementResponse> getSettlementsCallback = new RetryableCallback<SearchSettlementResponse>() {
            @Override
            public void onFinalResponse(Call<SearchSettlementResponse> call, Response<SearchSettlementResponse> response) {
                List<SearchSettlementResponse.SearchSettlementItem> searchSettlementItems = response.body().getCities();
                //Preprocess city items [adapting to api designers]
                for (int i = searchSettlementItems.size() - 1; i >= 0; i--) {
                    if (searchSettlementItems.get(i).getWarehouses() == 0) {
                        searchSettlementItems.remove(i);
                    }
                }
                //Region can end with "a"
                for (int i = searchSettlementItems.size() - 1; i >= 0; i--) {
                    String region = searchSettlementItems.get(i).getRegion();
                    String area = searchSettlementItems.get(i).getArea();
                    //if region is blank than area slides to region string, then don't change area and treat region as area instead
                    if (area.equals("")) {
                        searchSettlementItems.get(i).setRegion(region + " обл.");
                    } else {
                        searchSettlementItems.get(i).setRegion(region + " р-н");
                    }
                    if (!area.equals("")) {
                        searchSettlementItems.get(i).setArea(area + " обл.");
                    }
                }
                recyclerCities.setAdapter(new AdapterCity(ActivityCheckoutCity.this, searchSettlementItems));
                loadingMask.clearAnimation();
                loadingMask.setVisibility(View.GONE);
//                String cityName = response.body().getSettlements().get(0).getDescriptionRu();
//                etCity.setTag(cityName);
            }

            @Override
            public void onFinalFailure(Call<SearchSettlementResponse> call, Throwable t) {
                loadingMask.clearAnimation();
                loadingMask.setVisibility(View.GONE);
                t.printStackTrace();
            }
        };
        EditText.OnEditorActionListener onEditorActionListener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == IME_ACTION_DONE) {
                    if (textView.getText().length() == 0) {
                        novaPoshtaAPI.getSettlements(new SettlementPost("")).enqueue(getDefaultSettlementsCallback);
                        loadingMask.startAnimation(AnimationHelper.getDefaultRotateAnimation());
                        loadingMask.setVisibility(View.VISIBLE);
                        textView.clearFocus();
                        return true;
                    }
                    if (textView.getText().length() < 2) {
                        return true;
                    }
                    SearchSettlementPost searchSettlementPost = new SearchSettlementPost(textView.getText().toString());
                    novaPoshtaAPI.searchSettlements(searchSettlementPost).enqueue(getSettlementsCallback);
                    loadingMask.startAnimation(AnimationHelper.getDefaultRotateAnimation());
                    loadingMask.setVisibility(View.VISIBLE);

                    textView.clearFocus();
                    return true;
                }
                return false;
            }
        };
        etCity.setOnEditorActionListener(onEditorActionListener);
        recyclerCities.setLayoutManager(new LinearLayoutManagerSmoothScrollEdition(this, LinearLayoutManager.VERTICAL, false));
        loadingMask.startAnimation(AnimationHelper.getDefaultRotateAnimation());
        loadingMask.setVisibility(View.VISIBLE);
        novaPoshtaAPI.getSettlements(new SettlementPost("")).enqueue(getDefaultSettlementsCallback);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void onCityClicked(View view) {
        SearchSettlementResponse.SearchSettlementItem searchSettlementItem = (SearchSettlementResponse.SearchSettlementItem) view.getTag();
        String cityName = searchSettlementItem.getMainDescription();
        String regionName = searchSettlementItem.getRegion();
        String areaName = searchSettlementItem.getArea();
        Intent intent = new Intent();
        intent.putExtra("city_name", cityName);
        intent.putExtra("region_name", regionName);
        intent.putExtra("area_name", areaName);
        setResult(RESULT_OK, intent);
        finish();
    }
}
