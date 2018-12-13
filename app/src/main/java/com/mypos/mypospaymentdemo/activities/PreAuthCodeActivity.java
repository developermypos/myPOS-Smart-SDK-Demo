package com.mypos.mypospaymentdemo.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.KeyboardHandler;

public class PreAuthCodeActivity extends AppCompatActivity {

    private KeyboardHandler keyboardHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.activity_amount);

        TextView title = (TextView) findViewById(R.id.title_text);

        title.setText(R.string.preauth_code);
        findViewById(R.id.currency_layout).setVisibility(View.GONE);
        findViewById(R.id.code_layout).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.code_text)).setHint("******");

        String lastCode = "";
        if (keyboardHandler != null)
            lastCode = keyboardHandler.getCode();

        keyboardHandler = new KeyboardHandler(this, findViewById(R.id.root_view), KeyboardHandler.INPUT_TYPE_CODE, true, 6);
        keyboardHandler.setCode(lastCode);

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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        init();
    }
}
