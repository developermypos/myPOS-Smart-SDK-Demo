package com.mypos.mypospaymentdemo.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.mypos.mypospaymentdemo.PosApplication;
import com.mypos.smartsdk.MyPOSUtil;
import com.mypos.smartsdk.ReferenceType;

public class PreferencesManager {
    private static final String PREFERENCES_NAME = "preferences_data";

    private static PreferencesManager instance;

    private SharedPreferences sharedPreferences;

    public PreferencesManager() {
        sharedPreferences = PosApplication.getContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static PreferencesManager getInstance() {
        if (instance == null)
            instance = new PreferencesManager();

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

    public void setMCSonicEnabled(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("mc_sonic_enabled", value);
        editor.apply();
    }

    public boolean getMCSonicEnabled() {
        return sharedPreferences.getBoolean("mc_sonic_enabled", true);
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

    public void setLastTransactionStan(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_stan", value);
        editor.apply();
    }

    public String getLastTransactionStan() {
        return sharedPreferences.getString("last_stan", "");
    }

    public void setLastTransactionAuth(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_auth", value);
        editor.apply();
    }

    public String getLastTransactionAuth() {
        return sharedPreferences.getString("last_auth", "");
    }

    public void setLastTransactionDateTime(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_date_time", value);
        editor.apply();
    }

    public String getLastTransactionDateTime() {
        return sharedPreferences.getString("last_date_time", "");
    }

    public IPreferences getPreferences() {
        Preferences preferences = new Preferences();

        preferences.setTipEnabled(getTipEnabled());
        preferences.setMultiOperatorModeEnabled(getMultiOperatorModeEnabled());
        preferences.setSkipConfirmationScreenflag(getSkipConfirmationScreenflag());
        preferences.setMCSonicEnabled(getMCSonicEnabled());
        preferences.setReferenceNumberMode(getReferenceNumberMode());
        preferences.setCustomerReceiptMode(getCustomerReceiptMode());
        preferences.setMerchantReceiptMode(getMerchantReceiptMode());

        return preferences;
    }

    private static class Preferences implements IPreferences {

        private boolean tipEnabled;
        private boolean multiOperatorModeEnabled;
        private boolean skipConfirmationScreenFlag;
        private boolean mcSonicEnabled;
        private int referenceNumberMode;
        private int customerReceiptMode;
        private int merchantReceiptMode;

        public void setTipEnabled(boolean value) {
            tipEnabled = value;
        }

        @Override
        public boolean getTipEnabled() {
            return tipEnabled;
        }

        public void setMultiOperatorModeEnabled(boolean value) {
            multiOperatorModeEnabled = value;
        }

        @Override
        public boolean getMultiOperatorModeEnabled() {
            return multiOperatorModeEnabled;
        }

        public void setSkipConfirmationScreenflag(boolean value) {
            skipConfirmationScreenFlag = value;
        }

        @Override
        public boolean getSkipConfirmationScreenflag() {
            return skipConfirmationScreenFlag;
        }

        public void setMCSonicEnabled(boolean value) {
            mcSonicEnabled = value;
        }

        @Override
        public boolean getMCSonicEnabled() {
            return mcSonicEnabled;
        }

        public void setReferenceNumberMode(int value) {
            referenceNumberMode = value;
        }

        @Override
        public int getReferenceNumberMode() {
            return referenceNumberMode;
        }

        public void setCustomerReceiptMode(int value) {
            customerReceiptMode = value;
        }

        @Override
        public int getCustomerReceiptMode() {
            return customerReceiptMode;
        }

        public void setMerchantReceiptMode(int value) {
            merchantReceiptMode = value;
        }

        @Override
        public int getMerchantReceiptMode() {
            return merchantReceiptMode;
        }
    }
}
