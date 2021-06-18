package com.surajdev.mainprojectfrom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    // One Preview Image
    CircularImageView cc;
    TextView Useraddress;
    LottieAnimationView locationtag;
    FusedLocationProviderClient fusedLocationProviderClient;
    EditText EDTName, EDTPhone, EDTPassword;
    StringRequest stringRequest;
    Bitmap bitmap;
    String encodedImage;
    // constant to compare
    // the activity result code
    static final int GALLERY_REQUEST =2;
    List<Address> addresses;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).hide();
        cc = findViewById(R.id.userpht);
        locationtag = findViewById(R.id.logloc);
        Useraddress = findViewById(R.id.useraddress);
        EDTName = findViewById(R.id.username);
        EDTPhone = findViewById(R.id.name);
        EDTPassword = findViewById(R.id.editText);
        requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        }

    public void getCurrentLocation(){

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null){
                    Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());

                    try {
                        locationtag.setVisibility(View.GONE);
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    /*    latitude.setText("Lattitude: " + addresses.get(0).getLatitude());
                        latitude.setText("Longitude: " + addresses.get(0).getLongitude());
                        latitude.setText("Country: " + addresses.get(0).getCountryName());
                        latitude.setText("Locality: " + addresses.get(0).getLocality());
                        latitude.setText("Address: " + addresses.get(0).getAddressLine(0));*/

                        Log.d("TAG", "Lattitude: " + addresses.get(0).getLatitude());
                        Log.d("TAG", "Longitude: " + addresses.get(0).getLongitude());
                        Log.d("TAG", "Country: " + addresses.get(0).getCountryName());
                        Log.d("TAG", "Locality: " + addresses.get(0).getLocality());
                        Log.d("TAG", "Address: " + addresses.get(0).getAddressLine(0));
                        Useraddress.setText(addresses.get(0).getAddressLine(0));



                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    void insertUser(){
        StringRequest request = new StringRequest(Request.Method.POST, Util.LOCAL_INSERT_USER
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.w("rre",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("Status");
                    if (message.contains("OK")) {
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        Toast.makeText(RegisterActivity.this, "Inserted" + message, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(RegisterActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("image", encodedImage);
                params.put("Name",EDTName.getText().toString().trim());
                params.put("Phone", EDTPhone.getText().toString().trim());
                params.put("Password",EDTPassword.getText().toString().trim());
                params.put("Latitude", String.valueOf(addresses.get(0).getLatitude()));
                params.put("Longitude",String.valueOf(addresses.get(0).getLongitude()));
                params.put("Address", Useraddress.getText().toString().trim());
                params.put("Score", String.valueOf(0));
                return params;
            }
        };


        requestQueue.add(request);

    }






    // this function is triggered when user
    // selects the image from the imageChooser
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 1 && resultCode == RESULT_OK && data!=null){

            Uri filePath = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                cc.setImageBitmap(bitmap);

                imageStore(bitmap);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void imageStore(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

        byte[] imageBytes = stream.toByteArray();

        encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }

    public void ChooseImage(View view) {

        Intent intent  = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select Image"),1);

    }

    public void Register(View view) {
insertUser();    }

    public void GetLocation(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //printout the location
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    public void Login(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}