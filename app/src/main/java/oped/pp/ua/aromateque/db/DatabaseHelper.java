package oped.pp.ua.aromateque.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import oped.pp.ua.aromateque.model.LongProduct;
import oped.pp.ua.aromateque.model.Review;

public class DatabaseHelper extends SQLiteAssetHelper { // TODO data lifetime

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "aromateque.db";
    private SQLiteDatabase dbReadable;
    private SQLiteDatabase dbWritable;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
        dbReadable = getReadableDatabase();
        dbWritable = getWritableDatabase();

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
        dbWritable.insert("products", null, values);
        values.clear();
        String productId = product.getAttributes().get("product_id");
        for (String imageUrl :
                product.getImageUrls()) {
            values.put("img_url", imageUrl);
            values.put("product_id", productId);
            dbWritable.insert("img_urls", null, values);
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
            dbWritable.insert("reviews", null, values);
        }
    }

    public LongProduct deserializeProduct(int id) {
        LongProduct product = new LongProduct();
        HashMap<String, String> attributes = new HashMap<>();
        ArrayList<String> imageUrls = new ArrayList<>();
        ArrayList<Review> reviews = new ArrayList<>();
        String[] args = {String.valueOf(id)};
        Cursor c = dbReadable.query("products", null, "product_id=?", args, null, null, null);
        Log.d("DB", String.valueOf(c.getCount()));
        c.moveToNext();
        for (int i = 0; i < 22; i++) {
            attributes.put(c.getColumnName(i), c.getString(i));
        }
        product.setAttributes(attributes);
        c.close();
        c = dbReadable.query("img_urls", null, "product_id=?", args, null, null, null);
        while (c.moveToNext()) {
            imageUrls.add(c.getString(2));  // 2 = img_url
        }
        c.close();
        product.setImageUrls(imageUrls);
        c = dbReadable.query("reviews", null, "product_id=?", args, null, null, null);
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
        Cursor c = dbReadable.query("products", null, "product_id=?", args, null, null, null);
        boolean exists = c.getCount() > 0;
        Log.d("DB", "product exist: " + exists);
        c.close();
        return exists;


    }
}
