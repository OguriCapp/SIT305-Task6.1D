package com.example.llmexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Define all the elements
    private static final String TAG = "MainActivity";
    private EditText topicEditText;
    private Button generateButton;

    @Override   
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To find the specific text box and button from layout
        topicEditText = findViewById(R.id.topicEditText);
        generateButton = findViewById(R.id.generateButton);

        // Create this generator to handle button clicking
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To get what text user typed
                String topic = topicEditText.getText().toString().trim();
                
                // To check if user typed anything or empty
                if (topic.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a topic", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // To jump to the next screen with selected topic
                Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
                intent.putExtra("topic", topic);
                startActivity(intent);
            }
        });
    }
}