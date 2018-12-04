package com.mypos.mypospaymentdemo.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.KeyboardHandler;
import com.mypos.mypospaymentdemo.util.TerminalData;

public class TipAmountActivity extends AppCompatActivity {

    private KeyboardHandler keyboardHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.activity_amount);

        TextView title = (TextView) findViewById(R.id.title_text);
        TextView currency = (TextView) findViewById(R.id.currency_text);

        title.setText(R.string.tip_amount);
        currency.setText(TerminalData.posinfo.getCurrencyName());

        String lastAmount = "0.00";
        if (keyboardHandler != null)
            lastAmount = keyboardHandler.getAmount();

        keyboardHandler = new KeyboardHandler(this, findViewById(R.id.root_view), KeyboardHandler.INPUT_TYPE_AMOUNT, true, 8);
        keyboardHandler.setAmount(lastAmount);

        findViewById(R.id.charge_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double amountDouble = 0.0D;
                try {
                    amountDouble = Double.parseDouble(keyboardHandler.getAmount());
                } catch (NumberFormatException ignored) {}

                getIntent().putExtra("tip_amount", amountDouble);
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        init();
    }
}
