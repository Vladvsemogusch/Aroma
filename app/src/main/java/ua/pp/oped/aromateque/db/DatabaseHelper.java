package ua.pp.oped.aromateque.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.model.LongProduct;
import ua.pp.oped.aromateque.model.Review;

import static ua.pp.oped.aromateque.utility.Constants.CATEGORY_ALL_ID;

public class DatabaseHelper extends SQLiteAssetHelper { // TODO data lifetime

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "aromateque.db";
    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

    public static void initialize(Context context) {
        instance = new DatabaseHelper(context);
    }

    public static DatabaseHelper getInstance() {

        return instance;
    }

    public void serializeProduct(LongProduct product) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        HashMap<String, String> attributes = product.getAttributes();
        ArrayList<String> imageUrls = product.getImageUrls();
        ArrayList<Review> reviews = product.getReviews();
        ContentValues values = new ContentValues();
        for (Map.Entry<String, String> entry : product.getAttributes().entrySet()) {
            values.put(entry.getKey(), entry.getValue());
        }

        /*values.put("id", attributes.get("id"));
        values.put("name", attributes.get("name"));
        values.put("price", attributes.get("price"));
        values.put("sku", attributes.get("sku"));
        values.put("brand", attributes.get("brand"));
        values.put("gender", attributes.get("gender"));
        values.put("country", attributes.get("country"));
        values.put("type_of_product", attributes.get("type_of_product"));
        values.put("top_notes", attributes.get("top_notes"));
        values.put("middle_notes", attributes.get("middle_notes"));
        values.put("base_notes", attributes.get("base_notes"));
        values.put("is_saleable", attributes.get("is_saleable"));
        values.put("is_in_stock", attributes.get("is_in_stock"));
        values.put("reviews_count", attributes.get("reviews_count"));
        values.put("discount", attributes.get("discount"));
        values.put("volume", attributes.get("volume"));
        values.put("perfumer", attributes.get("perfumer"));
        values.put("year_of_manufacture", attributes.get("year_of_manufacture"));
        values.put("description", attributes.get("description"));
        values.put("brand_img_url", attributes.get("brand_img_url"));
        values.put("rating_summary", attributes.get("rating_summary"));
        values.put("short_description", attributes.get("short_description"));
        */

        //try / finally brings errors
        getWritableDatabase().beginTransaction();
        getWritableDatabase().insert("products", null, values);
        values.clear();
        String productId = product.getAttributes().get("product_id");
        for (String imageUrl :
                product.getImageUrls()) {
            values.put("img_url", imageUrl);
            values.put("product_id", productId);
            getWritableDatabase().insert("img_urls", null, values);
            values.clear();
        }
        values = new ContentValues();
        for (Review review :
                reviews) {
            values.put("product_id", productId);
            values.put("text", review.getText());
            values.put("nickname", review.getNickname());
            values.put("date", review.getDate());
            values.put("rating", review.getRating());
            getWritableDatabase().insert("reviews", null, values);
        }
        getWritableDatabase().setTransactionSuccessful();

        getWritableDatabase().endTransaction();
        getWritableDatabase().close();


    }

    public LongProduct deserializeProduct(int id) {
        LongProduct product = new LongProduct();
        HashMap<String, String> attributes = new HashMap<>();
        ArrayList<String> imageUrls = new ArrayList<>();
        ArrayList<Review> reviews = new ArrayList<>();
        String[] args = {String.valueOf(id)};
        getWritableDatabase().beginTransaction();
        Cursor c = getReadableDatabase().query("products", null, "product_id=?", args, null, null, null);
        Log.d("DB", String.valueOf(c.getCount()));
        c.moveToNext();
        for (int i = 0; i < 22; i++) {
            attributes.put(c.getColumnName(i), c.getString(i));
        }
        product.setAttributes(attributes);
        c.close();
        c = getReadableDatabase().query("img_urls", null, "product_id=?", args, null, null, null);
        while (c.moveToNext()) {
            imageUrls.add(c.getString(2));  // 2 = img_url
        }
        c.close();
        product.setImageUrls(imageUrls);
        c = getReadableDatabase().query("reviews", null, "product_id=?", args, null, null, null);
        Review review;
        while (c.moveToNext()) {
            review = new Review();
            review.setText(c.getString(2));
            review.setNickname(c.getString(3));
            review.setDate(c.getString(4));
            review.setRating(c.getString(5));
            reviews.add(review);
        }
        c.close();
        product.setReviews(reviews);
        return product;
    }

    public boolean productExists(int id) {
        String[] args = {String.valueOf(id)};
        Cursor c = getReadableDatabase().query("products", null, "product_id=?", args, null, null, null);
        boolean exists = c.getCount() > 0;
        Log.d("DB", "product exist: " + exists);
        c.close();
        return exists;


    }

    //Once per app launch
    public void serializeCategories(Category categoryAll) {
        Log.d("LAUNCH", "Started serializing categories");
        getWritableDatabase().beginTransaction();
        serializeCategory(categoryAll);
        getWritableDatabase().setTransactionSuccessful();
        getWritableDatabase().endTransaction();


    }

    private void serializeCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put("category_id", category.getId());
        values.put("name", category.getName());
        values.put("parent_id", category.getParentId());
        if (category.getChildrenIds() != null) {
            String childrenIds = "";
            for (int id : category.getChildrenIds()) {
                childrenIds += String.valueOf(id) + ",";
            }
            childrenIds = childrenIds.substring(0, childrenIds.length() - 1);
            values.put("children_ids", childrenIds);
            for (Category subCategory : category.getChildren()) {
                serializeCategory(subCategory);
            }
        }
        getWritableDatabase().insert("categories", null, values);
    }

    public Category deserializeCategory(int categoryId) {
        Log.d("DB", "deserializing " + categoryId);
        String[] args = {String.valueOf(categoryId)};
        Cursor c = getReadableDatabase().query("categories", null, "category_id=?", args, null, null, null);
        c.moveToNext();
        Category category = new Category();
        category.setId(c.getInt(0));
        category.setName(c.getString(1));
        category.setParentId(c.getInt(2));
        String childrenIds = c.getString(3);
        c.close();
        ArrayList<Integer> childrenIdsList = null;
        ArrayList<Category> childrenCategories = null;
        if (childrenIds != null) {
            childrenIdsList = new ArrayList<>();
            for (String childId : childrenIds.split(",")) {
                childrenIdsList.add(Integer.valueOf(childId));
            }
            childrenCategories = new ArrayList<>();
            for (int child_id : childrenIdsList) {
                childrenCategories.add(deserializeCategory(child_id));
            }
        }
        category.setChildrenIds(childrenIdsList);
        category.setChildren(childrenCategories);
        return category;
    }

    public boolean categoriesSerialized() {
        String[] args = {String.valueOf(CATEGORY_ALL_ID)};
        Cursor c = getReadableDatabase().query("categories", null, "category_id=?", args, null, null, null);
        Boolean isSerialized = c.getCount() > 0;
        c.close();
        return isSerialized;

    }
}
