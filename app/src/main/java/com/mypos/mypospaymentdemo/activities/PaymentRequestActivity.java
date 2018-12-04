package com.mypos.mypospaymentdemo.activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.PersistentDataManager;
import com.mypos.mypospaymentdemo.util.TerminalData;
import com.mypos.mypospaymentdemo.util.Utils;
import com.mypos.smartsdk.Currency;
import com.mypos.smartsdk.MyPOSAPI;
import com.mypos.smartsdk.MyPOSPaymentRequest;
import com.mypos.smartsdk.MyPOSPreauthorization;
import com.mypos.smartsdk.ReferenceType;

import java.util.UUID;

public class PaymentRequestActivity extends AppCompatActivity {

    MyPOSPaymentRequest.Builder paymentRequestBuilder;
    ValueAnimator anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        final View view = findViewById(R.id.background);

        final float[] from = new float[3], to =   new float[3];

        Color.colorToHSV(Color.parseColor("#ff00b2c1"), from);
        Color.colorToHSV(Color.parseColor("#ff0077c1"), to);

        anim = ValueAnimator.ofFloat(0, 1);
        anim.setDuration(1500);

        final float[] hsv  = new float[3];
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();

                view.setBackgroundColor(Color.HSVToColor(hsv));
            }
        });

        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.start();

        paymentRequestBuilder = MyPOSPaymentRequest.builder();
        paymentRequestBuilder.currency(Currency.valueOf(TerminalData.posinfo.getCurrencyName()));

        startActivityForResult(new Intent(this, AmountActivity.class), Utils.AMOUNT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.PAYMENT_REQUEST_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        }
        else if (resultCode == Activity.RESULT_OK) {
            gotoNextStep(requestCode, data);
        }
        else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void gotoNextStep(int prevRequestCode, Intent data) {
        if (prevRequestCode == Utils.AMOUNT_REQUEST_CODE) {
            if (data.hasExtra("product_amount"))
                paymentRequestBuilder.productAmount(data.getDoubleExtra("product_amount", 0.0D));

            startActivityForResult(new Intent(this, CredentialActivity.class), Utils.CREDENTIAL_REQUEST_CODE);
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
                startActivityForResult(new Intent(this, CredentialActivity.class), Utils.CREDENTIAL_REQUEST_CODE);
                return;
            }

            MyPOSAPI.createPaymentRequest(this, paymentRequestBuilder.build(), Utils.PAYMENT_REQUEST_REQUEST_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (anim != null) {
            anim.pause();
            anim.removeAllUpdateListeners();
            anim.cancel();
            anim = null;
        }
    }
}
