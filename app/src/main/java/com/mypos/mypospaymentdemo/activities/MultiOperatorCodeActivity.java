package com.mypos.mypospaymentdemo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.KeyboardHandler;

public class MultiOperatorCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount);

        TextView title = (TextView) findViewById(R.id.title_text);

        title.setText(R.string.operator_code);
        findViewById(R.id.currency_layout).setVisibility(View.GONE);
        findViewById(R.id.code_layout).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.code_text)).setHint("****");

        final KeyboardHandler keyboardHandler = new KeyboardHandler(this, findViewById(R.id.root_view), KeyboardHandler.INPUT_TYPE_CODE, true, 4);

        findViewById(R.id.charge_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!keyboardHandler.getCode().isEmpty()){
                    getIntent().putExtra("operator_code", keyboardHandler.getCode());
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            }
        });
    }
}
