package com.surajdev.mainprojectfrom;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {


    SharedPreferences sp;
    SharedPreferences.Editor edit;
    int USER_ID;
    String USER_NAME;
    String MOBILE;
    int otp;
    EditText EDTPhone;
    String PhoneNum;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
        sp = getApplicationContext().getSharedPreferences(Util.PREFS, MODE_PRIVATE);
        edit = sp.edit();
        EDTPhone = findViewById(R.id.email);
        requestQueue = Volley.newRequestQueue(LoginActivity.this);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(
                            PermissionGrantedResponse response) {/* ... */}
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();


    }

    public void SignUp(View view) {
        startActivity(new Intent(this, RegisterActivity.class));

    }

    public void LogIn(View view) {
        PhoneNum = EDTPhone.getText().toString().trim();
        login();

    }

    public int generateOTP() throws Exception {
        Random generator = new Random();
        generator.setSeed(System.currentTimeMillis());
        int num = generator.nextInt(99999) + 99999;
        if (num < 100000 || num > 999999) {
            num = generator.nextInt(99999) + 99999;
            if (num < 100000 || num > 999999) {
                throw new Exception("BONG123");
            }
        }
        return num;
    }

    public void login()
    {

        StringRequest request = new StringRequest(Request.Method.POST, Util.LOCAL_LOGIN_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.w("ass",""+response);

                try {
                    JSONObject object=new JSONObject(response);
                    JSONArray array=object.getJSONArray("user");
                    String message=object.getString("message");

                    if (message.contains("Login Successful")){
                        try {
                            otp= generateOTP();
                        } catch (Exception e) {
                            otp=127856;
                        }

                        if ((!EDTPhone.getText().toString().isEmpty())) {
                            try {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(EDTPhone.getText().toString(), null, "OTP to get register with us is "+otp, null, null);
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, "MSG Can not sent Check Your Balance", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Toast.makeText(LoginActivity.this, "All Fields Are Mandatory", Toast.LENGTH_SHORT).show();
                        }


                        JSONObject object1 = array.getJSONObject(0);
                        USER_NAME = object1.getString("NAME");
                        MOBILE = object1.getString("PHONE");
                        USER_ID = object1.getInt("ID");

                        Log.w("phn",MOBILE);


                        Log.w("iii",""+USER_ID);

                        edit.putString("mobile", MOBILE);
                        edit.putInt("custid",USER_ID);
                        edit.putString("username", USER_NAME);
                        edit.apply();
                        Intent intent=new Intent(LoginActivity.this,ValidateOTP.class);
                        intent.putExtra("otp",otp);
                        Toast.makeText(LoginActivity.this, "OTP Sent to your mobile number", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "User Found Successful", Toast.LENGTH_SHORT).show();
                        saveLoginDEtails();

                    }

                    else{
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                }

                catch (JSONException e)
                {
                    Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Toast.makeText(LoginActivity.this, "Volley error"+error.getMessage(), Toast.LENGTH_SHORT).show();

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(LoginActivity.this, "Connection timed out", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                } else if (error instanceof ServerError) {
                    Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {

                    Toast.makeText(LoginActivity.this, "Your device is not connected to internet.", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(LoginActivity.this, "Json Parsing Error", Toast.LENGTH_SHORT).show();

                }

            }
        })
        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> map=new HashMap<>();
                map.put("phone", PhoneNum);
                return map;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        requestQueue.add(request);    }

    void saveLoginDEtails(){
      //  edit.putString("Username", etLogin_id.getText().toString());
      //  edit.putString("Password", etPassword.getText().toString());
      //  edit.apply();
    }
}