package oped.pp.ua.aromateque;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface MagentoRestService {
    @Headers("Accept: application/json")
    @GET("api/rest/products")
    Call<HashMap<Integer, ShortProduct>> getProducts();

    @Headers("Accept: application/json")
    @GET("api/rest/custom/products/{id}")
    Call<LongProduct> getProduct(@Path("id") int productId);

    @Headers("Accept: application/json")
    @GET("api/rest/custom/categories/{id}")
    Call<Category> getCategoryWithChildren(@Path("id") int id);
}
