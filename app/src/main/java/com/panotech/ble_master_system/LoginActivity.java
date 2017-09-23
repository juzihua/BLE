package com.panotech.ble_master_system;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.text.format.Time;

import com.panotech.ble_master_system_bluetooth.CommonData;

import java.text.ParseException;

public class LoginActivity extends Activity {
    private Button mButton;
    private EditText mID, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (CommonData.isOver()) {
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_login);

        mID = (EditText)findViewById(R.id.user_id);
        mPassword = (EditText)findViewById(R.id.password);
        mButton = (Button)findViewById(R.id.login_button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mID.getText().toString().equals("demo") && mPassword.getText().toString().equals("0011")) {
                        Toast.makeText(getApplicationContext(), getText(R.string.login_ok), Toast.LENGTH_LONG).show();
                        Intent login = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(login);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), getText(R.string.login_failed), Toast.LENGTH_LONG).show();
                    }
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), getText(R.string.login_failed), Toast.LENGTH_LONG).show();
                }
            }
        });



    }
}
