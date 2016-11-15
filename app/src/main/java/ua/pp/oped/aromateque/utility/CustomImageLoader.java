package ua.pp.oped.aromateque.utility;

import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import ua.pp.oped.aromateque.R;


public class CustomImageLoader extends ImageLoader {

    @Override
    public void displayImage(String uri, ImageView imageView) {
        displayImage(uri, imageView, null, null);
    }

    public void displayImage(String uri, ImageView imageView, View viewToResize, int newHeightDp) {
        final DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .showImageOnLoading(R.drawable.loading)
                .build();
        displayImage(uri, imageView, displayImageOptions, new ResizingImageLoadingListener(viewToResize, newHeightDp));
    }
}
