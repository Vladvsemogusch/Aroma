package ua.pp.oped.aromateque.utility;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class ResizingImageLoadingListener implements ImageLoadingListener {
    private ViewGroup.LayoutParams layoutParams;
    private int originalHeight;
    private View viewToResize;
    private int newHeight;

    public ResizingImageLoadingListener() {

    }


    public ResizingImageLoadingListener(View viewToResize, int newHeightDp) {
        this.viewToResize = viewToResize;
        this.newHeight = newHeightDp;
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        if (viewToResize == null) {
            viewToResize = view;
        }
        layoutParams = viewToResize.getLayoutParams();
        originalHeight = layoutParams.height;
        if (newHeight == 0) {
            layoutParams.height = (int) Math.round(layoutParams.height * 0.3);
        } else {
            layoutParams.height = Utility.dpToPx(newHeight);
        }
        viewToResize.setLayoutParams(layoutParams);
        Log.d("IMAGEVIEW", "Reduced size of ImageView");
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        layoutParams.height = originalHeight;
        Log.d("IMAGEVIEW", "Restored size of ImageView");
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
    }
}