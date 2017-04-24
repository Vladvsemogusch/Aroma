package ua.pp.oped.aromateque.utility;

import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import timber.log.Timber;
import ua.pp.oped.aromateque.R;

import static ua.pp.oped.aromateque.utility.Constants.BASE_URL;


public class CustomImageLoader extends ImageLoader {
    private static volatile CustomImageLoader instance;

    @Override
    public void displayImage(String uri, ImageView imageView) {
        displayImage(uri, imageView, null, 0);
    }

    public void displayImage(String uri, ImageView imageView, View viewToResize, int newHeightDp) {
        final DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .showImageOnLoading(R.drawable.loading_spinner_better2)
                .build();
        Timber.d("Before: " + uri);
        uri = uri.replace("http://localhost/", BASE_URL);
        Timber.d("After: " + uri);
        displayImage(uri, imageView, displayImageOptions, new ResizingImageLoadingListener(viewToResize, newHeightDp));
    }

    public static CustomImageLoader getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new CustomImageLoader();
                }
            }
        }
        return instance;
    }
}
