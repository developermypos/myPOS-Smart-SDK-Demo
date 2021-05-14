package com.mypos.mypospaymentdemo.util;

import java.io.Serializable;

public interface IPreferences extends Serializable {

    boolean getTipEnabled();

    boolean getMultiOperatorModeEnabled();

    boolean getSkipConfirmationScreenflag();

    boolean getMCSonicEnabled();

    boolean getVisaSensoryEnabled();

    int getReferenceNumberMode();

    int getCustomerReceiptMode();

    int getMerchantReceiptMode();
}
