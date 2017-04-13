package ua.pp.oped.aromateque.api;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.model.RawLongProduct;
import ua.pp.oped.aromateque.model.ShortProduct;

public interface MagentoAPI {
    @Headers("Accept: application/json")
    @GET("api/rest/products")
    Call<HashMap<Integer, ShortProduct>> getProducts();

    @Headers("Accept: application/json")
    @GET("api/rest/products/?category")
    Call<HashMap<Integer, String>> getProductsFromCategory();

    @Headers("Accept: application/json")
    @GET("api/rest/custom/products/{id}")
    Call<RawLongProduct> getProduct(@Path("id") int productId);

    @Headers("Accept: application/json")
    @GET("api/rest/custom/category/{id}")
    Call<Category> getCategoryWithChildren(@Path("id") int id);
}

