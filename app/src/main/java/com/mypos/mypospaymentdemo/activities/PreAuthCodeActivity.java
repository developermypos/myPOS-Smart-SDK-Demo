package com.mypos.mypospaymentdemo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.KeyboardHandler;

public class PreAuthCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount);

        TextView title = (TextView) findViewById(R.id.title_text);

        title.setText(R.string.preauth_code);
        findViewById(R.id.currency_layout).setVisibility(View.GONE);
        findViewById(R.id.code_layout).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.code_text)).setHint("******");

        final KeyboardHandler keyboardHandler = new KeyboardHandler(this, findViewById(R.id.root_view), KeyboardHandler.INPUT_TYPE_CODE, true, 6);

        findViewById(R.id.charge_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!keyboardHandler.getCode().isEmpty()){
                    getIntent().putExtra("preauth_code", keyboardHandler.getCode());
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            }
        });
    }
}
