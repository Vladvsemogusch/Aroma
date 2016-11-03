package ua.pp.oped.aromateque;

import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import ua.pp.oped.aromateque.activity.CategoryLevel3Activity;
import ua.pp.oped.aromateque.activity.ProductListActivity;
import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.EndlessRecyclerViewScrollListener;
import ua.pp.oped.aromateque.utility.Utility;

public class CategoryViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private static final int TYPE_CHILD_ITEM = 3;
    private List<Category> categories;
    private Resources resources;
    private ImageLoader imgLoader;
    private LayoutInflater layoutInflater;
    private Context context;
    private RecyclerView recyclerView;

    public CategoryViewAdapter(Context context, List<Category> categories, RecyclerView recyclerView) {
        this.categories = categories;
        resources = context.getResources();
        this.imgLoader = ImageLoader.getInstance();
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.recyclerView = recyclerView;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View v;
        if (itemType == TYPE_HEADER) {
            v = layoutInflater.inflate(R.layout.category_list_header, viewGroup, false);
            return new HeaderViewHolder(v);
        }
        if (itemType == TYPE_FOOTER) {
            v = layoutInflater.inflate(R.layout.category_list_footer, viewGroup, false);
            return new FooterViewHolder(v);
        }
        if (itemType == TYPE_ITEM) {
            v = layoutInflater.inflate(R.layout.category_list_item, viewGroup, false);
            return new MainItemViewHolder(v);
        }
        if (itemType == TYPE_CHILD_ITEM) {
            v = layoutInflater.inflate(R.layout.category_list_child_item, viewGroup, false);
            return new ChildItemViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HeaderViewHolder) {
            fillHeader((HeaderViewHolder) viewHolder);
        }
        if (viewHolder instanceof FooterViewHolder) {
            //  Nothing to do yet
        }
        if (viewHolder instanceof MainItemViewHolder) {
            //  Offset because of header
            final Category category = categories.get(position - 1);
            ((MainItemViewHolder) viewHolder).txtCategoryName.setText(category.getName());
            ((MainItemViewHolder) viewHolder).imgArrow.setImageDrawable(Utility.compatGetDrawable(resources, R.drawable.arrow_right_black_24dp));
            if (category.getChildren() != null) {
                ((MainItemViewHolder) viewHolder).mainItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!((MainItemViewHolder) viewHolder).isExtended) {
                            for (Category childCategory :
                                    category.getChildren()) {
                                categories.add(viewHolder.getAdapterPosition(), childCategory);
                            }
                            CategoryViewAdapter.this.notifyItemRangeInserted(viewHolder.getAdapterPosition() + 1, category.getChildrenIds().size());
                            ObjectAnimator.ofFloat(((MainItemViewHolder) viewHolder).imgArrow, "rotation", 0, 90f)
                                    .setDuration(400)
                                    .start();
                            //Smooth scroll to last of just added child categories
                            recyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.smoothScrollToPosition(viewHolder.getAdapterPosition() + category.getChildrenIds().size());
                                }
                            }, recyclerView.getItemAnimator().getRemoveDuration());
                            //recyclerView.smoothScrollToPosition(viewHolder.getAdapterPosition() + category.getChildrenIds().size());
                            ((MainItemViewHolder) viewHolder).isExtended = true;
                        } else {
                            int positionInAdapter = viewHolder.getAdapterPosition();
                            categories.subList(positionInAdapter, positionInAdapter + category.getChildren().size()).clear();
                            CategoryViewAdapter.this.notifyItemRangeRemoved(positionInAdapter + 1, category.getChildren().size());
                            ObjectAnimator.ofFloat(((MainItemViewHolder) viewHolder).imgArrow, "rotation", 90f, 0)
                                    .setDuration(400)
                                    .start();
                            ((MainItemViewHolder) viewHolder).isExtended = false;
                        }
                    }
                });
            }
        }
        if (viewHolder instanceof ChildItemViewHolder) {
            //TODO for categories without subcategories go directly to activity for that category.
            // Important \\ Removed Clickable from Textview, because it took click event from layout.
            // Now category name without clicklistener is NOT clickable
            final Category category = categories.get(position - 1);
            ((ChildItemViewHolder) viewHolder).category = category;
            ((ChildItemViewHolder) viewHolder).txtCategoryName.setText(category.getName());
            if (((ChildItemViewHolder) viewHolder).category.getChildren() != null) {
                //Log.d("DEBUG", "Category HAVE children");
                ((ChildItemViewHolder) viewHolder).childLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, CategoryLevel3Activity.class);
                        intent.putExtra("category_id", ((ChildItemViewHolder) viewHolder).category.getId());
                        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(context, R.anim.right_to_center, R.anim.center_to_left);
                        context.startActivity(intent, activityOptions.toBundle());
                    }
                });
            } else {
                ((ChildItemViewHolder) viewHolder).childLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ProductListActivity.class);
                        intent.putExtra("category_id", ((ChildItemViewHolder) viewHolder).category.getId());
                        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(context, R.anim.right_to_center, R.anim.center_to_left);
                        context.startActivity(intent, activityOptions.toBundle());
                    }
                });
                // Log.d("DEBUG", "Category DON'T HAVE children");
                //((ChildItemViewHolder) viewHolder).childLayout.setOnClickListener(null);
            }
        }
    }

    @Override
    public int getItemCount() {
        return categories.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        if (position == categories.size() + 1) {
            return TYPE_FOOTER;
        }
        if (categories.get(position - 1).getParentId() != categories.get(0).getParentId()) {
            return TYPE_CHILD_ITEM;
        }
        return TYPE_ITEM;
    }

    private void fillHeader(HeaderViewHolder headerViewHolder) {
        final ViewPager viewpagerBanners = headerViewHolder.viewpager;
        final DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        class ImgPagerAdapter extends PagerAdapter {
            private List<String> productImgUrlList;

            private ImgPagerAdapter() {
                productImgUrlList = new ArrayList<>();
                productImgUrlList.add("http://aromateque.com.ua/media/adminforms/homepage_slider_b1/cache/1/cache/5e06319eda06f020e43594a9c230972d/m/f/mfk.jpg");
                productImgUrlList.add("http://aromateque.com.ua/media/adminforms/homepage_slider_b1/cache/1/cache/5e06319eda06f020e43594a9c230972d/e/t/etro_shanthung.jpg");
                productImgUrlList.add("http://aromateque.com.ua/media/adminforms/homepage_slider_b1/cache/1/cache/5e06319eda06f020e43594a9c230972d/j/h/jhg1.jpg");
                productImgUrlList.add("http://aromateque.com.ua/media/adminforms/homepage_slider_b1/cache/1/cache/5e06319eda06f020e43594a9c230972d/4/-/4-01.jpg");
                productImgUrlList.add("http://aromateque.com.ua/media/adminforms/homepage_slider_b1/cache/1/cache/5e06319eda06f020e43594a9c230972d/m/h/mh_1.jpg");
                productImgUrlList.add("http://aromateque.com.ua/media/adminforms/homepage_slider_b1/cache/1/cache/5e06319eda06f020e43594a9c230972d/f/i/file_5.jpg");
            }

            @Override
            public int getCount() {
                return productImgUrlList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imgView = (ImageView) layoutInflater.inflate(R.layout.banner_item, container, false);
                imgLoader.displayImage(productImgUrlList.get(position), imgView, options);
                container.addView(imgView);
                return imgView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((ImageView) object);
            }
        }
        viewpagerBanners.setAdapter(new ImgPagerAdapter());
        CirclePageIndicator viewPagerIndicator = headerViewHolder.circlePageIndicator;
        viewPagerIndicator.setViewPager(viewpagerBanners);

        HorizontalGridView bestsellersView = headerViewHolder.bestsellersView;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        bestsellersView.setLayoutManager(layoutManager);

        ArrayList<ShortProduct> bestsellersList = new ArrayList<>();
        final ShortProduct prod = new ShortProduct();
        prod.setBrand("Comme des Garcons Accessories");
        //prod.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/thumbnail/630x/602f0fa2c1f0d1ba5e241f914e856ff9/s/a/sa0110gsilv.jpg");
        prod.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/small_image/124x222/9df78eab33525d08d6e5fb8d27136e95/b/o/bohemes-50ml.jpg");
        prod.setName("Silver Wallet");
        //prod.setTypeAndVolume("набор \"двойное увлажнение\", 60мл+50мл+30мл+30мл");
        prod.setOldPrice("13210");
        prod.setPrice("12310");

        final ShortProduct prod2 = new ShortProduct();
        prod2.setBrand("Sensai");
        //prod.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/thumbnail/630x/602f0fa2c1f0d1ba5e241f914e856ff9/s/a/sa0110gsilv.jpg");
        prod2.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/small_image/124x222/9df78eab33525d08d6e5fb8d27136e95/b/o/bohemes-50ml.jpg");
        prod2.setName("SILKY DESIGN ROUGE");
        //prod2.setTypeAndVolume("набор \"двойное увлажнение\", 60мл+50мл+30мл+30мл");
        prod2.setOldPrice("13210");
        prod2.setPrice("12310");
        final ShortProduct prod3 = new ShortProduct();
        prod3.setBrand("Sensai");
        //prod.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/thumbnail/630x/602f0fa2c1f0d1ba5e241f914e856ff9/s/a/sa0110gsilv.jpg");
        prod3.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/small_image/124x222/9df78eab33525d08d6e5fb8d27136e95/b/o/bohemes-50ml.jpg");
        prod3.setName("BODY FIRMING EMULSION CELLULAR PERFORMANCE");
        prod3.setTypeAndVolume("набор \"двойное увлажнение\", 60мл+50мл+30мл+30мл");
        prod3.setOldPrice("13210");
        prod3.setPrice("12310");
        prod.setId(1425);
        prod2.setId(3519);
        prod3.setId(3518);
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
        bestsellersList.add(prod3);
        //final Bitmap filledHeart = IconSheet.getBitmap(132, 108, 45, 41);
        final BestsellersViewAdapter adapter = new BestsellersViewAdapter(bestsellersList, context);
        bestsellersView.setAdapter(adapter);
        bestsellersView.scrollToPosition(3);
        bestsellersView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager, adapter));
    }

    private class MainItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoryName;
        ImageView imgArrow;
        RelativeLayout mainItemLayout;
        boolean isExtended = false;

        MainItemViewHolder(View itemView) {
            super(itemView);
            txtCategoryName = (TextView) itemView.findViewById(R.id.txt_category);
            imgArrow = (ImageView) itemView.findViewById(R.id.category_list_arrow);
            mainItemLayout = (RelativeLayout) itemView.findViewById(R.id.category_mainitem_layout);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        ViewPager viewpager;
        CirclePageIndicator circlePageIndicator;
        HorizontalGridView bestsellersView;
        TextView txtCatalog;

        HeaderViewHolder(View viewHeader) {
            super(viewHeader);
            viewpager = (ViewPager) viewHeader.findViewById(R.id.banner_viewpager);
            circlePageIndicator = (CirclePageIndicator) viewHeader.findViewById(R.id.viewpager_banner_indicator);
            bestsellersView = (HorizontalGridView) viewHeader.findViewById(R.id.mainpage_bestsellers_gridview);
            txtCatalog = (TextView) viewHeader.findViewById(R.id.txt_catalog);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        View phone;

        FooterViewHolder(View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.phone);
        }
    }

    private class ChildItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoryName;
        LinearLayout childLayout;
        //TODO No need to supply full category object
        Category category;

        ChildItemViewHolder(View itemView) {
            super(itemView);
            childLayout = (LinearLayout) itemView.findViewById(R.id.child_layout);
            txtCategoryName = (TextView) itemView.findViewById(R.id.txt_category);
        }
    }
}