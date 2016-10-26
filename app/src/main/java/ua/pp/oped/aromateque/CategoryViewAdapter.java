package ua.pp.oped.aromateque;

import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import ua.pp.oped.aromateque.activity.CategoryLevel3Activity;
import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.EndlessRecyclerViewScrollListener;
import ua.pp.oped.aromateque.utility.IconSheet;
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

    public CategoryViewAdapter(Context context, List<Category> categories, Resources resources, ImageLoader imgLoader) {
        this.categories = categories;
        this.resources = resources;
        this.imgLoader = imgLoader;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
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
            //  Nothing to do
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
            } else {
                ((ChildItemViewHolder) viewHolder).txtCategory.setOnClickListener(null);
            }
        }
        if (viewHolder instanceof ChildItemViewHolder) {
            //TODO for categories without subcategories go directly to activity for that category.
            final Category category = categories.get(position - 1);
            ((ChildItemViewHolder) viewHolder).category = category;
            ((ChildItemViewHolder) viewHolder).txtCategory.setText(category.getName());
            if (((ChildItemViewHolder) viewHolder).category.getChildren() != null) {
                ((ChildItemViewHolder) viewHolder).txtCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, CategoryLevel3Activity.class);
                        intent.putExtra("category_id", ((ChildItemViewHolder) viewHolder).category.getId());
                        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(context, R.anim.right_to_center, R.anim.center_to_left);
                        context.startActivity(intent, activityOptions.toBundle());
                        //((Activity)context).overridePendingTransition();
                    }
                });
            } else {
                ((ChildItemViewHolder) viewHolder).txtCategory.setOnClickListener(null);
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
                return view == object;
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
        viewpagerBanners.setAdapter(new ImgPagerAdapter());
        CirclePageIndicator viewPagerIndicator = headerViewHolder.circlePageIndicator;
        viewPagerIndicator.setViewPager(viewpagerBanners);

        HorizontalGridView bestsellersView = headerViewHolder.bestsellersView;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        bestsellersView.setLayoutManager(layoutManager);

        ArrayList<ShortProduct> bestsellersList = new ArrayList<>();
        final ShortProduct prod = new ShortProduct();
        prod.setBrand("SASDA");
        prod.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/thumbnail/630x/602f0fa2c1f0d1ba5e241f914e856ff9/s/a/sa0110gsilv.jpg");
        //prod.setImageUrl("http://aromateque.com.ua/media/catalog/product/cache/1/small_image/124x222/9df78eab33525d08d6e5fb8d27136e95/b/o/bohemes-50ml.jpg");
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
        final BestsellersViewAdapter adapter = new BestsellersViewAdapter(bestsellersList, resources, imgLoader);
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
        TextView txtCategory;
        //LinearLayout layout;
        //TODO No need to supply full category object
        Category category;

        ChildItemViewHolder(View itemView) {
            super(itemView);
            //layout = (LinearLayout) itemView.findViewById(R.id.category_list_child_item_layout);
            txtCategory = (TextView) itemView.findViewById(R.id.txt_category);
        }
    }
}