package com.surajdev.mainprojectfrom;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    MediaPlayer mediaPlayer;
    private ImageView ball;
    private ImageView hole;
    private ImageView line1;
    private ImageView line2;
    private ImageView line3;
    private ImageView line4;
    private ImageView line5;
    private ImageView line6;
    private ImageView line7;

    public int score = 600;
    private int seconds = 0;
    private int screenWidth;
    static int newscore;
    private int screenHeight;
    private int minutes = 0;
    private int hours = 0;
    private boolean running = true;

    private TextView timeview;

    private float timer_x_pos = 750;
    private float timer_y_pos = -20;
    private float ball_x_pos = 0.0f;
    private float ball_y_pos = 0.0f;

    private float hole_x_pos = 850;
    private float hole_y_pos = 1850;

    private float line1_x = 0;
    private float line1_y = 50;

    private float line2_x = 700;
    private float line2_y = 400;

    private float line3_x = 0;
    private float line3_y = 700;

    private float line4_x = 550;
    private float line4_y = 1000;

    private float line5_x = 200;
    private float line5_y = 1300;

    private float line6_x = 700;
    private float line6_y = 1600;

    private float line7_x = 0;
    private float line7_y = 1600;

    SharedPreferences sp;
    SharedPreferences.Editor edit;

    RequestQueue requestQueue;
    SensorManager sensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Objects.requireNonNull(getSupportActionBar()).hide();
        ball = findViewById(R.id.basket_ball);
        hole = findViewById(R.id.hole);
        line1 = findViewById(R.id.line);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        line4 = findViewById(R.id.line4);
        line5 = findViewById(R.id.line5);
        line6 = findViewById(R.id.line6);
        line7 = findViewById(R.id.line7);
        timeview = findViewById(R.id.time_view);

        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.start();
        if (savedInstanceState != null) {

            // Get the previous state of the stopwatch

            seconds
                    = savedInstanceState
                    .getInt("seconds");

            running
                    = savedInstanceState
                    .getBoolean("running");


        }

        runTimer();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {

            Sensor gyrosensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (gyrosensor != null) {

                sensorManager.registerListener(this, gyrosensor, SensorManager.SENSOR_DELAY_GAME);
            }
        }


        timeview.getLayoutParams().height = 200;
        timeview.getLayoutParams().width = 500;

        ball.getLayoutParams().height = 150;
        ball.getLayoutParams().width = 150;

        hole.getLayoutParams().height = 200;
        hole.getLayoutParams().width = 200;

        line1.getLayoutParams().height = 400;
        line1.getLayoutParams().width = 400;

        line2.getLayoutParams().height = 400;
        line2.getLayoutParams().width = 400;

        line3.getLayoutParams().height = 400;
        line3.getLayoutParams().width = 400;

        line4.getLayoutParams().height = 400;
        line4.getLayoutParams().width = 400;


        line5.getLayoutParams().height = 400;
        line5.getLayoutParams().width = 400;

        line6.getLayoutParams().height = 400;
        line6.getLayoutParams().width = 400;

        line7.getLayoutParams().height = 400;
        line7.getLayoutParams().width = 400;


        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;


        timeview.setX(timer_x_pos);
        timeview.setY(timer_y_pos);

        ball.setX(ball_x_pos);
        ball.setY(ball_y_pos);


        hole.setX(hole_x_pos);
        hole.setY(hole_y_pos);

        line1.setX(line1_x);
        line1.setY(line1_y);

        line2.setX(line2_x);
        line2.setY(line2_y);

        line3.setX(line3_x);
        line3.setY(line3_y);

        line4.setX(line4_x);
        line4.setY(line4_y);

        line5.setX(line5_x);
        line5.setY(line5_y);

        line6.setX(line6_x);
        line6.setY(line6_y);

        line7.setX(line7_x);
        line7.setY(line7_y);

        sp = getApplicationContext().getSharedPreferences(Util.PREFS, MODE_PRIVATE);
        edit = sp.edit();
        requestQueue = Volley.newRequestQueue(GameActivity.this);


    }


    @Override
    public void onSensorChanged(SensorEvent event) {


        if (screenHeight > ball_y_pos) {

            ball_y_pos += event.values[1];

            ball.setY(ball_y_pos);


        }


        if (screenWidth + 800 < ball_y_pos) {

            ball_y_pos -= event.values[1];

            ball.setY(ball_y_pos);

        }

        if (screenWidth > ball_x_pos) {

            ball_x_pos += event.values[0];
            ball.setX(ball_x_pos);

        }

        if (screenWidth - 200 < ball_x_pos) {

            ball_x_pos -= event.values[0];
            ball.setX(ball_x_pos);


        }


        if (ball_x_pos < 0) {
            ball_x_pos -= event.values[0];
            ball.setX(ball_x_pos);

        }
        if (ball_y_pos < 0) {
            ball_y_pos -= event.values[1];
            ball.setY(ball_y_pos);

        }


        if (ball_x_pos >= 200 && ball_x_pos <= 500) {
            //Toast.makeText(this, "touched at left!!!", Toast.LENGTH_LONG).show();

            ball_y_pos -= event.values[1];

            ball.setY(ball_y_pos);


            // ball_y_pos = event.values[1];

            // ball.setY(ball_y_pos);
        }

        if (ball_y_pos >= 1850) {

            if (ball_x_pos >= 850) {

                running = false;
                mediaPlayer.stop();
                sensorManager.unregisterListener(this);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Game is over!!!" + "\n Your Score is: " + score);
                builder1.setCancelable(false);
                newscore = score;

                builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateScores();
                    }
                });



                AlertDialog alert11 = builder1.create();
                alert11.show();


            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
        mediaPlayer.stop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void runTimer() {

        // Get the text view.
        final TextView timeView
                = (TextView) findViewById(
                R.id.time_view);

        // Creates a new Handler
        final Handler handler
                = new Handler();

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(new Runnable() {
            @Override

            public void run() {


                int secs = seconds % 60;
                score = score - 2 * (secs / 6);
                String time
                        = String.format(Locale.getDefault(), "%d:%02d", minutes, secs);

                // Set the text view text.
                timeView.setText(time);

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++;
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 500);
            }
        });
    }





    void UpdateScores(){
        StringRequest request = new StringRequest(Request.Method.POST, Util.LOCAL_SCORE_UPDATE
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.w("rre",response);
               startActivity(new Intent(GameActivity.this, BottomNavigationActivity.class));
               finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GameActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", ""+sp.getInt("custid",0));
                Log.w("idididi", ""+sp.getInt("custid",0));
                params.put("score", ""+newscore);
                Log.w("idscore", ""+newscore);

                return params;
            }
        };


        requestQueue.add(request);

    }
}