package com.mypos.mypospaymentdemo.util;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kamen.troshev on 5.10.2016 г..
 */
public class TransactionData implements Parcelable {

    private String  mAuthCode;
    private String  mTransactionDateLocal;
    private String  mRRN;
    private String  mAmount;
    private String mCurrency;
    private String  mTerminalID;
    private String  mMerchantID;
    private String  mMerchantName;
    private String  mMerchantAddressLine1;
    private String  mMerchantAddressLine2;
    private String  mPANMasked;
    private String  mEmbossName;
    private String  mAID;
    private String  mAIDName;
    private String  mStan;
    private String  mPreAuthCode;
    private String mDccCurrency;
    private String  mDccAmount;
    private String  mDccCardExchangeRate;
    private String  mApplicationPrefName;
    private String  mCVM;
    private String  mCardEntryMode;
    private String  mTipAmount;
    private String  mOperatorCode;
    private String  mExpireDate;
    private boolean mSignatureRequired;
    private boolean mIsDccUsed;

    public TransactionData(){
        mAuthCode               = "";
        mTransactionDateLocal   = "";
        mRRN                    = "";
        mAmount                 = "";
        mCurrency               = "";
        mTerminalID             = "";
        mMerchantID             = "";
        mMerchantName           = "";
        mMerchantAddressLine1   = "";
        mMerchantAddressLine2   = "";
        mPANMasked              = "";
        mEmbossName             = "";
        mAID                    = "";
        mAIDName                = "";
        mStan                   = "";
        mPreAuthCode            = "";
        mDccCurrency            = "";
        mDccAmount              = "";
        mDccCardExchangeRate    = "";
        mApplicationPrefName    = "";
        mCVM                    = "";
        mCardEntryMode          = "";
        mTipAmount              = "";
        mOperatorCode           = "";
        mExpireDate             = "";
        mSignatureRequired      = false;
        mIsDccUsed              = false;
    }

    public TransactionData(Parcel in) {
        mAuthCode               = in.readString();
        mTransactionDateLocal   = in.readString();
        mRRN                    = in.readString();
        mAmount                 = in.readString();
        mCurrency               = in.readString();
        mTerminalID             = in.readString();
        mMerchantID             = in.readString();
        mMerchantName           = in.readString();
        mMerchantAddressLine1   = in.readString();
        mMerchantAddressLine2   = in.readString();
        mPANMasked              = in.readString();
        mEmbossName             = in.readString();
        mAID                    = in.readString();
        mAIDName                = in.readString();
        mStan                   = in.readString();
        mPreAuthCode            = in.readString();
        mDccCurrency            = in.readString();
        mDccAmount              = in.readString();
        mDccCardExchangeRate    = in.readString();
        mApplicationPrefName    = in.readString();
        mCVM                    = in.readString();
        mCardEntryMode          = in.readString();
        mTipAmount              = in.readString();
        mOperatorCode           = in.readString();
        mExpireDate             = in.readString();
        mSignatureRequired      = in.readInt() == 1;
        mIsDccUsed              = in.readInt() == 1;
    }

