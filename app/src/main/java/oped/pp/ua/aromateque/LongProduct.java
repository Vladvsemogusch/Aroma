package oped.pp.ua.aromateque;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

public class LongProduct {

    @SerializedName("list_attrs")
    private HashMap<String, String> listAttrs;
    private HashMap<String, String> attributes;
    private HashMap<String, String> notes;
    //@SerializedName("image_urls")
    //private ArrayList<String> imageUrls;
    @SerializedName("wrapper")
    private Wrapper wrapper;

    public HashMap<String, String> getListAttrs() {
        return listAttrs;
    }

    public void setListAttrs(HashMap<String, String> listAttrs) {
        this.listAttrs = listAttrs;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public HashMap<String, String> getNotes() {
        return notes;
    }

    public void setNotes(HashMap<String, String> notes) {
        this.notes = notes;
    }

    public Wrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(Wrapper wrapper) {
        this.wrapper = wrapper;
    }


   /*@SerializedName("entity_id")
   int entityId;
   @SerializedName("type_id")
   String typeId;
   String name;
   Float price;
   @SerializedName("special_price")
   Float specialPrice;
   String sku;
   @SerializedName("estimated_volume")
   String estimatedVolume;
   @SerializedName("shopbybrand_brand")
   String shopbybrandBrand;
   String volume;
   String gender;
   String country;
   String typeofproduct;
   String topnotes;
   String middlenotes;
   String basenotes;
   @SerializedName("is_saleable")
   int isSaleable;
   @SerializedName("image_url")
   String imageUrl;
   @SerializedName("is_in_stock")
   String isInStock;
   @SerializedName("total_reviews_count")
   int totalReviewsCount;
   String discount;
*/
}

class Wrapper {
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

