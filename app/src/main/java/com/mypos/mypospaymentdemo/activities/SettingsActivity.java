package com.mypos.mypospaymentdemo.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.PersistentDataManager;
import com.mypos.smartsdk.MyPOSUtil;
import com.mypos.smartsdk.ReferenceType;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat tippingToggle;
    private SwitchCompat multiOperatorToggle;
    private SwitchCompat skipConfScreenToggle;
    private TextView referenceNumberTypeTV;
    private RadioGroup customerReceiptTypes;
    private RadioGroup merchantReceiptTypes;

    private PersistentDataManager persistentDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        persistentDataManager = PersistentDataManager.getInstance();

        tippingToggle = (SwitchCompat) findViewById(R.id.tipping_toggle);
        multiOperatorToggle = (SwitchCompat) findViewById(R.id.multi_operator_toggle);
        skipConfScreenToggle = (SwitchCompat) findViewById(R.id.skip_conf_screen_toggle);
        referenceNumberTypeTV = (TextView) findViewById(R.id.reference_number_text);
        customerReceiptTypes = (RadioGroup) findViewById(R.id.customer_receipt_types);
        merchantReceiptTypes = (RadioGroup) findViewById(R.id.merchant_receipt_types);

        tippingToggle.setChecked(persistentDataManager.getTipEnabled());
        multiOperatorToggle.setChecked(persistentDataManager.getMultiOperatorModeEnabled());
        skipConfScreenToggle.setChecked(persistentDataManager.getSkipConfirmationScreenflag());
        referenceNumberTypeTV.setText(getReferenceNumberTxt(persistentDataManager.getReferenceNumberMode()));
        customerReceiptTypes.check(customerReceiptTypeToId(persistentDataManager.getCustomerReceiptMode()));
        merchantReceiptTypes.check(merchantReceiptTypeToId(persistentDataManager.getMerchantReceiptMode()));

        tippingToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                persistentDataManager.setTipEnabled(b);
            }
        });

        multiOperatorToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                persistentDataManager.setMultiOperatorModeEnabled(b);
            }
        });

        skipConfScreenToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                persistentDataManager.setSkipConfirmationScreenflag(b);
            }
        });

        findViewById(R.id.reference_number_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReferenceNumberDialog();
            }
        });

        customerReceiptTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                persistentDataManager.setCustomerReceiptMode(customerReceiptIdToType(i));
            }
        });

        merchantReceiptTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                persistentDataManager.setMerchantReceiptMode(merchantReceiptIdToType(i));
            }
        });
    }

    private void showReferenceNumberDialog(){
        final View view = LayoutInflater.from(this).inflate(R.layout.reference_type_dialog, null);
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);

        RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.radio_group);
        radioGroup.check(getLastSelectedMode(persistentDataManager.getReferenceNumberMode()));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if(checkedId == R.id.off_radio_button)
                    persistentDataManager.setReferenceNumberMode(ReferenceType.OFF);
                else if (checkedId == R.id.ref_number_radio_button)
                    persistentDataManager.setReferenceNumberMode(ReferenceType.REFERENCE_NUMBER);
                else if (checkedId == R.id.invoice_number_radio_button)
                    persistentDataManager.setReferenceNumberMode( ReferenceType.INVOICE_ID);
                else if (checkedId == R.id.reservation_number_radio_button)
                    persistentDataManager.setReferenceNumberMode(ReferenceType.RESERVATION_NUMBER);
                else if (checkedId == R.id.product_id_radio_button)
                    persistentDataManager.setReferenceNumberMode(ReferenceType.PRODUCT_ID);

                referenceNumberTypeTV.setText(getReferenceNumberTxt(persistentDataManager.getReferenceNumberMode()));

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private int getLastSelectedMode(int referenceType){
        if(referenceType == ReferenceType.OFF)
            return R.id.off_radio_button;
        else if(referenceType == ReferenceType.REFERENCE_NUMBER)
            return R.id.ref_number_radio_button;
        else if(referenceType == ReferenceType.INVOICE_ID)
            return R.id.invoice_number_radio_button;
        else if(referenceType == ReferenceType.RESERVATION_NUMBER)
            return R.id.reservation_number_radio_button;
        else if(referenceType == ReferenceType.PRODUCT_ID)
            return R.id.product_id_radio_button;
        else
            return R.id.off_radio_button;
    }

    private String getReferenceNumberTxt(int type) {
        switch (type) {
            case ReferenceType.OFF:
                return "Off";
            case ReferenceType.REFERENCE_NUMBER:
                return "Reference number";
            case ReferenceType.INVOICE_ID:
                return "Invoice number";
            case ReferenceType.PRODUCT_ID:
                return "Product ID";
            case ReferenceType.RESERVATION_NUMBER:
                return "Reservation number";
        }

        return "Off";
    }

    private int customerReceiptTypeToId(int type) {
        switch (type) {
            case MyPOSUtil.RECEIPT_ON:
                return R.id.cust_auto_radio;
            case MyPOSUtil.RECEIPT_OFF:
                return R.id.cust_disabled_radio;
            case MyPOSUtil.RECEIPT_AFTER_CONFIRMATION:
                return R.id.cust_after_conf_radio;
        }

        return R.id.cust_auto_radio;
    }

    private int customerReceiptIdToType(int id) {
        switch (id) {
            case R.id.cust_auto_radio:
                return MyPOSUtil.RECEIPT_ON;
            case  R.id.cust_disabled_radio:
                return MyPOSUtil.RECEIPT_OFF;
            case R.id.cust_after_conf_radio:
                return MyPOSUtil.RECEIPT_AFTER_CONFIRMATION;
        }

        return MyPOSUtil.RECEIPT_ON;
    }

    private int merchantReceiptTypeToId(int type) {
        switch (type) {
            case MyPOSUtil.RECEIPT_ON:
                return R.id.merch_auto_radio;
            case MyPOSUtil.RECEIPT_OFF:
                return R.id.merch_disabled_radio;
        }

        return R.id.merch_auto_radio;
    }

    private int merchantReceiptIdToType(int id) {
        switch (id) {
            case R.id.merch_auto_radio:
                return MyPOSUtil.RECEIPT_ON;
            case  R.id.merch_disabled_radio:
                return MyPOSUtil.RECEIPT_OFF;
        }

        return MyPOSUtil.RECEIPT_ON;
    }

}
