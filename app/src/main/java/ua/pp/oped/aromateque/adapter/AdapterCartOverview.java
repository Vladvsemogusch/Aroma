package ua.pp.oped.aromateque.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.data.db.DatabaseHelper;
import ua.pp.oped.aromateque.model.CartItem;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.ImageLoaderWrapper;

public class AdapterCartOverview extends RecyclerView.Adapter {
    private Resources resources;
    private Context context;
    private List<CartItem> cartItems;
    private DatabaseHelper db;

    public AdapterCartOverview(Context context, List<CartItem> cartItems) {
        this.resources = context.getResources();
        this.context = context;
        this.cartItems = cartItems;
        db = DatabaseHelper.getInstance(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_overview_item, viewGroup, false);
        return new AdapterCartOverview.CartItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        final CartItemViewHolder cartItemViewHolder = (CartItemViewHolder) viewHolder;
        final CartItem cartItem = cartItems.get(position);
        final ShortProduct cartProductItem = cartItem.getProduct();
        cartItemViewHolder.brand.setText(cartProductItem.getBrand());
        cartItemViewHolder.name.setText(cartProductItem.getName());
        String price = String.format(resources.getString(R.string.product_price), String.valueOf(cartItem.getCartPrice()));
        cartItemViewHolder.price.setText(price);
        cartItemViewHolder.qty.setText(String.format(resources.getString(R.string.product_qty), String.valueOf(cartItem.getQty())));
        ImageLoaderWrapper.loadImage(context, cartItemViewHolder.image, cartProductItem.getImageUrl());

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }


    private class CartItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView brand;
        private TextView name;
        private TextView price;
        private TextView qty;


        CartItemViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.product_image);
            brand = (TextView) itemView.findViewById(R.id.product_brand);
            name = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.cart_product_price);
            qty = (TextView) itemView.findViewById(R.id.qty);
        }
    }

}


