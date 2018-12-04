package com.mypos.mypospaymentdemo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.PersistentDataManager;
import com.mypos.mypospaymentdemo.util.TerminalData;
import com.mypos.mypospaymentdemo.util.Utils;
import com.mypos.smartsdk.Currency;
import com.mypos.smartsdk.MyPOSAPI;
import com.mypos.smartsdk.MyPOSPayment;
import com.mypos.smartsdk.ReferenceType;

import java.util.UUID;

public class PaymentActivity extends AppCompatActivity {

    MyPOSPayment.Builder paymentBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        paymentBuilder = MyPOSPayment.builder();
        paymentBuilder.foreignTransactionId(UUID.randomUUID().toString());
        paymentBuilder.currency(Currency.valueOf(TerminalData.posinfo.getCurrencyName()));
        paymentBuilder.printCustomerReceipt(PersistentDataManager.getInstance().getCustomerReceiptMode());
        paymentBuilder.printMerchantReceipt(PersistentDataManager.getInstance().getMerchantReceiptMode());

        if (getIntent().hasExtra("tran_spec")) {
            int transpec = getIntent().getIntExtra("tran_spec", Utils.TRANSACTION_SPEC_REGULAR);
            if (transpec == Utils.TRANSACTION_SPEC_MOTO)
                paymentBuilder.motoTransaction(true);
            else if (transpec == Utils.TRANSACTION_SPEC_GIFTCARD)
                paymentBuilder.giftCardTransaction(true);
        }

        startActivityForResult(new Intent(this, AmountActivity.class), Utils.AMOUNT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.PAYMENT_REQUEST_CODE) {
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
                paymentBuilder.productAmount(data.getDoubleExtra("product_amount", 0.0D));

            if (PersistentDataManager.getInstance().getTipEnabled())
                startActivityForResult(new Intent(this, TipAmountActivity.class), Utils.TIP_REQUEST_CODE);
            else
                gotoNextStep(Utils.TIP_REQUEST_CODE, data);
        }
        else
        if (prevRequestCode == Utils.TIP_REQUEST_CODE) {
            if (data.getDoubleExtra("tip_amount", 0.0D) > 0.0D) {
                paymentBuilder.tippingModeEnabled(true);
                paymentBuilder.tipAmount(data.getDoubleExtra("tip_amount", 0.0D));
            }

            if (PersistentDataManager.getInstance().getMultiOperatorModeEnabled())
                startActivityForResult(new Intent(this, MultiOperatorCodeActivity.class), Utils.MULTI_OPERATOR_CODE_REQUEST_CODE);
            else
                gotoNextStep(Utils.MULTI_OPERATOR_CODE_REQUEST_CODE, data);
        }
        else
        if (prevRequestCode == Utils.MULTI_OPERATOR_CODE_REQUEST_CODE) {
            if (data.hasExtra("operator_code"))
                paymentBuilder.operatorCode(data.getStringExtra("operator_code"));

            if (PersistentDataManager.getInstance().getReferenceNumberMode() != ReferenceType.OFF)
                startActivityForResult(new Intent(this, ReferenceNumberActivity.class), Utils.REFERENCE_NUMBER_REQUEST_CODE);
            else
                gotoNextStep(Utils.REFERENCE_NUMBER_REQUEST_CODE, data);
        }
        else
        if (prevRequestCode == Utils.REFERENCE_NUMBER_REQUEST_CODE) {
            if (data.hasExtra("reference_number")) {
                paymentBuilder.reference(data.getStringExtra("reference_number"), PersistentDataManager.getInstance().getReferenceNumberMode());
            }
            MyPOSAPI.openPaymentActivity(this, paymentBuilder.build(), Utils.PAYMENT_REQUEST_CODE, PersistentDataManager.getInstance().getSkipConfirmationScreenflag());
        }
    }
}
