package ua.pp.oped.aromateque.fragments.product;


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

import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;
import java.util.Map;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.data.db.DatabaseHelper;
import ua.pp.oped.aromateque.model.LongProduct;
import ua.pp.oped.aromateque.utility.ImageLoaderWrapper;
import ua.pp.oped.aromateque.utility.Utility;

public class ProductGeneralFragment extends Fragment {

    private Map<String, String> attributes;
    private List<String> imageUrls;

    public static ProductGeneralFragment newInstance(int productId) {
        Bundle args = new Bundle();
        args.putInt("id", productId);
        ProductGeneralFragment fragment = new ProductGeneralFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
        LongProduct product = dbHelper.deserializeProduct(getArguments().getInt("id"));
        attributes = product.getAttributes();
        imageUrls = product.getImageUrls();
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
        //HashMap<String, String> attributes = product.getListAttrs();
        final ViewPager productImgViewpager = (ViewPager) view.findViewById(R.id.product_img_viewpager);
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

        String fullPrice = attributes.get("price");
        fullPrice = fullPrice.substring(0, fullPrice.indexOf('.'));
        String discount = attributes.get("discount");
        String discountedPrice = Utility.getPriceWithDiscount(fullPrice, discount);
        productFullPrice.setText(String.format(res.getString(R.string.product_price), fullPrice));
        productFullPrice.setPaintFlags(productFullPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        productDiscountedPrice.setText(String.format(res.getString(R.string.product_price), discountedPrice));

        txtShortDescription.setText(attributes.get("short_description"));
        class ImgPagerAdapter extends PagerAdapter {

            private Context context;
            private LayoutInflater layoutInflater;
            private List<String> productImgList = imageUrls;

            private ImgPagerAdapter(Context context) {
                this.context = context;
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public int getCount() {
                return productImgList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView itemView = (ImageView) layoutInflater.inflate(R.layout.img_pager_item, container, false);
                //ImageView imgView = (ImageView) itemView.findViewById(R.id.img_view);
                Picasso.with(context).load(productImgList.get(position)).into(itemView);
                ImageLoaderWrapper.loadImage(context, itemView, productImgList.get(position));
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
        Spanned brandProductName = Utility.compatFromHtml(String.format(res.getString(R.string.brand_name), attributes.get("brand"), "<b>" + attributes.get("name") + "</b>"));
        txtBrandProductName.setText(brandProductName);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(brandProductName);
        }
        txtProductType.setText(attributes.get("type_of_product").toLowerCase());
        String mark = "";
        String reviews = attributes.get("reviews_count");
        int lastDigit = Integer.parseInt(reviews.substring(reviews.length() - 1));
        switch (lastDigit) {
            case 1:
                mark = "ОЦЕНКА";
                break;
            case 2:
            case 3:
            case 4:
                mark = "ОЦЕНКИ";
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 0:
                mark = "ОЦЕНОК";
        }
        txtReviewsCount.setText(String.format(res.getString(R.string.reviews_count), reviews, mark));
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
        if (!attributes.get("volume").equals("No")) {
            attrNameView = new TextView(getContext());
            attrNameView.setText("Объем");
            containerAttrNameList.addView(attrNameView);
            attrValueView = new TextView(getContext());
            attrValueView.setText(attributes.get("volume"));
            containerAttrValueList.addView(attrValueView);
        }
        if (attributes.get("perfumer") != null) {
            if (attributes.get("perfumer") != null || !attributes.get("perfumer").equals("No")) {
                attrNameView = new TextView(getContext());
                attrNameView.setText("Парфюмер");
                containerAttrNameList.addView(attrNameView);
                attrValueView = new TextView(getContext());
                attrValueView.setText(attributes.get("perfumer"));
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
        //fill attributes
        String stringNotes;//reusable
        int dontHaveNotes = 0; // if == 3 -> no attributes for product
        if (!attributes.get("top_notes").equals("false")) {
            stringNotes = Utility.checkAndCut(attributes.get("top_notes"));
            txtTopNotesContent.setText(stringNotes);
        } else {
            containerTopNotesList.setVisibility(View.GONE);
            dontHaveNotes++;
        }
        if (!attributes.get("middle_notes").equals("false")) {
            stringNotes = Utility.checkAndCut(attributes.get("middle_notes"));
            txtMiddleNotesContent.setText(stringNotes);
        } else {
            containerMiddleNotesList.setVisibility(View.GONE);
            dontHaveNotes++;
        }
        if (!attributes.get("base_notes").equals("false")) {
            stringNotes = Utility.checkAndCut(attributes.get("base_notes"));
            txtBaseNotesContent.setText(stringNotes);
        } else {
            containerBaseNotesList.setVisibility(View.GONE);
            dontHaveNotes++;
        }
        if (dontHaveNotes == 3) {
            txtFragranceNotes.setVisibility(View.GONE);     //TODO in case 2 or 1 notes are specified
            containerNotes.setVisibility(View.GONE);
        }


        return view;

    }
}
