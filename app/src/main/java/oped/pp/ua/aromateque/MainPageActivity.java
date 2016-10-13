package oped.pp.ua.aromateque;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import oped.pp.ua.aromateque.model.Category;
import oped.pp.ua.aromateque.model.ShortProduct;
import oped.pp.ua.aromateque.utility.EndlessRecyclerViewScrollListener;
import oped.pp.ua.aromateque.utility.IconSheet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static oped.pp.ua.aromateque.utility.Constants.BASE_URL;
import static oped.pp.ua.aromateque.utility.Constants.CATEGORY_ALL;

public class MainPageActivity extends CalligraphyActivity {
    Category categoryAll;
    Category curCategory;
    MagentoRestService api;
    private ImageLoader imgLoader;
    boolean isAnimationRunning;
    LayoutInflater layoutInflater;

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
        final ListView listviewCategories = (ListView) findViewById(R.id.listview_categories);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View listviewHeader = layoutInflater.inflate(R.layout.category_listview_header, null);
        listviewCategories.addHeaderView(listviewHeader);
        fillCategoriesHeaderAndFooter(listviewHeader);
        View txtListviewFooter = layoutInflater.inflate(R.layout.category_listview_footer, null);
        listviewCategories.addFooterView(txtListviewFooter);
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
        curCategory = categoryAll;
        listviewCategories.setAdapter(curCategory.getAdapter(this));
        /*class RecursiveOnItemClickListener implements AdapterView.OnItemClickListener {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isAnimationRunning && !curCategory.getChildren().get(position).getChildrenIds().equals("")) {
                    curCategory = curCategory.getChildren().get(position);
                    Log.d("LISTVIEW", curCategory.getChildrenIds());
                    ListView listViewFromRight = new ListView(ProductInfo.this);
                    Utility.compatSetBackgroundColor(res, listViewFromRight, R.color.white);
                    listViewFromRight.setId(View.generateViewId());
                    listViewFromRight.setAdapter(curCategory.getAdapter(ProductInfo.this));
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

    void fillCategoriesHeaderAndFooter(View root) {
        final ViewPager viewpagerBanners = (ViewPager) root.findViewById(R.id.banner_viewpager);
        final DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        class ImgPagerAdapter extends PagerAdapter {
            private List<String> productImgUrlList;

            private ImgPagerAdapter(Context context) {
                productImgUrlList = new ArrayList<>();
                productImgUrlList.add("http://10.0.1.50/media/adminforms/homepage_slider_b1/cache/1/cache/5e06319eda06f020e43594a9c230972d//r/b/rbr-1.jpg");
                productImgUrlList.add("http://10.0.1.50/media/adminforms/homepage_slider_b1/cache/1/cache/5e06319eda06f020e43594a9c230972d//r/b/rbr-1.jpg");
                productImgUrlList.add("http://10.0.1.50/media/adminforms/homepage_slider_b1/cache/1/cache/5e06319eda06f020e43594a9c230972d//r/b/rbr-1.jpg");
                productImgUrlList.add("http://10.0.1.50/media/adminforms/homepage_slider_b1/cache/1/cache/5e06319eda06f020e43594a9c230972d//r/b/rbr-1.jpg");
            }

            @Override
            public int getCount() {
                return productImgUrlList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == ((ImageView) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imgView = (ImageView) layoutInflater.inflate(R.layout.banner_item, container, false);
                //ImageView imgView = (ImageView) itemView.findViewById(R.id.img_view);
                //new DownloadImageTask(imgView).execute(productImgUrlList.get(position));
                imgLoader.displayImage(productImgUrlList.get(position), imgView, options);
                //imgView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ico_man_woman, null));
                container.addView(imgView);
                return imgView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((ImageView) object);
            }
        }
        viewpagerBanners.setAdapter(new ImgPagerAdapter(this));
        CirclePageIndicator viewPagerIndicator = (CirclePageIndicator) root.findViewById(R.id.viewpager_banner_indicator);
        viewPagerIndicator.setViewPager(viewpagerBanners);

        HorizontalGridView bestsellersView = (HorizontalGridView) root.findViewById(R.id.mainpage_bestsellers_gridview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MainPageActivity.this, LinearLayoutManager.HORIZONTAL, false);
        bestsellersView.setLayoutManager(layoutManager);

        ArrayList<ShortProduct> bestsellersList = new ArrayList<>();
        final ShortProduct prod = new ShortProduct();
        prod.setBrand("SASDA");
        prod.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/small_image/124x222/9df78eab33525d08d6e5fb8d27136e95/b/o/bohemes-50ml.jpg");
        prod.setName("asda");
        prod.setOldPrice("1321");
        prod.setPrice("1231");
        bestsellersList.add(prod);
        bestsellersList.add(prod);
        bestsellersList.add(prod);
        bestsellersList.add(prod);
        bestsellersList.add(prod);
        bestsellersList.add(prod);
        bestsellersList.add(prod);
        bestsellersList.add(prod);
        bestsellersList.add(prod);
        bestsellersList.add(prod);
        bestsellersList.add(prod);
        bestsellersList.add(prod);
        bestsellersList.add(prod);

        final Bitmap filledHeart = IconSheet.getBitmap(132, 108, 45, 41);


        final BestsellersViewAdapter adapter = new BestsellersViewAdapter(bestsellersList, getResources(), imgLoader);
        bestsellersView.setAdapter(adapter);
        bestsellersView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager, adapter));
    }


}

