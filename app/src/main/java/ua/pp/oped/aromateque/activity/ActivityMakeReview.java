package ua.pp.oped.aromateque.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.base_activity.SearchAppbarActivity;
import ua.pp.oped.aromateque.model.Rating;
import ua.pp.oped.aromateque.utility.EditTextBackEvent;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;


public class ActivityMakeReview extends SearchAppbarActivity {
    ListView ratingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ratingList = (ListView) findViewById(R.id.review_rating_list);
//        ratingList.setAdapter(new RatingAdapter(this, ratings));
//        ratingList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setSingleEditText(false);
        ArrayList<Rating> ratings = new ArrayList<>();
        ratings.add(new Rating(5, getResources().getString(R.string.rating_100)));
        ratings.add(new Rating(4, getResources().getString(R.string.rating_80)));
        ratings.add(new Rating(3, getResources().getString(R.string.rating_60)));
        ratings.add(new Rating(2, getResources().getString(R.string.rating_40)));
        ratings.add(new Rating(1, getResources().getString(R.string.rating_20)));


        ArrayList<View> ratingItems = new ArrayList<>();
        ratingItems.add(findViewById(R.id.rating_item_100));
        ratingItems.add(findViewById(R.id.rating_item_80));
        ratingItems.add(findViewById(R.id.rating_item_60));
        ratingItems.add(findViewById(R.id.rating_item_40));
        ratingItems.add(findViewById(R.id.rating_item_20));
        View.OnClickListener listener = new View.OnClickListener() {
            RadioButton lastClicked;

            @Override
            public void onClick(View view) {
                RadioButton radioButton = (RadioButton) view.getTag();
                if (!radioButton.isChecked()) {
                    radioButton.setChecked(true);
                    if (lastClicked != null) {
                        lastClicked.setChecked(false);
                    }
                    lastClicked = radioButton;
                }
            }
        };
        for (int i = 0; i < ratingItems.size(); i++) {
            View ratingItem = ratingItems.get(i);
            RatingBar ratingBar = (RatingBar) ratingItem.findViewById(R.id.rating_bar);
            RadioButton radioButton = (RadioButton) ratingItem.findViewById(R.id.rating_name);
            ratingBar.setRating(ratings.get(i).getStars());
            radioButton.setText(ratings.get(i).getName());
            ratingItem.setTag(radioButton);
            ratingItem.setOnClickListener(listener);
        }
        final EditTextBackEvent editTextReviewText = (EditTextBackEvent) findViewById(R.id.review_text);
        final EditTextBackEvent editTextReviewName = (EditTextBackEvent) findViewById(R.id.review_name);
        final EditTextBackEvent editTextReviewEmail = (EditTextBackEvent) findViewById(R.id.review_email);
//        editTextreviewText.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                    case MotionEvent.ACTION_UP:
//                        v.getParent().requestDisallowInterceptTouchEvent(false);
//                        break;
//                }
//                return false;
//            }
//        });
//        editTextreviewText.is
        EditTextBackEvent.EditTextImeBackListener imeBackListener = new EditTextBackEvent.EditTextImeBackListener() {
            @Override
            public void onImeBack(EditTextBackEvent editText, String text) {
                if (editText.isFocused()) {
                    editText.clearFocus();
                }
            }
        };
        editTextReviewText.setOnEditTextImeBackListener(imeBackListener);
        editTextReviewName.setOnEditTextImeBackListener(imeBackListener);
        editTextReviewEmail.setOnEditTextImeBackListener(imeBackListener);
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (!editTextReviewEmail.isFocused() && !editTextReviewName.isFocused() && !editTextReviewText.isFocused()) {
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        };
        editTextReviewEmail.setOnFocusChangeListener(onFocusChangeListener);
        editTextReviewName.setOnFocusChangeListener(onFocusChangeListener);
        editTextReviewText.setOnFocusChangeListener(onFocusChangeListener);
        EditText.OnEditorActionListener onEditorActionListener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == IME_ACTION_DONE) {
                    textView.clearFocus();
                    return true;
                }
                return false;
            }
        };
        editTextReviewEmail.setOnEditorActionListener(onEditorActionListener);
        editTextReviewName.setOnEditorActionListener(onEditorActionListener);
        editTextReviewText.setOnEditorActionListener(onEditorActionListener);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_make_review_top_layout;
    }

//    private class RatingAdapter extends ArrayAdapter<Rating> {
//        Context context;
//        ArrayList<Rating> ratings;
//
//        public RatingAdapter(Context context, ArrayList<Rating> ratings) {
//            super(context, -1);
//            this.context = context;
//            this.ratings = ratings;
//        }
//
//        @Override
//        public int getCount() {
//            return ratings.size();
//        }
//
//        @Override
//        @NonNull
//        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
//            LayoutInflater inflater = (LayoutInflater) context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            Rating rating = ratings.get(position);
//            View ratingView;
//            ViewHolder viewHolder;
//            if (convertView == null) {
//                ratingView = inflater.inflate(R.layout.rating_item, ratingList, false);
//                viewHolder = new ViewHolder();
//                viewHolder.ratingBar = (RatingBar) ratingView.findViewById(R.id.rating_bar);
//                viewHolder.ratingName = (RadioButton) ratingView.findViewById(R.id.rating_name);
//                ratingView.setTag(viewHolder);
//            } else {
//                ratingView = convertView;
//                viewHolder = (ViewHolder) ratingView.getTag();
//            }
//            viewHolder.ratingName.setText(rating.getName());
//            viewHolder.ratingBar.setRating(rating.getStars());
//            return ratingView;
//        }
//
//        private class ViewHolder {
//            RatingBar ratingBar;
//            RadioButton ratingName;
//        }
//    }
}
