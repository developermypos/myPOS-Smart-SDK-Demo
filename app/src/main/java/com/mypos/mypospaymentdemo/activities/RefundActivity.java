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
import com.mypos.smartsdk.MyPOSRefund;

import java.util.UUID;

public class RefundActivity extends AppCompatActivity {

    MyPOSRefund.Builder refundBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        refundBuilder = MyPOSRefund.builder();
        refundBuilder.foreignTransactionId(UUID.randomUUID().toString());
        refundBuilder.currency(Currency.valueOf(TerminalData.posinfo.getCurrencyName()));
        refundBuilder.printCustomerReceipt(PreferencesManager.getInstance().getCustomerReceiptMode());
        refundBuilder.printMerchantReceipt(PreferencesManager.getInstance().getMerchantReceiptMode());

        if (getIntent().hasExtra("tran_spec")) {
            int transpec = getIntent().getIntExtra("tran_spec", Utils.TRANSACTION_SPEC_REGULAR);
            if (transpec == Utils.TRANSACTION_SPEC_MOTO)
                refundBuilder.motoTransaction(true);
            else if (transpec == Utils.TRANSACTION_SPEC_GIFTCARD)
                refundBuilder.giftCardTransaction(true);
        }

        startActivityForResult(new Intent(this, AmountActivity.class), Utils.AMOUNT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.REFUND_REQUEST_CODE) {
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
                refundBuilder.refundAmount(data.getDoubleExtra("product_amount", 0.0D));

            MyPOSAPI.openRefundActivity(this, refundBuilder.build(), Utils.REFUND_REQUEST_CODE, PreferencesManager.getInstance().getSkipConfirmationScreenflag());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
