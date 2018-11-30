package com.mypos.mypospaymentdemo.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.mypos.smartsdk.MyPOSUtil;
import com.mypos.smartsdk.ReferenceType;

public class PersistentDataManager {
    private static final String PREFERENCES_NAME = "settings_data";

    private static PersistentDataManager instance;

    public static void setContext(Context context) {
        PersistentDataManager.context = context;
    }

    private static Context context;

    private SharedPreferences sharedPreferences;

    public PersistentDataManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static PersistentDataManager getInstance() {
        if (instance == null)
            instance = new PersistentDataManager(context);

        return instance;
    }

    public void setTipEnabled(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("tip_enabled", value);
        editor.apply();
    }

    public boolean getTipEnabled() {
        return sharedPreferences.getBoolean("tip_enabled", false);
    }

    public void setMultiOperatorModeEnabled(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("multi_operator_enabled", value);
        editor.apply();
    }

    public boolean getMultiOperatorModeEnabled() {
        return sharedPreferences.getBoolean("multi_operator_enabled", false);
    }

    public void setSkipConfirmationScreenflag(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("skip_conf_screen_enabled", value);
        editor.apply();
    }

    public boolean getSkipConfirmationScreenflag() {
        return sharedPreferences.getBoolean("skip_conf_screen_enabled", false);
    }

    public void setReferenceNumberMode(int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("reference_number_mode", value);
        editor.apply();
    }

    public int getReferenceNumberMode() {
        return sharedPreferences.getInt("reference_number_mode", ReferenceType.OFF);
    }

    public void setCustomerReceiptMode(int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("customer_receipt_mode", value);
        editor.apply();
    }

    public int getCustomerReceiptMode() {
        return sharedPreferences.getInt("customer_receipt_mode", MyPOSUtil.RECEIPT_ON);
    }

    public void setMerchantReceiptMode(int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("merchant_receipt_mode", value);
        editor.apply();
    }

    public int getMerchantReceiptMode() {
        return sharedPreferences.getInt("merchant_receipt_mode", MyPOSUtil.RECEIPT_ON);
    }
}
