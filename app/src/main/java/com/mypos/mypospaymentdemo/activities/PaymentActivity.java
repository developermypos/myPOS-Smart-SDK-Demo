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
import com.mypos.mypospaymentdemo.fragments.MultiOperatorCodeFragment;
import com.mypos.mypospaymentdemo.fragments.ReferenceNumberFragment;
import com.mypos.mypospaymentdemo.fragments.TipAmountFragment;
import com.mypos.mypospaymentdemo.util.IFragmentResult;
import com.mypos.mypospaymentdemo.util.IPreferences;
import com.mypos.mypospaymentdemo.util.TerminalData;
import com.mypos.mypospaymentdemo.util.Utils;
import com.mypos.smartsdk.Currency;
import com.mypos.smartsdk.MyPOSAPI;
import com.mypos.smartsdk.MyPOSPayment;
import com.mypos.smartsdk.ReferenceType;

import java.util.UUID;

public class PaymentActivity extends AppCompatActivity implements IFragmentResult {

    MyPOSPayment.Builder paymentBuilder;
    private IPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        preferences = (IPreferences) getIntent().getSerializableExtra("preferences");

        if (preferences == null)
            preferences = Utils.getDefaultPreferences();

        paymentBuilder = MyPOSPayment.builder();
        paymentBuilder.foreignTransactionId(UUID.randomUUID().toString());
        paymentBuilder.currency(Currency.valueOf(TerminalData.posinfo.getCurrencyName()));
        paymentBuilder.printCustomerReceipt(preferences.getCustomerReceiptMode());
        paymentBuilder.printMerchantReceipt(preferences.getMerchantReceiptMode());
        paymentBuilder.mastercardSonicBranding(preferences.getMCSonicEnabled());

        if (getIntent().hasExtra("tran_spec")) {
            int transpec = getIntent().getIntExtra("tran_spec", Utils.TRANSACTION_SPEC_REGULAR);
            if (transpec == Utils.TRANSACTION_SPEC_MOTO)
                paymentBuilder.motoTransaction(true);
            else if (transpec == Utils.TRANSACTION_SPEC_GIFTCARD)
                paymentBuilder.giftCardTransaction(true);
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

        if (requestCode == Utils.PAYMENT_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        }
    }

    private void gotoNextStep(int prevRequestCode, Intent data) {
        if (prevRequestCode == Utils.AMOUNT_REQUEST_CODE) {
            if (data.hasExtra("product_amount"))
                paymentBuilder.productAmount(data.getDoubleExtra("product_amount", 0.0D));

            if (preferences.getTipEnabled())
                addFragment(new TipAmountFragment(Utils.TIP_REQUEST_CODE, this));
            else
                gotoNextStep(Utils.TIP_REQUEST_CODE, data);
        }
        else
        if (prevRequestCode == Utils.TIP_REQUEST_CODE) {
            if (data.getDoubleExtra("tip_amount", 0.0D) > 0.0D) {
                paymentBuilder.tippingModeEnabled(true);
                paymentBuilder.tipAmount(data.getDoubleExtra("tip_amount", 0.0D));
            }

            if (preferences.getMultiOperatorModeEnabled())
                addFragment(new MultiOperatorCodeFragment(Utils.MULTI_OPERATOR_CODE_REQUEST_CODE, this));
            else
                gotoNextStep(Utils.MULTI_OPERATOR_CODE_REQUEST_CODE, data);
        }
        else
        if (prevRequestCode == Utils.MULTI_OPERATOR_CODE_REQUEST_CODE) {
            if (data.hasExtra("operator_code"))
                paymentBuilder.operatorCode(data.getStringExtra("operator_code"));

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
                paymentBuilder.reference(data.getStringExtra("reference_number"), preferences.getReferenceNumberMode());
            }
            MyPOSAPI.openPaymentActivity(this, paymentBuilder.build(), Utils.PAYMENT_REQUEST_CODE, preferences.getSkipConfirmationScreenflag());
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