    public static final Creator<TransactionData> CREATOR = new Creator<TransactionData>() {
        public TransactionData createFromParcel(Parcel in) {
            return new TransactionData(in);
        }

        public TransactionData[] newArray(int size) {
            return new TransactionData[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAuthCode);
        dest.writeString(mTransactionDateLocal);
        dest.writeString(mRRN);
        dest.writeString(mAmount);
        dest.writeString(mCurrency);
        dest.writeString(mTerminalID);
        dest.writeString(mMerchantID);
        dest.writeString(mMerchantName);
        dest.writeString(mMerchantAddressLine1);
        dest.writeString(mMerchantAddressLine2);
        dest.writeString(mPANMasked);
        dest.writeString(mEmbossName);
        dest.writeString(mAID);
        dest.writeString(mAIDName);
        dest.writeString(mStan);
        dest.writeString(mPreAuthCode);
        dest.writeString(mDccCurrency);
        dest.writeString(mDccAmount);
        dest.writeString(mDccCardExchangeRate);
        dest.writeString(mApplicationPrefName);
        dest.writeString(mCVM);
        dest.writeString(mCardEntryMode);
        dest.writeString(mTipAmount);
        dest.writeString(mOperatorCode);
        dest.writeString(mExpireDate);
        dest.writeInt(mSignatureRequired ? 1 : 0);
        dest.writeInt(mIsDccUsed ? 1 : 0);
    }

    public String getAuthCode() {
        return mAuthCode;
    }

    public void setAuthCode(String mAuthCode) {
        this.mAuthCode = mAuthCode;
    }

    public String getTransactionDateLocal() {
        return mTransactionDateLocal;
    }

    public void setTransactionDateLocal(String mTransactionDateLocal) {
        this.mTransactionDateLocal = mTransactionDateLocal;
    }

    public String getRRN() {
        return mRRN;
    }

    public void setRRN(String mRRN) {
        this.mRRN = mRRN;
    }

    public String getAmount() {
        return mAmount;
    }

    public void setAmount(String mAmount) {
        this.mAmount = mAmount;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String mCurrency) {
        this.mCurrency = mCurrency;
    }

    public String getTerminalID() {
        return mTerminalID;
    }

    public void setTerminalID(String mTerminalID) {
        this.mTerminalID = mTerminalID;
    }

    public String getMerchantID() {
        return mMerchantID;
    }

    public void setMerchantID(String mMerchantID) {
        this.mMerchantID = mMerchantID;
    }

    public String getMerchantName() {
        return mMerchantName;
    }

    public void setMerchantName(String mMerchantName) {
        this.mMerchantName = mMerchantName;
    }

    public String getPANMasked() {
        return mPANMasked;
    }

    public void setPANMasked(String mPANMasked) {
        this.mPANMasked = mPANMasked;
    }

    public String getEmbossName() {
        return mEmbossName;
    }

    public void setEmbossName(String mEmbossName) {
        this.mEmbossName = mEmbossName;
    }

    public String getAID() {
        return mAID;
    }

    public void setAID(String mAID) {
        this.mAID = mAID;
    }

    public String getAIDName() {
        return mAIDName;
    }

    public void setAIDName(String mAIDName) {
        this.mAIDName = mAIDName;
    }

    public String getStan() {
        return mStan;
    }

    public void setStan(String mStan) {
        this.mStan = mStan;
    }

    public String getPreAuthCode() {
        return mPreAuthCode;
    }

    public void setPreAuthCode(String mPreAuthCode) {
        this.mPreAuthCode = mPreAuthCode;
    }

    public String getDccCurrency() {
        return mDccCurrency;
    }

    public void setDccCurrency(String mDccCurrency) {
        this.mDccCurrency = mDccCurrency;
    }

    public String getDccAmount() {
        return mDccAmount;
    }

    public void setDccAmount(String mDccAmount) {
        this.mDccAmount = mDccAmount;
    }

    public String getDccCardExchangeRate() {
        return mDccCardExchangeRate;
    }

    public void setDccCardExchangeRate(String mDccCardExchangeRate) {
        this.mDccCardExchangeRate = mDccCardExchangeRate;
    }

    public String getApplicationPrefName() {
        return mApplicationPrefName;
    }

    public void setApplicationPrefName(String mApplicationPrefName) {
        this.mApplicationPrefName = mApplicationPrefName;
    }

    public String getCVM() {
        return mCVM;
    }

    public void setCVM(String mCVM) {
        this.mCVM = mCVM;
    }

    public String getCardEntryMode() {
        return mCardEntryMode;
    }

    public void setCardEntryMode(String mCardEntryMode) {
        this.mCardEntryMode = mCardEntryMode;
    }

    public String getTipAmount() {
        return mTipAmount;
    }

    public void setTipAmount(String mTipAmount) {
        this.mTipAmount = mTipAmount;
    }

    public String getOperatorCode() {
        return mOperatorCode;
    }

    public void setOperatorCode(String mOperatorCode) {
        this.mOperatorCode = mOperatorCode;
    }

    public boolean getIsDccUsed() {
        return mIsDccUsed;
    }

    public void setIsDccUsed(boolean mIsDccUsed) {
        this.mIsDccUsed = mIsDccUsed;
    }

    public String getExpireDate() {
        return mExpireDate;
    }

    public void setExpireDate(String mExpireDate) {
        this.mExpireDate = mExpireDate;
    }

    public boolean isSignatureRequired() {
        return mSignatureRequired;
    }

    public void setSignatureRequired(boolean mSignatureRequired) {
        this.mSignatureRequired = mSignatureRequired;
    }

    public String getMerchantAddressLine1() {
        return mMerchantAddressLine1;
    }

    public void setMerchantAddressLine1(String mMerchantAddressLine1) {
        this.mMerchantAddressLine1 = mMerchantAddressLine1;
    }

    public String getMerchantAddressLine2() {
        return mMerchantAddressLine2;
    }

    public void setMerchantAddressLine2(String mMerchantAddressLine2) {
        this.mMerchantAddressLine2 = mMerchantAddressLine2;
    }

    public static TransactionData fromBundle(Bundle bundle) {
        TransactionData tranData = new TransactionData();

        tranData.setTerminalID(             TerminalData.posinfo.getTID());
        tranData.setMerchantID(             TerminalData.posinfo.getMerchantData().getMerchantID());
        tranData.setMerchantName(           TerminalData.posinfo.getMerchantData().getMerchantName());
        tranData.setMerchantAddressLine1(   TerminalData.posinfo.getMerchantData().getAddressLine1());
        tranData.setMerchantAddressLine2(   TerminalData.posinfo.getMerchantData().getAddressLine2());
        tranData.setAuthCode(               getOptString(bundle,"authorization_code",""));
        tranData.setAIDName(                getOptString(bundle,"card_brand",        ""));
        tranData.setTransactionDateLocal(   getOptString(bundle,"date_time",         ""));
        tranData.setRRN(                    getOptString(bundle,"reference_number",  ""));
        tranData.setCurrency(               getOptString(bundle,"currency",          ""));
        tranData.setPANMasked(              getOptString(bundle,"pan",               ""));
        tranData.setEmbossName(             getOptString(bundle,"cardholder_name",   ""));
        tranData.setAID(                    getOptString(bundle,"AID",               ""));
        tranData.setPreAuthCode(            getOptString(bundle,"preauth_code",      ""));
        tranData.setDccCurrency(            getOptString(bundle,"currency_dcc",      ""));
        tranData.setApplicationPrefName(    getOptString(bundle,"application_name",  ""));
        tranData.setCVM(                    getOptString(bundle,"CVM",               ""));
        tranData.setCardEntryMode(          getOptString(bundle,"card_entry_mode",   ""));
        tranData.setOperatorCode(           getOptString(bundle,"operator_code",     ""));
        tranData.setExpireDate(             getOptString(bundle, "expire_date",      ""));
        tranData.setAmount(                 String.valueOf(bundle.getDouble("amount")));
        tranData.setTipAmount(              String.valueOf(bundle.getDouble("tip_amount")));
        tranData.setDccAmount(              String.valueOf(bundle.getDouble("amount_dcc")));
        tranData.setDccCardExchangeRate(    String.valueOf(bundle.getDouble("exchange_rate")));
        tranData.setStan(                   String.valueOf(bundle.getInt("STAN")));
        tranData.setSignatureRequired(      bundle.getBoolean("signature_required"));
        tranData.setIsDccUsed(              bundle.getBoolean("dcc_available"));

        return tranData;
    }

    private static String getOptString(Bundle bundle, String key, String optValue) {
        if (bundle.getString(key) == null)
            return optValue;

        return bundle.getString(key);
    }

}
