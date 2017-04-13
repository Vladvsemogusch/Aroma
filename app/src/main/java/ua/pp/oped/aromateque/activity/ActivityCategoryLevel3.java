package ua.pp.oped.aromateque.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import timber.log.Timber;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.adapter.AdapterSubCategoryView;
import ua.pp.oped.aromateque.base_activity.SearchAppbarActivity;
import ua.pp.oped.aromateque.data.db.DatabaseHelper;
import ua.pp.oped.aromateque.model.Category;


public class ActivityCategoryLevel3 extends SearchAppbarActivity {
    private static final String TAG = "ActivityCategoryLevel3";
    private RecyclerView recyclerviewLevel3Categories;
    private ArrayList<Category> categories;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        int mainCategoryId = getIntent().getIntExtra("category_id", -1);
        final Category mainCategory = DatabaseHelper.getInstance(this).deserializeCategory(mainCategoryId);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mainCategory.getName());
        recyclerviewLevel3Categories = (RecyclerView) findViewById(R.id.subcategories_recyclerview);
        recyclerviewLevel3Categories.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerviewLevel3Categories.setAdapter(new AdapterSubCategoryView(ActivityCategoryLevel3.this, mainCategory, true));
        recyclerviewLevel3Categories.setItemViewCacheSize(30);


        //  If recyclerview is shorter than mainLayout, then move footer from recyclerview to bottom of main layout.
        recyclerviewLevel3Categories.post(new Runnable() {
            @Override
            public void run() {
                View appbarLayout = findViewById(R.id.appbar_layout);
                int actionbarHeight = appbarLayout.getHeight();
                //Log.d(TAG, "appbarLayout.getHeight() = " + appbarLayout.getHeight());
                int filledSpace = actionbarHeight + recyclerviewLevel3Categories.getHeight();
                //Log.d(TAG, "filledSpace = " + filledSpace);
                RelativeLayout mainLayout = (RelativeLayout) ActivityCategoryLevel3.this.findViewById(R.id.category_level3_layout);
                int allSpace = mainLayout.getHeight();
                //Log.d(TAG, "allSpace = " + allSpace);
                if (filledSpace < allSpace) {
                    //Log.d("FOOTER", "Relocating footer");
                    AdapterSubCategoryView adapter =
                            new AdapterSubCategoryView(ActivityCategoryLevel3.this, mainCategory, false);
                    recyclerviewLevel3Categories.setAdapter(adapter);
                    LayoutInflater layoutInflater = LayoutInflater.from(ActivityCategoryLevel3.this);
                    layoutInflater.inflate(R.layout.phone, mainLayout, true);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_category_level3_top_layout;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_center, R.anim.center_to_right);
    }


    public void onAddToCartClicked(View v) {
        int productId = (int) v.getTag();
        if (!DatabaseHelper.getInstance(this).isInCart(productId)) {
            DatabaseHelper.getInstance(this).addToCart(productId);
            updateCartCounter();
            Timber.d("Added to cart");
        } else {
            Timber.d("Already in cart product id: " + productId);
        }
        onCartClicked(null);
    }


}
