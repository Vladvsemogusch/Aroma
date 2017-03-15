package ua.pp.oped.aromateque.base_activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import timber.log.Timber;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.activity.ActivityCart;
import ua.pp.oped.aromateque.activity.ActivityProductList;
import ua.pp.oped.aromateque.data.db.DatabaseHelper;
import ua.pp.oped.aromateque.utility.EditTextBackEvent;

import static android.view.View.GONE;
import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;

public class SearchAppbarActivity extends GlobalDrawerActivity {
    protected TextView cartCounter;
    protected String title;
    protected EditTextBackEvent edittextSearch;
    protected ImageButton btnSearch;
    protected InputMethodManager inputMethodManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartCounter = (TextView) findViewById(R.id.cart_counter);
        setupSearch();
    }

    private void setupSearch() {
        inputMethodManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        edittextSearch = (EditTextBackEvent) findViewById(R.id.edittext_search);
        edittextSearch.setOnEditTextImeBackListener(new EditTextBackEvent.EditTextImeBackListener() {
            @Override
            public void onImeBack(EditTextBackEvent ctrl, String text) {
                Timber.d("onImeBack()");
                if (ctrl.isFocused()) {
                    ctrl.clearFocus();
                }
            }
        });
        EditText.OnEditorActionListener onEditorActionListener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == IME_ACTION_SEARCH) {
                    String searchString = String.valueOf(textView.getText());
                    //TODO REST_READY ask rest api for search result
                    Intent intent = new Intent(getApplicationContext(), ActivityProductList.class);
                    startActivity(intent);
                    textView.setText("");
                    textView.clearFocus();
                    textView.setVisibility(GONE);
                    btnSearch.setVisibility(View.VISIBLE);

                    return true;
                }
                return false;
            }
        };
        edittextSearch.setOnEditorActionListener(onEditorActionListener);
        edittextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Timber.d("onFocusChange()");
                if (!view.isFocused()) {
                    Timber.d("!view.isFocused(), closing keyboard");
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        btnSearch = (ImageButton) findViewById(R.id.btn_search);
    }

    public void onSearchClicked(View view) {
        view.setVisibility(GONE);
        getSupportActionBar().setTitle("");
        edittextSearch.setVisibility(View.VISIBLE);
        edittextSearch.requestFocus();
        inputMethodManager.showSoftInput(edittextSearch, InputMethodManager.HIDE_NOT_ALWAYS);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    protected void updateCartCounter() {
        int cartQty = DatabaseHelper.getInstance(this).getCartQty();
        if (cartQty == 0) {
            Timber.d("cartQty == 0");
            cartCounter.setVisibility(GONE);
        } else {
            Timber.d("set counter text");
            if (cartCounter.getVisibility() == View.GONE) {
                cartCounter.setVisibility(View.VISIBLE);
            }
            cartCounter.setText(String.valueOf(cartQty));
        }
    }

    public void onCartClicked(View v) {
        Intent intent = new Intent(this, ActivityCart.class);
        this.startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume()");
        updateCartCounter();
    }

}
