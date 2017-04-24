package ua.pp.oped.aromateque.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import timber.log.Timber;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.adapter.AdapterProductList;
import ua.pp.oped.aromateque.base_activity.SearchAppbarActivity;
import ua.pp.oped.aromateque.data.db.DatabaseHelper;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.Utility;

public class ActivityFavorites extends SearchAppbarActivity {
    private DatabaseHelper db;
    private RecyclerView favoriteProductsRecyclerView;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DatabaseHelper.getInstance(this);
        cartCounter = (TextView) findViewById(R.id.cart_counter);
        favoriteProductsRecyclerView = (RecyclerView) findViewById(R.id.product_list_recycler_view);
        gridLayoutManager = new GridLayoutManager(this, 1, RecyclerView.VERTICAL, false);
        favoriteProductsRecyclerView.setLayoutManager(gridLayoutManager);
        List<ShortProduct> favoriteProducts = db.getFavorites();
        favoriteProductsRecyclerView.setLayoutManager(gridLayoutManager);
        favoriteProductsRecyclerView.setAdapter(new AdapterProductList(favoriteProducts, this, R.layout.short_product_item_with_type_wide));
    }

    protected int getLayoutId() {
        return R.layout.activity_favorites_top;
    }

    public void onAddToCartClicked(View v) {
        int productId = (int) v.getTag();

        Timber.d("ADD TO CART CLICKED");
        if (!db.isInCart(productId)) {
            db.addToCart(productId);
            updateCartCounter();
            ((AdapterProductList) favoriteProductsRecyclerView.getAdapter()).updateCartItems(productId);
            ((ImageView) v).setImageDrawable(Utility.compatGetDrawable(getResources(), R.drawable.ico_cart_full));
            Animation addToCartAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_cart);
            v.startAnimation(addToCartAnimation);
            Timber.d("Added to cart");
        } else {
            db.removeFromCart(productId);
            ((ImageView) v).setImageDrawable(Utility.compatGetDrawable(getResources(), R.drawable.ico_cart_empty));
//            Intent intent = new Intent(this, ActivityCart.class);
//            this.startActivity(intent);
//            Log.d(TAG, "Already in cart product id: " + productId);
        }
        //TODO Visuals
    }

    public void onAddToFavoritesClicked(View v) {
        int productId = (int) v.getTag();
        if (db.isInFavorites(productId)) {
            db.removeFavorite(productId);
            ((ImageButton) v).setImageDrawable(Utility.compatGetDrawable(getResources(), R.drawable.heart_empty_beige));
        } else {
            db.addFavorite(productId);
            ((ImageButton) v).setImageDrawable(Utility.compatGetDrawable(getResources(), R.drawable.heart_beige));
        }
    }
}
