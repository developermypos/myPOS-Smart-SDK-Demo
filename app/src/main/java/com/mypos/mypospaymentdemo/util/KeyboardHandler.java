package com.mypos.mypospaymentdemo.util;

import android.content.Context;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.mypos.mypospaymentdemo.R;


public class KeyboardHandler implements View.OnClickListener, View.OnLongClickListener {

    public static final int INPUT_TYPE_AMOUNT           = 0;
    public static final int INPUT_TYPE_CODE             = 1;

    public static final int          LARGE_TEXT_LIMIT   = 7;


    private Context                 mContext;
    private View                    mLayout;
    private TextView                mSumTextView;
    private TextView                mCurrencyTextView;
    private TextView mCodeTextView;

    private Vibrator                mVibrator;

    private int                     maxChars;
    private float                   mInitialSumTextSize;
    private float                   mInitialCurrencyTextSize;
    private String                  mAmount                     = "";
    private String mCode = "";
    private String                  mSumText                    = "";
    private String                  mAmountChars                = "";
    private int                     mInputType;
    private boolean                 mIsDecimalAllowed           = true;

    public KeyboardHandler(Context context, View view, int inputType){
        mContext    = context;
        mLayout     = view;
        mInputType  = inputType;

        init();
    }

    public KeyboardHandler(Context context, View view, int inputType,boolean isDecimalAllowed, int maxChars){
        mContext    = context;
        mLayout     = view;
        mInputType  = inputType;
        mIsDecimalAllowed = isDecimalAllowed;
        this.maxChars = maxChars;

        init();
    }

    private void init(){
        mVibrator           = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        mCurrencyTextView = (TextView) mLayout.findViewById(R.id.currency_text);
        mSumTextView = (TextView) mLayout.findViewById(R.id.sum_text);
        mCodeTextView = (TextView) mLayout.findViewById(R.id.code_text);

        mInitialSumTextSize = mSumTextView.getTextSize() / mContext.getResources().getDisplayMetrics().scaledDensity;
        mInitialCurrencyTextSize = mCurrencyTextView.getTextSize() / mContext.getResources().getDisplayMetrics().scaledDensity;

        mLayout.findViewById(R.id.backspace_btn).setOnClickListener(this);
        mLayout.findViewById(R.id.keyboard_number_00).setOnClickListener(this);
        mLayout.findViewById(R.id.backspace_btn).setOnLongClickListener(this);

        mSumTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                scaleAmount();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        setKeyboardNumberClickListener();
    }

    @Override
    public void onClick(View view) {
        if( view.getId() == R.id.backspace_btn)
            setBackspaceListener();
        else if (view.getId() == R.id.keyboard_number_00)
            setKeyboardNumber00Listener();
    }

    @Override
    public boolean onLongClick(View view) {
        if( view.getId() == R.id.backspace_btn)
            setLongBackspaceListener();

        return false;
    }

    private void setKeyboardNumberClickListener() {
        switch (mInputType) {
            case INPUT_TYPE_AMOUNT:
                setAmountListener();
                break;
            case INPUT_TYPE_CODE:
                setCodeListener();
                break;
        }
    }

