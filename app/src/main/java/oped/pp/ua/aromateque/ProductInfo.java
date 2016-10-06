package oped.pp.ua.aromateque;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.HashMap;

import oped.pp.ua.aromateque.db.DatabaseHelper;
import oped.pp.ua.aromateque.model.Category;
import oped.pp.ua.aromateque.model.LongProduct;
import oped.pp.ua.aromateque.model.RawLongProduct;
import oped.pp.ua.aromateque.product.fragments.ProductDescriptionFragment;
import oped.pp.ua.aromateque.product.fragments.ProductGeneralFragment;
import oped.pp.ua.aromateque.product.fragments.ProductReviewsFragment;
import oped.pp.ua.aromateque.utility.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductInfo extends CalligraphyActivity {
    public static final String BASE_URL = "http://10.0.1.50/";
    final int productId = 177;
    final int CATEGORY_ALL = 2;
    Toolbar toolbar;
    MagentoRestService api;
    LongProduct product;
    Category categoryAll;
    Category curCategory;
    boolean isAnimationRunning;
    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawerLayout;
    Resources res;
    DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        setTheme(R.style.AromatequeTheme_NoActionBar);
        res = getResources();
        dbHelper = new DatabaseHelper(this);
        //Initialize utility with icon sheet
        Utility.initialize(BitmapFactory.decodeResource(getResources(), R.drawable.icon_sheet));
        //Preparing toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setupDrawerWithFancyButton();
        setupFooter();

        //Working with RestAPI
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(MagentoRestService.class);

        if (!dbHelper.productExists(productId)) {
            class LongProductRecursiveCallback<T> implements Callback<T> {
                public void onResponse(Call<T> call, Response<T> response) {
                    try {
                        product = ((RawLongProduct) response.body()).convertToLongProduct();
                        dbHelper.serializeProduct(product);
                        fillProductInfo();
                    } catch (Exception e) {
                        Log.d("ERRORProduct", e.toString());
                        e.printStackTrace();
                    }
                }

                public void onFailure(Call<T> call, Throwable t) {
                    Snackbar.make(toolbar, t.toString(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    t.printStackTrace();
                    api.getProduct(productId).enqueue(new LongProductRecursiveCallback<RawLongProduct>());
                }
            }
            api.getProduct(productId).enqueue(new LongProductRecursiveCallback<RawLongProduct>());
        } else {
            product = dbHelper.deserializeProduct(productId);
            fillProductInfo();
        }
        class CategoryRecursiveCallback<T> implements Callback<T> {
            public void onResponse(Call<T> call, Response<T> response) {
                try {
                    categoryAll = (Category) response.body();
                    Log.d("INFO", categoryAll.getChildren().get(1).getName());
                    fillDrawer();
                } catch (Exception e) {
                    Log.d("ERRORCategory", e.toString());
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

    private void setupFooter() {
        ImageButton btnToFavourites = (ImageButton) findViewById(R.id.to_favorites);
        ImageButton btnToCart = (ImageButton) findViewById(R.id.to_cart);

        final Bitmap emptyHeart = Utility.getBitmapFromSheet(128, 64, 45, 41);
        final Bitmap filledHeart = Utility.getBitmapFromSheet(132, 108, 45, 41);
        btnToFavourites.setImageBitmap(emptyHeart);
        final boolean isHeartEmpty = true;
        btnToFavourites.setTag(isHeartEmpty);
        btnToFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((boolean) view.getTag()) {
                    ((ImageButton) view).setImageBitmap(filledHeart);
                    view.setTag(false); //just fuck this shit
                } else {
                    ((ImageButton) view).setImageBitmap(emptyHeart);
                    view.setTag(true);
                }
            }
        });
        btnToCart.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        btnToCart.setImageBitmap(BitmapFactory.decodeResource(res, R.drawable.cart));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private void setupDrawerWithFancyButton() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    //Dealing with Navigation Drawer content
    void fillDrawer() {
        //mDrawerList = (ListView)findViewById(R.id.navList);
        final ViewGroup sceneRoot = (ViewGroup) findViewById(R.id.drawer_scene_root);
        final ListView navListA = new ListView(this);//(ListView) findViewById(R.id.nav_list_a);
        sceneRoot.addView(navListA);
        Utility.compatSetBackgroundColor(res, navListA, R.color.white);
        /*sceneNavA = new Scene(sceneRoot, (View) navListA);
        sceneNavB = new Scene(sceneRoot, (View) navListB);
        transSetDrawer = new TransitionSet();
        Slide transSlide = new Slide(Gravity.START); //reusable
        transSlide.setMode(Slide.MODE_OUT);
        transSlide.addTarget(navListA);
        transSetDrawer.addTransition(transSlide);
        Fade transFade = new Fade(Fade.OUT);    //reusable
        transFade.addTarget(navListA);
        //transSetDrawer.addTransition(transFade);
        transSlide = new Slide(Gravity.END);
        transSlide.addTarget(navListB);
        transSetDrawer.addTransition(transSlide);
        transFade = new Fade(Fade.IN);
        transFade.addTarget(navListB);
        //transSetDrawer.addTransition(transFade);
        transSetDrawer.setOrdering(TransitionSet.ORDERING_TOGETHER);
        transSetDrawer.setDuration(5000);
        transSetDrawer.setInterpolator(new LinearInterpolator());
        transManager = new TransitionManager();
        transManager.setTransition(sceneNavA,sceneNavB,transSetDrawer);*/
        final Animation rightToCenter = AnimationUtils.loadAnimation(ProductInfo.this, R.anim.right_to_center);
        final Animation centerToLeft = AnimationUtils.loadAnimation(ProductInfo.this, R.anim.center_to_left);
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
        navListA.setAdapter(curCategory.getAdapter(this));
        Utility.compatSetBackgroundColor(res, navListA, R.color.white);
        class RecursiveOnItemClickListener implements AdapterView.OnItemClickListener {
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
        navListA.setOnItemClickListener(new RecursiveOnItemClickListener());
    }


    void fillProductInfo() {
        final HashMap<String, String> attributes = product.getAttributes();
        //Price transform
        //TextView productPrice = (TextView) findViewById(R.id.product_price);
        String stringPrice = attributes.get("price");
        stringPrice = stringPrice.substring(0, stringPrice.indexOf('.'));
        String stringDiscount = attributes.get("discount");
        stringDiscount = stringDiscount.substring(0, stringDiscount.indexOf('%'));
        long longPrice = Math.round(Integer.parseInt(stringPrice) * 0.01 * (100 - Integer.parseInt(stringDiscount)));
        //productPrice.setText(String.valueOf(longPrice));
        class ProductFragmentPagerAdapter extends FragmentPagerAdapter {
            private final int TAB_COUNT = 3;
            private String tabTitles[] = new String[]{res.getString(R.string.product_general), res.getString(R.string.product_description), res.getString(R.string.product_reviews)};
            private Context context;

            private ProductFragmentPagerAdapter(FragmentManager fm, Context context) {
                super(fm);
                this.context = context;
            }

            @Override
            public int getCount() {
                return TAB_COUNT;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return ProductGeneralFragment.newInstance(productId);
                    case 1:
                        return ProductDescriptionFragment.newInstance(attributes);
                    case 2:
                        return ProductReviewsFragment.newInstance(product.getReviews());
                    default:
                        return ProductGeneralFragment.newInstance(productId);
                }
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabTitles[position];
            }
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.product_content_tabs);
        ViewPager productContentViewpager = (ViewPager) findViewById(R.id.product_content_viewpager);
        productContentViewpager.setAdapter(new ProductFragmentPagerAdapter(getSupportFragmentManager(), this));
        tabLayout.setupWithViewPager(productContentViewpager);
    }

}


