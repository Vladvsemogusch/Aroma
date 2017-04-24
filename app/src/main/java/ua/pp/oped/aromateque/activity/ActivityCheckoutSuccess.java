package ua.pp.oped.aromateque.activity;

import android.os.Bundle;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.base_activity.SearchAppbarActivity;

public class ActivityCheckoutSuccess extends SearchAppbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_checkout_success_top;
    }

}
