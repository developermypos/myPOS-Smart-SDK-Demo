package com.mypos.mypospaymentdemo.util;

import android.content.Intent;

public interface IFragmentResult {
    public void setResult(int requestCode, int resultCode, Intent data);
}
