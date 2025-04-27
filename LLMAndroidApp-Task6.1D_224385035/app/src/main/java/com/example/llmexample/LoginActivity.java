package com.example.llmexample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // Define all the elements
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView needAccountTextView;
    private TextView welcomeTextView;
    private TextView studentTextView;
    private TextView encouragementTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // To connect Java with the specific XML elements
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        needAccountTextView = findViewById(R.id.needAccountTextView);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        studentTextView = findViewById(R.id.studentTextView);
        encouragementTextView = findViewById(R.id.encouragementTextView);

        // To make some animation for login page
        animateLoginElements();

        // To make login button function
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get username and password
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // To check if they are empty
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    // To add some other animation when button is pressed
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(loginButton, "scaleX", 0.9f);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(loginButton, "scaleY", 0.9f);
                    scaleDownX.setDuration(100);
                    scaleDownY.setDuration(100);
                    scaleDownX.start();
                    scaleDownY.start();
                    
                    scaleDownX.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Make button go back to normal size
                            ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(loginButton, "scaleX", 1f);
                            ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(loginButton, "scaleY", 1f);
                            scaleUpX.setDuration(100);
                            scaleUpY.setDuration(100);
                            scaleUpX.start();
                            scaleUpY.start();
                            
                            scaleUpY.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    // Go to home page when login is done
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    finish();
                                }
                            });
                        }
                    });
                }
            }
        });

        // To make "need account" text clickable
        needAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To go to register page
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    // To create animations for all screen items
    private void animateLoginElements() {
        // To setup items before animation
        welcomeTextView.setAlpha(0f);
        welcomeTextView.setVisibility(View.VISIBLE);
        
        studentTextView.setAlpha(0f);
        studentTextView.setVisibility(View.VISIBLE);
        
        encouragementTextView.setAlpha(0f);
        encouragementTextView.setVisibility(View.VISIBLE);
        
        usernameEditText.setAlpha(0f);
        usernameEditText.setVisibility(View.VISIBLE);
        usernameEditText.setTranslationY(50f);
        
        passwordEditText.setAlpha(0f);
        passwordEditText.setVisibility(View.VISIBLE);
        passwordEditText.setTranslationY(50f);
        
        loginButton.setAlpha(0f);
        loginButton.setVisibility(View.VISIBLE);
        loginButton.setTranslationY(50f);
        
        needAccountTextView.setAlpha(0f);
        needAccountTextView.setVisibility(View.VISIBLE);
        
        // To animate the welcome text first
        welcomeTextView.animate()
            .alpha(1f)
            .setDuration(500)
            .setInterpolator(new DecelerateInterpolator())
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // To animate student text next
                    studentTextView.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .setInterpolator(new DecelerateInterpolator())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                // To animate encouragement text next
                                encouragementTextView.animate()
                                    .alpha(1f)
                                    .setDuration(500)
                                    .setInterpolator(new DecelerateInterpolator())
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            // To animate login form last
                                            animateFormElements();
                                        }
                                    })
                                    .start();
                            }
                        })
                        .start();
                }
            })
            .start();
    }

    // To show login form with animation
    private void animateFormElements() {
        // To fade in username box
        usernameEditText.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setInterpolator(new DecelerateInterpolator())
            .start();
        
        // To fade in password box after username
        passwordEditText.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(100)
            .setDuration(400)
            .setInterpolator(new DecelerateInterpolator())
            .start();
        
        // To fade in login button after password
        loginButton.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(200)
            .setDuration(400)
            .setInterpolator(new DecelerateInterpolator())
            .start();
        
        // To fade in register link last
        needAccountTextView.animate()
            .alpha(1f)
            .setStartDelay(300)
            .setDuration(400)
            .setInterpolator(new DecelerateInterpolator())
            .start();
    }
} 