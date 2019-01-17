package com.mypos.mypospaymentdemo.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.IFragmentResult;
import com.mypos.mypospaymentdemo.util.KeyboardHandler;

@SuppressLint("ValidFragment")
public class MultiOperatorCodeFragment extends Fragment {

    private FrameLayout frameLayout;
    private KeyboardHandler keyboardHandler;
    private IFragmentResult resultListener;
    private int requestCode;

    public MultiOperatorCodeFragment(int requestCode, IFragmentResult resultListener) {
        super();

        this.requestCode = requestCode;
        this.resultListener = resultListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        frameLayout = new FrameLayout(getActivity());
        View view = inflater.inflate(R.layout.fragment_amount, null);
        frameLayout.addView(view);
        return frameLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {

        TextView title = (TextView) view.findViewById(R.id.title_text);

        title.setText(R.string.operator_code);
        view.findViewById(R.id.currency_layout).setVisibility(View.GONE);
        view.findViewById(R.id.code_layout).setVisibility(View.VISIBLE);

        ((TextView) view.findViewById(R.id.code_text)).setHint("****");

        String lastCode = "";

        if (keyboardHandler != null)
            lastCode = keyboardHandler.getCode();

        keyboardHandler = new KeyboardHandler(getActivity(), view.findViewById(R.id.root_view), KeyboardHandler.INPUT_TYPE_CODE, true, 4);
        keyboardHandler.setCode(lastCode);

        view.findViewById(R.id.charge_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!keyboardHandler.getCode().isEmpty()){
                    Intent intent = new Intent();
                    intent.putExtra("operator_code", keyboardHandler.getCode());
                    resultListener.setResult(requestCode, Activity.RESULT_OK, intent);
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
