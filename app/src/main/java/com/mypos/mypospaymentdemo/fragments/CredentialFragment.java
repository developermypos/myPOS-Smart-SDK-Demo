package com.mypos.mypospaymentdemo.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
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

@SuppressLint("ValidFragment")
public class CredentialFragment extends Fragment {

    private FrameLayout frameLayout;
    private EditText inputET;
    private IFragmentResult resultListener;
    private int requestCode;

    public CredentialFragment(int requestCode, IFragmentResult resultListener) {
        super();

        this.requestCode = requestCode;
        this.resultListener = resultListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {frameLayout = new FrameLayout(getActivity());
        View view = inflater.inflate(R.layout.fragment_reference_number, null);
        frameLayout.addView(view);
        return frameLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {

        String lastInput = "";
        if (inputET != null)
            lastInput = inputET.getText().toString();

        TextView title = (TextView) view.findViewById(R.id.title_text);
        TextView inputTitle = (TextView) view.findViewById(R.id.reference_number_title);
        inputET = (EditText) view.findViewById(R.id.reference_number_et);

        inputET.setText(lastInput);

        title.setText(R.string.send_to);
        inputTitle.setText(R.string.credential_title);
        inputET.setHint(R.string.credential_hint);

        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(40);
        inputET.setFilters(fArray);

        inputET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_NEXT){
                    if (!inputET.getText().toString().isEmpty()) {
                        Intent intent = new Intent();
                        intent.putExtra("credential", inputET.getText().toString());
                        resultListener.setResult(requestCode, Activity.RESULT_OK, intent);
                    }
                    else {
                        Toast.makeText(getActivity(), "Please enter valid phone or e-mail", Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
                return false;
            }
        });

        view.findViewById(R.id.charge_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputET.getText().toString().isEmpty()) {
                    Intent intent = new Intent();
                    intent.putExtra("credential", inputET.getText().toString());
                    resultListener.setResult(requestCode, Activity.RESULT_OK, intent);
                }
                else {
                    Toast.makeText(getActivity(), "Please enter valid phone or e-mail", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        frameLayout.removeAllViews();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_amount, null);
        frameLayout.addView(view);

        init(view);
    }
}
