package com.mypos.mypospaymentdemo.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mypos.mypospaymentdemo.R;

public class CredentialActivity extends AppCompatActivity {

    private EditText inputET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        setContentView(R.layout.activity_reference_number);

        String lastInput = "";
        if (inputET != null)
            lastInput = inputET.getText().toString();

        TextView title = (TextView) findViewById(R.id.title_text);
        TextView inputTitle = (TextView) findViewById(R.id.reference_number_title);
        inputET = (EditText) findViewById(R.id.reference_number_et);

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
                        getIntent().putExtra("credential", inputET.getText().toString());
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }
                    else {
                        Toast.makeText(CredentialActivity.this, "Please enter valid phone or e-mail", Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.charge_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputET.getText().toString().isEmpty()) {
                    getIntent().putExtra("credential", inputET.getText().toString());
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
                else {
                    Toast.makeText(CredentialActivity.this, "Please enter valid phone or e-mail", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        init();
    }
}
