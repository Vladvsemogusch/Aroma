package ua.pp.oped.aromateque.fragments.info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.utility.Utility;

public class PaymentAndDelivery extends Fragment {


    public PaymentAndDelivery() {
    }


    public static PaymentAndDelivery newInstance() {
        return new PaymentAndDelivery();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView = (TextView) getView().findViewById(R.id.about_text);
        String text = Utility.getStringFromRaw(getResources(), R.raw.payment_and_delivery);
        Spanned spannableText = Utility.compatFromHtml(text);
        textView.setText(spannableText);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
