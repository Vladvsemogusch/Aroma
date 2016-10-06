package oped.pp.ua.aromateque.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Wrapper {
    @SerializedName("image_urls")
    private ArrayList<String> imageUrls;
    private ArrayList<Review> reviews;

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }
}
