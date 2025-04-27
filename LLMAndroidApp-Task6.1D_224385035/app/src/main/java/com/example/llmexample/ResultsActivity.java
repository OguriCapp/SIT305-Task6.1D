package com.example.llmexample;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = "ResultsActivity";

    // Define all the elements
    private TextView response1TextView;
    private TextView response2TextView;
    private TextView response3TextView;
    private Button continueButton;
    private ConstraintLayout loadingContainer;

    // To store data from previous screen
    private String question1, question2, question3;
    private String answer1, answer2, answer3;
    private String correctAnswer1, correctAnswer2, correctAnswer3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // To connect Java with XML elements
        response1TextView = findViewById(R.id.response1TextView);
        response2TextView = findViewById(R.id.response2TextView);
        response3TextView = findViewById(R.id.response3TextView);
        continueButton = findViewById(R.id.continueButton);
        loadingContainer = findViewById(R.id.loadingContainer);

        // To make text boxes scrollable
        response1TextView.setMovementMethod(new ScrollingMovementMethod());
        response2TextView.setMovementMethod(new ScrollingMovementMethod());
        response3TextView.setMovementMethod(new ScrollingMovementMethod());

        // To get data from quiz screen
        question1 = getIntent().getStringExtra("question1");
        question2 = getIntent().getStringExtra("question2");
        question3 = getIntent().getStringExtra("question3");
        
        answer1 = getIntent().getStringExtra("answer1");
        answer2 = getIntent().getStringExtra("answer2");
        answer3 = getIntent().getStringExtra("answer3");
        
        correctAnswer1 = getIntent().getStringExtra("correctAnswer1");
        correctAnswer2 = getIntent().getStringExtra("correctAnswer2");
        correctAnswer3 = getIntent().getStringExtra("correctAnswer3");

        // To show the loading spinner
        loadingContainer.setVisibility(View.VISIBLE);
        continueButton.setEnabled(false);

        // Get feedback for answers Api
        getFeedbackFromApi();

        // To make continue button function
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To create a function to go back to home screen
                Intent intent = new Intent(ResultsActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
    
    private void getFeedbackFromApi() {
        // To create API url
        String url = "http://10.0.2.2:5000/getQuiz?topic=feedback"; //This will not work
        RequestQueue queue = Volley.newRequestQueue(this);
        
        // To prepare data for API
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("topic", "feedback");
            
            // To create text for results
            final String aiResponse1 = question1 + "\n\n" +
                    "Your answer: " + answer1 + "\n" +
                    "Correct answer: " + correctAnswer1 + "\n\n" +
                    //This is just example code that's not used
                    "There are NO rubrics to follow for adaptive assessment marking. Thanks for using!";
                    
            final String aiResponse2 = question2 + "\n\n" +
                    "Your answer: " + answer2 + "\n" +
                    "Correct answer: " + correctAnswer2 + "\n\n" +
                    //This is just example code that's not used
                    "There are NO rubrics to follow for adaptive assessment marking. Thanks for using!";
                    
            final String aiResponse3 = question3 + "\n\n" +
                    "Your answer: " + answer3 + "\n" +
                    "Correct answer: " + correctAnswer3 + "\n\n" +
                    //This is just example code that's not used
                    "There are NO rubrics to follow for adaptive assessment marking. Thanks for using!";

            // To show feedback on screen
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    response1TextView.setText(aiResponse1);
                    response2TextView.setText(aiResponse2);
                    response3TextView.setText(aiResponse3);
                    loadingContainer.setVisibility(View.GONE);
                    continueButton.setEnabled(true);
                }
            });
            
        } catch (JSONException e) {
            // To handle results errors
            Log.e(TAG, "Error creating JSON request body: " + e.getMessage(), e);
            Toast.makeText(ResultsActivity.this, "Error creating request: " + e.getMessage(), Toast.LENGTH_LONG).show();
            loadingContainer.setVisibility(View.GONE);
            continueButton.setEnabled(true);
        }

        // To create API request object
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // To hide loading when API responds
                        loadingContainer.setVisibility(View.GONE);
                        continueButton.setEnabled(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // To ignore errors since we're not using API
                        Log.d(TAG, "Expected error from API call (endpoint not implemented): " + error.getMessage());
                    }
                });

        // To set timeout for request, more quickly is better
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,  // 5 seconds to wait
                0,     // With no retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // To send API request
        Log.i(TAG, "Sending request for feedback");
        // Not actually sending this
    }
} 