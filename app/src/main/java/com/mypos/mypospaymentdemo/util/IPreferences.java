package com.mypos.mypospaymentdemo.util;

import java.io.Serializable;

public interface IPreferences extends Serializable {

    boolean getTipEnabled();

    boolean getMultiOperatorModeEnabled();

    boolean getSkipConfirmationScreenflag();

    boolean getMCSonicEnabled();

    int getReferenceNumberMode();

    int getCustomerReceiptMode();

    int getMerchantReceiptMode();
}
