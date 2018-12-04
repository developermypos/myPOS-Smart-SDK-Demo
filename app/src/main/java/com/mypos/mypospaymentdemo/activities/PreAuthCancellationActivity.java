package com.mypos.mypospaymentdemo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.PersistentDataManager;
import com.mypos.mypospaymentdemo.util.Utils;
import com.mypos.smartsdk.MyPOSAPI;
import com.mypos.smartsdk.MyPOSPreauthorizationCancellation;

import java.util.UUID;

public class PreAuthCancellationActivity extends AppCompatActivity {

    MyPOSPreauthorizationCancellation.Builder preauthBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        preauthBuilder = MyPOSPreauthorizationCancellation.builder();
        preauthBuilder.foreignTransactionId(UUID.randomUUID().toString());
        preauthBuilder.printCustomerReceipt(PersistentDataManager.getInstance().getCustomerReceiptMode());
        preauthBuilder.printMerchantReceipt(PersistentDataManager.getInstance().getMerchantReceiptMode());

        startActivityForResult(new Intent(this, PreAuthCodeActivity.class), Utils.PREAUTH_CODE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.PREAUTH_CANCELLATION_REQUEST_CODE) {
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

            MyPOSAPI.cancelPreauthorization(this, preauthBuilder.build(), Utils.PREAUTH_CANCELLATION_REQUEST_CODE, PersistentDataManager.getInstance().getSkipConfirmationScreenflag());
        }
    }
}