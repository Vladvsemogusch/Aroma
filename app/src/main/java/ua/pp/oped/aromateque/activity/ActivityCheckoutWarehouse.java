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
import ua.pp.oped.aromateque.adapter.AdapterWarehouse;
import ua.pp.oped.aromateque.api.NovaPoshtaAPI;
import ua.pp.oped.aromateque.api.WarehousePost;
import ua.pp.oped.aromateque.api.WarehouseResponse;
import ua.pp.oped.aromateque.base_activity.CalligraphyActivity;
import ua.pp.oped.aromateque.utility.AnimationHelper;
import ua.pp.oped.aromateque.utility.EditTextBackEvent;
import ua.pp.oped.aromateque.utility.LinearLayoutManagerSmoothScrollEdition;
import ua.pp.oped.aromateque.utility.RetryableCallback;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

public class ActivityCheckoutWarehouse extends CalligraphyActivity {
    View loaderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_warehouse_top);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final EditTextBackEvent etWarehouse = (EditTextBackEvent) findViewById(R.id.et_city);
        final RecyclerView recyclerWarehouses = (RecyclerView) findViewById(R.id.recycler_cities);
        loaderSpinner = findViewById(R.id.loading_mask);
        final InputMethodManager inputMethodManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        final String cityName = getIntent().getStringExtra("city_name");
        Timber.d("cityName = " + cityName);
        // Some strange bug
        etWarehouse.getBackground().clearColorFilter();
        etWarehouse.setOnEditTextImeBackListener(new EditTextBackEvent.EditTextImeBackListener() {
            @Override
            public void onImeBack(EditTextBackEvent editText, String text) {
                if (editText.isFocused()) {
                    editText.clearFocus();
                }
            }
        });
        etWarehouse.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        final NovaPoshtaAPI novaPoshtaAPI = AromatequeApplication.getNovaPoshtaAPI();
        final RetryableCallback<WarehouseResponse> getDefaultWarehousesCallback = new RetryableCallback<WarehouseResponse>() {
            @Override
            public void onFinalResponse(Call<WarehouseResponse> call, Response<WarehouseResponse> response) {
                List<WarehouseResponse.WarehouseItem> defaultWarehouseItems = response.body().getData();
                recyclerWarehouses.setAdapter(new AdapterWarehouse(ActivityCheckoutWarehouse.this, defaultWarehouseItems));
                loaderSpinner.clearAnimation();
                loaderSpinner.setVisibility(View.GONE);
            }

            @Override
            public void onFinalFailure(Call<WarehouseResponse> call, Throwable t) {
                loaderSpinner.clearAnimation();
                loaderSpinner.setVisibility(View.GONE);
            }
        };
        final RetryableCallback<WarehouseResponse> getWarehousesCallback = new RetryableCallback<WarehouseResponse>() {
            @Override
            public void onFinalResponse(Call<WarehouseResponse> call, Response<WarehouseResponse> response) {
                List<WarehouseResponse.WarehouseItem> warehouseItems = response.body().getData();
                recyclerWarehouses.setAdapter(new AdapterWarehouse(ActivityCheckoutWarehouse.this, warehouseItems));
                loaderSpinner.clearAnimation();
                loaderSpinner.setVisibility(View.GONE);
//                String cityName = response.body().getSettlements().get(0).getDescriptionRu();
//                etCity.setTag(cityName);
            }

            @Override
            public void onFinalFailure(Call<WarehouseResponse> call, Throwable t) {
                loaderSpinner.clearAnimation();
                loaderSpinner.setVisibility(View.GONE);
                t.printStackTrace();
            }
        };
        EditText.OnEditorActionListener onEditorActionListener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == IME_ACTION_DONE) {
                    if (textView.getText().length() == 0) {
                        novaPoshtaAPI.getWarehouses(new WarehousePost(cityName, "")).enqueue(getDefaultWarehousesCallback);
                        loaderSpinner.startAnimation(AnimationHelper.getDefaultRotateAnimation());
                        loaderSpinner.setVisibility(View.VISIBLE);
                        textView.clearFocus();
                        return true;
                    }
                    novaPoshtaAPI.getWarehouses(new WarehousePost(cityName, textView.getText().toString())).enqueue(getWarehousesCallback);
                    loaderSpinner.startAnimation(AnimationHelper.getDefaultRotateAnimation());
                    loaderSpinner.setVisibility(View.VISIBLE);

                    textView.clearFocus();
                    return true;
                }
                return false;
            }
        };
        etWarehouse.setOnEditorActionListener(onEditorActionListener);
        recyclerWarehouses.setLayoutManager(new LinearLayoutManagerSmoothScrollEdition(this, LinearLayoutManager.VERTICAL, false));
        loaderSpinner.startAnimation(AnimationHelper.getDefaultRotateAnimation());
        loaderSpinner.setVisibility(View.VISIBLE);

        novaPoshtaAPI.getWarehouses(new WarehousePost(cityName, "")).enqueue(getDefaultWarehousesCallback);

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
        WarehouseResponse.WarehouseItem warehouseItem = (WarehouseResponse.WarehouseItem) view.getTag();
        String fullDescription = warehouseItem.getDescriptionRu();
        String[] splitFullDescription = fullDescription.split(": ", 2);
        Intent intent = new Intent();
        intent.putExtra("warehouse_number", splitFullDescription[0]);
        intent.putExtra("warehouse_address", splitFullDescription[1]);
        setResult(RESULT_OK, intent);
        finish();
    }
}
