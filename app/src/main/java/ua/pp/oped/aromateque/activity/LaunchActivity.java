package ua.pp.oped.aromateque.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ContentLengthInputStream;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

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
import ua.pp.oped.aromateque.utility.CustomImageLoader;
import ua.pp.oped.aromateque.utility.IconSheet;
import ua.pp.oped.aromateque.utility.Utility;

import static ua.pp.oped.aromateque.utility.Constants.BASE_URL;
import static ua.pp.oped.aromateque.utility.Constants.CATEGORY_ALL_ID;


public class LaunchActivity extends CalligraphyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        // Initialize IconSheet
        IconSheet.initialize(BitmapFactory.decodeResource(getResources(), R.drawable.icon_sheet));
        // Initialize Utility
        Utility.initialize(getResources());
        // Initialize ImageLoader
        BaseImageDownloader imageDownloader = new BaseImageDownloader(this) {
            @Override
            protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
                HttpURLConnection conn = createConnection(imageUri, extra);
                // Test for refused connection. If connection refused, try again until no exception.
                testConnection(conn, imageUri, extra);
                int redirectCount = 0;
                while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
                    conn = createConnection(conn.getHeaderField("Location"), extra);
                    redirectCount++;
                }
                InputStream imageStream;
                try {
                    imageStream = conn.getInputStream();
                } catch (IOException e) {
                    // Read all data to allow reuse connection (http://bit.ly/1ad35PY)
                    IoUtils.readAndCloseStream(conn.getErrorStream());
                    throw e;
                }
                if (!shouldBeProcessed(conn)) {
                    IoUtils.closeSilently(imageStream);
                    throw new IOException("Image request failed with response code " + conn.getResponseCode());
                }

                return new ContentLengthInputStream(new BufferedInputStream(imageStream, BUFFER_SIZE), conn.getContentLength());
            }

            private synchronized void testConnection(HttpURLConnection conn, String imageUri, Object extra) throws IOException {
                try {
                    conn.getResponseCode();
                } catch (Exception e) {
                    Log.d("BaseImageDownloader", "Failed to connect, trying again.");
                    Log.d("BaseImageDownloader", "URL: " + imageUri);
                    e.printStackTrace();
                    try {
                        wait(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    getStreamFromNetwork(imageUri, extra);
                }
            }

        };
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .imageDownloader(imageDownloader)
                .build();
        CustomImageLoader.getInstance().init(config);

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
        startActivity(new Intent(this, ProductListActivity.class));
    }

}
