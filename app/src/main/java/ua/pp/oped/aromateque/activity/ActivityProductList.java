package ua.pp.oped.aromateque.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import timber.log.Timber;
import ua.pp.oped.aromateque.AdapterFilter;
import ua.pp.oped.aromateque.AdapterProductList;
import ua.pp.oped.aromateque.CalligraphyActivity;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.data.db.DatabaseHelper;
import ua.pp.oped.aromateque.fragments.productlist.FilterFragment;
import ua.pp.oped.aromateque.fragments.productlist.SortFragment;
import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.model.FilterParameterValue;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.EmptyRecycleViewAdapter;
import ua.pp.oped.aromateque.utility.Utility;

public class ActivityProductList extends CalligraphyActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "ActivityProductList";
    public static final int SORT_TYPE_EXPENSIVE_FIRST = 199;
    public static final int SORT_TYPE_CHEAP_FIRST = 753;
    public static final int SORT_TYPE_LATEST = 877;
    public static final int SORT_TYPE_DISCOUNT = 825;
    public static final int LIST_TYPE_WIDE = 169;
    public static final int LIST_TYPE_BIG = 245;
    public static final int LIST_TYPE_GRID = 70;
    public static final int DEFAULT_SORT_TYPE = SORT_TYPE_LATEST;
    private static final int DEFAULT_LIST_TYPE = LIST_TYPE_WIDE;
    private RecyclerView productListRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<ShortProduct> bestsellersList;
    private DrawerLayout drawer;
    private TextView sortTypeSmall;
    private TextView filteredAmountSmall;
    private SharedPreferences settings;
    private SortFragment sortFragment;
    private FilterFragment filterFragment;
    private DatabaseHelper dbHelper;
    TextView cartCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = DatabaseHelper.getInstance(this);
        int currentCategoryId = getIntent().getIntExtra("category_id", 16); //TODO change after REST implemented
        Category currentCategory = dbHelper.deserializeCategory(currentCategoryId);
        settings = getPreferences(MODE_PRIVATE);
        final int sortTypeSetting = settings.getInt("sort_type", DEFAULT_SORT_TYPE);
        final int listTypeSetting = settings.getInt("list_type", DEFAULT_LIST_TYPE);
        new android.os.Handler().post(new Runnable() {
            @Override
            public void run() {
                onSortTypeClicked(sortTypeSetting);
                onListTypeClicked(listTypeSetting);
            }
        });

        getSupportActionBar().setTitle(currentCategory.getName());
        // Drawer setup
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        cartCounter = (TextView) findViewById(R.id.cart_counter);
        sortTypeSmall = (TextView) findViewById(R.id.txt_sort_type_small);
        filteredAmountSmall = (TextView) findViewById(R.id.txt_filtered_amount_small);
        productListRecyclerView = (RecyclerView) findViewById(R.id.product_list_recycler_view);
        gridLayoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        productListRecyclerView.setLayoutManager(gridLayoutManager);
        productListRecyclerView.setAdapter(new EmptyRecycleViewAdapter());
        productListRecyclerView.setItemViewCacheSize(7);
        View sortByButton = findViewById(R.id.sort_by_button);
        final View filterByButton = findViewById(R.id.filter_by_button);
        final LinearLayout rightDrawerLayout = (LinearLayout) findViewById(R.id.product_list_right_drawer);
        bestsellersList = new ArrayList<>();
        fillProductList();
        final android.app.FragmentManager fragmentManager = getFragmentManager();
        sortFragment = new SortFragment();
        filterFragment = new FilterFragment();
        sortByButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if (filterFragment.isAdded()) {
                    //Log.d(TAG,"fragmentManager.getBackStackEntryCount() "+fragmentManager.getBackStackEntryCount());
                    filterFragment.prepareForRemoval();
                    if (fragmentManager.getBackStackEntryCount() > 0) {
                        fragmentManager.popBackStack();
                        //Log.d(TAG,"fragmentManager.popBackStack(); SORT");
                    } else {
                        transaction.replace(R.id.product_list_right_drawer, sortFragment);
                        transaction.addToBackStack(null);
                    }
                } else if (!sortFragment.isAdded()) {
                    transaction.add(R.id.product_list_right_drawer, sortFragment);
                }
                if (!transaction.isEmpty()) {
                    transaction.commit();
                }
                drawer.openDrawer(GravityCompat.END);
            }
        });
        filterByButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if (sortFragment.isAdded()) {
                    if (fragmentManager.getBackStackEntryCount() > 0) {
                        fragmentManager.popBackStack();
                        //Log.d(TAG,"fragmentManager.popBackStack(); FILTER");
                    } else {
                        transaction.replace(R.id.product_list_right_drawer, filterFragment);
                        transaction.addToBackStack(null);
                    }
                } else if (!filterFragment.isAdded()) {
                    transaction.add(R.id.product_list_right_drawer, filterFragment);
                }
                if (!transaction.isEmpty()) {
                    transaction.commit();
                }
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
        //productListRecyclerView.setAdapter(new AdapterProductList(bestsellersList, this, R.layout.short_product_item_with_type));

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
        // Saving here because onListTypeClicked(int viewId) called on startup, don't need to save there
        onListTypeClicked((Integer) view.getTag());
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putInt("list_type", (Integer) view.getTag());
        settingsEditor.apply();
    }

    public void onListTypeClicked(int listType) {
        switch (listType) {
            case LIST_TYPE_GRID:
                productListRecyclerView.setAdapter(new AdapterProductList(bestsellersList, ActivityProductList.this, R.layout.short_product_item_with_type));
                gridLayoutManager.setSpanCount(2);
                break;
            case LIST_TYPE_BIG:
                gridLayoutManager.setSpanCount(1);
                productListRecyclerView.setAdapter(new AdapterProductList(bestsellersList, ActivityProductList.this, R.layout.short_product_item_with_type_big));
                break;
            case LIST_TYPE_WIDE:
                productListRecyclerView.setAdapter(new AdapterProductList(bestsellersList, ActivityProductList.this, R.layout.short_product_item_with_type_wide));
                gridLayoutManager.setSpanCount(1);
                break;

        }
    }

    //TODO fetch new data based on sort type (or rearrange)
    public void onSortTypeClicked(View view) {
        Log.d(TAG, String.valueOf(view.getTag()));
        onSortTypeClicked((Integer) view.getTag());
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putInt("sort_type", (Integer) view.getTag());
        settingsEditor.apply();
    }

    public void onSortTypeClicked(int sortType) {
        switch (sortType) {
            case SORT_TYPE_CHEAP_FIRST:
                sortTypeSmall.setText(getResources().getString(R.string.cheaper_first));
                break;
            case SORT_TYPE_EXPENSIVE_FIRST:
                sortTypeSmall.setText(getResources().getString(R.string.expensive_first));
                break;
            default:
            case SORT_TYPE_LATEST:
                sortTypeSmall.setText(getResources().getString(R.string.latest));
                break;
            case SORT_TYPE_DISCOUNT:
                sortTypeSmall.setText(getResources().getString(R.string.discount));
                break;
        }
    }

    public void onCloseClicked(View view) {
        drawer.closeDrawer(GravityCompat.END);
    }

    @Subscribe
    public void onActiveFilterParametersChanged(AdapterFilter.ActiveParametersChanged event) {
        ArrayList<FilterParameterValue> activeFilterParameters = event.activeFilterParameters;
        Log.d(TAG, "onActiveFilterParametersChanged called");
        //TODO REST-READY Update productlist based on new filter parameters
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

    public void onCartClicked(View v) {
        Intent intent = new Intent(this, ActivityCart.class);
        this.startActivity(intent);
    }

    public void onAddToCartClicked(View v) {
        int productId = (int) v.getTag();
        if (!dbHelper.isInCart(productId)) {
            dbHelper.addToCart(productId);
            setupCart();
            ((ImageView) v).setImageDrawable(Utility.compatGetDrawable(getResources(), R.drawable.icon_cart_black));
            ((AdapterProductList) productListRecyclerView.getAdapter()).updateCartItems(productId);
            Timber.d("Added to cart");
        } else {
            Intent intent = new Intent(this, ActivityCart.class);
            this.startActivity(intent);
            Log.d(TAG, "Already in cart product id: " + productId);
        }
        //TODO Visuals
    }

    private void setupCart() {
        int cartQty = DatabaseHelper.getInstance(this).getCartQty();
        if (cartQty == 0) {
            cartCounter.setVisibility(View.GONE);
        } else {
            cartCounter.setText(String.valueOf(cartQty));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupCart();
        productListRecyclerView.getAdapter().notifyDataSetChanged();
        if (productListRecyclerView.getAdapter() instanceof AdapterProductList) {
            ((AdapterProductList) productListRecyclerView.getAdapter()).updateCartItems();
        }
    }
}
