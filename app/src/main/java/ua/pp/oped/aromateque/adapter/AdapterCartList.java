package ua.pp.oped.aromateque.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.data.db.DatabaseHelper;
import ua.pp.oped.aromateque.model.CartItem;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.ImageLoaderWrapper;

public class AdapterCartList extends RecyclerView.Adapter {
    private Resources resources;
    private Context context;
    private ArrayList<CartItem> cartItems;
    private DatabaseHelper db;

    public AdapterCartList(Context context, ArrayList<CartItem> cartItems) {
        this.resources = context.getResources();
        this.context = context;
        this.cartItems = cartItems;
        db = DatabaseHelper.getInstance(context);
        Log.d("ADAPTER", "AdapterCartList created");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_item, viewGroup, false);
        return new AdapterCartList.CartItemViewHolder(v);

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
        cartItemViewHolder.qty.setText(String.valueOf(cartItem.getQty()));

        ImageLoaderWrapper.loadImage(context, cartItemViewHolder.image, cartProductItem.getImageUrl());
        cartItemViewHolder.addQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update quantity everywhere
                int curQty = cartItem.getQty();
                cartItem.setQty(++curQty);
                db.incrementQty(cartProductItem.getId());
                cartItemViewHolder.qty.setText(String.valueOf(curQty));
                //Update price everywhere
                String newCartPrice = String.valueOf(Integer.parseInt(cartItem.getProduct().getPrice()) * curQty);
                String formattedCartPrice = String.format(resources.getString(R.string.product_price), newCartPrice);
                cartItem.setCartPrice(newCartPrice);
                cartItemViewHolder.price.setText(formattedCartPrice);
                EventBus.getDefault().post(new EventQtyChanged());
            }
        });
        cartItemViewHolder.reduceQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int curQty = cartItem.getQty();
                if (curQty != 1) {
                    cartItem.setQty(--curQty);
                    db.decrementQty(cartProductItem.getId());
                    cartItemViewHolder.qty.setText(String.valueOf(curQty));
                    String newCartPrice = String.valueOf(Integer.parseInt(cartItem.getProduct().getPrice()) * curQty);
                    String formattedCartPrice = String.format(resources.getString(R.string.product_price), newCartPrice);
                    cartItem.setCartPrice(newCartPrice);
                    cartItemViewHolder.price.setText(formattedCartPrice);
                    EventBus.getDefault().post(new EventQtyChanged());
                }
            }
        });
        cartItemViewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.removeFromCart(cartItem.getProductId());
                cartItems.remove(cartItemViewHolder.getAdapterPosition());
                AdapterCartList.this.notifyItemRemoved(cartItemViewHolder.getAdapterPosition());
                EventBus.getDefault().post(new EventQtyChanged());

            }
        });
//            ((AdapterSubCategoryView.MainItemViewHolder) viewHolder).txtCategoryName.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, ActivityProductList.class);
//                    intent.putExtra("category_id", ((AdapterSubCategoryView.MainItemViewHolder) viewHolder).category_id);
//                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(context, R.anim.right_to_center, R.anim.center_to_left);
//                    context.startActivity(intent, activityOptions.toBundle());
//                }
//            });


    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    } //TODO

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
        private ImageButton addQty;
        private ImageButton reduceQty;
        private ImageButton remove;


        CartItemViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.product_image);
            brand = (TextView) itemView.findViewById(R.id.product_brand);
            name = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.cart_product_price);
            qty = (TextView) itemView.findViewById(R.id.qty);
            addQty = (ImageButton) itemView.findViewById(R.id.add_qty);
            reduceQty = (ImageButton) itemView.findViewById(R.id.reduce_qty);
            remove = (ImageButton) itemView.findViewById(R.id.remove_from_cart);

        }
    }

    public static class EventQtyChanged {
    }
}


