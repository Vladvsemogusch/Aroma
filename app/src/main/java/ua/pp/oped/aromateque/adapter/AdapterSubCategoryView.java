package ua.pp.oped.aromateque.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.activity.ActivityProductList;
import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.EndlessRecyclerViewScrollListener;
import ua.pp.oped.aromateque.utility.IconSheet;

public class AdapterSubCategoryView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private ArrayList<Category> categories;
    private Resources resources;
    private LayoutInflater layoutInflater;
    private Context context;
    private Category parentCategory;
    private boolean withFooter;
    private int offset;

    public AdapterSubCategoryView(Context context, Category parentCategory, boolean withFooter) {
        this.categories = parentCategory.getChildren();
        this.resources = context.getResources();
        this.context = context;
        this.parentCategory = parentCategory;
        layoutInflater = LayoutInflater.from(context);
        this.withFooter = withFooter;
        Log.d("ADAPTER", "Subcategory Adapter created");
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View v;
        if (itemType == TYPE_HEADER) {
            v = layoutInflater.inflate(R.layout.subcategory_list_header, viewGroup, false);
            return new HeaderViewHolder(v);
        }
        if (itemType == TYPE_ITEM) {
            v = layoutInflater.inflate(R.layout.subcategory_list_item, viewGroup, false);
            return new MainItemViewHolder(v);
        }
        if (itemType == TYPE_FOOTER && withFooter) {
            v = layoutInflater.inflate(R.layout.category_list_footer, viewGroup, false);
            return new FooterViewHolder(v);
        } else {
            v = layoutInflater.inflate(R.layout.divider_dark_brown, viewGroup, false);
            return new FooterViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HeaderViewHolder) {
            fillHeader((HeaderViewHolder) viewHolder);
        }
        if (viewHolder instanceof MainItemViewHolder) {
            //  Offset because of header
            final Category category = categories.get(position - 1);
            MainItemViewHolder mainItemViewHolder = ((MainItemViewHolder) viewHolder);
            mainItemViewHolder.category_id = category.getId();
            mainItemViewHolder.txtCategoryName.setText(category.getName());
            mainItemViewHolder.txtCategoryName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ActivityProductList.class);
                    intent.putExtra("category_id", ((MainItemViewHolder) viewHolder).category_id);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(context, R.anim.right_to_center, R.anim.center_to_left);
                    context.startActivity(intent, activityOptions.toBundle());
                }
            });
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
        return TYPE_ITEM;
    }

    private void fillHeader(HeaderViewHolder headerViewHolder) {
        HorizontalGridView subcategoryBestsellersView = headerViewHolder.subcategoryBestsellersView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        subcategoryBestsellersView.setLayoutManager(layoutManager);
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
        final AdapterBestsellersView adapter = new AdapterBestsellersView(bestsellersList, context);
        subcategoryBestsellersView.setAdapter(adapter);
        subcategoryBestsellersView.scrollToPosition(3);
        subcategoryBestsellersView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager, adapter));
    }

    private class MainItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoryName;
        //LinearLayout layout;
        int category_id;

        MainItemViewHolder(View itemView) {
            super(itemView);
            txtCategoryName = (TextView) itemView.findViewById(R.id.txt_category);
            //layout = (LinearLayout) itemView.findViewById(R.id.subcategory_item_layout);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        HorizontalGridView subcategoryBestsellersView;
        TextView txtCatalog;

        HeaderViewHolder(View viewHeader) {
            super(viewHeader);
            subcategoryBestsellersView = (HorizontalGridView) viewHeader.findViewById(R.id.subcategory_bestsellers_gridview);
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

}