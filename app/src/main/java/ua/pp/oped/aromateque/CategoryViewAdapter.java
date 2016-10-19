package ua.pp.oped.aromateque;

import android.content.Context;
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
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.EndlessRecyclerViewScrollListener;
import ua.pp.oped.aromateque.utility.IconSheet;

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

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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
            ((MainItemViewHolder) viewHolder).txtCategoryName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!((MainItemViewHolder) viewHolder).isExtended) {
                        if (category.getChildrenIds() != null) {
                            for (Category childCategory :
                                    category.getChildren()) {
                                categories.add(viewHolder.getAdapterPosition(), childCategory);
                            }
                            CategoryViewAdapter.this.notifyItemRangeInserted(viewHolder.getAdapterPosition() + 1, category.getChildrenIds().size());
                            ((MainItemViewHolder) viewHolder).isExtended = true;
                        }
                    } else {
                        categories.subList(viewHolder.getAdapterPosition(), viewHolder.getAdapterPosition() + category.getChildren().size()).clear();
                        CategoryViewAdapter.this.notifyItemRangeRemoved(viewHolder.getAdapterPosition() + 1, category.getChildren().size());
                        ((MainItemViewHolder) viewHolder).isExtended = false;
                    }
                }

            });

        }
        if (viewHolder instanceof ChildItemViewHolder) {
            final Category category = categories.get(position - 1);
            ((ChildItemViewHolder) viewHolder).category = category;
            ((ChildItemViewHolder) viewHolder).txtCategory.setText(category.getName());
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
        final ViewPager viewpagerBanners = (ViewPager) headerViewHolder.viewpager;
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
        viewpagerBanners.setAdapter(new ImgPagerAdapter());
        CirclePageIndicator viewPagerIndicator = headerViewHolder.circlePageIndicator;
        viewPagerIndicator.setViewPager(viewpagerBanners);

        HorizontalGridView bestsellersView = headerViewHolder.bestsellersView;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
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
        final BestsellersViewAdapter adapter = new BestsellersViewAdapter(bestsellersList, resources, imgLoader);
        bestsellersView.setAdapter(adapter);
        bestsellersView.scrollToPosition(3);
        bestsellersView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager, adapter));
    }

    class MainItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoryName;
        ImageView imgArrow;
        boolean isExtended = false;

        MainItemViewHolder(View itemView) {
            super(itemView);
            txtCategoryName = (TextView) itemView.findViewById(R.id.txt_category);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
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

    class FooterViewHolder extends RecyclerView.ViewHolder {
        View phone;

        FooterViewHolder(View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.phone);
        }
    }

    class ChildItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategory;
        Category category;

        ChildItemViewHolder(View itemView) {
            super(itemView);
            txtCategory = (TextView) itemView.findViewById(R.id.txt_category);
        }
    }
}