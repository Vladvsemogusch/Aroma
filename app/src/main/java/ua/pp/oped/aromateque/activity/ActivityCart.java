package ua.pp.oped.aromateque.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;
import ua.pp.oped.aromateque.AdapterCartList;
import ua.pp.oped.aromateque.AromatequeApplication;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.data.db.DatabaseHelper;
import ua.pp.oped.aromateque.model.CartItem;
import ua.pp.oped.aromateque.model.LongProduct;
import ua.pp.oped.aromateque.model.RawLongProduct;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.RetryableCallback;

public class ActivityCart extends AppCompatActivity {
    private ArrayList<CartItem> cartItems;
    private TextView totalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        totalPrice = (TextView) findViewById(R.id.price_total);
        final RecyclerView cartList = (RecyclerView) findViewById(R.id.cart_list);
        cartList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // In case responses will be received in random order we need to ensure needed order is retained
        cartItems = db.getCart();
        for (int i = 0; i < cartItems.size(); i++) {
            final CartItem cartItem = cartItems.get(i);
            final int productId = cartItem.getProductId();
            if (db.productExists(productId)) {
                cartItem.setProduct(db.deserializeShortProduct(productId));
                String cartPrice = String.valueOf(Integer.parseInt(cartItem.getProduct().getPrice()) * cartItem.getQty());
                cartItem.setCartPrice(cartPrice);
                if (i == cartItems.size() - 1 && isDataReady(cartItems)) {
                    cartList.setAdapter(new AdapterCartList(ActivityCart.this, cartItems));
                    Timber.d("Successfully set adapter to cart list");
                    updateTotalPrice();
                }
            } else {
                AromatequeApplication.getApiMagento().getProduct(productId).enqueue(new RetryableCallback<RawLongProduct>() {

                    @Override
                    public void onFinalResponse(Call call, Response response) {
                        RawLongProduct rawLongProduct = (RawLongProduct) response.body();
                        LongProduct longProduct = rawLongProduct.convertToLongProduct();
                        DatabaseHelper.getInstance(ActivityCart.this).serializeProduct(longProduct);
                        ShortProduct shortProduct = DatabaseHelper.getInstance(ActivityCart.this).deserializeShortProduct(productId);
                        cartItem.setProduct(shortProduct);
                        String cartPrice = String.valueOf(Integer.parseInt(cartItem.getProduct().getPrice()) * cartItem.getQty());
                        cartItem.setCartPrice(cartPrice);
                        // Check if all data has been received
                        if (isDataReady(cartItems)) {
                            cartList.setAdapter(new AdapterCartList(ActivityCart.this, cartItems));
                            Timber.d("Successfully set adapter to cart list");
                            updateTotalPrice();
                        }
                    }

                    @Override
                    public void onFinalFailure(Call call, Throwable t) {

                    }
                });
            }

        }
    }

    private boolean isDataReady(ArrayList<CartItem> cartItems) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getProduct() == null) {
                return false;
            }
        }
        return true;
    }

    private void updateTotalPrice() {
        //Recalc total price
        int updatedTotalPrice = 0;
        for (CartItem cartItem : cartItems) {
            updatedTotalPrice += Integer.parseInt(cartItem.getCartPrice());
        }
        String formattedUpdatedTotalPrice = String.format(getResources().getString(R.string.product_price), String.valueOf(updatedTotalPrice));
        totalPrice.setText(String.valueOf(formattedUpdatedTotalPrice));
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventQtyChanged(AdapterCartList.EventQtyChanged event) {
        updateTotalPrice();
    }

}
