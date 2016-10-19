package ua.pp.oped.aromateque.model;

import java.util.ArrayList;
import java.util.HashMap;

public class LongProduct {

    private HashMap<String, String> listAttrs;
    private HashMap<String, String> attributes;
    private HashMap<String, String> notes;
    private ArrayList<String> imageUrls;
    private ArrayList<Review> reviews;

    public HashMap<String, String> getListAttrs() {
        return listAttrs;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public HashMap<String, String> getNotes() {
        return notes;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setListAttrs(HashMap<String, String> listAttrs) {
        this.listAttrs = listAttrs;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public void setNotes(HashMap<String, String> notes) {
        this.notes = notes;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }
}


