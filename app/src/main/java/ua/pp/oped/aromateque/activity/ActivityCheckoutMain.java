package ua.pp.oped.aromateque.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import timber.log.Timber;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.base_activity.CalligraphyActivity;
import ua.pp.oped.aromateque.utility.EditTextBackEvent;
import ua.pp.oped.aromateque.utility.TextValidation;
import ua.pp.oped.aromateque.utility.Utility;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

public class ActivityCheckoutMain extends CalligraphyActivity {
    View lytChooseCity;
    View lytChooseCityTextHolder;
    View lytChooseDepartment;
    View lytChooseDepartmentTextHolder;
    TextView tvCityName;
    TextView tvRegionName;
    TextView tvAreaName;
    TextView tvCityPlaceHolder;
    TextView tvWarehousePlaceholder;
    TextView tvWarehouseNumber;
    TextView tvWarehouseAddress;
    View cityDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_main_top_layout);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final InputMethodManager inputMethodManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        final EditTextBackEvent etNameSurname = (EditTextBackEvent) findViewById(R.id.checkout_name_surname);
        final EditTextBackEvent etPhone = (EditTextBackEvent) findViewById(R.id.checkout_phone);
        final EditTextBackEvent etEmail = (EditTextBackEvent) findViewById(R.id.checkout_email);
        final TextView tvErrorNoCity = (TextView) findViewById(R.id.txt_checkout_no_city);
        final TextView tvErrorNoName = (TextView) findViewById(R.id.txt_checkout_no_name);
        final TextView tvErrorNoPhone = (TextView) findViewById(R.id.txt_checkout_no_phone);
        final TextView tvErrorNoEmail = (TextView) findViewById(R.id.txt_checkout_no_email);
        tvCityName = (TextView) findViewById(R.id.checkout_city);
        tvRegionName = (TextView) findViewById(R.id.checkout_region);
        tvAreaName = (TextView) findViewById(R.id.checkout_area);
        tvCityPlaceHolder = (TextView) findViewById(R.id.checkout_city_placeholder);
        tvWarehouseNumber = (TextView) findViewById(R.id.checkout_department_number);
        tvWarehouseAddress = (TextView) findViewById(R.id.checkout_department_address);
        tvWarehousePlaceholder = (TextView) findViewById(R.id.checkout_department_placeholder);
        Button btnReviewSubmit = (Button) findViewById(R.id.checkout_submit);
        lytChooseCity = findViewById(R.id.lyt_choose_city);
        lytChooseCityTextHolder = findViewById(R.id.checkout_choose_city_text_holder);
        lytChooseDepartment = findViewById(R.id.lyt_choose_department);
        lytChooseDepartmentTextHolder = findViewById(R.id.checkout_choose_department_text_holder);
        cityDivider = findViewById(R.id.city_divider);
        EditTextBackEvent.EditTextImeBackListener imeBackListener = new EditTextBackEvent.EditTextImeBackListener() {
            @Override
            public void onImeBack(EditTextBackEvent editText, String text) {
                if (editText.isFocused()) {
                    editText.clearFocus();
                }
            }
        };
        etNameSurname.setOnEditTextImeBackListener(imeBackListener);
        etPhone.setOnEditTextImeBackListener(imeBackListener);
        etEmail.setOnEditTextImeBackListener(imeBackListener);
