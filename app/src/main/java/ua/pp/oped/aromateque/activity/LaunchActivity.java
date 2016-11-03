package ua.pp.oped.aromateque.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.pp.oped.aromateque.CalligraphyActivity;
import ua.pp.oped.aromateque.MagentoRestService;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.db.DatabaseHelper;
import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.utility.IconSheet;

import static ua.pp.oped.aromateque.utility.Constants.BASE_URL;
import static ua.pp.oped.aromateque.utility.Constants.CATEGORY_ALL_ID;


public class LaunchActivity extends CalligraphyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        //Initialize IconSheet
        IconSheet.initialize(BitmapFactory.decodeResource(getResources(), R.drawable.icon_sheet));
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        DatabaseHelper.initialize(getApplicationContext());
        if (!DatabaseHelper.getInstance().categoriesSerialized()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final MagentoRestService api = retrofit.create(MagentoRestService.class);
            class CategoryRecursiveCallback<T> implements Callback<T> {
                public void onResponse(Call<T> call, Response<T> response) {
                    try {
                        DatabaseHelper.getInstance().serializeCategories((Category) response.body());
                        Log.d("INFO", "Categories serialized");
                        startNextActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(Call<T> call, Throwable t) {
                    t.printStackTrace();
                    api.getCategoryWithChildren(CATEGORY_ALL_ID).enqueue(new CategoryRecursiveCallback<Category>());
                }
            }
            api.getCategoryWithChildren(CATEGORY_ALL_ID).enqueue(new CategoryRecursiveCallback<Category>());
        } else {
            Log.d("LAUNCH", "Categories already serialized");
            startNextActivity();
        }
    }

    private void startNextActivity() {
        startActivity(new Intent(LaunchActivity.this, MainPageActivity.class));
    }

}
