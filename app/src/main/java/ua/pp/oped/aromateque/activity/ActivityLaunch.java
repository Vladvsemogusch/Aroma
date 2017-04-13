package ua.pp.oped.aromateque.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;
import ua.pp.oped.aromateque.AromatequeApplication;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.api.MagentoAPI;
import ua.pp.oped.aromateque.base_activity.CalligraphyActivity;
import ua.pp.oped.aromateque.data.db.DatabaseHelper;
import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.utility.IconSheet;
import ua.pp.oped.aromateque.utility.RetryableCallback;

import static ua.pp.oped.aromateque.utility.Constants.BASE_URL;
import static ua.pp.oped.aromateque.utility.Constants.CATEGORY_ALL_ID;


public class ActivityLaunch extends CalligraphyActivity {
    private static final String TAG = "ActivityLaunch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        // Initialize IconSheet
        IconSheet.initialize(BitmapFactory.decodeResource(getResources(), R.drawable.icon_sheet));
        // Initialize ImageLoader
        Picasso.Builder picassoBuilder = new Picasso.Builder(this);
        picassoBuilder.requestTransformer(new Picasso.RequestTransformer() {
            @Override
            public Request transformRequest(Request request) {
                String url = request.uri.toString();
                url = url.replace("http://localhost/", BASE_URL);
                Request.Builder builder = request.buildUpon();
                return builder.setUri(Uri.parse(url)).build();
            }
        });
//        picassoBuilder.loggingEnabled(true);
        picassoBuilder.indicatorsEnabled(true);
        picassoBuilder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });
        picassoBuilder.downloader(new OkHttp3Downloader(this));

        try {
            Picasso.setSingletonInstance(picassoBuilder.build());
        } catch (Exception e) {
            Timber.d("Picasso singleton already exist");
        }
//        picassoBuilder.build().load().
        /*BaseImageDownloader imageDownloader = new BaseImageDownloader(this) {
            @Override
            protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
                HttpURLConnection conn = createConnection(imageUri, extra);
                // Test for refused connection. If connection refused, try again until no exception.
//                testConnection(conn, imageUri, extra);
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

                    getStreamFromNetwork(imageUri, extra);
                }
            }

        };
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .imageDownloader(imageDownloader)
                .build();
//        ImageLoader.getInstance().init(config);
        CustomImageLoader.getInstance().init(config);
*/
        if (!DatabaseHelper.getInstance(this).categoriesSerialized()) {
            final MagentoAPI api = AromatequeApplication.getMagentoAPI();
            api.getCategoryWithChildren(CATEGORY_ALL_ID).enqueue(new RetryableCallback<Category>() {
                public void onFinalResponse(Call<Category> call, Response<Category> response) {
                    try {
                        if (response.body() == null) {
                            Log.e(TAG, "response.body()==null)");
                        }
                        DatabaseHelper.getInstance(ActivityLaunch.this).serializeCategories(response.body());
                        Log.d("INFO", "Categories serialized");
                        startNextActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFinalFailure(Call<Category> call, Throwable t) {
                }
            });
        } else {
            Log.d("LAUNCH", "Categories already serialized");
            startNextActivity();
        }
    }

    private void startNextActivity() {
        startActivity(new Intent(this, ActivityCheckoutMain.class));
        finish();
    }

}
