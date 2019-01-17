package com.mypos.mypospaymentdemo.util;

import java.io.Serializable;

public interface IPreferences extends Serializable {

    boolean getTipEnabled();

    boolean getMultiOperatorModeEnabled();

    boolean getSkipConfirmationScreenflag();

    int getReferenceNumberMode();

    int getCustomerReceiptMode();

    int getMerchantReceiptMode() ;
}
