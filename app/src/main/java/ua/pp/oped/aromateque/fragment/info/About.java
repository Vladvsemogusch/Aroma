package ua.pp.oped.aromateque.fragment.info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.utility.Utility;

public class About extends Fragment {


    public About() {
    }


    public static About newInstance() {
        return new About();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_plain_text, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView = (TextView) getView().findViewById(R.id.about_text);
        textView.setText(Utility.getStringFromRaw(getResources(), R.raw.about));
    }

}
