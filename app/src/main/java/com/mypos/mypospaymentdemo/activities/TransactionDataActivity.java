package com.mypos.mypospaymentdemo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mypos.mypospaymentdemo.R;

public class TransactionDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_data);

        TextView container = (TextView) findViewById(R.id.container_layout);
        container.setText(bundleAsString(getIntent().getExtras()));
    }

    private String bundleAsString(Bundle data) {
        return bundleAsString(data, false);
    }

    private String bundleAsString(Bundle data, boolean indent) {
        String retString = "";
        if (data != null) {
            for (String s : data.keySet()) {
                Object extra = data.get(s);
                if (extra instanceof Bundle) {
                    retString += "\n\tBundle \"" + s + "\":\n" + bundleAsString((Bundle) extra, true);
                } else {
                    retString += (String.format("%s%s => %s\n", indent ? "\t\t" : "\t", s, extra));
                }
            }
        }
        return retString;
    }

}
