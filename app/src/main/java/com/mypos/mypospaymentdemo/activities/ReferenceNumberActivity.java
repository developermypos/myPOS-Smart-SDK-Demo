package com.mypos.mypospaymentdemo.activities;

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
import com.mypos.smartsdk.MyPOSAPI;
import com.mypos.smartsdk.MyPOSUtil;
import com.mypos.smartsdk.ReferenceType;

public class ReferenceNumberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference_number);

        TextView title = (TextView) findViewById(R.id.title_text);
        TextView referenceNumberTitle = (TextView) findViewById(R.id.reference_number_title);
        final EditText referenceNumberInput = (EditText) findViewById(R.id.reference_number_et);

        title.setText("Reference number");
        referenceNumberTitle.setText("Enter: " + getReferenceNumberTxt(PersistentDataManager.getInstance().getReferenceNumberMode()));

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
                return "Off";
            case ReferenceType.REFERENCE_NUMBER:
                return "Reference number";
            case ReferenceType.INVOICE_ID:
                return "Invoice number";
            case ReferenceType.PRODUCT_ID:
                return "Product ID";
            case ReferenceType.RESERVATION_NUMBER:
                return "Reservation number";
        }

        return "Off";
    }
}
