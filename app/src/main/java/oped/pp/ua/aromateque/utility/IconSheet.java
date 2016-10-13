package oped.pp.ua.aromateque.utility;


import android.graphics.Bitmap;

public class IconSheet {
    static private Bitmap iconSheet;

    public static void initialize(Bitmap iconSheetIn) {
        iconSheet = iconSheetIn;
    }

    public static Bitmap getBitmap(int offsetX, int offsetY, int width, int height) {
        return Bitmap.createBitmap(iconSheet, offsetX, offsetY, width, height);
    }
}
