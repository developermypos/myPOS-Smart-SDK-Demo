package com.mypos.mypospaymentdemo.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.PreferencesManager;
import com.mypos.smartsdk.MyPOSUtil;
import com.mypos.smartsdk.ReferenceType;

public class SettingsActivity extends AppCompatActivity {

    private TextView referenceNumberTypeTV;

    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferencesManager = PreferencesManager.getInstance();

        SwitchCompat tippingToggle = (SwitchCompat) findViewById(R.id.tipping_toggle);
        SwitchCompat multiOperatorToggle = (SwitchCompat) findViewById(R.id.multi_operator_toggle);
        SwitchCompat skipConfScreenToggle = (SwitchCompat) findViewById(R.id.skip_conf_screen_toggle);
        SwitchCompat mcSonicToggle = (SwitchCompat) findViewById(R.id.mc_sonic_toggle);
        referenceNumberTypeTV = (TextView) findViewById(R.id.reference_number_text);
        RadioGroup customerReceiptTypes = (RadioGroup) findViewById(R.id.customer_receipt_types);
        RadioGroup merchantReceiptTypes = (RadioGroup) findViewById(R.id.merchant_receipt_types);

        tippingToggle.setChecked(preferencesManager.getTipEnabled());
        multiOperatorToggle.setChecked(preferencesManager.getMultiOperatorModeEnabled());
        skipConfScreenToggle.setChecked(preferencesManager.getSkipConfirmationScreenflag());
        mcSonicToggle.setChecked(preferencesManager.getMCSonicEnabled());
        referenceNumberTypeTV.setText(getReferenceNumberTxt(preferencesManager.getReferenceNumberMode()));
        customerReceiptTypes.check(customerReceiptTypeToId(preferencesManager.getCustomerReceiptMode()));
        merchantReceiptTypes.check(merchantReceiptTypeToId(preferencesManager.getMerchantReceiptMode()));

        tippingToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferencesManager.setTipEnabled(b);
            }
        });

        multiOperatorToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferencesManager.setMultiOperatorModeEnabled(b);
            }
        });

        skipConfScreenToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferencesManager.setSkipConfirmationScreenflag(b);
            }
        });

        mcSonicToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferencesManager.setMCSonicEnabled(b);
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
                preferencesManager.setCustomerReceiptMode(customerReceiptIdToType(i));
            }
        });

        merchantReceiptTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                preferencesManager.setMerchantReceiptMode(merchantReceiptIdToType(i));
            }
        });
    }

    private void showReferenceNumberDialog(){
        final int[] checkedId = new int[1];
        final CharSequence[] items = new CharSequence[] {getString(R.string.off), getString(R.string.reference_number), getString(R.string.invoice_number), getString(R.string.product_id), getString(R.string.reservation_number)};
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.reference_number)
                .setSingleChoiceItems(items, preferencesManager.getReferenceNumberMode(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface var1, int position) {
                        checkedId[0] = position;
                    }
                }).setPositiveButton(R.string.contin, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                switch (checkedId[0]) {
                                    case ReferenceType.OFF:
                                        preferencesManager.setReferenceNumberMode(ReferenceType.OFF);
                                        break;
                                    case ReferenceType.REFERENCE_NUMBER:
                                        preferencesManager.setReferenceNumberMode(ReferenceType.REFERENCE_NUMBER);
                                        break;
                                    case ReferenceType.INVOICE_ID:
                                        preferencesManager.setReferenceNumberMode(ReferenceType.INVOICE_ID);
                                        break;
                                    case ReferenceType.PRODUCT_ID:
                                        preferencesManager.setReferenceNumberMode(ReferenceType.PRODUCT_ID);
                                        break;
                                    case ReferenceType.RESERVATION_NUMBER:
                                        preferencesManager.setReferenceNumberMode(ReferenceType.RESERVATION_NUMBER);
                                        break;
                                }

                                referenceNumberTypeTV.setText(getReferenceNumberTxt(preferencesManager.getReferenceNumberMode()));
                            }
                        }
                ).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                }).create();

        dialog.show();
    }

    private String getReferenceNumberTxt(int type) {
        switch (type) {
            case ReferenceType.OFF:
                return getString(R.string.off);
            case ReferenceType.REFERENCE_NUMBER:
                return getString(R.string.reference_number);
            case ReferenceType.INVOICE_ID:
                return getString(R.string.invoice_number);
            case ReferenceType.PRODUCT_ID:
                return getString(R.string.product_id);
            case ReferenceType.RESERVATION_NUMBER:
                return getString(R.string.reservation_number);
        }

        return getString(R.string.off);
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
