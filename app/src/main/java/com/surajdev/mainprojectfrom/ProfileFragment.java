package com.surajdev.mainprojectfrom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.GetChars;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ProfileFragment extends Fragment {
    View view;
    CircularImageView profilePicture;
    TextView txtname, txtaddress, txtScores;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    RequestQueue requestQueue;
    String Name;
    String Address, Score;
    String UrlImg;
    Button Logout;

    private BottomNavigationView bottomNavigationView;



    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = this.getActivity().getSharedPreferences(Util.PREFS, Context.MODE_PRIVATE);
        editor = sp.edit();
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

    }

    @Override
    public void onStart() {
        super.onStart();
        fetchProfile();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        Logout = view.findViewById(R.id.logout);
        profilePicture = view.findViewById(R.id.userpht);
        txtname = view.findViewById(R.id.prfname);
                txtaddress = view.findViewById(R.id.prdaddr);
                txtScores = view.findViewById(R.id.prdscore);
        bottomNavigationView.getMenu().findItem(R.id.profile).setChecked(true);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext() , LoginActivity.class));
                SharedPreferences settings = Objects.requireNonNull(getContext()).getSharedPreferences(Util.PREFS, Context.MODE_PRIVATE);
                settings.edit().clear().apply();
                Objects.requireNonNull(getActivity()).finish();




            }
        });

        return  view;
    }

    public void fetchProfile()

    {

        StringRequest request = new StringRequest(Request.Method.POST, Util.LOCAL_FETCH_PROFILE,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.w("response",response);

                try {
                    JSONObject object = new JSONObject(response);
                    String message = object.getString("message");
                    JSONArray jsonArray = object.getJSONArray("data");
                    if(message.equals("success"))
                    {

                        for(int i=0; i<jsonArray.length();i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            int user_id = jsonObject.getInt("ID");
                            Log.d("pi", String.valueOf(user_id));
                             Name = jsonObject.getString("NAME");
                            String ImageUrl = jsonObject.getString("IMAGE");
                            Address = jsonObject.getString("ADDRESS");
                            Score = jsonObject.getString("SCORE");
                            UrlImg = Util.LOCAL_USER_IMAGE + "/" + ImageUrl;

                        }

                        Glide
                                .with(requireContext()) // get context of Fragment
                                .load(UrlImg)
                                .into(profilePicture);

                                txtname.setText("Name: "+Name);
                                 txtaddress.setText("Address: "+Address);
                                 txtScores.setText("Score: "+Score);

                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();



            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> map = new HashMap<>();

                map.put("cust_id",""+sp.getInt("custid",0));
                Log.w("iio",""+sp.getInt("custid",0));
                return map;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }
}