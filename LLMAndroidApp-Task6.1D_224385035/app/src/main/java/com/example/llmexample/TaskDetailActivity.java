package com.example.llmexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskDetailActivity extends AppCompatActivity {

    private static final String TAG = "TaskDetailActivity";
    
    // Define all the elements
    private TextView taskTitleTextView;
    private TextView taskDescriptionTextView;
    
    // Define Question 1 parts
    private TextView question1TextView;
    private TextView question1DetailTextView;
    private RadioGroup question1RadioGroup;
    private RadioButton q1OptionA, q1OptionB, q1OptionC, q1OptionD;
    
    // Define Question 2 parts
    private TextView question2TextView;
    private TextView question2DetailTextView;
    private RadioGroup question2RadioGroup;
    private RadioButton q2OptionA, q2OptionB, q2OptionC, q2OptionD;
    
    // Define Question 3 parts
    private TextView question3TextView;
    private TextView question3DetailTextView;
    private RadioGroup question3RadioGroup;
    private RadioButton q3OptionA, q3OptionB, q3OptionC, q3OptionD;
    
    private Button submitButton;
    private ConstraintLayout loadingContainer;
    
    // To create a list to save all quiz questions
    private List<QuizQuestion> quizQuestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // To setup all screen items
        taskTitleTextView = findViewById(R.id.taskTitleTextView);
        taskDescriptionTextView = findViewById(R.id.taskDescriptionTextView);
        
        // To setup question 1 items
        question1TextView = findViewById(R.id.question1TextView);
        question1DetailTextView = findViewById(R.id.question1DetailTextView);
        question1RadioGroup = findViewById(R.id.question1RadioGroup);
        q1OptionA = findViewById(R.id.q1OptionA);
        q1OptionB = findViewById(R.id.q1OptionB);
        q1OptionC = findViewById(R.id.q1OptionC);
        q1OptionD = findViewById(R.id.q1OptionD);
        
        // To setup question 2 items
        question2TextView = findViewById(R.id.question2TextView);
        question2DetailTextView = findViewById(R.id.question2DetailTextView);
        question2RadioGroup = findViewById(R.id.question2RadioGroup);
        q2OptionA = findViewById(R.id.q2OptionA);
        q2OptionB = findViewById(R.id.q2OptionB);
        q2OptionC = findViewById(R.id.q2OptionC);
        q2OptionD = findViewById(R.id.q2OptionD);
        
        // To setup question 3 items
        question3TextView = findViewById(R.id.question3TextView);
        question3DetailTextView = findViewById(R.id.question3DetailTextView);
        question3RadioGroup = findViewById(R.id.question3RadioGroup);
        q3OptionA = findViewById(R.id.q3OptionA);
        q3OptionB = findViewById(R.id.q3OptionB);
        q3OptionC = findViewById(R.id.q3OptionC);
        q3OptionD = findViewById(R.id.q3OptionD);
        
        submitButton = findViewById(R.id.submitButton);
        loadingContainer = findViewById(R.id.loadingContainer);

        // To get topic from previous page
        String topic = getIntent().getStringExtra("topic");
        if (topic == null || topic.isEmpty()) {
            topic = "Deakin University";
        }

        // To show loading message
        taskTitleTextView.setText(topic + " Quiz");
        taskDescriptionTextView.setText("Loading questions about " + topic + "...");
        
        // To get quiz from internet
        fetchQuizFromApi(topic);

        // To create submit button function
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To check if all questions answered
                if (question1RadioGroup.getCheckedRadioButtonId() == -1 || 
                    question2RadioGroup.getCheckedRadioButtonId() == -1 ||
                    question3RadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(TaskDetailActivity.this, "Please answer all questions and then you can submit", Toast.LENGTH_SHORT).show();
                    return;
                }

                // To get what information user selected
                int selectedQ1Id = question1RadioGroup.getCheckedRadioButtonId();
                RadioButton selectedQ1 = findViewById(selectedQ1Id);
                String answer1 = selectedQ1.getText().toString();
                
                int selectedQ2Id = question2RadioGroup.getCheckedRadioButtonId();
                RadioButton selectedQ2 = findViewById(selectedQ2Id);
                String answer2 = selectedQ2.getText().toString();
                
                int selectedQ3Id = question3RadioGroup.getCheckedRadioButtonId();
                RadioButton selectedQ3 = findViewById(selectedQ3Id);
                String answer3 = selectedQ3.getText().toString();

                // To go to results page with answers
                Intent intent = new Intent(TaskDetailActivity.this, ResultsActivity.class);
                intent.putExtra("answer1", answer1);
                intent.putExtra("correctAnswer1", quizQuestions.get(0).getOptions().get(getAnswerOptionIndex(quizQuestions.get(0).getCorrectAnswer())));
                intent.putExtra("question1", quizQuestions.get(0).getQuestion());
                
                intent.putExtra("answer2", answer2);
                intent.putExtra("correctAnswer2", quizQuestions.get(1).getOptions().get(getAnswerOptionIndex(quizQuestions.get(1).getCorrectAnswer())));
                intent.putExtra("question2", quizQuestions.get(1).getQuestion());
                
                intent.putExtra("answer3", answer3);
                intent.putExtra("correctAnswer3", quizQuestions.get(2).getOptions().get(getAnswerOptionIndex(quizQuestions.get(2).getCorrectAnswer())));
                intent.putExtra("question3", quizQuestions.get(2).getQuestion());
                
                startActivity(intent);
            }
        });
    }
    
    private void fetchQuizFromApi(String topic) {
        // To show loading screen
        loadingContainer.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);
        
        // To set up API address
        // 10.0.2.2 means localhost on my computer from emulator
        // Then I set that users could select any topic they are interested in
        String url = "http://10.0.2.2:5000/getQuiz?topic=" + topic;
        RequestQueue queue = Volley.newRequestQueue(this);

        // To create request to get quiz
        // And choose those good questions as much as possible
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // To hide loading screen when done
                        loadingContainer.setVisibility(View.GONE);
                        submitButton.setEnabled(true);
                        
                        try {
                            Log.i(TAG, "Response received: " + response.toString());
                            JSONArray quizArray = response.getJSONArray("quiz");
                            
                            // To skip example questions that are not correct
                            List<JSONObject> validQuestions = new ArrayList<>();
                            for (int i = 0; i < quizArray.length(); i++) {
                                JSONObject quizQuestion = quizArray.getJSONObject(i);
                                String question = quizQuestion.getString("question");
                                
                                // Skip fake questions
                                if (question.equals("[Your question here]?")) {
                                    continue;
                                }
                                
                                // Skip fake answers
                                String correctAnswer = quizQuestion.getString("correct_answer");
                                if (correctAnswer.equals("[Correct answer letter]")) {
                                    continue;
                                }
                                
                                // If the questions are good, save it
                                validQuestions.add(quizQuestion);
                            }
                            
                            // To check if enough questions exist
                            if (validQuestions.size() < 3) {
                                Toast.makeText(TaskDetailActivity.this, 
                                    "Not enough valid questions received from API. Only got " + 
                                    validQuestions.size(), Toast.LENGTH_LONG).show();
                                return;
                            }

                            // To process first 3 good questions
                            for (int i = 0; i < Math.min(3, validQuestions.size()); i++) {
                                JSONObject quizQuestion = validQuestions.get(i);

                                String question = quizQuestion.getString("question");
                                JSONArray optionsArray = quizQuestion.getJSONArray("options");
                                String correctAnswer = quizQuestion.getString("correct_answer");
                                
                                // To get all answer choices
                                List<String> options = new ArrayList<>();
                                for (int j = 0; j < Math.min(4, optionsArray.length()); j++) {
                                    options.add(optionsArray.getString(j));
                                }
                                
                                // To save question in my list
                                quizQuestions.add(new QuizQuestion(question, options, correctAnswer));
                            }

                            // To show questions on screen
                            updateQuizUI();
                            
                            Toast.makeText(TaskDetailActivity.this, "Questions loaded successfully!", Toast.LENGTH_SHORT).show();
                            
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON: " + e.getMessage(), e);
                            Toast.makeText(TaskDetailActivity.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Hide loading and show error
                        loadingContainer.setVisibility(View.GONE);
                        submitButton.setEnabled(true);

                        // Error checking message
                        String errorMsg = "Unknown error";
                        if (error.networkResponse != null) {
                            errorMsg = "HTTP " + error.networkResponse.statusCode + ": " + new String(error.networkResponse.data);
                        } else if (error.getMessage() != null) {
                            errorMsg = error.getMessage();
                        }
                        Log.e(TAG, "Error fetching quiz: " + errorMsg, error);
                        Toast.makeText(TaskDetailActivity.this, "Error fetching questions: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });

        // To make request wait longer before timeout
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                40000, // 40 seconds to wait
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // To send the request
        Log.i(TAG, "Sending request to: " + url);
        queue.add(jsonObjectRequest);
    }
    
    private void updateQuizUI() {
        if (quizQuestions.size() >= 3) {
            // Update quiz title and instructions
            taskDescriptionTextView.setText("This is a quiz about the selected topic. Please answer the following three questions.");
            
            // Show completed question 1
            QuizQuestion q1 = quizQuestions.get(0);
            question1TextView.setText("Question 1:");
            question1DetailTextView.setText(q1.getQuestion());
            q1OptionA.setText(q1.getOptions().get(0));
            q1OptionB.setText(q1.getOptions().get(1));
            q1OptionC.setText(q1.getOptions().get(2));
            q1OptionD.setText(q1.getOptions().get(3));
            
            // Show completed question 2
            QuizQuestion q2 = quizQuestions.get(1);
            question2TextView.setText("Question 2:");
            question2DetailTextView.setText(q2.getQuestion());
            q2OptionA.setText(q2.getOptions().get(0));
            q2OptionB.setText(q2.getOptions().get(1));
            q2OptionC.setText(q2.getOptions().get(2));
            q2OptionD.setText(q2.getOptions().get(3));
            
            // Show completed question 3
            QuizQuestion q3 = quizQuestions.get(2);
            question3TextView.setText("Question 3:");
            question3DetailTextView.setText(q3.getQuestion());
            q3OptionA.setText(q3.getOptions().get(0));
            q3OptionB.setText(q3.getOptions().get(1));
            q3OptionC.setText(q3.getOptions().get(2));
            q3OptionD.setText(q3.getOptions().get(3));
        }
    }
    
    // To store question information
    private static class QuizQuestion {
        // Define all the question information elements
        private final String question;
        private final List<String> options;
        private final String correctAnswer;
        
        public QuizQuestion(String question, List<String> options, String correctAnswer) {
            this.question = question;
            this.options = options;
            this.correctAnswer = correctAnswer;
        }
        
        public String getQuestion() {
            return question;
        }
        
        public List<String> getOptions() {
            return options;
        }
        
        public String getCorrectAnswer() {
            return correctAnswer;
        }
    }
    
    // Convert letter to number
    private int getAnswerOptionIndex(String answerLetter) {
        switch (answerLetter) {
            case "A": return 0;
            case "B": return 1;
            case "C": return 2;
            case "D": return 3;
            default: return 0; // Default to A if unknown
        }
    }
} 