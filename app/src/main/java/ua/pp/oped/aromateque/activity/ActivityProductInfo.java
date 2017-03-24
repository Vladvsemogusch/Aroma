package ua.pp.oped.aromateque.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;
import ua.pp.oped.aromateque.AromatequeApplication;
import ua.pp.oped.aromateque.MagentoRestService;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.base_activity.SearchAppbarActivity;
import ua.pp.oped.aromateque.data.db.DatabaseHelper;
import ua.pp.oped.aromateque.fragment.product.ProductDescriptionFragment;
import ua.pp.oped.aromateque.fragment.product.ProductGeneralFragment;
import ua.pp.oped.aromateque.fragment.product.ProductReviewsFragment;
import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.model.LongProduct;
import ua.pp.oped.aromateque.model.RawLongProduct;
import ua.pp.oped.aromateque.utility.Constants;
import ua.pp.oped.aromateque.utility.IconSheet;
import ua.pp.oped.aromateque.utility.RetryableCallback;

public class ActivityProductInfo extends SearchAppbarActivity {
    private int productId;
    private MagentoRestService api;
    private LongProduct product;
    private Category categoryAll;
    private Category curCategory;
    private boolean isAnimationRunning;
    private Resources res;
    private DatabaseHelper dbHelper;
    private String appbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AromatequeTheme_NoActionBar);
        res = getResources();
        dbHelper = DatabaseHelper.getInstance(this);
        //Preparing toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        productId = getIntent().getIntExtra("product_id", -1);
        setupFooter();
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Working with RestAPI
        api = AromatequeApplication.getApiMagento();

        if (!dbHelper.productExists(productId)) {
            api.getProduct(productId).enqueue(new RetryableCallback<RawLongProduct>() {
                public void onFinalResponse(Call<RawLongProduct> call, Response<RawLongProduct> response) {
                    try {
                        product = response.body().convertToLongProduct();
                        dbHelper.serializeProduct(product);
                        fillProductInfo();
                    } catch (Exception e) {
                        Log.e("ERRORProduct", e.toString());
                        e.printStackTrace();
                    }
                }

                public void onFinalFailure(Call<RawLongProduct> call, Throwable t) {
                    Snackbar.make(drawerLayout, t.toString(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        } else {
            product = dbHelper.deserializeProduct(productId);
            fillProductInfo();
        }
        dbHelper.close();
        api.getCategoryWithChildren(Constants.CATEGORY_ALL_ID).enqueue(new RetryableCallback<Category>() {
            public void onFinalResponse(Call<Category> call, Response<Category> response) {
                try {
                    categoryAll = response.body();
                    Log.d("INFO", categoryAll.getChildren().get(1).getName());
                } catch (Exception e) {
                    Log.e("ERRORCategory", e.toString());
                }
            }

            public void onFinalFailure(Call<Category> call, Throwable t) {
                Snackbar.make(drawerLayout, t.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    protected int getLayoutId() {
        return R.layout.activity_product_info_top_layout;
    }

    private void setupFooter() {
        ImageButton btnToFavourites = (ImageButton) findViewById(R.id.to_favorites);
        ImageButton btnToCart = (ImageButton) findViewById(R.id.to_cart);

        final Bitmap emptyHeart = IconSheet.getBitmap(128, 64, 45, 41);
        final Bitmap filledHeart = IconSheet.getBitmap(132, 108, 45, 41);
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


    //Dealing with Navigation Drawer content
//    void fillDrawer() {
//        //mDrawerList = (ListView)findViewById(R.id.navList);
//        final ViewGroup sceneRoot = (ViewGroup) findViewById(R.id.drawer_scene_root);
//        final ListView navListA = new ListView(this);//(ListView) findViewById(R.id.nav_list_a);
//        sceneRoot.addView(navListA);
//        navListA.setBackgroundColor(Utility.compatGetColor(res, R.color.white));
//        /*sceneNavA = new Scene(sceneRoot, (View) navListA);
//        sceneNavB = new Scene(sceneRoot, (View) navListB);
//        transSetDrawer = new TransitionSet();
//        Slide transSlide = new Slide(Gravity.START); //reusable
//        transSlide.setMode(Slide.MODE_OUT);
//        transSlide.addTarget(navListA);
//        transSetDrawer.addTransition(transSlide);
//        Fade transFade = new Fade(Fade.OUT);    //reusable
//        transFade.addTarget(navListA);
//        //transSetDrawer.addTransition(transFade);
//        transSlide = new Slide(Gravity.END);
//        transSlide.addTarget(navListB);
//        transSetDrawer.addTransition(transSlide);
//        transFade = new Fade(Fade.IN);
//        transFade.addTarget(navListB);
//        //transSetDrawer.addTransition(transFade);
//        transSetDrawer.setOrdering(TransitionSet.ORDERING_TOGETHER);
//        transSetDrawer.setDuration(5000);
//        transSetDrawer.setInterpolator(new LinearInterpolator());
//        transManager = new TransitionManager();
//        transManager.setTransition(sceneNavA,sceneNavB,transSetDrawer);*/
//        final Animation rightToCenter = AnimationUtils.loadAnimation(this, R.anim.right_to_center);
//        final Animation centerToLeft = AnimationUtils.loadAnimation(this, R.anim.center_to_left);
//        class MiniAnimationListener implements Animation.AnimationListener {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                isAnimationRunning = true;
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                isAnimationRunning = false;
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        }
//        rightToCenter.setAnimationListener(new MiniAnimationListener());
//        curCategory = categoryAll;
//        navListA.setAdapter(curCategory.getAdapter(this));
//        class RecursiveOnItemClickListener implements AdapterView.OnItemClickListener {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (!isAnimationRunning && curCategory.getChildren().get(position).getChildrenIds() != null) {
//                    curCategory = curCategory.getChildren().get(position);
//                    ListView listViewFromRight = new ListView(ActivityProductInfo.this);
//                    listViewFromRight.setBackgroundColor(Utility.compatGetColor(res, R.color.white));
//                    listViewFromRight.setId(View.generateViewId());
//                    listViewFromRight.setAdapter(curCategory.getAdapter(ActivityProductInfo.this));
//                    listViewFromRight.setTag(R.id.left_listview, parent.getId());
//                    listViewFromRight.setOnItemClickListener(new RecursiveOnItemClickListener());
//                    parent.startAnimation(centerToLeft);
//                    parent.setVisibility(View.GONE);
//                    listViewFromRight.startAnimation(rightToCenter);
//                }
//            }
//        }
//        navListA.setOnItemClickListener(new RecursiveOnItemClickListener());
//    }


    void fillProductInfo() {
        final HashMap<String, String> attributes = product.getAttributes();
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
        getSupportActionBar().setTitle(attributes.get("name"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_center, R.anim.center_to_right);
    }



    @Override
    public void onResume() {
        super.onResume();
        if (getSupportActionBar().getTitle() == "") {
            getSupportActionBar().setTitle(appbarTitle);
        }
    }

}


