package ua.pp.oped.aromateque.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;
import ua.pp.oped.aromateque.AromatequeApplication;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.adapter.AdapterCartOverview;
import ua.pp.oped.aromateque.base_activity.SearchAppbarActivity;
import ua.pp.oped.aromateque.data.db.DatabaseHelper;
import ua.pp.oped.aromateque.model.CartItem;
import ua.pp.oped.aromateque.model.LongProduct;
import ua.pp.oped.aromateque.model.RawLongProduct;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.LinearLayoutManagerSmoothScrollEdition;
import ua.pp.oped.aromateque.utility.RetryableCallback;

public class ActivityCheckoutSuccess extends SearchAppbarActivity {
    DatabaseHelper db;
    RecyclerView recyclerCartList;
    List<CartItem> cartItems;
    String orderNumber = "1902123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerCartList = (RecyclerView) findViewById(R.id.recycler_order);
        recyclerCartList.setLayoutManager(new LinearLayoutManagerSmoothScrollEdition(this, LinearLayoutManager.VERTICAL, false));
        db = DatabaseHelper.getInstance(this);
        buildCartItems();
        TextView tvOrderNumber = (TextView) findViewById(R.id.order_number);
        TextView tvOrderDate = (TextView) findViewById(R.id.order_date);
//        TextView tvOrderSum = (TextView) findViewById(R.id.order_sum);
        TextView tvOrderSumNumber = (TextView) findViewById(R.id.order_sum_number);
        TextView tvManagerWillCall = (TextView) findViewById(R.id.order_success_3);
        Button btnOk = (Button) findViewById(R.id.order_info_ok);
        String orderNumberFormatted = String.format(getResources().getString(R.string.order_number), orderNumber);
        tvOrderNumber.setText(orderNumberFormatted);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        tvOrderDate.setText(simpleDateFormat.format(Calendar.getInstance().getTime()));
        int cartPrice = getCartPrice();
        String sCartPrice = String.format(getResources().getString(R.string.price), String.valueOf(cartPrice));
        tvOrderSumNumber.setText(sCartPrice);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityCheckoutSuccess.this, ActivityMainPage.class);
                //TODO clear cart
                startActivity(intent);
            }
        });
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.SUNDAY) {
            tvManagerWillCall.setText(getResources().getString(R.string.manager_will_call_at_monday));
        } else {
            tvManagerWillCall.setText(getResources().getString(R.string.manager_will_call));
        }
    }

    private List<CartItem> buildCartItems() {
        cartItems = db.getCart();
        for (int i = 0; i < cartItems.size(); i++) {
            final CartItem cartItem = cartItems.get(i);
            final int productId = cartItem.getProductId();
            if (db.productExists(productId)) {
                cartItem.setProduct(db.deserializeShortProduct(productId));
                String cartPrice = String.valueOf(Integer.parseInt(cartItem.getProduct().getPrice()) * cartItem.getQty());
                cartItem.setCartPrice(cartPrice);
                if (i == cartItems.size() - 1 && isDataReady(cartItems)) {
                    recyclerCartList.setAdapter(new AdapterCartOverview(ActivityCheckoutSuccess.this, cartItems));
                    Timber.d("Successfully set adapter to cart list");
                }
            } else {
                AromatequeApplication.getMagentoAPI().getProduct(productId).enqueue(new RetryableCallback<RawLongProduct>() {

                    @Override
                    public void onFinalResponse(Call call, Response response) {
                        RawLongProduct rawLongProduct = (RawLongProduct) response.body();
                        LongProduct longProduct = rawLongProduct.convertToLongProduct();
                        DatabaseHelper.getInstance(ActivityCheckoutSuccess.this).serializeProduct(longProduct);
                        ShortProduct shortProduct = DatabaseHelper.getInstance(ActivityCheckoutSuccess.this).deserializeShortProduct(productId);
                        cartItem.setProduct(shortProduct);
                        String cartPrice = String.valueOf(Integer.parseInt(cartItem.getProduct().getPrice()) * cartItem.getQty());
                        cartItem.setCartPrice(cartPrice);
                        // Check if all data has been received
                        if (isDataReady(cartItems)) {
                            recyclerCartList.setAdapter(new AdapterCartOverview(ActivityCheckoutSuccess.this, cartItems));
                            Timber.d("Successfully set adapter to cart list");
                        }
                    }

                    @Override
                    public void onFinalFailure(Call call, Throwable t) {

                    }
                });
            }

        }
        return cartItems;
    }

    private int getCartPrice() {
        int cartPrice = 0;
        for (CartItem cartItem :
                cartItems) {
            cartPrice += Integer.parseInt(cartItem.getCartPrice());
        }
        return cartPrice;
    }

    private boolean isDataReady(List<CartItem> cartItems) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getProduct() == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_checkout_success_top;
    }

}
