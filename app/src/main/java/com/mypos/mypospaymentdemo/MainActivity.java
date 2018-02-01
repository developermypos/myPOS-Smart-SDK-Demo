package com.mypos.mypospaymentdemo;

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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mypos.smartsdk.Currency;
import com.mypos.smartsdk.MyPOSAPI;
import com.mypos.smartsdk.MyPOSPayment;
import com.mypos.smartsdk.MyPOSPaymentRequest;
import com.mypos.smartsdk.MyPOSRefund;
import com.mypos.smartsdk.MyPOSUtil;
import com.mypos.smartsdk.MyPOSVoidEx;
import com.mypos.smartsdk.OnPOSInfoListener;
import com.mypos.smartsdk.ReceiptPrintMode;
import com.mypos.smartsdk.SAMCard;
import com.mypos.smartsdk.TransactionProcessingResult;
import com.mypos.smartsdk.data.POSInfo;
import com.mypos.smartsdk.print.PrinterCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PAYMENT_REQUEST_CODE = 1;
    private static final int REFUND_REQUEST_CODE  = 2;
    private static final int PAYMENT_REQUEST_REQUEST_CODE = 3;
    private static final int VOID_REQUEST_CODE  = 4;

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
                Toast.makeText(MainActivity.this, "pos info is received", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n") // Suppressing i18n warnings since this is just a demo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Distinguish between transaction types
        if (requestCode == PAYMENT_REQUEST_CODE) {
            Toast.makeText(this, "Payment operation completed", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REFUND_REQUEST_CODE) {
            Toast.makeText(this, "Refund operation completed", Toast.LENGTH_SHORT).show();
        } else if (requestCode == PAYMENT_REQUEST_REQUEST_CODE) {
            Toast.makeText(this, "Payment request operation completed", Toast.LENGTH_SHORT).show();
        }

        // The user cancelled the transaction at some point
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }


        // Something went wrong in the Payment core app and the result couldn't be returned properly
        if (data == null) {
            Toast.makeText(this, "Transaction cancelled", Toast.LENGTH_SHORT).show();
            return;
        }

        // Print all the returned data
        Log.d(TAG, "Response: " + bundleAsString(data.getExtras()));

        // Populate the TextViews with data from the response

        TextView tmpTextView;

        tmpTextView = (TextView) findViewById(R.id.transaction_approved);
        tmpTextView.setText("Transaction approved: " +
                (data.getBooleanExtra("transaction_approved", false) ? "Yes" : "No"));

        tmpTextView = (TextView) findViewById(R.id.response_code);
        tmpTextView.setText("Response code: " + data.getStringExtra("response_code"));


        tmpTextView = (TextView) findViewById(R.id.status);
        tmpTextView.setText("Status: " + data.getIntExtra("status", TransactionProcessingResult.TRANSACTION_FAILED));

        tmpTextView = (TextView) findViewById(R.id.status_text);
        tmpTextView.setText("Status text: " + data.getStringExtra("status_text"));

        tmpTextView = (TextView) findViewById(R.id.transaction_date);
        tmpTextView.setText("Transaction date: " + data.getStringExtra("date_time"));

        tmpTextView = (TextView) findViewById(R.id.card_brand);
        tmpTextView.setText("Card brand: " + data.getStringExtra("card_brand"));

        tmpTextView = (TextView) findViewById(R.id.card_entry_mode);
        tmpTextView.setText("Card entry mode: " + data.getStringExtra("card_entry_mode"));

        tmpTextView = (TextView) findViewById(R.id.pan);
        tmpTextView.setText("PAN: " + data.getStringExtra("pan"));

        tmpTextView = (TextView) findViewById(R.id.cardholder_name);
        tmpTextView.setText("Cardholder name: " + data.getStringExtra("cardholder_name"));

        tmpTextView = (TextView) findViewById(R.id.rrn);
        tmpTextView.setText("RRN: " + data.getStringExtra("rrn"));

        tmpTextView = (TextView) findViewById(R.id.aid);
        tmpTextView.setText("AID: " + data.getStringExtra("aid"));

        tmpTextView = (TextView) findViewById(R.id.tvr);
        tmpTextView.setText("TVR: " + data.getStringExtra("TVR"));

        tmpTextView = (TextView) findViewById(R.id.tsi);
        tmpTextView.setText("TSI: " + data.getStringExtra("TSI"));

        voidDataSTAN = data.getIntExtra("STAN", 0);
        voidDataAuthCode = data.getStringExtra("authorization_code");
        voidDataDateTime = data.getStringExtra("date_time");
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
            case R.id.start_payment:
                startPayment(false, false);
                break;
            case R.id.start_payment_no_confirmation_screen:
                startPayment(true, false);
                break;
            case R.id.start_payment_no_receipt:
                startPayment(true, true);
                break;
            case R.id.start_payment_only_merchant_receipt:
                startPayment2(false, true);
                break;
            case R.id.start_payment_only_customer_receipt:
                startPayment2(true, false);
                break;
            case R.id.start_refund:
                startRefund();
                break;
            case R.id.test_void:
                startVoid();
                break;
            case R.id.test_void_ex:
                startVoidEx();
                break;
            case R.id.test_print:
                testPrint();
                break;
            case R.id.test_reprint:
                reprintLastReceipt();
                break;
            case R.id.test_paymentRequest:
                startPaymentRequest();
                break;
            case R.id.test_SAM:
                startSAMTest();
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

        // Send broadcast
        sendBroadcast(intent);
    }

    private void startPayment(boolean skipConfirmationScreen, boolean skipReceipt) {
        // Build the payment
        MyPOSPayment payment = MyPOSPayment.builder()
                .productAmount(13.37)
                .currency(Currency.EUR)
                .foreignTransactionId(UUID.randomUUID().toString())
                .build();

        // Start the transaction
        MyPOSAPI.openPaymentActivity(MainActivity.this, payment, PAYMENT_REQUEST_CODE,
                skipConfirmationScreen,
                skipReceipt ? ReceiptPrintMode.NO_RECEIPT : ReceiptPrintMode.AUTOMATICALLY);

    }

    private void startPayment2(boolean skipMerchantReceipt, boolean skipCustomerReceipt) {
        // Build the payment
        MyPOSPayment payment = MyPOSPayment.builder()
                .productAmount(13.37)
                .currency(Currency.EUR)
                .foreignTransactionId(UUID.randomUUID().toString())
                .printMerchantReceipt(skipMerchantReceipt ? MyPOSUtil.RECEIPT_OFF : MyPOSUtil.RECEIPT_ON)
                .printCustomerReceipt(skipCustomerReceipt ? MyPOSUtil.RECEIPT_OFF : MyPOSUtil.RECEIPT_AFTER_CONFIRMATION)
                .build();

        // Start the transaction
        MyPOSAPI.openPaymentActivity(MainActivity.this, payment, PAYMENT_REQUEST_CODE);

    }

    /**
     * Starts a refund transaction
     */
    private void startRefund() {
        // Build the refund request
        MyPOSRefund refund = MyPOSRefund.builder()
                .refundAmount(1.23)
                .currency(Currency.EUR)
                .foreignTransactionId(UUID.randomUUID().toString())
                .build();

        // Start the transaction
        MyPOSAPI.openRefundActivity(MainActivity.this, refund, REFUND_REQUEST_CODE);

    }

    /**
     * Starts a void transaction
     */
    private void startVoid() {
        // Build the void request
        displayVoidOptions(new IOptionsSelected() {
            @Override
            public void onReady(boolean[] options) {
                MyPOSAPI.openVoidActivity(MainActivity.this, null, VOID_REQUEST_CODE, options[0]);
            }
        });

    }

    /**
     * Starts a void transaction by transaction data
     */
    private void startVoidEx() {
        // Build the void request

        if (voidDataSTAN == 0 || voidDataAuthCode == null || voidDataDateTime == null) {
            showToast("No last transaction data");
            return;
        }

        final MyPOSVoidEx voidEx = MyPOSVoidEx.builder()
                .STAN(voidDataSTAN)
                .authCode(voidDataAuthCode)
                .dateTime(voidDataDateTime)
                .build();

        displayVoidOptions(new IOptionsSelected() {
            @Override
            public void onReady(boolean[] options) {
                MyPOSAPI.openVoidActivity(MainActivity.this, voidEx, VOID_REQUEST_CODE, options[0]);
            }
        });

    }

    private void displayVoidOptions(final IOptionsSelected optionsListener)
    {
        final CharSequence[] items = {getResources().getString(R.string.skip_void_conf)};
        final boolean[] skipVoiFlags = new boolean[1];

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.options)
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                       skipVoiFlags[0] = isChecked;
                    }
                }).setPositiveButton(R.string.contin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //available in myPOS OS version: 1.0.1
                        if (optionsListener != null)
                            optionsListener.onReady(skipVoiFlags);
                    }
                }
                ).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                }).create();

        dialog.show();
    }

    /**
     * Starts a payment request transaction
     */
    private void startPaymentRequest() {
        // Build the payment request
        MyPOSPaymentRequest paymentRequest = MyPOSPaymentRequest.builder()
                .productAmount(3.55)
                .currency(Currency.EUR)
                .expiryDays(60)
                .recipientName("John Doe")
                .GSM("0899070087")
                .eMail("john.doe@email.arpa")
                .reason("System test")
                .build();

        // Start the transaction
        MyPOSAPI.createPaymentRequest(MainActivity.this, paymentRequest, PAYMENT_REQUEST_REQUEST_CODE);
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
        void onReady(boolean[] options);
    }

}
