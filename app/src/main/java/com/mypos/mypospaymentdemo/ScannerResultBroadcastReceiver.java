package com.mypos.mypospaymentdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mypos.smartsdk.print.PrinterStatus;

/**
 * Receives the response broadcast from the printer
 */
public class ScannerResultBroadcastReceiver extends BroadcastReceiver {

    @SuppressLint("DefaultLocale")
    @Override
    public void onReceive(Context context, Intent intent) {
        String resultCode = intent.getStringExtra("code");
        int status = intent.getIntExtra("status", Activity.RESULT_CANCELED);

        if (status == Activity.RESULT_OK) {
            Toast.makeText(context, "Scanner result: " + resultCode, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(context, "Scanner cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
