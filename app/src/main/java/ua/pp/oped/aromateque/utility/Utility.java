package ua.pp.oped.aromateque.utility;


import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Utility {


    public static Spanned compatFromHtml(String input) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(input);
        }
    }

    // limit note length to 12 and split notes string by space
    public static String checkAndCut(String input) {
        List<String> inputDivided = Arrays.asList(input.split(", "));
        String returnNotes = "";
        for (String note : inputDivided) {
            if (note.length() > 12) {
                returnNotes += note.replace(" ", "\n");
            } else {
                returnNotes += note;
            }
            if (inputDivided.indexOf(note) != (inputDivided.size() - 1)) {
                returnNotes += ",\n";
            }
        }
        return returnNotes;
    }


    public static int compatGetColor(Resources res, int colorId) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return res.getColor(colorId, null);
        } else {
            return res.getColor(colorId);
        }
    }

    public static Drawable compatGetDrawable(Resources res, int drawableId) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return res.getDrawable(drawableId, null);
        } else {
            return res.getDrawable(drawableId);
        }
    }

    public static ColorStateList compatGetColorStateList(Resources res, int colorStateListId) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return res.getColorStateList(colorStateListId, null);
        } else {
            return res.getColorStateList(colorStateListId);
        }
    }

    public static int dpToPx(Resources res, int dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
        return Math.round(px);
    }

    public static String getPriceWithDiscount(String fullPrice, String discount) {
        if (fullPrice.contains(".")) {
            fullPrice = fullPrice.substring(0, fullPrice.indexOf('.'));
        }
        discount = discount.substring(0, discount.indexOf('%'));
        long discountedPrice = Math.round(Float.parseFloat(fullPrice) * (100 - Float.parseFloat(discount)) / 100);
        return String.valueOf(discountedPrice);
    }

    public static String getStringFromRaw(Resources resources, int resourceId) {
        try {
            InputStream in_s = resources.openRawResource(resourceId);
            byte[] buffer = new byte[in_s.available()];
            in_s.read(buffer);
            return new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFromAssets(AssetManager assets, String filepath) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream;
        try {
            inputStream = assets.open(filepath);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                stringBuilder.append(str);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


}
