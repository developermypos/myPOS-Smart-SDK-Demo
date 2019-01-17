package com.mypos.mypospaymentdemo.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.widget.Toast;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.fragments.AmountFragment;
import com.mypos.mypospaymentdemo.fragments.CredentialFragment;
import com.mypos.mypospaymentdemo.util.IFragmentResult;
import com.mypos.mypospaymentdemo.util.TerminalData;
import com.mypos.mypospaymentdemo.util.Utils;
import com.mypos.smartsdk.Currency;
import com.mypos.smartsdk.MyPOSAPI;
import com.mypos.smartsdk.MyPOSPaymentRequest;

public class PaymentRequestActivity extends AppCompatActivity implements IFragmentResult {

    MyPOSPaymentRequest.Builder paymentRequestBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        paymentRequestBuilder = MyPOSPaymentRequest.builder();
        paymentRequestBuilder.currency(Currency.valueOf(TerminalData.posinfo.getCurrencyName()));

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

        if (requestCode == Utils.PAYMENT_REQUEST_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        }
    }

    private void gotoNextStep(int prevRequestCode, Intent data) {
        if (prevRequestCode == Utils.AMOUNT_REQUEST_CODE) {
            if (data.hasExtra("product_amount"))
                paymentRequestBuilder.productAmount(data.getDoubleExtra("product_amount", 0.0D));

           addFragment(new CredentialFragment( Utils.CREDENTIAL_REQUEST_CODE, this));
        }
        else
        if (prevRequestCode == Utils.CREDENTIAL_REQUEST_CODE) {
            String credential = data.getStringExtra("credential");

            if (Patterns.EMAIL_ADDRESS.matcher(credential).matches())
                paymentRequestBuilder.eMail(credential);
            else
            if (Patterns.PHONE.matcher(credential).matches())
                paymentRequestBuilder.GSM(credential);
            else {
                Toast.makeText(this, "Invalid e-mail/phone number", Toast.LENGTH_LONG).show();
                addFragment(new CredentialFragment( Utils.CREDENTIAL_REQUEST_CODE, this));
                return;
            }

            MyPOSAPI.createPaymentRequest(this, paymentRequestBuilder.build(), Utils.PAYMENT_REQUEST_REQUEST_CODE);
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
