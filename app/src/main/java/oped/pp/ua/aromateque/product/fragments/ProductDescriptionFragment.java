package oped.pp.ua.aromateque.product.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import oped.pp.ua.aromateque.ProductInfo;
import oped.pp.ua.aromateque.R;

public class ProductDescriptionFragment extends Fragment {
    HashMap<String, String> attributes;

    public static ProductDescriptionFragment newInstance(HashMap<String, String> attributes) {
        Bundle args = new Bundle();
        args.putSerializable("attributes", attributes);

        ProductDescriptionFragment fragment = new ProductDescriptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attributes = (HashMap<String, String>) getArguments().getSerializable("attributes");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root;
        root = inflater.inflate(R.layout.product_description, container, false);
        TextView txtDescriptionTitle = (TextView) root.findViewById(R.id.txt_description_title);
        TextView txtDescription = (TextView) root.findViewById(R.id.txt_description);
        txtDescriptionTitle.setText(String.format(getResources().getString(R.string.description_title), attributes.get("name")));
        txtDescription.setText(ProductInfo.compatFromHtml(attributes.get("description")));

        return root;

    }
}
