package com.surajdev.mainprojectfrom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    View view;
    Button StartGame;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    RecyclerView recyclerView;
    MyAdapter myAdapter;
    List<User> imageList;
    User modelImage;
    LinearLayoutManager linearLayoutManager;
    RequestQueue requestQueue;
    TextView WelcomeUser;
    StringRequest stringRequest;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).hide();
        sp = this.getActivity().getSharedPreferences(Util.PREFS, Context.MODE_PRIVATE);
        editor = sp.edit();
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

      view = inflater.inflate(R.layout.fragment_home, container, false);
      StartGame = view.findViewById(R.id.ggn);
      WelcomeUser = view.findViewById(R.id.txtwelcome);
        recyclerView = view.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        imageList = new ArrayList<>();
        myAdapter = new MyAdapter(getContext(),imageList);
        recyclerView.setAdapter(myAdapter);


      StartGame.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              startActivity(new Intent(getContext(), GameActivity.class));

             // replace(new ProfileFragment());
          }
      });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        WelcomeUser.setText("Welcome\n"+sp.getString("username"," "));

        if(imageList!=null && imageList.size()>0){
            imageList.clear();
        }
        FetchTopThree();
    }

    public void FetchTopThree(){
        stringRequest = new StringRequest(Request.Method.GET, Util.LOCAL_FETCH_SCORE, new Response.Listener<String>() {
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
                        int id = jsonObject.getInt("ID");
                        String Name = jsonObject.getString("NAME");
                        int Score = jsonObject.getInt("SCORE");
                        String ImageUrl = jsonObject.getString("IMAGE");
                       String UrlImg = Util.LOCAL_USER_IMAGE + "/" + ImageUrl;

                        modelImage = new User(id, +Score, Name, UrlImg, lat, longg);
                        imageList.add(modelImage);
                        myAdapter.notifyDataSetChanged();


                    }

                }


                catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Some exeception"+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
        );

        requestQueue.add(stringRequest);

    }



}