package ua.pp.oped.aromateque.fragments.product;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.utility.ImageLoaderWrapper;
import ua.pp.oped.aromateque.utility.Utility;

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
        ImageView imgBrand = (ImageView) root.findViewById(R.id.img_brand);
        String brandImgUrl = attributes.get("brand_img_url");
        brandImgUrl = brandImgUrl.replace("\\", "/");
        // DEBUG
        ImageLoaderWrapper.loadImage(getContext(), imgBrand, brandImgUrl);
        txtDescriptionTitle.setText(attributes.get("name"));
//        txtDescriptionTitle.setText(String.format(getResources().getString(R.string.description_title), attributes.get("name")));
        String description = attributes.get("description");
        if (description.startsWith("<p style=\"text-align: justify;\">")) {
            description = description.replace("<p style=\"text-align: justify;\">", "");
            description = description.substring(0, description.length() - 4);
        }
        txtDescription.setText(Utility.compatFromHtml(description));

        return root;

    }
}
