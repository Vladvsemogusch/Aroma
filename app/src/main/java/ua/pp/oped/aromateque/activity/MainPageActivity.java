package ua.pp.oped.aromateque.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;

import ua.pp.oped.aromateque.CalligraphyActivity;
import ua.pp.oped.aromateque.CategoryViewAdapter;
import ua.pp.oped.aromateque.MagentoRestService;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.db.DatabaseHelper;
import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.utility.LinearLayoutManagerSmoothScrollEdition;

import static ua.pp.oped.aromateque.utility.Constants.CATEGORY_ALL_ID;

public class MainPageActivity extends CalligraphyActivity {
    Category categoryAll;
    MagentoRestService api;
    boolean isAnimationRunning;
    LayoutInflater layoutInflater;
    RecyclerView recyclerviewCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        layoutInflater = (LayoutInflater) this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //Initialize recycleview early with empty adapter to avoid errors about absent adapter.
        recyclerviewCategories = (RecyclerView) findViewById(R.id.categories_main_recyclerview);
        final LinearLayoutManagerSmoothScrollEdition layoutManager = new LinearLayoutManagerSmoothScrollEdition(this, RecyclerView.VERTICAL, false);
        layoutManager.setSmoothScroller(new LinearSmoothScroller(this) {
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 0.8f / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, displayMetrics);
            }

        });
        recyclerviewCategories.setLayoutManager(layoutManager);
        //recyclerviewCategories.setAdapter(new EmptyRecycleViewAdapter());
        // Because no heavy duty on this list disable removing offscreen views
        recyclerviewCategories.setItemViewCacheSize(30);
        //Get categories from DB and put to new adapter
        categoryAll = DatabaseHelper.getInstance().deserializeCategory(CATEGORY_ALL_ID);
        recyclerviewCategories.setAdapter(new CategoryViewAdapter(this, categoryAll.getChildren(), recyclerviewCategories));
        //recyclerviewCategories.smoothScrollToPosition(5);
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, RecyclerView.VERTICAL);
        //dividerItemDecoration.setDrawable();
        //recyclerviewCategories.addItemDecoration(dividerItemDecoration);
        //fillCategories();


    }

    void fillCategories() {

        /*
        final Animation rightToCenter = AnimationUtils.loadAnimation(this, R.anim.right_to_center);
        final Animation centerToLeft = AnimationUtils.loadAnimation(this, R.anim.center_to_left);
        class MiniAnimationListener implements Animation.AnimationListener {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimationRunning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimationRunning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        }
        rightToCenter.setAnimationListener(new MiniAnimationListener());

        class RecursiveOnItemClickListener implements AdapterView.OnItemClickListener {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isAnimationRunning && !curCategory.getChildren().get(position).getChildrenIds().equals("")) {
                    curCategory = curCategory.getChildren().get(position);
                    Log.d("LISTVIEW", curCategory.getChildrenIds());
                    ListView listViewFromRight = new ListView(ProductInfoActivity.this);
                    Utility.compatSetBackgroundColor(res, listViewFromRight, R.color.white);
                    listViewFromRight.setId(View.generateViewId());
                    listViewFromRight.setAdapter(curCategory.getAdapter(ProductInfoActivity.this));
                    listViewFromRight.setTag(R.id.left_listview, parent.getId());
                    listViewFromRight.setOnItemClickListener(new RecursiveOnItemClickListener());
                    sceneRoot.addView(listViewFromRight);
                    parent.startAnimation(centerToLeft);
                    parent.setVisibility(View.GONE);
                    listViewFromRight.startAnimation(rightToCenter);
                }
            }
        }
        listviewCategories.setOnItemClickListener(new RecursiveOnItemClickListener());
        */
    }


}

