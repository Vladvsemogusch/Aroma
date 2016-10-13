package oped.pp.ua.aromateque;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import oped.pp.ua.aromateque.model.LongProduct;

public class MainActivity extends CalligraphyActivity {
    final String BASE_URL = "http://10.0.1.50/";
    TextView text;
    LongProduct pr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AromatequeTheme);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, ProductInfo.class);
        intent.putExtra("product_id", "174");
        startActivity(intent);
        /*text = (TextView) findViewById(R.id.text);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MagentoRestService api = retrofit.create(MagentoRestService.class);
        Call<HashMap<Integer,ShortProduct>> callGetProducts = api.getProducts();
        callGetProducts.enqueue(new Callback<HashMap<Integer,ShortProduct>>() {
            @Override
            public void onResponse(Call<HashMap<Integer,ShortProduct>> call, Response<HashMap<Integer,ShortProduct>> response) {
                int statusCode = response.code();
                try {
                    productsMap = response.body();
                    text.setText(response.raw().toString());
                    //text.setText(String.valueOf(productsMap.get(39).entity_id));
                } catch (Exception e) {
                    Log.d("ERRRORRR", e.toString());
                }
            }

            @Override
            public void onFailure(Call<HashMap<Integer,ShortProduct>> call, Throwable t) {
                text.setText(t.toString());
            }
        });
        Call<LongProduct> callGetProduct = api.getProduct(174);
        callGetProduct.enqueue(new Callback<LongProduct>() {
            @Override
            public void onResponse(Call<LongProduct> call, Response<LongProduct> response) {
                int statusCode = response.code();
                try {
                    LongProduct product = response.body();
                    text.setText(response.raw().toString());
                    text.setText(product.getAttributes().get("name"));
                } catch (Exception e) {
                    Log.d("ERRRORRR2", e.toString());
                }
            }

            @Override
            public void onFailure(Call<LongProduct> call, Throwable t) {
                text.setText(t.toString());
            }
        });*/
    }


}





