package oped.pp.ua.aromateque.product.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import oped.pp.ua.aromateque.R;
import oped.pp.ua.aromateque.Review;

public class ProductReviewsFragment extends Fragment {
    ArrayList<Review> reviews;

    public static ProductReviewsFragment newInstance(ArrayList<Review> inputReviews) {
        Bundle args = new Bundle();
        ArrayList<String> reviewsText = new ArrayList<>();
        ArrayList<String> reviewsNickname = new ArrayList<>();
        ArrayList<String> reviewsDate = new ArrayList<>();
        ArrayList<String> reviewsRating = new ArrayList<>();
        for (Review review : inputReviews) {
            reviewsText.add(review.getText());
            reviewsNickname.add(review.getNickname());
            reviewsDate.add(review.getDate());
            reviewsRating.add(review.getRating());
        }

        args.putStringArrayList("reviews_text", reviewsText);
        args.putStringArrayList("reviews_nickname", reviewsNickname);
        args.putStringArrayList("reviews_date", reviewsDate);
        args.putStringArrayList("reviews_rating", reviewsRating);

        ProductReviewsFragment fragment = new ProductReviewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reviews = new ArrayList<>();
        Review review;
        ArrayList<String> reviewsText = getArguments().getStringArrayList("reviews_text");
        ArrayList<String> reviewsNickname = getArguments().getStringArrayList("reviews_nickname");
        ArrayList<String> reviewsDate = getArguments().getStringArrayList("reviews_date");
        ArrayList<String> reviewsRating = getArguments().getStringArrayList("reviews_rating");
        if (reviewsRating != null) {
            for (int i = 0; i < getArguments().getStringArrayList("reviews_rating").size(); i++) {
                review = new Review();
                review.setText(reviewsText.get(i));
                review.setNickname(reviewsNickname.get(i));
                review.setDate(reviewsDate.get(i));
                review.setRating(reviewsRating.get(i));
                reviews.add(review);
            }
        }
        //reviews = (ArrayList<Review>) getArguments().getSerializable("reviews");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_reviews, container, false);
        ListView reviewsListview = (ListView) view.findViewById(R.id.reviews_listview);
        reviewsListview.setAdapter(new ReviewArrayAdapter(getContext(), reviews));
        return view;

    }
}

class ReviewArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<Review> reviews;

    public ReviewArrayAdapter(Context context, ArrayList<Review> reviews) {
        super(context, -1);
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        Log.d("REVIEW", String.valueOf(reviews.size()));
        return reviews.size();
    }

    private class ViewHolder {
        TextView txtNickname;
        TextView txtDate;
        TextView txtRating;
        TextView txtText;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder viewHolder;
        View rowView = convertView;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.review_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txtNickname = (TextView) rowView.findViewById(R.id.review_nickname);
            viewHolder.txtDate = (TextView) rowView.findViewById(R.id.review_date);
            viewHolder.txtRating = (TextView) rowView.findViewById(R.id.review_rating);
            viewHolder.txtText = (TextView) rowView.findViewById(R.id.review_text);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        viewHolder.txtNickname.setText(reviews.get(position).getNickname().toUpperCase());
        viewHolder.txtDate.setText(reviews.get(position).getDate());
        viewHolder.txtRating.setText(reviews.get(position).getRating());
        String text = reviews.get(position).getText();
        text = text.substring(0, 1).toUpperCase() + text.substring(1);
        viewHolder.txtText.setText(text);

        return rowView;
    }
}