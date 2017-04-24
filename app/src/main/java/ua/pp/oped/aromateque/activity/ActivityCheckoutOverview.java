package ua.pp.oped.aromateque.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;
import ua.pp.oped.aromateque.AromatequeApplication;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.adapter.AdapterCartOverview;
import ua.pp.oped.aromateque.base_activity.CalligraphyActivity;
import ua.pp.oped.aromateque.data.db.DatabaseHelper;
import ua.pp.oped.aromateque.model.CartItem;
import ua.pp.oped.aromateque.model.LongProduct;
import ua.pp.oped.aromateque.model.RawLongProduct;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.EditTextBackEvent;
import ua.pp.oped.aromateque.utility.LinearLayoutManagerSmoothScrollEdition;
import ua.pp.oped.aromateque.utility.RetryableCallback;

import static android.view.View.GONE;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

public class ActivityCheckoutOverview extends CalligraphyActivity {
    DatabaseHelper db;
    RecyclerView recyclerCartList;
    List<CartItem> cartItems;
    String orderComment;
    String certificate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_overview_top);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerCartList = (RecyclerView) findViewById(R.id.cart_list);
        recyclerCartList.setLayoutManager(new LinearLayoutManagerSmoothScrollEdition(this, LinearLayoutManager.VERTICAL, false));
        db = DatabaseHelper.getInstance(this);
        buildCartItems();
        String city = getIntent().getStringExtra("city");
        String warehouseNumber = getIntent().getStringExtra("warehouse_number");
        String warehouseAddress = getIntent().getStringExtra("warehouse_address");
        TextView tvWarehouseNumber = (TextView) findViewById(R.id.warehouse_number);
        TextView tvWarehouseAddress = (TextView) findViewById(R.id.warehouse_address);
        TextView tvDelivery = (TextView) findViewById(R.id.delivery_price_amount);
        TextView tvCheckoutPayAmount = (TextView) findViewById(R.id.checkout_pay_amount);
        final TextView tvPaymentMethodChoice = (TextView) findViewById(R.id.payment_method_choice);
        View dividerGiftWWrap = findViewById(R.id.divider_gift_wrap);
        final CheckedTextView cbGiftWrap = (CheckedTextView) findViewById(R.id.gift_wrap);
        final CheckedTextView cbSubscribe = (CheckedTextView) findViewById(R.id.subscribe);
        Button btnSubmit = (Button) findViewById(R.id.checkout_submit);
        View lytPaymentMethod = findViewById(R.id.lyt_payment_method);
        View lytOrderComment = findViewById(R.id.lyt_order_comment);
        View lytUseCertificate = findViewById(R.id.lyt_certificate);
        // Setup warehouse part
        String warehouseNumberWithCity = String.format(getResources().getString(R.string.warehouse_number_with_city), city, warehouseNumber);
        tvWarehouseNumber.setText(warehouseNumberWithCity);
        tvWarehouseAddress.setText(warehouseAddress);
        // Setup delivery part
        int cartPrice = getCartPrice();
        if (cartPrice >= 500) {
            tvDelivery.setText(R.string.free);
        } else {
            tvDelivery.setText(R.string.delivery_estimate);
        }
        String cartPriceString = String.format(getResources().getString(R.string.price), String.valueOf(cartPrice));
        tvCheckoutPayAmount.setText(cartPriceString);
        // Setup gift wrap
        int GIFT_WRAP_THRESHOLD = 1500;
        if (cartPrice < GIFT_WRAP_THRESHOLD) {
            cbGiftWrap.setVisibility(GONE);
            dividerGiftWWrap.setVisibility(GONE);
        }
        // Setup payment methods
        final String[] payMethods = getResources().getStringArray(R.array.payment_methods);
        class PositiveButtonListener implements DialogInterface.OnClickListener {
            private int i = 0;

            public void onClick(DialogInterface dialog, int id) {
                tvPaymentMethodChoice.setText(payMethods[i]);
                dialog.cancel();
            }

            void setChoice(int i) {
                this.i = i;
            }
        }
        ;
        final PositiveButtonListener positiveButtonListener = new PositiveButtonListener();
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.payment_method)
                .setPositiveButton(R.string.ok, positiveButtonListener)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setSingleChoiceItems(payMethods, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        positiveButtonListener.setChoice(i);
                    }
                });
        final AlertDialog dialogPaymentMethod = builder.create();
        lytPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPaymentMethod.show();
            }
        });
        // Setup gift wrap checkbox
        cbGiftWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cbGiftWrap.toggle();
            }
        });
        // Setup Subscribe
        cbSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cbSubscribe.toggle();
            }
        });
        // Setup order comment
        View lytDialogOrderComment = getLayoutInflater().inflate(R.layout.dialog_order_comment, null);
        final EditTextBackEvent etOrderComment = (EditTextBackEvent) lytDialogOrderComment.findViewById(R.id.et_order_comment);
        builder = new AlertDialog.Builder(this)
                .setTitle(R.string.order_comment)
                .setView(lytDialogOrderComment)
                .setPositiveButton(R.string.ok_order_comment, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        orderComment = etOrderComment.getText().toString();
                        Timber.d("Comment Submitted: " + orderComment);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog dialogOrderComment = builder.create();
        lytOrderComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOrderComment.show();
            }
        });
        // Setup editText in order comment dialog
        EditTextBackEvent.EditTextImeBackListener imeBackListener = new EditTextBackEvent.EditTextImeBackListener() {
            @Override
            public void onImeBack(EditTextBackEvent editText, String text) {
                if (editText.isFocused()) {
                    editText.clearFocus();
                }
            }
        };
        final InputMethodManager inputMethodManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        };
        etOrderComment.setOnEditTextImeBackListener(imeBackListener);
        etOrderComment.setOnFocusChangeListener(onFocusChangeListener);
        etOrderComment.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Timber.d("onEditorAction");
                if (i == IME_ACTION_DONE) {
                    Timber.d("IME_ACTION_DONE");
                    textView.clearFocus();
                    return true;
                }
                return false;
            }
        });
        // Setup certificate part
        View lytDialogCertificate = getLayoutInflater().inflate(R.layout.dialog_certificate, null);
        final EditTextBackEvent etCertificate = (EditTextBackEvent) lytDialogCertificate.findViewById(R.id.et_certificate);
        builder = new AlertDialog.Builder(this)
                .setTitle(R.string.use_certificate)
                .setView(lytDialogCertificate)
                .setPositiveButton(R.string.ok_2, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO ask server about certificate and change view accordingly
                        //TODO if certidficate valid add it's number to order
                        certificate = etCertificate.getText().toString();
                        Timber.d("Certificate submitted: " + orderComment);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog dialogCertificate = builder.create();
        lytUseCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCertificate.show();
            }
        });
        // Setup submit
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityCheckoutOverview.this, ActivityCheckoutSuccess.class);
            }
        });
    }


    private int getCartPrice() {
        int cartPrice = 0;
        for (CartItem cartItem :
                cartItems) {
            cartPrice += Integer.parseInt(cartItem.getCartPrice());
        }
        return cartPrice;
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
                    recyclerCartList.setAdapter(new AdapterCartOverview(ActivityCheckoutOverview.this, cartItems));
                    Timber.d("Successfully set adapter to cart list");
                }
            } else {
                AromatequeApplication.getMagentoAPI().getProduct(productId).enqueue(new RetryableCallback<RawLongProduct>() {

                    @Override
                    public void onFinalResponse(Call call, Response response) {
                        RawLongProduct rawLongProduct = (RawLongProduct) response.body();
                        LongProduct longProduct = rawLongProduct.convertToLongProduct();
                        DatabaseHelper.getInstance(ActivityCheckoutOverview.this).serializeProduct(longProduct);
                        ShortProduct shortProduct = DatabaseHelper.getInstance(ActivityCheckoutOverview.this).deserializeShortProduct(productId);
                        cartItem.setProduct(shortProduct);
                        String cartPrice = String.valueOf(Integer.parseInt(cartItem.getProduct().getPrice()) * cartItem.getQty());
                        cartItem.setCartPrice(cartPrice);
                        // Check if all data has been received
                        if (isDataReady(cartItems)) {
                            recyclerCartList.setAdapter(new AdapterCartOverview(ActivityCheckoutOverview.this, cartItems));
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


    private boolean isDataReady(List<CartItem> cartItems) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getProduct() == null) {
                return false;
            }
        }
        return true;
    }
}
