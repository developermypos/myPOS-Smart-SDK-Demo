package com.mypos.mypospaymentdemo.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.IFragmentResult;
import com.mypos.smartsdk.MyPOSUtil;
import com.mypos.smartsdk.ReferenceType;

@SuppressLint("ValidFragment")
public class ReferenceNumberFragment extends Fragment {

    private FrameLayout frameLayout;
    private EditText referenceNumberInput;
    private IFragmentResult resultListener;
    private int requestCode;
    private int referenceNumberMode;

    public ReferenceNumberFragment(int requestCode, IFragmentResult resultListener) {
        super();

        this.requestCode = requestCode;
        this.resultListener = resultListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        frameLayout = new FrameLayout(getActivity());
        View view = inflater.inflate(R.layout.fragment_reference_number, null);
        frameLayout.addView(view);
        return frameLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    public void setReferenceNumberMode(int mode) {
        referenceNumberMode = mode;
    }

    private void init(View view) {

        String lastRefNumber = "";
        if (referenceNumberInput != null)
            lastRefNumber = referenceNumberInput.getText().toString();

        TextView title = (TextView) view.findViewById(R.id.title_text);
        TextView referenceNumberTitle = (TextView) view.findViewById(R.id.reference_number_title);
        referenceNumberInput = (EditText) view.findViewById(R.id.reference_number_et);

        referenceNumberTitle.setText(lastRefNumber);

        title.setText(R.string.reference_number);
        referenceNumberTitle.setText(getString(R.string.enter) + getReferenceNumberTxt(referenceNumberMode));

        referenceNumberInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_NEXT){
                    if (referenceNumberInput.getText().toString().isEmpty() || MyPOSUtil.isReferenceNumberValid(referenceNumberInput.getText().toString())) {
                        Intent intent = new Intent();
                        String refNumber = referenceNumberInput.getText().toString();
                        intent.putExtra("reference_number", refNumber.isEmpty() ? null : refNumber);
                        resultListener.setResult(requestCode, Activity.RESULT_OK, intent);
                    }
                    else {
                        Toast.makeText(getActivity(), "Please enter valid reference number", Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
                return false;
            }
        });

        view.findViewById(R.id.charge_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (referenceNumberInput.getText().toString().isEmpty() || MyPOSUtil.isReferenceNumberValid(referenceNumberInput.getText().toString())) {
                    Intent intent = new Intent();
                    String refNumber = referenceNumberInput.getText().toString();
                    intent.putExtra("reference_number", refNumber.isEmpty() ? null : refNumber);
                    resultListener.setResult(requestCode, Activity.RESULT_OK, intent);
                }
                else {
                    Toast.makeText(getActivity(), "Please enter valid reference number", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String getReferenceNumberTxt(int type) {
        switch (type) {
            case ReferenceType.OFF:
                return getString(R.string.off);
            case ReferenceType.REFERENCE_NUMBER:
                return getString(R.string.reference_number);
            case ReferenceType.INVOICE_ID:
                return getString(R.string.invoice_number);
            case ReferenceType.PRODUCT_ID:
                return getString(R.string.product_id);
            case ReferenceType.RESERVATION_NUMBER:
                return getString(R.string.reservation_number);
        }

        return getString(R.string.off);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        frameLayout.removeAllViews();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_reference_number, null);
        frameLayout.addView(view);

        init(view);
    }
}
