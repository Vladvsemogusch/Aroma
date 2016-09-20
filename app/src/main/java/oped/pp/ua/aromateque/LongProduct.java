package oped.pp.ua.aromateque;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LongProduct {
    @SerializedName("list_attrs")
    private HashMap<String, String> listAttrs;
    private HashMap<String, String> attributes;
    private HashMap<String, String> notes;
    @SerializedName("image_urls")
    private ArrayList<String> imageUrls;
    @SerializedName("image_urls_wrapper")
    private ImageUrlWrapper imageUrlsWrapper;

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public ImageUrlWrapper getImageUrlsWrapper() {
        return imageUrlsWrapper;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }


    public HashMap<String, String> getListAttrs() {
        return listAttrs;
    }

    public HashMap<String, String> getNotes() {
        return notes;
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

class ImageUrlWrapper {
    @SerializedName("image_urls")
    private ArrayList<String> imageUrls;

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }
}