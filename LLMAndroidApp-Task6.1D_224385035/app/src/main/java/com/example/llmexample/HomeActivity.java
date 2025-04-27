package com.example.llmexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeActivity extends AppCompatActivity {

    // Define all the elements
    private TextView userNameTextView;
    private CardView taskCardView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // To find elements from screen
        userNameTextView = findViewById(R.id.userNameTextView);
        taskCardView = findViewById(R.id.taskCardView);
        logoutButton = findViewById(R.id.logoutButton);

        // Show username on screen
        String username = getIntent().getStringExtra("username");
        if (username != null && !username.isEmpty()) {
            userNameTextView.setText(username);
        }

        // To make card clickable for quiz
        taskCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To open topic selection page
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        
        // To create logout function
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To show message to user
                Toast.makeText(HomeActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                // To go back to login page
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                // To prevent going back with back button
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
} 