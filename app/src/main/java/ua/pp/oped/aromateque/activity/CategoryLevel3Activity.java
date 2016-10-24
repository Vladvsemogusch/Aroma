package ua.pp.oped.aromateque.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.SubCategoryViewAdapter;
import ua.pp.oped.aromateque.db.DatabaseHelper;
import ua.pp.oped.aromateque.model.Category;


public class CategoryLevel3Activity extends CalligraphyActivity {
    private ImageLoader imgLoader;
    RecyclerView recyclerviewLevel3Categories;
    ArrayList<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_level3);
        int mainCategoryId = getIntent().getIntExtra("category_id", -1);
        Category mainCategory = DatabaseHelper.getInstance().deserializeCategory(mainCategoryId);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imgLoader = ImageLoader.getInstance();
        recyclerviewLevel3Categories = (RecyclerView) findViewById(R.id.subcategories_recyclerview);
        recyclerviewLevel3Categories.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerviewLevel3Categories.setAdapter(new SubCategoryViewAdapter(CategoryLevel3Activity.this, mainCategory, getResources(), imgLoader));
        recyclerviewLevel3Categories.setItemViewCacheSize(30);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_center, R.anim.center_to_right);
    }

}