//        txtErrorNoCity
//        txtErrorNoName
//        txtErrorNoPhone
//        txtErrorBadEmail
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (!etNameSurname.isFocused() && !etPhone.isFocused() && !etEmail.isFocused()) {
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        };
        etNameSurname.setOnFocusChangeListener(onFocusChangeListener);
        etPhone.setOnFocusChangeListener(onFocusChangeListener);
        etEmail.setOnFocusChangeListener(onFocusChangeListener);
        EditText.OnEditorActionListener onEditorActionListener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == IME_ACTION_DONE) {
                    textView.clearFocus();
                    return true;
                }
                return false;
            }
        };
        etNameSurname.setOnEditorActionListener(onEditorActionListener);
        etPhone.setOnEditorActionListener(onEditorActionListener);
        etEmail.setOnEditorActionListener(onEditorActionListener);

        btnReviewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = true;
                if (TextValidation.validateEmail(etEmail.getText().toString())) {
                    tvErrorNoEmail.setVisibility(View.GONE);
                    etEmail.getBackground().clearColorFilter();
                } else {
                    etEmail.getBackground().setColorFilter(Utility.compatGetColor(getResources(), R.color.error_red), PorterDuff.Mode.SRC_ATOP);
                    if (etEmail.getText().toString().equals("")) {
                        tvErrorNoEmail.setText(getResources().getString(R.string.no_email));
                    } else {
                        tvErrorNoEmail.setText(getResources().getString(R.string.bad_email));
                    }
                    tvErrorNoEmail.setVisibility(View.VISIBLE);
                    success = false;
                }
                if (etNameSurname.getText().toString().equals("")) {
                    etNameSurname.getBackground().setColorFilter(Utility.compatGetColor(getResources(), R.color.error_red), PorterDuff.Mode.SRC_ATOP);
                    tvErrorNoName.setVisibility(View.VISIBLE);
                    success = false;
                } else {
                    etNameSurname.getBackground().clearColorFilter();
                    tvErrorNoName.setVisibility(View.GONE);
                }
                if (etPhone.getText().toString().equals("")) {
                    etPhone.getBackground().setColorFilter(Utility.compatGetColor(getResources(), R.color.error_red), PorterDuff.Mode.SRC_ATOP);
                    tvErrorNoPhone.setVisibility(View.VISIBLE);
                    success = false;
                } else {
                    etPhone.getBackground().clearColorFilter();
                    tvErrorNoPhone.setVisibility(View.GONE);
                }
                // If placeholder is visible than city isn't chosen => show error text.
                if (tvCityPlaceHolder.getVisibility() == View.VISIBLE) {
                    cityDivider.setBackgroundColor(Utility.compatGetColor(getResources(), R.color.error_red));
                    tvErrorNoCity.setVisibility(View.VISIBLE);
                } else {
                    cityDivider.setBackgroundColor(Utility.compatGetColor(getResources(), R.color.colorAccent));
                    tvErrorNoCity.setVisibility(View.INVISIBLE);
                }
                if (tvWarehousePlaceholder.getVisibility() == View.VISIBLE) {
                    if (tvWarehousePlaceholder.getText().toString().equals(getResources().getString(R.string.warehouse_placeholder))) {
                        tvWarehousePlaceholder.setTextColor(Utility.compatGetColor(getResources(), R.color.error_red));
                    }
                    success = false;
                } else {

                }
                if (!success) {
                    return;
                }
                Intent intent = new Intent(ActivityCheckoutMain.this, ActivityCheckoutOverview.class);
                startActivity(intent);
                //TODO REST-READY Submit checkout.
            }
        });

        lytChooseCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityCheckoutMain.this, ActivityCheckoutCity.class);
                startActivityForResult(intent, 1);
            }
        });
        lytChooseDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvCityPlaceHolder.getVisibility() == View.VISIBLE) {
                    tvWarehousePlaceholder.setText(getResources().getString(R.string.city_first));
                    tvWarehousePlaceholder.setTextColor(Utility.compatGetColor(getResources(), R.color.error_red));
                    return;
                }
                Intent intent = new Intent(ActivityCheckoutMain.this, ActivityCheckoutWarehouse.class);
                intent.putExtra("city_name", tvCityName.getText().toString());
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            String cityName = data.getStringExtra("city_name");
            String regionName = data.getStringExtra("region_name");
            String areaName = data.getStringExtra("area_name");
            if (regionName != null && !regionName.equals("")) {
                Timber.d("regionName!=null");
                tvRegionName.setVisibility(View.VISIBLE);
                tvRegionName.setText(regionName);
            } else {
                tvRegionName.setVisibility(View.GONE);
            }
            if (areaName != null && !areaName.equals("")) {
                Timber.d("areaName!=null");
                tvAreaName.setVisibility(View.VISIBLE);
                tvAreaName.setText(areaName);
            } else {
                tvAreaName.setVisibility(View.GONE);
            }
            tvCityName.setText(cityName);
            lytChooseCityTextHolder.setVisibility(View.VISIBLE);
            tvCityPlaceHolder.setVisibility(View.GONE);
            if (tvWarehousePlaceholder.getVisibility() == View.VISIBLE) {
                tvWarehousePlaceholder.setText(getResources().getString(R.string.warehouse_placeholder));
                tvWarehousePlaceholder.setTextColor(Utility.compatGetColor(getResources(), R.color.dark_gray));
            }
        }
        if (requestCode == 2) {
            String warehouseNumber = data.getStringExtra("warehouse_number");
            String warehouseAddress = data.getStringExtra("warehouse_address");
            tvWarehouseNumber.setText(warehouseNumber);
            tvWarehouseAddress.setText(warehouseAddress);
            lytChooseDepartmentTextHolder.setVisibility(View.VISIBLE);
            tvWarehousePlaceholder.setVisibility(View.GONE);
        }
    }

}
