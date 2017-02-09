package ua.pp.oped.aromateque;

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

import java.util.ArrayList;

import ua.pp.oped.aromateque.model.CartItem;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.CustomImageLoader;

public class AdapterCartList extends RecyclerView.Adapter {
    private Resources resources;
    private LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<CartItem> cartItems;
    private ArrayList<ShortProduct> cartProductItems;

    public AdapterCartList(Context context, ArrayList<CartItem> cartItems, ArrayList<ShortProduct> cartProductItems) {
        this.resources = context.getResources();
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.cartItems = cartItems;
        Log.d("ADAPTER", "AdapterCartList created");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View v = layoutInflater.inflate(R.layout.subcategory_list_item, viewGroup, false);
        return new AdapterCartList.CartItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        CartItem cartItem = cartItems.get(position);
        ShortProduct cartProductItem = cartProductItems.get(position);
        CustomImageLoader.getInstance().displayImage(cartProductItem.getImageUrl(), ((CartItemViewHolder) viewHolder).image);
        ((CartItemViewHolder) viewHolder).brand.setText(cartProductItem.getBrand());
        ((CartItemViewHolder) viewHolder).name.setText(cartProductItem.getName());
        ((CartItemViewHolder) viewHolder).price.setText(String.valueOf(cartProductItem.getPrice()));
        ((CartItemViewHolder) viewHolder).qty.setText(String.valueOf(cartItem.getQty()));


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


        CartItemViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.product_image);
            brand = (TextView) itemView.findViewById(R.id.product_brand);
            name = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.product_price);
            qty = (TextView) itemView.findViewById(R.id.qty);
            addQty = (ImageButton) itemView.findViewById(R.id.add_qty);
            reduceQty = (ImageButton) itemView.findViewById(R.id.reduce_qty);

        }
    }
}
