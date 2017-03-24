package ua.pp.oped.aromateque.activity;

import android.os.Bundle;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.base_activity.SearchAppbarActivity;

public class ActivityCheckout extends SearchAppbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
    }
}
