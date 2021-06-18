package com.surajdev.mainprojectfrom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class MapsFragment extends Fragment {

    private GoogleMap mMap;
    private MarkerOptions options = new MarkerOptions();
    RequestQueue requestQueue;
    StringRequest stringRequest;
    LatLng sydney;
    CameraPosition cameraPosition;
    int scoreTopUser;
    double latTopUser;
    double longiTopUser;
    String nameTopUser;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {



        @Override
        public void onMapReady(final GoogleMap googleMap) {
            mMap = googleMap;
            FetchMarkers();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {



        return inflater.inflate(R.layout.fragment_maps, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        requestQueue = Volley.newRequestQueue(requireActivity().getApplicationContext());


    }

    public void FetchMarkers(){
        stringRequest = new StringRequest(Request.Method.GET, Util.LOCAL_FETCH_MARKERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("data");


                    for(int i =0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);


                        // Lat , Long columns de names va Table ch
                        double lat =  jsonObject.getDouble("LATITUDE");
                        double longg = jsonObject.getDouble("LONGITUDE");
                        String Name = jsonObject.getString("NAME");
                        String Score = jsonObject.getString("SCORE");


                        sydney = new LatLng(lat, longg);
                        mMap.addMarker(new MarkerOptions()
                                .position(sydney)
                                .title("Name: "+Name+"  Score: "+Score)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));


                    }
                    // this is whenthe user is coming from recyclerview
                    if (getArguments() != null)

                    {
                        latTopUser = getArguments().getDouble("lat");
                        longiTopUser = getArguments().getDouble("longi");
                        nameTopUser = getArguments().getString("name");
                        scoreTopUser = getArguments().getInt("score");

                        cameraPosition = new CameraPosition.Builder().target(new LatLng(latTopUser, longiTopUser)).zoom(15).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }

                    else {
                        cameraPosition = new CameraPosition.Builder().target(new LatLng(sydney.latitude, sydney.longitude)).zoom(10).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }



                }



                catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Some exeception"+e, Toast.LENGTH_SHORT).show();

                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error"+error, Toast.LENGTH_SHORT).show();

                    }
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }
}