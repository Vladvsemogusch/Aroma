package ua.pp.oped.aromateque.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.base_activities.SearchAppbarActivity;
import ua.pp.oped.aromateque.fragments.info.About;
import ua.pp.oped.aromateque.fragments.info.OurStores;
import ua.pp.oped.aromateque.fragments.info.PaymentAndDelivery;
import ua.pp.oped.aromateque.fragments.info.QuestionAnswer;


public class ActivityInfo extends SearchAppbarActivity {
    private FragmentManager fragmentManager;
    private static final int ABOUT = 0;
    private static final int OUR_STORES = 1;
    private static final int PAYMENT_AND_DELIVERY = 2;
    private static final int Q_A = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView infoListView = (ListView) findViewById(R.id.info_listview);
        final String[] titles = {getResources().getString(R.string.about),
                getResources().getString(R.string.our_stores),
                getResources().getString(R.string.payment_and_delivery),
                getResources().getString(R.string.q_a)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        infoListView.setAdapter(adapter);
        fragmentManager = getSupportFragmentManager();
        infoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment fragment;
                switch (i) {
                    case ABOUT:
                        getSupportActionBar().setTitle(R.string.about);
                        fragment = About.newInstance();
                        transaction.add(R.id.info_framelayout, fragment);
                        break;
                    case OUR_STORES:
                        getSupportActionBar().setTitle(R.string.our_stores);
                        fragment = OurStores.newInstance();
                        transaction.add(R.id.info_framelayout, fragment);
                        break;
                    case PAYMENT_AND_DELIVERY:
                        getSupportActionBar().setTitle(R.string.payment_and_delivery);
                        fragment = PaymentAndDelivery.newInstance();
                        transaction.add(R.id.info_framelayout, fragment);
                        break;
                    case Q_A:
                        getSupportActionBar().setTitle(R.string.q_a);
                        fragment = QuestionAnswer.newInstance();
                        transaction.add(R.id.info_framelayout, fragment);
                        break;
                }
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_info_top_layout;
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            getSupportActionBar().setTitle(R.string.info);
        } else {
            super.onBackPressed();
        }
    }

}
