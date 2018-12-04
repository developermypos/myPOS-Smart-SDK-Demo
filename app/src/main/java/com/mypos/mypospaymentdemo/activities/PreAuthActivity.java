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
import com.mypos.smartsdk.MyPOSPreauthorization;
import com.mypos.smartsdk.ReferenceType;

import java.util.UUID;

public class PreAuthActivity extends AppCompatActivity {

    MyPOSPreauthorization.Builder preauthBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        preauthBuilder = MyPOSPreauthorization.builder();
        preauthBuilder.foreignTransactionId(UUID.randomUUID().toString());
        preauthBuilder.currency(Currency.valueOf(TerminalData.posinfo.getCurrencyName()));
        preauthBuilder.printCustomerReceipt(PersistentDataManager.getInstance().getCustomerReceiptMode());
        preauthBuilder.printMerchantReceipt(PersistentDataManager.getInstance().getMerchantReceiptMode());

        if (getIntent().hasExtra("tran_spec")) {
            int transpec = getIntent().getIntExtra("tran_spec", Utils.TRANSACTION_SPEC_REGULAR);
            if (transpec == Utils.TRANSACTION_SPEC_MOTO)
                preauthBuilder.motoTransaction(true);
        }

        startActivityForResult(new Intent(this, AmountActivity.class), Utils.AMOUNT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.PREAUTH_REQUEST_CODE) {
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
                preauthBuilder.productAmount(data.getDoubleExtra("product_amount", 0.0D));

            if (PersistentDataManager.getInstance().getReferenceNumberMode() != ReferenceType.OFF)
                startActivityForResult(new Intent(this, ReferenceNumberActivity.class), Utils.REFERENCE_NUMBER_REQUEST_CODE);
            else
                gotoNextStep(Utils.REFERENCE_NUMBER_REQUEST_CODE, data);
        }
        else
        if (prevRequestCode == Utils.REFERENCE_NUMBER_REQUEST_CODE) {
            if (data.hasExtra("reference_number")) {
                preauthBuilder.reference(data.getStringExtra("reference_number"), PersistentDataManager.getInstance().getReferenceNumberMode());
            }
            MyPOSAPI.createPreauthorization(this, preauthBuilder.build(), Utils.PREAUTH_REQUEST_CODE, PersistentDataManager.getInstance().getSkipConfirmationScreenflag());
        }
    }
}