    private void setAmountListener() {
        mSumText = mIsDecimalAllowed ? "0.00" : "0";
        mSumTextView.setText(mSumText);
        for (int i = 0; i < 10; i++) {
            int keyId = mContext.getResources().getIdentifier("keyboard_number_" + i, "id", mContext.getPackageName());
            TextView keyboardKey = (TextView) mLayout.findViewById(keyId);

            final int finalI = i;
            keyboardKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mAmountChars.length() < maxChars) {

                        if(mAmountChars.length() == 0 && finalI == 0)
                            return;

                        mAmountChars += String.valueOf(finalI);
                        if (mIsDecimalAllowed) {
                            switch (mAmountChars.length()) {
                                case 0:
                                    mAmount = "0.00";
                                    break;
                                case 1:
                                    mAmount = "0.0" + mAmountChars;
                                    break;
                                case 2:
                                    mAmount = "0." + mAmountChars;
                                    break;
                                default:
                                    mAmount = mAmountChars.substring(0, mAmountChars.length() - 2) + "." + mAmountChars.substring(mAmountChars.length() - 2);
                                    break;
                            }
                        } else {
                            mAmount = mAmountChars;
                        }
                        mSumText = mAmount;
                        mSumTextView.setText(mSumText);
                        mVibrator.vibrate(15);
                    }
                }
            });
        }
    }

    private void setCodeListener() {
        for (int i = 0; i < 10; i++) {
            int keyId = mContext.getResources().getIdentifier("keyboard_number_" + i, "id", mContext.getPackageName());
            TextView keyboardKey = (TextView) mLayout.findViewById(keyId);

            final int finalI = i;
            keyboardKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCode.length() < maxChars) {
                        mCode += String.valueOf(finalI);

                        mCodeTextView.setText(mCode);
                        mVibrator.vibrate(15);
                    }
                }
            });
        }
    }

    private void setBackspaceListener() {
        switch (mInputType) {
            case INPUT_TYPE_AMOUNT:
                setAmountBackspaceListener();
                break;
            case INPUT_TYPE_CODE:
                setCodeBackspaceListener();
                break;
        }
    }

    private void setAmountBackspaceListener() {
        if (mAmountChars.length() > 0) {
            mAmountChars = mAmountChars.substring(0, mAmountChars.length() - 1);
            if (mIsDecimalAllowed) {
                switch (mAmountChars.length()) {
                    case 0:
                        mAmount = "0.00";
                        break;
                    case 1:
                        mAmount = "0.0" + mAmountChars;
                        break;
                    case 2:
                        mAmount = "0." + mAmountChars;
                        break;
                    default:
                        mAmount = mAmountChars.substring(0, mAmountChars.length() - 2) + "." + mAmountChars.substring(mAmountChars.length() - 2);
                        break;
                }
            }
            else {
                mAmount = mAmountChars.isEmpty() ? "0" : mAmountChars;
            }
            mSumText = mAmount;
            mSumTextView.setText(mSumText);
            mVibrator.vibrate(15);
        }
    }

    private void setCodeBackspaceListener() {
        if (mCode.length() > 0) {
            mCode = mCode.substring(0, mCode.length() - 1);

            mCodeTextView.setText(mCode);
            mVibrator.vibrate(15);
        }
    }

    private void setLongBackspaceListener() {
        switch (mInputType) {
            case INPUT_TYPE_AMOUNT:
                setAmountClearListener();
                break;
            case INPUT_TYPE_CODE:
                setCodeClearListener();
                break;
        }
    }

    private void setAmountClearListener() {
        mAmountChars       = "";
        mSumText = mAmount = mIsDecimalAllowed ? "0.00" : "0";
        mSumTextView.setText(mSumText);
        mVibrator.vibrate(15);
    }

    private void setCodeClearListener() {
        mCode = "";
        mCodeTextView.setText(mCode);
        mVibrator.vibrate(15);
    }

    private void setKeyboardNumber00Listener() {
        switch (mInputType) {
            case INPUT_TYPE_AMOUNT:
                setAmount00Listener();
                break;

            case INPUT_TYPE_CODE:
                setCode00Listener();
                break;
        }
    }

    private void setAmount00Listener() {
        if(mAmountChars.length() > 0 && mAmountChars.length() < maxChars - 1){
            mAmountChars    += "00";
            if (mIsDecimalAllowed) {
                mAmount = mAmountChars.substring(0, mAmountChars.length() - 2) + "." + mAmountChars.substring(mAmountChars.length() - 2);
            }
            else {
                mAmount = mAmountChars;
            }
            mSumText           = mAmount;
            mSumTextView.setText(mSumText);
        }
    }

    private void setCode00Listener() {
        if (mCode.length() < maxChars - 1) {
            mCode += "00";

            mCodeTextView.setText(mCode);
            mVibrator.vibrate(15);
        }
    }

    private void scaleAmount(){
        if(mSumText.length() > LARGE_TEXT_LIMIT) {
            mSumTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Math.round(mInitialSumTextSize * LARGE_TEXT_LIMIT / mSumText.length()));
            mCurrencyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Math.round(mInitialCurrencyTextSize * LARGE_TEXT_LIMIT / mSumText.length()));
        }
        else {
            mSumTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialSumTextSize);
            mCurrencyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialCurrencyTextSize);
        }
    }

    public void setInputType(int inputType) {
        if (mInputType == inputType)
            return;

        mInputType = inputType;

        setKeyboardNumberClickListener();
    }

    public int getInputType() {
        return mInputType;
    }

    public void setAmount(String amount){
        mAmountChars = amount.replace(".", "").trim();

        while (mAmountChars.startsWith("0")){
            mAmountChars = mAmountChars.substring(1);
        }

        if (mIsDecimalAllowed) {
            switch (mAmountChars.length()) {
                case 0:
                    mAmount = "0.00";
                    break;
                case 1:
                    mAmount = "0.0" + mAmountChars;
                    break;
                case 2:
                    mAmount = "0." + mAmountChars;
                    break;
                default:
                    mAmount = mAmountChars.substring(0, mAmountChars.length() - 2) + "." + mAmountChars.substring(mAmountChars.length() - 2);
                    break;
            }
        }
        else {
            mAmount = mAmountChars.isEmpty() ? "0" : mAmountChars;
        }

        mSumText = mAmount;

        mSumTextView.setText(mSumText);
    }

    public void setCode(String code){
        mCode = code;

        mCodeTextView.setText(mCode);

    }

    public String getAmount(){
        return mAmount;
    }

    public String getCode(){
        return mCode;
    }

    public void reset(){
        if (mInputType == INPUT_TYPE_AMOUNT) {
            mSumText = mIsDecimalAllowed ? "0.00" : "0";
            mAmount = mSumText;
            mAmountChars = "";
            mSumTextView.setText(mSumText);
        }
        else {
            mSumText = "";
            mCode = "";
            mAmountChars = "";
            mCodeTextView.setText(mSumText);
        }
    }
}