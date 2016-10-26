package ua.pp.oped.aromateque.utility;


import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.View;

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

    public static void compatSetBackgroundColor(Resources res, View view, int colorId) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(res.getColor(colorId, null));
        } else {
            view.setBackgroundColor(res.getColor(colorId));
        }
    }

    public static int compatGetColor(Resources res, int colorId) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return res.getColor(colorId, null);
        } else {
            return res.getColor(colorId);
        }
    }

    public static Drawable compatGetDrawable(Resources res, int drawableId) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return res.getDrawable(drawableId, null);
        } else {
            return res.getDrawable(drawableId);
        }
    }

}
