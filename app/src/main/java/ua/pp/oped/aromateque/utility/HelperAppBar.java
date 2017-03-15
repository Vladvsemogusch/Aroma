package ua.pp.oped.aromateque.utility;


import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import static android.view.View.GONE;

public class HelperAppBar {

    public static void onSearchClicked(View view, EditText edittextSearch, ActionBar actionBar, InputMethodManager inputMethodManager) {
        view.setVisibility(GONE);
        actionBar.setTitle("");
        edittextSearch.setVisibility(View.VISIBLE);
        edittextSearch.requestFocus();
        inputMethodManager.showSoftInput(edittextSearch, InputMethodManager.HIDE_NOT_ALWAYS);

    }


}
