package ua.pp.oped.aromateque.utility;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import ua.pp.oped.aromateque.R;

public class ImageLoaderWrapper {

    public static void loadImage(Context context, final ImageView imageView, String url) {
        Resources resources = context.getResources();
        final ImageView.ScaleType curScaleType = imageView.getScaleType();
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        Bitmap bitmapLoading = BitmapFactory.decodeResource(resources, R.drawable.loading_spinner_better2);
        bitmapLoading = Bitmap.createScaledBitmap(bitmapLoading, Utility.dpToPx(resources, 45), Utility.dpToPx(resources, 45), false);
        Drawable loadingDrawable = new BitmapDrawable(resources, bitmapLoading);
        Animation animation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(900);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(animation);
        Picasso.with(context)
                .load(url)
                .placeholder(loadingDrawable)
                .fit()
                .centerInside()
                .noFade()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.clearAnimation();
                        imageView.setScaleType(curScaleType);
                    }

                    @Override
                    public void onError() {
                    }
                });
    }
}
