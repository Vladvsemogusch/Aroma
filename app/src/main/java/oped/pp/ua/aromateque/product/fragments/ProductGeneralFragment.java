package oped.pp.ua.aromateque.product.fragments;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oped.pp.ua.aromateque.DownloadImageTask;
import oped.pp.ua.aromateque.R;
import oped.pp.ua.aromateque.Utility;

public class ProductGeneralFragment extends Fragment {

    private Map<String, String> listAttrs;
    private Map<String, String> attributes;
    private Map<String, String> notes;
    private List imageUrls;

    public static ProductGeneralFragment newInstance(HashMap<String, String> listAttrs, HashMap<String, String> attributes, HashMap<String, String> notes, ArrayList<String> imageUrls) {
        Bundle args = new Bundle();
        args.putSerializable("listAttrs", listAttrs);
        args.putSerializable("attributes", attributes);
        args.putSerializable("notes", notes);
        args.putSerializable("imageUrls", imageUrls);
        ProductGeneralFragment fragment = new ProductGeneralFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAttrs = (HashMap<String, String>) getArguments().getSerializable("listAttrs");
        attributes = (HashMap<String, String>) getArguments().getSerializable("attributes");
        notes = (HashMap<String, String>) getArguments().getSerializable("notes");
        imageUrls = (ArrayList<String>) getArguments().getSerializable("imageUrls");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.product_general, container, false);

