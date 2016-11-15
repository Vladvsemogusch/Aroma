package ua.pp.oped.aromateque.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ua.pp.oped.aromateque.CalligraphyActivity;
import ua.pp.oped.aromateque.ProductListAdapter;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.fragments.productlist.FilterFragment;
import ua.pp.oped.aromateque.fragments.productlist.SortFragment;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.EmptyRecycleViewAdapter;

public class ProductListActivity extends CalligraphyActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    final private String TAG = "ProductListActivity";
    RecyclerView productListRecyclerView;
    GridLayoutManager gridLayoutManager;
    ArrayList<ShortProduct> bestsellersList;
    DrawerLayout drawer;
    TextView sortTypeSmall;
    TextView filteredAmountSmall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int currentCategoryId = getIntent().getIntExtra("category_id", -1);
        // Drawer setup
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        sortTypeSmall = (TextView) findViewById(R.id.txt_sort_type_small);
        filteredAmountSmall = (TextView) findViewById(R.id.txt_filtered_amount_small);

        productListRecyclerView = (RecyclerView) findViewById(R.id.product_list_recycler_view);
        gridLayoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        productListRecyclerView.setLayoutManager(gridLayoutManager);
        productListRecyclerView.setAdapter(new EmptyRecycleViewAdapter());
        View sortByButton = findViewById(R.id.sort_by_button);
        final View filterByButton = findViewById(R.id.filter_by_button);
        final LinearLayout rightDrawerLayout = (LinearLayout) findViewById(R.id.product_list_right_drawer);
        bestsellersList = new ArrayList<>();
        fillProductList();
        final android.app.FragmentManager fragmentManager = getFragmentManager();
        final Fragment sortFragment = new SortFragment();
        final Fragment filterFragment = new FilterFragment();
        sortByButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if (filterFragment.isAdded()) {
                    transaction.replace(R.id.product_list_right_drawer, sortFragment);
                } else if (!sortFragment.isAdded()) {
                    transaction.add(R.id.product_list_right_drawer, sortFragment);
                }
                transaction.commit();
                drawer.openDrawer(GravityCompat.END);

            }
        });
        filterByButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if (sortFragment.isAdded()) {
                    transaction.replace(R.id.product_list_right_drawer, filterFragment);
                } else if (!filterFragment.isAdded()) {
                    transaction.add(R.id.product_list_right_drawer, filterFragment);
                }
                transaction.commit();
                drawer.openDrawer(GravityCompat.END);
            }
        });
    }

    private void fillProductList() {
        final ShortProduct prod = new ShortProduct();
        prod.setBrand("Comme des Garcons Accessories");
        //prod.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/thumbnail/630x/602f0fa2c1f0d1ba5e241f914e856ff9/s/a/sa0110gsilv.jpg");
        prod.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/small_image/124x222/9df78eab33525d08d6e5fb8d27136e95/b/o/bohemes-50ml.jpg");
        prod.setName("Silver Wallet");
        prod.setTypeAndVolume("набор \"двойное увлажнение\", 60мл+50мл+30мл+30мл");
        prod.setOldPrice("13210");
        prod.setPrice("12310");
        final ShortProduct prod2 = new ShortProduct();
        prod2.setBrand("Sensai");
        //prod.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/thumbnail/630x/602f0fa2c1f0d1ba5e241f914e856ff9/s/a/sa0110gsilv.jpg");
        prod2.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/small_image/124x222/9df78eab33525d08d6e5fb8d27136e95/b/o/bohemes-50ml.jpg");
        prod2.setName("SILKY DESIGN ROUGE");
        prod2.setTypeAndVolume("набор \"двойное увлажнение\", 60мл+50мл+30мл+30мл");
        prod2.setOldPrice("13210");
        prod2.setPrice("12310");
        bestsellersList.add(prod);
        bestsellersList.add(prod2);
        bestsellersList.add(prod);
        bestsellersList.add(prod2);
        bestsellersList.add(prod);
        bestsellersList.add(prod2);
        bestsellersList.add(prod);
        bestsellersList.add(prod2);
        bestsellersList.add(prod);
        bestsellersList.add(prod2);
        bestsellersList.add(prod);
        bestsellersList.add(prod2);
        bestsellersList.add(prod);
        final ShortProduct prod3 = new ShortProduct();
        prod3.setBrand("Sensai");
        //prod.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/thumbnail/630x/602f0fa2c1f0d1ba5e241f914e856ff9/s/a/sa0110gsilv.jpg");
        prod3.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/small_image/124x222/9df78eab33525d08d6e5fb8d27136e95/b/o/bohemes-50ml.jpg");
        prod3.setName("BODY FIRMING EMULSION CELLULAR PERFORMANCE");
        prod3.setTypeAndVolume("набор \"двойное увлажнение\", 60мл+50мл+30мл+30мл");
        prod3.setOldPrice("13210");
        prod3.setPrice("12310");
        bestsellersList.add(prod3);
        prod.setId(1425);
        prod2.setId(3519);
        prod3.setId(3518);
        productListRecyclerView.setAdapter(new ProductListAdapter(bestsellersList, this, R.layout.short_product_item_with_type));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START) || drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawers();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.left_to_center, R.anim.center_to_right);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void onListTypeClicked(View view) {
        switch (view.getId()) {
            case R.id.grid:
                productListRecyclerView.setAdapter(new ProductListAdapter(bestsellersList, ProductListActivity.this, R.layout.short_product_item_with_type));
                gridLayoutManager.setSpanCount(2);
                break;
            case R.id.wide:
                gridLayoutManager.setSpanCount(1);
                productListRecyclerView.setAdapter(new ProductListAdapter(bestsellersList, ProductListActivity.this, R.layout.short_product_item_with_type_wide));
                break;
            case R.id.list:
                productListRecyclerView.setAdapter(new ProductListAdapter(bestsellersList, ProductListActivity.this, R.layout.short_product_item_with_type_horizontal));
                gridLayoutManager.setSpanCount(1);
                break;
        }
    }

    //TODO fetch new data based on sort type (or rearrange)
    public void onSortTypeClicked(View view) {
        switch (view.getId()) {
            case R.id.cheaper_first:
                sortTypeSmall.setText(getResources().getString(R.string.cheaper_first));
                break;
            case R.id.expensive_first:
                sortTypeSmall.setText(getResources().getString(R.string.expensive_first));
                break;
            case R.id.latest:
                sortTypeSmall.setText(getResources().getString(R.string.latest));
                break;
            case R.id.discount:
                sortTypeSmall.setText(getResources().getString(R.string.discount));
                break;
        }
    }

    public void onCloseClicked(View view) {
        drawer.closeDrawer(GravityCompat.END);
    }
}
