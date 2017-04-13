package ua.pp.oped.aromateque.base_activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import timber.log.Timber;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.activity.ActivityCallback;
import ua.pp.oped.aromateque.activity.ActivityCart;
import ua.pp.oped.aromateque.activity.ActivityInfo;
import ua.pp.oped.aromateque.activity.ActivityMainPage;

public class GlobalDrawerActivity extends CalligraphyActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawer;
    protected CustomToggle toggle;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new CustomToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateSelectedItem();
    }

    //Stub; must be overridden
    protected int getLayoutId() {
        return -1;
    }


    private void updateSelectedItem() {
        switch (getLayoutId()) {
            case R.layout.activity_main_page_top_layout:
                navigationView.setCheckedItem(R.id.nav_main_page);
                Timber.d("navigationView.setCheckedItem(R.id.nav_main_page);");
                break;
            case R.layout.activity_cart_top_layout:
                navigationView.setCheckedItem(R.id.nav_cart);
                Timber.d("navigationView.setCheckedItem(R.id.nav_cart);");
                break;
            case R.layout.activity_callback_top_layout:
                navigationView.setCheckedItem(R.id.nav_callback);
                Timber.d("navigationView.setCheckedItem(R.id.nav_callback);");
                break;
            case R.layout.activity_info_top_layout:
                navigationView.setCheckedItem(R.id.nav_info);
                Timber.d("navigationView.setCheckedItem(R.id.nav_info);");
                break;
        }
    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        toggle.setPendingMenuItem(item);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class CustomToggle extends ActionBarDrawerToggle {
        private MenuItem pendingMenuItem;

        public CustomToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        public void setPendingMenuItem(MenuItem pendingMenuItem) {
            this.pendingMenuItem = pendingMenuItem;
        }

        /**
         * Called when a drawer has settled in a completely closed state.
         */
        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            //Resolve pending menu click;
            if (pendingMenuItem == null) {
                return;
            }
            int id = pendingMenuItem.getItemId();
            Intent intent = null;
            switch (id) {
                case R.id.nav_main_page:
                    if (!(GlobalDrawerActivity.this instanceof ActivityMainPage)) {
                        intent = new Intent(GlobalDrawerActivity.this, ActivityMainPage.class);
                    }
                    break;
                case R.id.nav_cart:
                    if (!(GlobalDrawerActivity.this instanceof ActivityCart)) {
                        intent = new Intent(GlobalDrawerActivity.this, ActivityCart.class);
                    }
                    break;
                case R.id.nav_callback:
                    if (!(GlobalDrawerActivity.this instanceof ActivityCallback)) {
                        intent = new Intent(GlobalDrawerActivity.this, ActivityCallback.class);
                    }
                    break;
                case R.id.nav_info:
                    if (!(GlobalDrawerActivity.this instanceof ActivityInfo)) {
                        intent = new Intent(GlobalDrawerActivity.this, ActivityInfo.class);
                    }
                    break;
            }
            if (intent != null) {
                pendingMenuItem = null;
                startActivity(intent);
            }
        }

        /**
         * Called when a drawer has settled in a completely open state.
         */
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSelectedItem();
    }
}
