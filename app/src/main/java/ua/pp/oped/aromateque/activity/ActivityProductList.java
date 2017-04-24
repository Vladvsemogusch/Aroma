package ua.pp.oped.aromateque.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.adapter.AdapterFilter;
import ua.pp.oped.aromateque.adapter.AdapterProductList;
import ua.pp.oped.aromateque.base_activity.SearchAppbarActivity;
import ua.pp.oped.aromateque.data.db.DatabaseHelper;
import ua.pp.oped.aromateque.fragment.productlist.FilterFragment;
import ua.pp.oped.aromateque.fragment.productlist.SortFragment;
import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.model.FilterParameterValue;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.EmptyRecycleViewAdapter;
import ua.pp.oped.aromateque.utility.Utility;

public class ActivityProductList extends SearchAppbarActivity {
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
    private List<ShortProduct> shortProducts;
    private TextView sortTypeSmall;
    private TextView filteredAmountSmall;
    private SharedPreferences settings;
    private FragmentManager fragmentManager;
    private SortFragment sortFragment;
    private FilterFragment filterFragment;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DatabaseHelper.getInstance(this);
        int currentCategoryId = getIntent().getIntExtra("category_id", 16); //TODO change after REST implemented
        Category currentCategory = db.deserializeCategory(currentCategoryId);
        settings = getPreferences(MODE_PRIVATE);
        final int sortTypeSetting = settings.getInt("sort_type", DEFAULT_SORT_TYPE);
        final int listTypeSetting = settings.getInt("list_type", DEFAULT_LIST_TYPE);
        getSupportActionBar().setTitle(currentCategory.getName());
        new android.os.Handler().post(new Runnable() {
            @Override
            public void run() {
                onSortTypeClicked(sortTypeSetting);
                onListTypeClicked(listTypeSetting);
            }
        });
        cartCounter = (TextView) findViewById(R.id.cart_counter);
        sortTypeSmall = (TextView) findViewById(R.id.txt_sort_type_small);
        filteredAmountSmall = (TextView) findViewById(R.id.txt_filtered_amount_small);
        productListRecyclerView = (RecyclerView) findViewById(R.id.product_list_recycler_view);
        gridLayoutManager = new GridLayoutManager(this, 1, RecyclerView.VERTICAL, false);
        productListRecyclerView.setLayoutManager(gridLayoutManager);
        productListRecyclerView.setAdapter(new EmptyRecycleViewAdapter());
        productListRecyclerView.setItemViewCacheSize(7);
        View sortByButton = findViewById(R.id.sort_by_button);
        final View filterByButton = findViewById(R.id.filter_by_button);
        final LinearLayout rightDrawerLayout = (LinearLayout) findViewById(R.id.product_list_right_drawer);
        shortProducts = fillProductList();
        // Setup right drawer
        fragmentManager = getFragmentManager();
        sortFragment = new SortFragment();
        filterFragment = new FilterFragment();
        sortByButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if (filterFragment.isAdded()) {
                    //Log.d(TAG,"fragmentManager.getBackStackEntryCount() " + fragmentManager.getBackStackEntryCount());
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
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.product_list_right_drawer, filterFragment);
        transaction.commit();
    }

    protected int getLayoutId() {
        return R.layout.activity_product_list_top_layout;
    }

    private List<ShortProduct> fillProductList() {
        List<ShortProduct> productList = new ArrayList<>();
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
        productList.add(prod);
        productList.add(prod2);
        productList.add(prod);
        productList.add(prod2);
        productList.add(prod);
        productList.add(prod2);
        productList.add(prod);
        productList.add(prod2);
        productList.add(prod);
        productList.add(prod2);
        productList.add(prod);
        productList.add(prod2);
        productList.add(prod);
        final ShortProduct prod3 = new ShortProduct();
        prod3.setBrand("Sensai");
        //prod.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/thumbnail/630x/602f0fa2c1f0d1ba5e241f914e856ff9/s/a/sa0110gsilv.jpg");
        prod3.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/small_image/124x222/9df78eab33525d08d6e5fb8d27136e95/b/o/bohemes-50ml.jpg");
        prod3.setName("BODY FIRMING EMULSION CELLULAR PERFORMANCE");
        prod3.setTypeAndVolume("набор \"двойное увлажнение\", 60мл+50мл+30мл+30мл");
        prod3.setOldPrice("13210");
        prod3.setPrice("12310");
        productList.add(prod3);
        prod.setId(1425);
        prod2.setId(3519);
        prod3.setId(3518);
        //productListRecyclerView.setAdapter(new AdapterProductList(shortProducts, this, R.layout.short_product_item_with_type));
        return productList;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START) || drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawers();
        } else {
            //Disregard fragment backstack and close activity
            if (fragmentManager.getBackStackEntryCount() > 0) {
                FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(0);
                fragmentManager.popBackStackImmediate(entry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.left_to_center, R.anim.center_to_right);
        }
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
                productListRecyclerView.setAdapter(new AdapterProductList(shortProducts, ActivityProductList.this, R.layout.short_product_item_with_type));
                gridLayoutManager.setSpanCount(2);
                break;
            case LIST_TYPE_BIG:
                gridLayoutManager.setSpanCount(1);
                productListRecyclerView.setAdapter(new AdapterProductList(shortProducts, ActivityProductList.this, R.layout.short_product_item_with_type_big));
                break;
            case LIST_TYPE_WIDE:
                productListRecyclerView.setAdapter(new AdapterProductList(shortProducts, ActivityProductList.this, R.layout.short_product_item_with_type_wide));
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


    public void onAddToCartClicked(View v) {
        int productId = (int) v.getTag();

        Timber.d("ADD TO CART CLICKED");
        if (!db.isInCart(productId)) {
            db.addToCart(productId);
            updateCartCounter();
            ((AdapterProductList) productListRecyclerView.getAdapter()).updateCartItems(productId);
            ((ImageView) v).setImageDrawable(Utility.compatGetDrawable(getResources(), R.drawable.ico_cart_full));
            Animation addToCartAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_cart);
            v.startAnimation(addToCartAnimation);
            Timber.d("Added to cart");
        } else {
            db.removeFromCart(productId);
            ((ImageView) v).setImageDrawable(Utility.compatGetDrawable(getResources(), R.drawable.ico_cart_empty));
//            Intent intent = new Intent(this, ActivityCart.class);
//            this.startActivity(intent);
//            Log.d(TAG, "Already in cart product id: " + productId);
        }
        //TODO Visuals
    }


    @Override
    public void onResume() {
        super.onResume();
        productListRecyclerView.getAdapter().notifyDataSetChanged();
        if (productListRecyclerView.getAdapter() instanceof AdapterProductList) {
            ((AdapterProductList) productListRecyclerView.getAdapter()).updateCartItems();
        }
    }

    public void onAddToFavoritesClicked(View v) {
        int productId = (int) v.getTag();
        if (db.isInFavorites(productId)) {
            db.removeFavorite(productId);
            ((ImageButton) v).setImageDrawable(Utility.compatGetDrawable(getResources(), R.drawable.heart_empty_beige));
        } else {
            db.addFavorite(productId);
            ((ImageButton) v).setImageDrawable(Utility.compatGetDrawable(getResources(), R.drawable.heart_beige));
        }
    }

}
