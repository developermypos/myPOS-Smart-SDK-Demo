package com.mypos.mypospaymentdemo.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.fragments.AmountFragment;
import com.mypos.mypospaymentdemo.fragments.ReferenceNumberFragment;
import com.mypos.mypospaymentdemo.util.IFragmentResult;
import com.mypos.mypospaymentdemo.util.IPreferences;
import com.mypos.mypospaymentdemo.util.TerminalData;
import com.mypos.mypospaymentdemo.util.Utils;
import com.mypos.smartsdk.Currency;
import com.mypos.smartsdk.MyPOSAPI;
import com.mypos.smartsdk.MyPOSPreauthorization;
import com.mypos.smartsdk.ReferenceType;

import java.util.UUID;

public class PreAuthActivity extends AppCompatActivity implements IFragmentResult {

    MyPOSPreauthorization.Builder preauthBuilder;
    private IPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        preferences = (IPreferences) getIntent().getSerializableExtra("preferences");

        if (preferences == null)
            preferences = Utils.getDefaultPreferences();

        preauthBuilder = MyPOSPreauthorization.builder();
        preauthBuilder.foreignTransactionId(UUID.randomUUID().toString());
        preauthBuilder.currency(Currency.valueOf(TerminalData.posinfo.getCurrencyName()));
        preauthBuilder.printCustomerReceipt(preferences.getCustomerReceiptMode());
        preauthBuilder.printMerchantReceipt(preferences.getMerchantReceiptMode());
        preauthBuilder.eReceiptReceiver(preferences.getEReceiptReceiver());
        if (preferences.getAppColor() != null) {
            preauthBuilder.baseColor(Color.parseColor(preferences.getAppColor()));
        }

        if (getIntent().hasExtra("tran_spec")) {
            int transpec = getIntent().getIntExtra("tran_spec", Utils.TRANSACTION_SPEC_REGULAR);
            if (transpec == Utils.TRANSACTION_SPEC_MOTO)
                preauthBuilder.motoTransaction(true);
        }

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

        if (requestCode == Utils.PREAUTH_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        }
    }

    private void gotoNextStep(int prevRequestCode, Intent data) {
        if (prevRequestCode == Utils.AMOUNT_REQUEST_CODE) {
            if (data.hasExtra("product_amount"))
                preauthBuilder.productAmount(data.getDoubleExtra("product_amount", 0.0D));

            if (preferences.getReferenceNumberMode() != ReferenceType.OFF) {
                ReferenceNumberFragment fragment = new ReferenceNumberFragment(Utils.REFERENCE_NUMBER_REQUEST_CODE, this);
                fragment.setReferenceNumberMode(preferences.getReferenceNumberMode());
                addFragment(fragment);
            }
            else
                gotoNextStep(Utils.REFERENCE_NUMBER_REQUEST_CODE, data);
        }
        else
        if (prevRequestCode == Utils.REFERENCE_NUMBER_REQUEST_CODE) {
            if (data.hasExtra("reference_number")) {
                preauthBuilder.reference(data.getStringExtra("reference_number"), preferences.getReferenceNumberMode());
            }
            MyPOSAPI.createPreauthorization(this, preauthBuilder.build(), Utils.PREAUTH_REQUEST_CODE, preferences.getSkipConfirmationScreenflag());
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
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
