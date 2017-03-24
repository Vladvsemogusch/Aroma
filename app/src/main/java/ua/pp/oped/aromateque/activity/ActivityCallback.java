package ua.pp.oped.aromateque.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.base_activity.SearchAppbarActivity;
import ua.pp.oped.aromateque.utility.Constants;


public class ActivityCallback extends SearchAppbarActivity {
    private static final int CALL = 0;
    private static final int SEND_EMAIL = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView infoListView = (ListView) findViewById(R.id.info_listview);
        final String[] titles = {getResources().getString(R.string.call),
                getResources().getString(R.string.send_email)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        infoListView.setAdapter(adapter);
        infoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                switch (i) {
                    case CALL:
                        intent = new Intent(Intent.ACTION_DIAL);
                        String uri = "tel:" + Constants.AROMATEQUE_PHONE;
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                        break;
                    case SEND_EMAIL:
                        String[] emails = {Constants.AROMATEQUE_EMAIL};
                        composeEmail(emails);
                        break;
                }

            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_callback_top_layout;
    }

    public void composeEmail(String[] addresses) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_email_clients), Toast.LENGTH_SHORT).show();
        }
    }


}