        Resources res = getResources();
        ScrollView productScrollviewMain = (ScrollView) view.findViewById(R.id.product_scrollview_main);
        productScrollviewMain.setForegroundGravity(Gravity.END);
        //HashMap<String, String> attributes = product.getAttributes();
        //HashMap<String, String> listAttrs = product.getListAttrs();
        ViewPager productImgViewpager = (ViewPager) view.findViewById(R.id.product_img_viewpager);
        ImageView imgGender = (ImageView) view.findViewById(R.id.img_gender);
        TextView txtBrandProductName = (TextView) view.findViewById(R.id.txt_brand_product_name);
        TextView txtProductType = (TextView) view.findViewById(R.id.txt_product_type);
        TextView txtReviewsCount = (TextView) view.findViewById(R.id.txt_reviews_count);
        TextView txtGender = (TextView) view.findViewById(R.id.txt_gender);
        TextView txtTopNotesContent = (TextView) view.findViewById(R.id.txt_top_notes_content);
        TextView txtMiddleNotesContent = (TextView) view.findViewById(R.id.txt_middle_notes_content);
        TextView txtBaseNotesContent = (TextView) view.findViewById(R.id.txt_base_notes_content);
        TextView txtFragranceNotes = (TextView) view.findViewById(R.id.txt_fragrance_notes);
        TextView txtSku = (TextView) view.findViewById(R.id.txt_sku);
        TextView txtShortDescription = (TextView) view.findViewById(R.id.txt_short_description);
        TextView productFullPrice = (TextView) view.findViewById(R.id.product_full_price);
        TextView productDiscountedPrice = (TextView) view.findViewById(R.id.product_discounted_price);
        LinearLayout containerAttrNameList = (LinearLayout) view.findViewById(R.id.container_attr_name_list);
        LinearLayout containerAttrValueList = (LinearLayout) view.findViewById(R.id.container_attr_value_list);
        LinearLayout containerTopNotesList = (LinearLayout) view.findViewById(R.id.container_top_notes_list);
        LinearLayout containerMiddleNotesList = (LinearLayout) view.findViewById(R.id.container_middle_notes_list);
        LinearLayout containerBaseNotesList = (LinearLayout) view.findViewById(R.id.container_base_notes_list);
        LinearLayout containerNotes = (LinearLayout) view.findViewById(R.id.container_notes);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);

        String stringFullPrice = attributes.get("price");
        stringFullPrice = stringFullPrice.substring(0, stringFullPrice.indexOf('.'));
        String stringDiscount = attributes.get("discount");
        stringDiscount = stringDiscount.substring(0, stringDiscount.indexOf('%'));
        long discountedPrice = Math.round(Integer.parseInt(stringFullPrice) * 0.01 * (100 - Integer.parseInt(stringDiscount)));

        productFullPrice.setText(String.format(res.getString(R.string.product_price), stringFullPrice));
        productFullPrice.setPaintFlags(productFullPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        productDiscountedPrice.setText(String.format(res.getString(R.string.product_price), String.valueOf(discountedPrice)));

        txtShortDescription.setText(attributes.get("short_description"));
        class ImgPagerAdapter extends PagerAdapter {

            private Context context;
            private LayoutInflater layoutInflater;
            private List<String> productImgList = imageUrls;

            private ImgPagerAdapter(Context context) {
                this.context = context;
                layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public int getCount() {
                return productImgList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == ((ImageView) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View itemView = layoutInflater.inflate(R.layout.img_pager_item, container, false);
                ImageView imgView = (ImageView) itemView.findViewById(R.id.img_view);
                new DownloadImageTask(imgView).execute(productImgList.get(position));
                container.addView(itemView);
                return itemView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((ImageView) object);
            }
        }
        productImgViewpager.setAdapter(new ImgPagerAdapter(getContext()));
        CirclePageIndicator viewPagerIndicator = (CirclePageIndicator) view.findViewById(R.id.viewpager_indicator);
        viewPagerIndicator.setViewPager(productImgViewpager);
        Spanned brandProductName = Utility.compatFromHtml(String.format(res.getString(R.string.brand_name), attributes.get("shopbybrand_brand"), "<b>" + attributes.get("name") + "</b>"));
        txtBrandProductName.setText(brandProductName);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(brandProductName);
        }
        txtProductType.setText(attributes.get("typeofproduct").toLowerCase());
        String marks = "";
        String reviews = attributes.get("total_reviews_count");
        int lastDigit = Integer.parseInt(reviews.substring(reviews.length() - 1));
        switch (lastDigit) {
            case 1:
                marks = "ОЦЕНКА";
                break;
            case 2:
            case 3:
            case 4:
                marks = "ОЦЕНКИ";
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 0:
                marks = "ОЦЕНОК";
        }
        txtReviewsCount.setText(String.format(res.getString(R.string.reviews_count), reviews, marks));
        txtGender.setText(attributes.get("gender").toUpperCase());
        switch (attributes.get("gender")) {
            case "для детей":
                imgGender.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.ico_child, null));
                break;
            case "для женщин":
                imgGender.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.ico_women, null));
                break;
            case "для мужчин":
                imgGender.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.ico_man, null));
                break;
            case "для женщин и мужчин":
                imgGender.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.ico_man_woman, null));
                break;
        }

        txtSku.setText(String.format(res.getString(R.string.sku), attributes.get("sku")));
        if (attributes.get("rating_summary") != null) {
            ratingBar.setRating(5.0f / 100 * Integer.parseInt(attributes.get("rating_summary")));
        } else {
            ratingBar.setVisibility(View.GONE);
        }
        //fill first attribute list
        TextView attrNameView;  //reusable
        TextView attrValueView; //reusable
        if (!listAttrs.get("volume").equals("No")) {
            attrNameView = new TextView(getContext());
            attrNameView.setText("Объем");
            containerAttrNameList.addView(attrNameView);
            attrValueView = new TextView(getContext());
            attrValueView.setText(listAttrs.get("volume"));
            containerAttrValueList.addView(attrValueView);
        }
        if (listAttrs.get("perfumer") != null) {
            if (listAttrs.get("perfumer") != null || !listAttrs.get("perfumer").equals("No")) {
                attrNameView = new TextView(getContext());
                attrNameView.setText("Парфюмер");
                containerAttrNameList.addView(attrNameView);
                attrValueView = new TextView(getContext());
                attrValueView.setText(listAttrs.get("perfumer"));
                containerAttrValueList.addView(attrValueView);
            }
        }
        if (!attributes.get("country").equals("No")) {
            attrNameView = new TextView(getContext());
            attrNameView.setText("Страна");
            containerAttrNameList.addView(attrNameView);
            attrValueView = new TextView(getContext());
            if (!attributes.get("year_of_manufacture").equals("No")) {
                attrValueView.setText(String.format(getString(R.string.country_year), attributes.get("country"), attributes.get("year_of_manufacture")));
            } else {
                attrValueView.setText(attributes.get("country"));
            }
            containerAttrValueList.addView(attrValueView);
        }
        //fill notes
        String stringNotes;//reusable
        int dontHaveNotes = 0; // if ==3 -> no notes for product
        if (!notes.get("topnotes").equals("false")) {
            stringNotes = Utility.checkAndCut(notes.get("topnotes"));
            txtTopNotesContent.setText(stringNotes);
        } else {
            containerTopNotesList.setVisibility(View.GONE);
            dontHaveNotes++;
        }
        if (!notes.get("middlenotes").equals("false")) {
            stringNotes = Utility.checkAndCut(notes.get("middlenotes"));
            txtMiddleNotesContent.setText(stringNotes);
        } else {
            containerMiddleNotesList.setVisibility(View.GONE);
            dontHaveNotes++;
        }
        if (!notes.get("basenotes").equals("false")) {
            stringNotes = Utility.checkAndCut(notes.get("basenotes"));
            txtBaseNotesContent.setText(stringNotes);
        } else {
            containerBaseNotesList.setVisibility(View.GONE);
            dontHaveNotes++;
        }
        if (dontHaveNotes == 3) {
            txtFragranceNotes.setVisibility(View.GONE);
            containerNotes.setVisibility(View.GONE);
        }


        return view;

    }
}
