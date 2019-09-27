package com.mypos.mypospaymentdemo.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mypos.mypospaymentdemo.PrinterResultBroadcastReceiver;
import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.ScannerResultBroadcastReceiver;
import com.mypos.mypospaymentdemo.util.PreferencesManager;
import com.mypos.mypospaymentdemo.util.TerminalData;
import com.mypos.mypospaymentdemo.util.TransactionData;
import com.mypos.mypospaymentdemo.util.Utils;
import com.mypos.smartsdk.MyPOSAPI;
import com.mypos.smartsdk.MyPOSUtil;
import com.mypos.smartsdk.MyPOSVoid;
import com.mypos.smartsdk.OnPOSInfoListener;
import com.mypos.smartsdk.SAMCard;
import com.mypos.smartsdk.data.POSInfo;
import com.mypos.smartsdk.print.PrinterCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    PrinterResultBroadcastReceiver printerBroadcastReceiver;
    ScannerResultBroadcastReceiver scannerBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        printerBroadcastReceiver = new PrinterResultBroadcastReceiver();
        scannerBroadcastReceiver = new ScannerResultBroadcastReceiver();

        // Register the printer result broadcast receivers
        registerReceiver(printerBroadcastReceiver, new IntentFilter(MyPOSUtil.PRINTING_DONE_BROADCAST));
        registerReceiver(scannerBroadcastReceiver, new IntentFilter(MyPOSUtil.SCANNER_RESULT_BROADCAST));

        setContentView(R.layout.activity_main);

        MyPOSAPI.registerPOSInfo(this, new OnPOSInfoListener() {
            @Override
            public void onReceive(POSInfo info) {
                TerminalData.posinfo = info;
                findViewById(R.id.progress_layout).setVisibility(View.GONE);
                showToast("POS info is received");
            }
        });

    }

    @SuppressLint("SetTextI18n") // Suppressing i18n warnings since this is just a demo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data.getExtras() != null) {
                Log.d(TAG, bundleAsString(data.getExtras()));

                TransactionData transactionData = TransactionData.fromBundle(data.getExtras());

                PreferencesManager.getInstance().setLastTransactionStan(transactionData.getStan());
                PreferencesManager.getInstance().setLastTransactionAuth(transactionData.getAuthCode());
                PreferencesManager.getInstance().setLastTransactionDateTime(transactionData.getTransactionDateLocal());

                showTransactionDataAlert(transactionData);
            }
        } else {
            showToast("Operation is cancelled");
        }
    }

    private void showTransactionDataAlert(TransactionData transactionData){
        String message = "Auth code: " + transactionData.getAuthCode() + "\n";
        message += "Transaction Local Date: " + transactionData.getTransactionDateLocal() + "\n";
        message += "RRN: " + transactionData.getRRN() + "\n";
        message += "Amount: " + transactionData.getAmount() + "\n";
        message += "Currency: " + transactionData.getCurrency() + "\n";
        message += "Terminal ID: " + transactionData.getTerminalID() + "\n";
        message += "Merchant ID: " + transactionData.getMerchantID() + "\n";
        message += "Merchant Name: " + transactionData.getMerchantName() + "\n";
        message += "Merchant Address Line 1: " + transactionData.getMerchantAddressLine1() + "\n";
        message += "Merchant Address Line 2: " + transactionData.getMerchantAddressLine2() + "\n";
        message += "PAN Masked: " + transactionData.getPANMasked() + "\n";
        message += "Emboss Name: " + transactionData.getEmbossName() + "\n";
        message += "AID: " + transactionData.getAID() + "\n";
        message += "AID Name: " + transactionData.getAIDName() + "\n";
        message += "STAN: " + transactionData.getStan() + "\n";
        message += "Is Signature Required: " + transactionData.isSignatureRequired() +"\n";
        message += "Application pref name: " + transactionData.getApplicationPrefName() +"\n";
        message += "CVM: " + transactionData.getCVM() +"\n";
        message += "card entry mode: " + transactionData.getCardEntryMode() +"\n";

        if (!transactionData.getPreAuthCode().isEmpty())
            message += "PreAuth Code: " + transactionData.getPreAuthCode() + "\n";

        if (!transactionData.getTipAmount().isEmpty())
            message += "Tip amount: " + transactionData.getTipAmount() + "\n";

        if (!transactionData.getOperatorCode().isEmpty())
            message += "Operator code: " + transactionData.getOperatorCode() + "\n";

        if (transactionData.getIsDccUsed()) {
            message += "DCC Currency: " + transactionData.getDccCurrency() + "\n";
            message += "DCC Amount: " + transactionData.getDccAmount() + "\n";
            message += "DCC Exchange rate: " + transactionData.getDccCardExchangeRate() + "\n";
        }

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Transaction data");
        alertBuilder.setPositiveButton("OK", null);
        alertBuilder.setMessage(message);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        });
    }

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
            case R.id.preauth_btn:
                startPreAuth();
                break;
            case R.id.preauth_completion_btn:
                startPreAuthCompletion();
                break;
            case R.id.preauth_cancellation_btn:
                startPreAuthCancellation();
                break;
            case R.id.payment_request_btn:
                startPaymentRequest();
                break;
            case R.id.print_receipt_btn:
                testPrint();
                break;
            case R.id.reprint_receipt_btn:
                reprintLastReceipt();
                break;
            case R.id.sam_btn:
                startSAMTest();
                break;
            case R.id.giftcard_btn:
                startGiftCards();
                break;
            case R.id.scanner_btn:
                sendBroadcast(new Intent(MyPOSUtil.SCANNER_BROADCAST));
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
            public void onReady(int position) {
               Intent i = new Intent(MainActivity.this, PaymentActivity.class);
               switch (position) {
                   case IOptionsSelected.POSITION_FIRST:
                       i.putExtra("tran_spec", Utils.TRANSACTION_SPEC_REGULAR);
                       break;
                   case IOptionsSelected.POSITION_SECOND:
                       i.putExtra("tran_spec", Utils.TRANSACTION_SPEC_MOTO);
                       break;
                   case IOptionsSelected.POSITION_THIRD:
                       i.putExtra("tran_spec", Utils.TRANSACTION_SPEC_GIFTCARD);
                       break;
               }
               i.putExtra("preferences", PreferencesManager.getInstance().getPreferences());
               startActivityForResult(i, Utils.PAYMENT_ACTIVITY_REQUEST_CODE);
            }
        }, new CharSequence[] {getString(R.string.regular_transaction), getString(R.string.moto_transaction), getString(R.string.giftcard_transaction)});
    }

    /**
     * Starts a refund transaction
     */
    private void startRefund() {
        displayPaymentOptions(new IOptionsSelected() {
            @Override
            public void onReady(int position) {
                Intent i = new Intent(MainActivity.this, RefundActivity.class);
                switch (position) {
                    case IOptionsSelected.POSITION_FIRST:
                        i.putExtra("tran_spec", Utils.TRANSACTION_SPEC_REGULAR);
                        break;
                    case IOptionsSelected.POSITION_SECOND:
                        i.putExtra("tran_spec", Utils.TRANSACTION_SPEC_MOTO);
                        break;
                    case IOptionsSelected.POSITION_THIRD:
                        i.putExtra("tran_spec", Utils.TRANSACTION_SPEC_GIFTCARD);
                        break;
                }
                i.putExtra("preferences", PreferencesManager.getInstance().getPreferences());
                startActivityForResult(i, Utils.REfUND_ACTIVITY_REQUEST_CODE);
            }
        }, new CharSequence[] {getString(R.string.regular_transaction), getString(R.string.moto_transaction), getString(R.string.giftcard_transaction)});
    }

    /**
     * Starts a void transaction
     */
    private void startVoid() {
        displayPaymentOptions(new IOptionsSelected() {
            @Override
            public void onReady(int position) {
                startVoid(position == IOptionsSelected.POSITION_SECOND);
            }
        }, new CharSequence[]{getString(R.string.void_last_tran), getString(R.string.void_by_tran_data)});
    }

    private void startPreAuth() {
        displayPaymentOptions(new IOptionsSelected() {
            @Override
            public void onReady(int position) {
                Intent i = new Intent(MainActivity.this, PreAuthActivity.class);
                switch (position) {
                    case IOptionsSelected.POSITION_FIRST:
                        i.putExtra("tran_spec", Utils.TRANSACTION_SPEC_REGULAR);
                        break;
                    case IOptionsSelected.POSITION_SECOND:
                        i.putExtra("tran_spec", Utils.TRANSACTION_SPEC_MOTO);
                        break;
                }
                i.putExtra("preferences", PreferencesManager.getInstance().getPreferences());
                startActivityForResult(i, Utils.PREAUTH_ACTIVITY_REQUEST_CODE);
            }
        }, new CharSequence[] {getString(R.string.regular_transaction), getString(R.string.moto_transaction)});
    }

    private void startPreAuthCompletion() {
        Intent i = new Intent(MainActivity.this, PreAuthCompletionActivity.class);
        i.putExtra("preferences", PreferencesManager.getInstance().getPreferences());
        startActivityForResult(i, Utils.PREAUTH_COMPLETION_ACTIVITY_REQUEST_CODE);
    }


    private void startPreAuthCancellation() {
        Intent i = new Intent(MainActivity.this, PreAuthCancellationActivity.class);
        i.putExtra("preferences", PreferencesManager.getInstance().getPreferences());
        startActivityForResult(i, Utils.PREAUTH_CANCELLATION_ACTIVITY_REQUEST_CODE);
    }


    /**
     * Starts a void transaction by transaction data
     */
    private void startVoid(boolean isByTranData) {
        MyPOSVoid.Builder voidParams = MyPOSVoid.builder();

        voidParams.printCustomerReceipt(PreferencesManager.getInstance().getCustomerReceiptMode())
                .printMerchantReceipt(PreferencesManager.getInstance().getMerchantReceiptMode());

        if (isByTranData) {
            int voidStan = 0;
            try {
                voidStan = Integer.parseInt(PreferencesManager.getInstance().getLastTransactionStan());
            } catch (NumberFormatException ignored) {
            }

            String voidAuthCode = PreferencesManager.getInstance().getLastTransactionAuth();
            String voidDateTime = PreferencesManager.getInstance().getLastTransactionDateTime();

            if (voidStan == 0 || voidAuthCode == null || voidDateTime == null) {
                showToast("No last transaction data found");
                return;
            }

            voidParams.STAN(voidStan)
                    .authCode(voidAuthCode)
                    .dateTime(voidDateTime)
                    .voidLastTransactionFlag(false);
        }
        else
            voidParams.voidLastTransactionFlag(true);

        MyPOSAPI.openVoidActivity(this, voidParams.build(), Utils.VOID_REQUEST_CODE, PreferencesManager.getInstance().getSkipConfirmationScreenflag());
    }

    /**
     * Starts a payment request transaction
     */
    private void startPaymentRequest() {
        startActivityForResult(new Intent(MainActivity.this, PaymentRequestActivity.class), Utils.PAYMENT_REQUEST_ACTIVITY_REQUEST_CODE);
    }

    private void startGiftCards() {
        displayPaymentOptions(new IOptionsSelected() {
            @Override
            public void onReady(int position) {
                switch (position) {
                    case IOptionsSelected.POSITION_FIRST:
                        Intent i = new Intent(MainActivity.this, GiftCardActivationActivity.class);
                        i.putExtra("preferences", PreferencesManager.getInstance().getPreferences());
                        startActivityForResult(i, Utils.GIFTCARD_ACTIVATION_ACTIVITY_REQUEST_CODE);
                        break;
                    case IOptionsSelected.POSITION_SECOND:
                        MyPOSAPI.openGiftCardDeactivationActivity(MainActivity.this, UUID.randomUUID().toString(), Utils.GIFTCARD_DEACTIVATION_REQUEST_CODE);
                        break;
                    case IOptionsSelected.POSITION_THIRD:
                        MyPOSAPI.openGiftCardCheckBalanceActivity(MainActivity.this, UUID.randomUUID().toString(), Utils.GIFTCARD_BALANCE_CHECK_REQUEST_CODE);
                        break;
                }
            }
        }, new CharSequence[] {getString(R.string.giftcard_activation), getString(R.string.giftcard_deactivation), getString(R.string.giftcard_balance)});
    }

    private void displayPaymentOptions(final IOptionsSelected optionsSelected, CharSequence[] items) {
        final int[] position = new int[1];

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(getString(R.string.transaction_type))
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface var1, int pos) {
                        position[0] = pos;
                    }
                }).setPositiveButton(R.string.contin, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                optionsSelected.onReady(position[0]);
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


    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private interface IOptionsSelected {
        int POSITION_FIRST = 0;
        int POSITION_SECOND = 1;
        int POSITION_THIRD = 2;

        void onReady(int position);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(printerBroadcastReceiver);
        unregisterReceiver(scannerBroadcastReceiver);
    }
}
