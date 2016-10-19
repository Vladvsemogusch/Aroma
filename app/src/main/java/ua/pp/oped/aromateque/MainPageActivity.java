package ua.pp.oped.aromateque;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.utility.EmptyRecycleViewAdapter;
import ua.pp.oped.aromateque.utility.IconSheet;

import static ua.pp.oped.aromateque.utility.Constants.BASE_URL;
import static ua.pp.oped.aromateque.utility.Constants.CATEGORY_ALL;

public class MainPageActivity extends CalligraphyActivity {
    Category categoryAll;
    Category curCategory;
    MagentoRestService api;
    private ImageLoader imgLoader;
    boolean isAnimationRunning;
    LayoutInflater layoutInflater;
    RecyclerView recyclerviewCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        imgLoader = ImageLoader.getInstance();
        imgLoader.init(ImageLoaderConfiguration.createDefault(this));
        layoutInflater = (LayoutInflater) this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        //Initialize IconSheet
        IconSheet.initialize(BitmapFactory.decodeResource(getResources(), R.drawable.icon_sheet));
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(MagentoRestService.class);
        //Initialize recycleview early with empty adapter to avoid errors about absent adapter.
        recyclerviewCategories = (RecyclerView) findViewById(R.id.categories_recycleview);
        recyclerviewCategories.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerviewCategories.setAdapter(new EmptyRecycleViewAdapter());
        // Because no heavy duty on this list disable removing offscreen views
        recyclerviewCategories.setItemViewCacheSize(30);
        class CategoryRecursiveCallback<T> implements Callback<T> {
            public void onResponse(Call<T> call, Response<T> response) {
                try {
                    categoryAll = (Category) response.body();
                    Log.d("INFO", categoryAll.getChildren().get(1).getName());
                    fillCategories();
                } catch (Exception e) {
                    Log.d("ERRORCategory", e.toString());
                    e.printStackTrace();
                }
            }

            public void onFailure(Call<T> call, Throwable t) {
                Snackbar.make(toolbar, t.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                t.printStackTrace();
                api.getCategoryWithChildren(CATEGORY_ALL).enqueue(new CategoryRecursiveCallback<Category>());
            }
        }
        api.getCategoryWithChildren(CATEGORY_ALL).enqueue(new CategoryRecursiveCallback<Category>());

    }

    void fillCategories() {
        curCategory = categoryAll;
        recyclerviewCategories.setAdapter(new CategoryViewAdapter(this, curCategory.getChildren(), getResources(), imgLoader));
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

        /*class RecursiveOnItemClickListener implements AdapterView.OnItemClickListener {
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

