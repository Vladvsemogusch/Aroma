package ua.pp.oped.aromateque.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import ua.pp.oped.aromateque.AdapterCartList;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.db.DatabaseHelper;
import ua.pp.oped.aromateque.model.CartItem;
import ua.pp.oped.aromateque.model.ShortProduct;

public class ActivityCart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DatabaseHelper db = DatabaseHelper.getInstance();
        ArrayList<CartItem> cartItems = db.getCart();
        ArrayList<ShortProduct> cartProductItems = new ArrayList<>();
        for (CartItem cartItem :
                cartItems) {
            cartProductItems.add(db.deserializeShortProduct(cartItem.getProductId()));
        }
        RecyclerView cartList = (RecyclerView) findViewById(R.id.cart_list);
        cartList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        cartList.setAdapter(new AdapterCartList(this, cartItems, cartProductItems));

    }

    public void onAddQtyClicked(View view) {
    }

    public void onReduceQtyClicked(View view) {
    }
}
