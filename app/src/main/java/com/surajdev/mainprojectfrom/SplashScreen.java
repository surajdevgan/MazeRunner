package com.surajdev.mainprojectfrom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    Handler handler;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    SharedPreferences sp;
    SharedPreferences.Editor edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();
        sp = getApplicationContext().getSharedPreferences(Util.PREFS, MODE_PRIVATE);
        edit = sp.edit();

        handler = new Handler();


            handler.postDelayed(new Runnable() {@Override
            public void run() {

                if(sp.getBoolean("isLogin", false))
                {
                    startActivity(new Intent(SplashScreen.this, BottomNavigationActivity.class));


                }

                else {

                    startActivity(new Intent(SplashScreen.this,RegisterActivity.class));
                }



            }
            }, 4000);

    }
}