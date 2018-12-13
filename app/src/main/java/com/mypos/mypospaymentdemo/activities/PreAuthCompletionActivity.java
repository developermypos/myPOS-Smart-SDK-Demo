package com.mypos.mypospaymentdemo.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.PreferencesManager;
import com.mypos.mypospaymentdemo.util.TerminalData;
import com.mypos.mypospaymentdemo.util.Utils;
import com.mypos.smartsdk.Currency;
import com.mypos.smartsdk.MyPOSAPI;
import com.mypos.smartsdk.MyPOSPreauthorizationCompletion;

import java.util.UUID;

public class PreAuthCompletionActivity extends AppCompatActivity {

    MyPOSPreauthorizationCompletion.Builder preauthBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        preauthBuilder = MyPOSPreauthorizationCompletion.builder();
        preauthBuilder.foreignTransactionId(UUID.randomUUID().toString());
        preauthBuilder.currency(Currency.valueOf(TerminalData.posinfo.getCurrencyName()));
        preauthBuilder.printCustomerReceipt(PreferencesManager.getInstance().getCustomerReceiptMode());
        preauthBuilder.printMerchantReceipt(PreferencesManager.getInstance().getMerchantReceiptMode());

        startActivityForResult(new Intent(this, PreAuthCodeActivity.class), Utils.PREAUTH_CODE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.PREAUTH_COMPLETION_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        } else if (resultCode == Activity.RESULT_OK) {
            gotoNextStep(requestCode, data);
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void gotoNextStep(int prevRequestCode, Intent data) {
        if (prevRequestCode == Utils.PREAUTH_CODE_REQUEST_CODE) {
            if (data.hasExtra("preauth_code"))
                preauthBuilder.preauthorizationCode(data.getStringExtra("preauth_code"));

            startActivityForResult(new Intent(this, AmountActivity.class), Utils.AMOUNT_REQUEST_CODE);
        } else if (prevRequestCode == Utils.AMOUNT_REQUEST_CODE) {
            if (data.hasExtra("product_amount")) {
                preauthBuilder.productAmount(data.getDoubleExtra("product_amount", 0.0D));
            }
            MyPOSAPI.completePreauthorization(this, preauthBuilder.build(), Utils.PREAUTH_COMPLETION_REQUEST_CODE, PreferencesManager.getInstance().getSkipConfirmationScreenflag());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}