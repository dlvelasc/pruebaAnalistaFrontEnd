package com.example.tokenfronend;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    ProgressBar timerBar;
    TextSwitcher textSwitcher;
    private TextView textView;
    String clientID = "1";
    long timeUntilNextStep=60000;
    RequestQueue queue;
    long timeCr;
    boolean sync = false;

    CountDownTimer cdTimer;

    CountDownTimer fixedTimer = new CountDownTimer(60000,100) {
        @Override
        public void onTick(long millisUntilFinished) {
            timerBar.setProgress((int)millisUntilFinished);

        }

        @Override
        public void onFinish(){
            updateToken();
            fixedTimer.start();
        }
    };

private void updateToken(){
    postDataUsingVolley(this.clientID);


}

private void postDataUsingVolley(String clientID) {
    RequestQueue queue = Volley.newRequestQueue(this);
    String url = "http://10.0.2.2:3000/generarToken/"+ clientID ;

// Request a string response from the provided URL.
    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.
                    try {
                        JSONObject obj=new JSONObject(response);
                        textSwitcher.setText(obj.getString("token"));
                        timeCr = obj.getLong("createdOn");
                        if(!sync) {
                            sync=true;
                            timeUntilNextStep = ((timeCr+1000)% 60000);
                            timerBar.setProgress((int) timeUntilNextStep);
                            cdTimer = new CountDownTimer(timeUntilNextStep, 100) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    timerBar.setProgress((int) (millisUntilFinished));

                                }

                                @Override
                                public void onFinish() {
                                    updateToken();
                                    fixedTimer.start();
                                }
                            };
                            cdTimer.start();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    });

// Add the request to the RequestQueue.
    queue.add(stringRequest);
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerBar=(ProgressBar) findViewById(R.id.timeBar);
        textSwitcher = (TextSwitcher) findViewById(R.id.textSwitcher);
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView switcherTextView = new TextView(getApplicationContext());
                switcherTextView.setTextSize(48);
                switcherTextView.setTextColor(Color.RED);
                switcherTextView.setGravity(Gravity.CENTER);

                return switcherTextView;
            }
        });
         updateToken();


    }

}