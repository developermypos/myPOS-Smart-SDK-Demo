package com.mypos.mypospaymentdemo.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.KeyboardHandler;
import com.mypos.mypospaymentdemo.util.TerminalData;
import com.mypos.smartsdk.Currency;

public class AmountActivity extends AppCompatActivity {

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

        title.setText(R.string.amount);
        currency.setText(TerminalData.posinfo.getCurrencyName());

        boolean isDecimalAllowed = !Currency.ISK.name().equalsIgnoreCase(TerminalData.posinfo.getCurrencyName())             ;

        String lastAmount = isDecimalAllowed ? "0.00" : "0";
        if (keyboardHandler != null)
            lastAmount = keyboardHandler.getAmount();

        keyboardHandler = new KeyboardHandler(this, findViewById(R.id.root_view), KeyboardHandler.INPUT_TYPE_AMOUNT, isDecimalAllowed, 8);
        keyboardHandler.setAmount(lastAmount);

        findViewById(R.id.charge_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double amountDouble = 0.0D;
                try {
                    amountDouble = Double.parseDouble(keyboardHandler.getAmount());
                } catch (NumberFormatException ignored) {}

                if(amountDouble > 0.0D) {
                    getIntent().putExtra("product_amount", amountDouble);
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
