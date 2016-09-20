package oped.pp.ua.aromateque;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductInfo extends CalligraphyActivity {
    final static String BASE_URL = "http://10.0.1.50/";
    final int CATEGORY_ALL = 2;
    Toolbar toolbar;
    Call<LongProduct> callGetProduct;
    MagentoRestService api;
    LongProduct product;
    Category categoryAll;
    Category curCategory;
    private ArrayAdapter<String> mAdapter;
    boolean isAnimationRunning;
    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_info);
        setTheme(R.style.AromatequeTheme_NoActionBar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupDrawerWithFancyButton();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(MagentoRestService.class);
        final int productId = 4344;
        callGetProduct = api.getProduct(productId);
        class LongProductRecursiveCallback<T> implements Callback<T> {
            public void onResponse(Call<T> call, Response<T> response) {
                try {
                    product = (LongProduct) response.body();
                    //Log.d("imageUrls","DGGDSGSDG"+product.getImageUrls().get(0));
                    //productImgList.addAll(product.getImageUrls());

                } catch (Exception e) {
                    Log.d("ERROR2", e.toString());
                }
                fillProductInfo();
            }

            public void onFailure(Call<T> call, Throwable t) {
                Snackbar.make(toolbar, t.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                t.printStackTrace();
                api.getProduct(productId).enqueue(new LongProductRecursiveCallback<LongProduct>());
            }
        }
        callGetProduct.enqueue(new LongProductRecursiveCallback<LongProduct>());
        Call<Category> callGetCategories = api.getCategoryWithChildren(CATEGORY_ALL);
        class CategoryRecursiveCallback<T> implements Callback<T> {
            public void onResponse(Call<T> call, Response<T> response) {
                try {
                    categoryAll = (Category) response.body();
                    Log.d("INFO", categoryAll.getChildren().get(1).getName());
                    fillDrawer();
                } catch (Exception e) {
                    Log.d("ERROR2", e.toString());
                }
            }

            public void onFailure(Call<T> call, Throwable t) {
                Snackbar.make(toolbar, t.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                t.printStackTrace();
                api.getProduct(productId).enqueue(new LongProductRecursiveCallback<LongProduct>());
            }
        }
        callGetCategories.enqueue(new CategoryRecursiveCallback<Category>());


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
        compatSetBackgroundColor(navListA, R.color.white);
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
        compatSetBackgroundColor(navListA, R.color.white);
        class RecursiveOnItemClickListener implements AdapterView.OnItemClickListener {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isAnimationRunning && !curCategory.getChildren().get(position).getChildrenIds().equals("")) {
                    curCategory = curCategory.getChildren().get(position);
                    Log.d("LISTVIEW", curCategory.getChildrenIds());
                    ListView listViewFromRight = new ListView(ProductInfo.this);
                    compatSetBackgroundColor(listViewFromRight, R.color.white);
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
        Resources res = getResources();
        NestedScrollView productScrollviewMain = (NestedScrollView) findViewById(R.id.product_scrollview_main);
        productScrollviewMain.setForegroundGravity(Gravity.END);
        HashMap<String, String> attributes = product.getAttributes();
        HashMap<String, String> listAttrs = product.getListAttrs();
        ViewPager productImgViewpager = (ViewPager) findViewById(R.id.product_img_viewpager);
        //ImageView imgProduct = (ImageView) findViewById(R.id.img_product);
        ImageView imgGender = (ImageView) findViewById(R.id.img_gender);
        TextView txtBrandProductName = (TextView) findViewById(R.id.txt_brand_product_name);
        TextView txtProductType = (TextView) findViewById(R.id.txt_product_type);
        TextView txtReviewsCount = (TextView) findViewById(R.id.txt_reviews_count);
        TextView txtGender = (TextView) findViewById(R.id.txt_gender);
        TextView txtDescriptionTitle = (TextView) findViewById(R.id.txt_description_title);
        TextView txtDescription = (TextView) findViewById(R.id.txt_description);
        TextView txtTopNotesContent = (TextView) findViewById(R.id.txt_top_notes_content);
        TextView txtMiddleNotesContent = (TextView) findViewById(R.id.txt_middle_notes_content);
        TextView txtBaseNotesContent = (TextView) findViewById(R.id.txt_base_notes_content);
        TextView txtFragranceNotes = (TextView) findViewById(R.id.txt_fragrance_notes);
        LinearLayout containerAttrNameList = (LinearLayout) findViewById(R.id.container_attr_name_list);
        LinearLayout containerAttrValueList = (LinearLayout) findViewById(R.id.container_attr_value_list);
        LinearLayout containerTopNotesList = (LinearLayout) findViewById(R.id.container_top_notes_list);
        LinearLayout containerMiddleNotesList = (LinearLayout) findViewById(R.id.container_middle_notes_list);
        LinearLayout containerBaseNotesList = (LinearLayout) findViewById(R.id.container_base_notes_list);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        class ImgPagerAdapter extends PagerAdapter {

            Context context;
            LayoutInflater layoutInflater;
            List<String> productImgList = product.getImageUrlsWrapper().getImageUrls();

            public ImgPagerAdapter(Context context) {
                this.context = context;
                layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public int getCount() {
                return productImgList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == ((ImageView) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View itemView = layoutInflater.inflate(R.layout.img_pager_item, container, false);
                ImageView imgView = (ImageView) itemView.findViewById(R.id.img_view);
                new DownloadImageTask(imgView).execute(productImgList.get(position));
                container.addView(itemView);
                return itemView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((LinearLayout) object);
            }
        }
        productImgViewpager.setAdapter(new ImgPagerAdapter(this));
        CirclePageIndicator viewPagerIndicator = (CirclePageIndicator) findViewById(R.id.viewpager_indicator);
        viewPagerIndicator.setViewPager(productImgViewpager);
        //viewPagerIndicator.setFillColor(R.color.colorAccent);
        //viewPagerIndicator.setStrokeColor(R.color.colorAccent);
        //viewPagerIndicator.setSnap(true);
        //viewPagerIndicator.setPageColor(R.color.colorAccent);
        Spanned brandProductName = compatFromHtml(String.format(res.getString(R.string.brand_name), attributes.get("shopbybrand_brand"), "<b>" + attributes.get("name") + "</b>"));
        txtBrandProductName.setText(brandProductName);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(brandProductName);
        }
        txtProductType.setText(attributes.get("typeofproduct").toLowerCase());
        String marks = "";
        String reviews = attributes.get("total_reviews_count");
        int lastDigit = Integer.parseInt(reviews.substring(reviews.length() - 1));
        switch (lastDigit) {
            case 1:
                marks = "ОЦЕНКА";
                break;
            case 2:
            case 3:
            case 4:
                marks = "ОЦЕНКИ";
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 0:
                marks = "ОЦЕНОК";
        }
        txtReviewsCount.setText(String.format(res.getString(R.string.reviews_count), reviews, marks));
        txtGender.setText(attributes.get("gender").toUpperCase());
        txtDescriptionTitle.setText(String.format(res.getString(R.string.description_title), attributes.get("name")));
        txtDescription.setText(compatFromHtml(attributes.get("description")));
        switch (attributes.get("gender")) {
            case "для детей":
                imgGender.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.ico_child, null));
                break;
            case "для женщин":
                imgGender.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.ico_women, null));
                break;
            case "для мужчин":
                imgGender.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.ico_man, null));
                break;
            case "для женщин и мужчин":
                imgGender.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.ico_man_woman, null));
                break;
        }
        if (attributes.get("rating_summary") != null) {
            ratingBar.setRating(5.0f / 100 * Integer.parseInt(attributes.get("rating_summary")));
        } else {
            ratingBar.setVisibility(View.GONE);
        }
        //fill first attribute list
        TextView attrNameView;  //reusable
        TextView attrValueView; //reusable
        if (!listAttrs.get("volume").equals("No")) {
            attrNameView = new TextView(this);
            attrNameView.setText("Объем");
            containerAttrNameList.addView(attrNameView);
            attrValueView = new TextView(this);
            attrValueView.setText(listAttrs.get("volume"));
            containerAttrValueList.addView(attrValueView);
        }
        if (listAttrs.get("perfumer") != null) {
            if (listAttrs.get("perfumer") != null || !listAttrs.get("perfumer").equals("No")) {
                attrNameView = new TextView(this);
                attrNameView.setText("Парфюмер");
                containerAttrNameList.addView(attrNameView);
                attrValueView = new TextView(this);
                attrValueView.setText(listAttrs.get("perfumer"));
                containerAttrValueList.addView(attrValueView);
            }
        }
        if (!attributes.get("country").equals("No")) {
            attrNameView = new TextView(this);
            attrNameView.setText("Страна");
            containerAttrNameList.addView(attrNameView);
            attrValueView = new TextView(this);
            if (!attributes.get("year_of_manufacture").equals("No")) {
                attrValueView.setText(String.format(getString(R.string.country_year), attributes.get("country"), attributes.get("year_of_manufacture")));
            } else {
                attrValueView.setText(attributes.get("country"));
            }
            containerAttrValueList.addView(attrValueView);
        }
        //fill notes
        String stringNotes;//reusable
        Map<String, String> notes = product.getNotes();
        if (!notes.get("topnotes").equals("false")) {
            stringNotes = checkAndCut(notes.get("topnotes"));
            txtTopNotesContent.setText(stringNotes);
        } else {
            containerTopNotesList.setVisibility(View.GONE);
        }
        if (!notes.get("middlenotes").equals("false")) {
            stringNotes = checkAndCut(notes.get("middlenotes"));
            txtMiddleNotesContent.setText(stringNotes);
        } else {
            containerMiddleNotesList.setVisibility(View.GONE);
        }
        if (!notes.get("basenotes").equals("false")) {
            stringNotes = checkAndCut(notes.get("basenotes"));
            txtBaseNotesContent.setText(stringNotes);
        } else {
            containerBaseNotesList.setVisibility(View.GONE);
        }


    }

    public static Spanned compatFromHtml(String input) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(input);
        }
    }

    public void compatSetBackgroundColor(View view, int colorId) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(getResources().getColor(colorId, null));
        } else {
            view.setBackgroundColor(getResources().getColor(colorId));
        }

    }

    // limit note length to 12 and split notes string by space
    public String checkAndCut(String input) {
        List<String> inputDivided = Arrays.asList(input.split(", "));
        String returnNotes = "";
        for (String note : inputDivided) {
            if (note.length() > 12) {
                returnNotes += note.replace(" ", "\n");
            } else {
                returnNotes += note;
            }
            if (inputDivided.indexOf(note) != (inputDivided.size() - 1)) {
                returnNotes += ",\n";
            }
        }
        return returnNotes;
    }

}

