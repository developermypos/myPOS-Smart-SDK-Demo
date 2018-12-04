package com.mypos.mypospaymentdemo.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mypos.mypospaymentdemo.R;
import com.mypos.mypospaymentdemo.util.PersistentDataManager;
import com.mypos.smartsdk.MyPOSUtil;
import com.mypos.smartsdk.ReferenceType;

public class ReferenceNumberActivity extends AppCompatActivity {

    private EditText referenceNumberInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.activity_reference_number);

        String lastRefNumber = "";
        if (referenceNumberInput != null)
            lastRefNumber = referenceNumberInput.getText().toString();

        TextView title = (TextView) findViewById(R.id.title_text);
        TextView referenceNumberTitle = (TextView) findViewById(R.id.reference_number_title);
        referenceNumberInput = (EditText) findViewById(R.id.reference_number_et);

        referenceNumberTitle.setText(lastRefNumber);

        title.setText(R.string.reference_number);
        referenceNumberTitle.setText(getString(R.string.enter) + getReferenceNumberTxt(PersistentDataManager.getInstance().getReferenceNumberMode()));

        referenceNumberInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_NEXT){
                    if (referenceNumberInput.getText().toString().isEmpty() || MyPOSUtil.isReferenceNumberValid(referenceNumberInput.getText().toString())) {
                        getIntent().putExtra("operator_code", referenceNumberInput.getText().toString());
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }
                    else {
                        Toast.makeText(ReferenceNumberActivity.this, "Please enter valid reference number", Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.charge_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (referenceNumberInput.getText().toString().isEmpty() || MyPOSUtil.isReferenceNumberValid(referenceNumberInput.getText().toString())) {
                    getIntent().putExtra("operator_code", referenceNumberInput.getText().toString());
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
                else {
                    Toast.makeText(ReferenceNumberActivity.this, "Please enter valid reference number", Toast.LENGTH_LONG).show();
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

        init();
    }
}
