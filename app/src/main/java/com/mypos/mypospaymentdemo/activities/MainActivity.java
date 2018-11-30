package com.mypos.mypospaymentdemo.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mypos.mypospaymentdemo.PrinterResultBroadcastReceiver;
import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.TerminalData;
import com.mypos.smartsdk.MyPOSAPI;
import com.mypos.smartsdk.MyPOSUtil;
import com.mypos.smartsdk.OnPOSInfoListener;
import com.mypos.smartsdk.SAMCard;
import com.mypos.smartsdk.data.POSInfo;
import com.mypos.smartsdk.print.PrinterCommand;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PAYMENT_REQUEST_CODE = 1;
    private static final int REFUND_REQUEST_CODE  = 2;
    private static final int PAYMENT_REQUEST_REQUEST_CODE = 3;
    private static final int VOID_REQUEST_CODE  = 4;
    private static final int ACTIVATION_CODE  = 5;
    private static final int DEACTIVATION_CODE  = 6;
    private static final int CHECK_BALANCE_CODE  = 7;

    private int voidDataSTAN = 0;
    private String voidDataAuthCode = null;
    private String voidDataDateTime = null;

    PrinterResultBroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        broadcastReceiver = new PrinterResultBroadcastReceiver();

        // Register the printer result broadcast receiver
        registerReceiver(broadcastReceiver, new IntentFilter(MyPOSUtil.PRINTING_DONE_BROADCAST));


        setContentView(R.layout.activity_main);

        MyPOSAPI.registerPOSInfo(this, new OnPOSInfoListener() {
            @Override
            public void onReceive(POSInfo info) {
                TerminalData.posinfo = info;
                findViewById(R.id.progress_layout).setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "pos info is received", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n") // Suppressing i18n warnings since this is just a demo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    /**
     * Prints the contents of a {@link Bundle} for debug purposes
     */
    private String bundleAsString(Bundle data) {
        return bundleAsString(data, false);
    }

    private String bundleAsString(Bundle data, boolean indent) {
        String retString = "";
        if (data != null) {
            for (String s : data.keySet()) {
                Object extra = data.get(s);
                if (extra instanceof Bundle) {
                    retString += "\n\tBundle \"" + s + "\":\n" + bundleAsString((Bundle) extra, true);
                } else {
                    retString += (String.format("%s%s => %s\n", indent ? "\t\t" : "\t", s, extra));
                }
            }
        }
        return retString;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_btn:
                startSettings();
                break;
            case R.id.purchase_btn:
                startPayment();
                break;
            case R.id.refund_btn:
                startRefund();
                break;
            case R.id.void_btn:
                startVoid();
                break;
           /* case R.id.test_void_ex:
                startVoidEx();
                break;*/
            case R.id.print_receipt_btn:
                testPrint();
                break;
            case R.id.reprint_receipt_btn:
                reprintLastReceipt();
                break;
           /* case R.id.test_paymentRequest:
                startPaymentRequest();
                break;*/
           /* case R.id.test_SAM:
                startSAMTest();
                break;*/
            case R.id.giftcard_btn:
                startGiftCards();
                break;
        }
    }

    /**
     * Prints the last transaction's receipt
     */
    private void reprintLastReceipt() {

        System.out.println("Printing last receipt...");

        Intent intent = new Intent(MyPOSUtil.PRINT_LAST_RECEIPT_BROADCAST);
        // Whether or not a copy for the customer should be printed
        intent.putExtra("print_customer_receipt", true);
        sendBroadcast(intent);
    }

    private void testPrint() {
        Intent intent = new Intent(MyPOSUtil.PRINT_BROADCAST);
        String json;

        List<PrinterCommand> commands = new ArrayList<>();

        // Add commands to be sent
        commands.add(new PrinterCommand(PrinterCommand.CommandType.TEXT, "Normal row 1\n"));
        commands.add(new PrinterCommand(PrinterCommand.CommandType.TEXT, "Normal row 2\n"));
        commands.add(new PrinterCommand(PrinterCommand.CommandType.TEXT, "Normal row 3\n\n"));
        commands.add(new PrinterCommand(PrinterCommand.CommandType.TEXT, "Double height\n\n\n", false, true));
        commands.add(new PrinterCommand(PrinterCommand.CommandType.TEXT, "Normal row again\n"));
        commands.add(new PrinterCommand(PrinterCommand.CommandType.TEXT, "LEFT TEXT", "RIGHT TEXT"));
        commands.add(new PrinterCommand(PrinterCommand.CommandType.TEXT, "Size 35 right", 35, PrinterCommand.Alignment.ALIGN_RIGHT));
        commands.add(new PrinterCommand(PrinterCommand.CommandType.TEXT, "Center row", PrinterCommand.Alignment.ALIGN_CENTER));
        commands.add(new PrinterCommand(PrinterCommand.CommandType.TEXT, "Size 60", 60));
        commands.add(new PrinterCommand(PrinterCommand.CommandType.TEXT, "1.00 EUR").setAlignment(PrinterCommand.Alignment.ALIGN_RIGHT).setDoubleHeight(true));
        commands.add(new PrinterCommand(PrinterCommand.CommandType.FOOTER));

        // Add image to be sent
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample);
        commands.add(new PrinterCommand(PrinterCommand.CommandType.IMAGE, bitmap));

        // Serialize the command list
        Gson gson = new Gson();

        json = gson.toJson(commands);

        System.out.println("Sending print broadcast: " + json);

        // Add the commands
        intent.putExtra("commands", json);
        intent.putExtra("bottom_space", false);

        // Send broadcast
        sendBroadcast(intent);
    }

    private void startSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void startPayment() {
        displayPaymentOptions(new IOptionsSelected() {
            @Override
            public void onReady(int transpec) {
               Intent i = new Intent(MainActivity.this, PaymentActivity.class);
               i.putExtra("tran_spec", transpec);
               startActivity(i);
            }
        }, true);
    }

    /**
     * Starts a refund transaction
     */
    private void startRefund() {

    }

    /**
     * Starts a void transaction
     */
    private void startVoid() {

    }

    /**
     * Starts a void transaction by transaction data
     */
    private void startVoidEx() {
        // Build the void request

        /*if (voidDataSTAN == 0 || voidDataAuthCode == null || voidDataDateTime == null) {
            showToast("No last transaction data");
            return;
        }*/
    }

    /**
     * Starts a payment request transaction
     */
    private void startPaymentRequest() {

    }

    private void displayPaymentOptions(final IOptionsSelected optionsSelected, boolean isGiftcardAllowed) {
        final CharSequence[] items = new CharSequence[isGiftcardAllowed ? 3 : 2];
        items[0] = "REGULAR Transaction";
        items[1] = "MO/TO Transaction";

        if (isGiftcardAllowed)
            items[2] = "GiftCard Transaction";

        final int[] tranSpec = new int[1];

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.options)
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface var1, int position) {
                        tranSpec[0] = position;
                    }
                }).setPositiveButton(R.string.contin, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                optionsSelected.onReady(tranSpec[0]);
                            }
                        }
                ).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                }).create();

        dialog.show();
    }

    /**
     * Does a SAM module operation
     */
    //Build SAM module operation
    private static final int SAM_SLOT_1 = 1;
    private static final int SAM_SLOT_2 = 2;

    private void startSAMTest() {
        final Context context = this;
        Thread r = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    int slotNumber = SAM_SLOT_1;
                    int timeoutMs = 1000;
                    boolean hasCard;

                    byte[] resp;
                    byte[] cmd = new byte[] {(byte)0x00,(byte)0xA4,(byte)0x00,(byte)0x00,(byte)0x02, (byte) 0x3f, (byte) 0x00}; // SELECT command for file 0x3F00 (GSM card master file)

                    hasCard = SAMCard.detect(context, slotNumber, timeoutMs);
                    if (!hasCard) {
                        showToast("No SAM card detected in slot " + slotNumber);
                        return;
                    }
                    showToast("SAM card detected in slot " + slotNumber + ". Initializing");

                    resp = SAMCard.open(context, slotNumber, timeoutMs);
                    showToast("Initializing SAM successful. Sending command");

                    resp = SAMCard.isoCommand(context, slotNumber, timeoutMs, cmd);
                    showToast("Response to SAM command received. Closing SAM");

                    SAMCard.close(context, slotNumber, timeoutMs);
                    showToast("SAM module closed");
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(e.getMessage());
                }
            }
        });
        r.start();
    }

    private void startGiftCards() {

    }


    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private interface IOptionsSelected
    {
        void onReady(int transpec);
    }

}
