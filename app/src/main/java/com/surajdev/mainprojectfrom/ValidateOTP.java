package com.surajdev.mainprojectfrom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;
import java.util.Random;

public class ValidateOTP extends AppCompatActivity {
    EditText EDTOTP;
    int otp;
    int userEnteredOTP;
    SharedPreferences sp;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_otp);
        Objects.requireNonNull(getSupportActionBar()).hide();
        EDTOTP = findViewById(R.id.otpenter);
        Bundle bundle=getIntent().getExtras();
        otp = bundle.getInt("otp");
        Log.w("machotp",""+otp);
        sp = getApplicationContext().getSharedPreferences(Util.PREFS, MODE_PRIVATE);
        edit = sp.edit();

    }


    public void Verify(View view) {

userEnteredOTP = Integer.parseInt(EDTOTP.getText().toString().trim());
        if(otp == userEnteredOTP){
            edit.putBoolean("isLogin",true);
            edit.apply();
            startActivity(new Intent(this, BottomNavigationActivity.class));
            finish();

        }else{
            Toast.makeText(ValidateOTP.this, "Invalid Otp", Toast.LENGTH_SHORT).show();
        }

    }
}