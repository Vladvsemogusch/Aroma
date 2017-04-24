package ua.pp.oped.aromateque.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;
import ua.pp.oped.aromateque.model.CartItem;
import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.model.LongProduct;
import ua.pp.oped.aromateque.model.Review;
import ua.pp.oped.aromateque.model.ShortProduct;
import ua.pp.oped.aromateque.utility.Utility;

import static ua.pp.oped.aromateque.utility.Constants.CATEGORY_ALL_ID;

public class DatabaseHelper extends SQLiteAssetHelper { // TODO data lifetime
    private static final String TAG = "DATABASE_HELPER";
    private static final int DATABASE_VERSION = 8;
    private static final String DATABASE_NAME = "aromateque.db";
    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            return new DatabaseHelper(context.getApplicationContext());
        }
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


    }

    public LongProduct deserializeLongProduct(int id) {
        LongProduct product = new LongProduct();
        HashMap<String, String> attributes = new HashMap<>();
        ArrayList<String> imageUrls = new ArrayList<>();
        ArrayList<Review> reviews = new ArrayList<>();
        String[] args = {String.valueOf(id)};
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
        // Remove after moving to deployment domain
        String brandImgUrl = product.getAttributes().get("brand_img_url");
//        brandImgUrl = brandImgUrl.replace("http://localhost/", Constants.BASE_URL);
        product.getAttributes().put("brand_img_url", brandImgUrl);
        for (String url :
                product.getImageUrls()) {
            int urlPosition = product.getImageUrls().indexOf(url);
//            url = url.replace("http://localhost/", Constants.BASE_URL);
            product.getImageUrls().remove(urlPosition);
            product.getImageUrls().add(urlPosition, url);
        }
        return product;
    }

    public ShortProduct deserializeShortProduct(int productId) {
        ShortProduct product = new ShortProduct();
        HashMap<String, String> attributes = new HashMap<>();
        String[] columns = {"product_id", "brand", "name", "type_of_product", "price", "discount"};
        String[] args = {String.valueOf(productId)};
        Cursor c = getReadableDatabase().query("products", columns, "product_id=?", args, null, null, null);
        Log.d("DB", String.valueOf(c.getCount()));
        c.moveToNext();
        product.setId(c.getInt(0));
        product.setBrand(c.getString(1));
        product.setName(c.getString(2));
        product.setTypeAndVolume(c.getString(3));
        String discountedPrice = Utility.getPriceWithDiscount(c.getString(4), c.getString(5));
        product.setPrice(discountedPrice);
        product.setOldPrice(c.getString(4));
        c.close();
        c = getReadableDatabase().query("img_urls", null, "product_id=?", args, null, null, null);
        c.moveToNext();
        product.setImageUrl(c.getString(2));
        c.close();
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
    public void serializeCategories(Category category) {
        Log.d("LAUNCH", "Started serializing categories");
        getWritableDatabase().beginTransaction();
        serializeCategory(category);
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

    public void addToCart(int id) {
        addToCart(id, 1);
    }

    public void addToCart(int id, int qty) {
        ContentValues values = new ContentValues();
        values.put("product_id", id);
        values.put("qty", qty);
        getWritableDatabase().insert("cart", null, values);
        Log.d(TAG, "Added to cart product id: " + id);
    }

    public void removeFromCart(int id) {
        String[] args = {String.valueOf(id)};
        getWritableDatabase().delete("cart", "product_id=?", args);
        Log.d(TAG, "Removed from cart product id: " + id);
    }

    public void incrementQty(int productId) {
        String[] values = {String.valueOf(productId)};
        String[] columns = {"qty"};
        Cursor c = getReadableDatabase().query("cart", columns, "product_id = ?", values, null, null, null);
        c.moveToNext();
        int qty = c.getInt(0);
        c.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("qty", ++qty);
        getWritableDatabase().update("cart", contentValues, "product_id = ?", values);
        Log.d(TAG, "Qty incremented product id: " + productId);
    }

    public void decrementQty(int productId) {
        String[] values = {String.valueOf(productId)};
        String[] columns = {"qty"};
        Cursor c = getReadableDatabase().query("cart", columns, "product_id = ?", values, null, null, null);
        c.moveToNext();
        int qty = c.getInt(0);
        c.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("qty", --qty);
        getWritableDatabase().update("cart", contentValues, "product_id = ?", values);
        Log.d(TAG, "Qty decremented product id: " + productId);
        if (qty == 0) {
            removeFromCart(productId);
            Log.d(TAG, "Qty is 0. Needs deletion from cart product id: " + productId);
        }
    }

    public List<CartItem> getCart() {
        Cursor c = getReadableDatabase().query("cart", null, null, null, null, null, "id ASC");
        if (c.getCount() == 0) {
            Log.d(TAG, "getCart() cursor empty");
        }
        ArrayList<CartItem> cart = new ArrayList<>();
        while (c.moveToNext()) {
            // 0 - id, 1 - product_id, 2 - qty.
            CartItem cartItem = new CartItem(c.getInt(1), c.getInt(2));
            cart.add(cartItem);
            Log.d(TAG, "Retrieved cart item from db and put at position: " + (cart.indexOf(cartItem)) + " ;product id: " + c.getInt(1) + " ; qty: " + c.getInt(2));
        }
        Timber.d("Cart size: " + String.valueOf(cart.size()));
        c.close();
        return cart;
    }

    public boolean isInCart(int productId) {
        String[] columns = {"product_id"};
        String[] args = {String.valueOf(productId)};
        Cursor c = getReadableDatabase().query("cart", columns, "product_id = ?", args, null, null, null);
        boolean isInCart = c.getCount() != 0;
        c.close();
        return isInCart;
    }

    public int getCartItemQty(int productId) {
        String[] columns = {"qty"};
        String[] args = {String.valueOf(productId)};
        Cursor c = getReadableDatabase().query("cart", columns, "product_id=?", args, null, null, null);
        c.moveToNext();
        int result = c.getInt(0);
        c.close();
        return result;
    }

    public int getCartQty() {
        String[] columns = {"qty"};
        Cursor c = getReadableDatabase().query("cart", columns, null, null, null, null, null);
        int totalQty = 0;
        while (c.moveToNext()) {
            totalQty += c.getInt(0);
            Timber.d("Qty part: " + totalQty);
        }
        c.close();
        return totalQty;
    }

    public void addFavorite(int id) {
        ContentValues values = new ContentValues();
        values.put("product_id", id);
        getWritableDatabase().insert("favorites", null, values);
        Log.d(TAG, "Added to favorites product id: " + id);
    }

    public void removeFavorite(int id) {
        String[] args = {String.valueOf(id)};
        getWritableDatabase().delete("favorites", "product_id=?", args);
        Log.d(TAG, "Removed from favorites product id: " + id);
    }

    public List<ShortProduct> getFavorites() {
        Cursor c = getReadableDatabase().query("favorites", null, null, null, null, null, "id ASC");
        if (c.getCount() == 0) {
            Log.d(TAG, "getFavorites() cursor empty");
        }
        List<ShortProduct> favorites = new ArrayList<>();
        while (c.moveToNext()) {
            // 0 - id, 1 - product_id
            ShortProduct favoriteProduct = deserializeShortProduct(c.getInt(1));
            favorites.add(favoriteProduct);
            Log.d(TAG, "Retrieved cart item from db and put at position: " + (favorites.indexOf(favoriteProduct)) + " ;product id: " + c.getInt(1));
        }
        Timber.d("Favorites size: " + String.valueOf(favorites.size()));
        c.close();
        return favorites;
    }

    public boolean isInFavorites(int productId) {
        String[] columns = {"product_id"};
        String[] args = {String.valueOf(productId)};
        Cursor c = getReadableDatabase().query("favorites", columns, "product_id = ?", args, null, null, null);
        boolean isInCart = c.getCount() != 0;
        c.close();
        return isInCart;
    }
}
