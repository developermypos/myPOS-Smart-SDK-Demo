package com.mypos.mypospaymentdemo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mypos.smartsdk.print.PrinterStatus;

/**
 * Receives the response broadcast from the printer
 */
public class PrinterResultBroadcastReceiver extends BroadcastReceiver {

    @SuppressLint("DefaultLocale")
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean printing_started = intent.getBooleanExtra("printing_started", false);
        int printer_status = intent.getIntExtra("printer_status", PrinterStatus.PRINTER_STATUS_UNKNOWN_ERROR);

        // If the printing has actually started, handle the status
        if (printing_started) {

            // Handle success and errors
            if (printer_status == PrinterStatus.PRINTER_STATUS_SUCCESS) {
                Toast.makeText(context, "Printing successful!", Toast.LENGTH_SHORT).show();
                // Printing is successful
            } else if (printer_status == PrinterStatus.PRINTER_STATUS_OUT_OF_PAPER) {
                // Show "missing paper" dialog
                Toast.makeText(context, "No paper in the printer", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, String.format("Some error occurred while printing. Status: %d", printer_status), Toast.LENGTH_SHORT).show();
            }
            // etc.
        } else {
            // Some error occurred. Maybe there's no transaction data (when printing last transaction receipt).
            Toast.makeText(context, String.format("Error occurred while printing. Status: %d", printer_status), Toast.LENGTH_SHORT).show();
        }
    }
}
