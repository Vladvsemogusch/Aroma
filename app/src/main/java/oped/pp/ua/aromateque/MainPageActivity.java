package oped.pp.ua.aromateque;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

import oped.pp.ua.aromateque.utility.DownloadImageTask;

public class MainPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        ViewPager viewpagerBanners = (ViewPager) findViewById(R.id.banner_viewpager);
        class ImgPagerAdapter extends PagerAdapter {

            private Context context;
            private LayoutInflater layoutInflater;
            private List<String> productImgList;

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
        viewpagerBanners.setAdapter(new ImgPagerAdapter(this));
        CirclePageIndicator viewPagerIndicator = (CirclePageIndicator) findViewById(R.id.viewpager_banner_indicator);
        viewPagerIndicator.setViewPager(viewpagerBanners);
    }
}
