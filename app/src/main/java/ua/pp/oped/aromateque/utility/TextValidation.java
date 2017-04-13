package ua.pp.oped.aromateque.utility;


import android.text.TextUtils;

public class TextValidation {

    public static boolean validateEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }
}
