package com.mypos.mypospaymentdemo.util;

import com.mypos.smartsdk.MyPOSUtil;
import com.mypos.smartsdk.ReferenceType;

public class Utils {

    public static final int PAYMENT_ACTIVITY_REQUEST_CODE = 1;
    public static final int REfUND_ACTIVITY_REQUEST_CODE = 2;
    public static final int PREAUTH_ACTIVITY_REQUEST_CODE = 3;
    public static final int PREAUTH_COMPLETION_ACTIVITY_REQUEST_CODE = 4;
    public static final int PREAUTH_CANCELLATION_ACTIVITY_REQUEST_CODE = 5;
    public static final int PAYMENT_REQUEST_ACTIVITY_REQUEST_CODE = 6;
    public static final int GIFTCARD_ACTIVATION_ACTIVITY_REQUEST_CODE = 7;

    public static final int AMOUNT_REQUEST_CODE = 10;
    public static final int TIP_REQUEST_CODE = 11;
    public static final int MULTI_OPERATOR_CODE_REQUEST_CODE = 12;
    public static final int REFERENCE_NUMBER_REQUEST_CODE = 13;
    public static final int PREAUTH_CODE_REQUEST_CODE = 14;
    public static final int CREDENTIAL_REQUEST_CODE = 15;

    public static final int PAYMENT_REQUEST_CODE = 101;
    public static final int REFUND_REQUEST_CODE = 102;
    public static final int VOID_REQUEST_CODE = 103;
    public static final int GIFTCARD_ACTIVATION_REQUEST_CODE = 103;
    public static final int GIFTCARD_DEACTIVATION_REQUEST_CODE = 104;
    public static final int GIFTCARD_BALANCE_CHECK_REQUEST_CODE = 105;
    public static final int PAYMENT_REQUEST_REQUEST_CODE = 106;
    public static final int PREAUTH_REQUEST_CODE = 107;
    public static final int PREAUTH_COMPLETION_REQUEST_CODE = 108;
    public static final int PREAUTH_CANCELLATION_REQUEST_CODE = 109;

    public static final int TRANSACTION_SPEC_REGULAR = 0;
    public static final int TRANSACTION_SPEC_MOTO = 1;
    public static final int TRANSACTION_SPEC_GIFTCARD = 2;

    public static IPreferences getDefaultPreferences() {
        return new IPreferences() {
            @Override
            public boolean getTipEnabled() {
                return false;
            }

            @Override
            public boolean getMultiOperatorModeEnabled() {
                return false;
            }

            @Override
            public boolean getSkipConfirmationScreenflag() {
                return false;
            }

            @Override
            public boolean getMCSonicEnabled() {
                return true;
            }

            @Override
            public boolean getVisaSensoryEnabled() {
                return true;
            }

            @Override
            public int getReferenceNumberMode() {
                return ReferenceType.OFF;
            }

            @Override
            public int getCustomerReceiptMode() {
                return MyPOSUtil.RECEIPT_ON;
            }

            @Override
            public int getMerchantReceiptMode() {
                return MyPOSUtil.RECEIPT_ON;
            }
        };
    }
}
