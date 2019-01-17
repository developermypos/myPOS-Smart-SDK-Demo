package com.mypos.mypospaymentdemo.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.fragments.AmountFragment;
import com.mypos.mypospaymentdemo.util.IFragmentResult;
import com.mypos.mypospaymentdemo.util.IPreferences;
import com.mypos.mypospaymentdemo.util.TerminalData;
import com.mypos.mypospaymentdemo.util.Utils;
import com.mypos.smartsdk.Currency;
import com.mypos.smartsdk.MyPOSAPI;
import com.mypos.smartsdk.MyPOSGiftCardActivation;

import java.util.UUID;

public class GiftCardActivationActivity extends AppCompatActivity implements IFragmentResult {

    MyPOSGiftCardActivation.Builder mBuilder;
    private IPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        preferences = (IPreferences) getIntent().getSerializableExtra("preferences");

        if (preferences == null)
            preferences = Utils.getDefaultPreferences();

        mBuilder = MyPOSGiftCardActivation.builder();
        mBuilder.foreignTransactionId(UUID.randomUUID().toString());
        mBuilder.currency(Currency.valueOf(TerminalData.posinfo.getCurrencyName()));
        mBuilder.printCustomerReceipt(preferences.getCustomerReceiptMode());
        mBuilder.printMerchantReceipt(preferences.getMerchantReceiptMode());

        addFragment(new AmountFragment(Utils.AMOUNT_REQUEST_CODE, this));
    }

    private void addFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.background, fragment);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.GIFTCARD_ACTIVATION_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        }
    }

    private void gotoNextStep(int prevRequestCode, Intent data) {
        if (prevRequestCode == Utils.AMOUNT_REQUEST_CODE) {
            if (data.hasExtra("product_amount"))
                mBuilder.productAmount(data.getDoubleExtra("product_amount", 0.0D));

            MyPOSAPI.openGiftCardActivationActivity(this, mBuilder.build(), Utils.GIFTCARD_ACTIVATION_REQUEST_CODE, preferences.getSkipConfirmationScreenflag());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void setResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            gotoNextStep(requestCode, data);
        }
        else {
            GiftCardActivationActivity.this.setResult(RESULT_CANCELED);
            finish();
        }
    }
}
