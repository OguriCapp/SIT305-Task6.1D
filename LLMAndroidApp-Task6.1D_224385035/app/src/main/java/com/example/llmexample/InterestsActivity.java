package com.example.llmexample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class InterestsActivity extends AppCompatActivity {

    // Define all the elements
    private Button nextButton;
    private List<TextView> interestTags;
    private List<String> selectedInterests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        // Initialize UI components
        nextButton = findViewById(R.id.nextButton);
        interestTags = new ArrayList<>();
        selectedInterests = new ArrayList<>();

        // Get all interest tags
        interestTags.add(findViewById(R.id.algorithmTag1));
        interestTags.add(findViewById(R.id.dataStructuresTag1));
        interestTags.add(findViewById(R.id.webDevTag1));
        interestTags.add(findViewById(R.id.testingTag1));
        interestTags.add(findViewById(R.id.algorithmTag2));
        interestTags.add(findViewById(R.id.dataStructuresTag2));
        interestTags.add(findViewById(R.id.webDevTag2));
        interestTags.add(findViewById(R.id.testingTag2));
        interestTags.add(findViewById(R.id.algorithmTag3));
        interestTags.add(findViewById(R.id.dataStructuresTag3));
        interestTags.add(findViewById(R.id.webDevTag3));
        interestTags.add(findViewById(R.id.testingTag3));

        // Set clicking events for all the interest tags
        for (final TextView tag : interestTags) {
            tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toggle the selection state of the tag with animation
                    toggleInterestSelection(tag);
                }
            });
        }

        // Apply fade-in animation for all the tags
        for (int i = 0; i < interestTags.size(); i++) {
            final TextView tag = interestTags.get(i);
            tag.setAlpha(0f);
            tag.setVisibility(View.VISIBLE);
            
            tag.animate()
                .alpha(1f)
                .setDuration(300)
                .setStartDelay(i * 50) 
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        }

        // Set next button click event with animation
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To get username from previous Activity
                String username = getIntent().getStringExtra("username");
                
                // This is the button press animation
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(nextButton, "scaleX", 0.9f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(nextButton, "scaleY", 0.9f);
                scaleDownX.setDuration(100);
                scaleDownY.setDuration(100);
                scaleDownX.start();
                scaleDownY.start();
                
                scaleDownX.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(nextButton, "scaleX", 1f);
                        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(nextButton, "scaleY", 1f);
                        scaleUpX.setDuration(100);
                        scaleUpY.setDuration(100);
                        scaleUpX.start();
                        scaleUpY.start();
                        
                        scaleUpY.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                // Navigate to home page after animation completes
                                Intent intent = new Intent(InterestsActivity.this, HomeActivity.class);
                                intent.putExtra("username", username);
                                
                                // Add transition animation
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    // Toggle the selection state of an interest tag with animation
    private void toggleInterestSelection(TextView tag) {
        String interest = tag.getText().toString();
        
        // If already selected, unselect it
        if (selectedInterests.contains(interest)) {
            selectedInterests.remove(interest);
            
            // Animation for unselection
            tag.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(150)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        tag.setBackgroundResource(R.drawable.interest_tag_background);
                        tag.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .setListener(null)
                            .start();
                    }
                })
                .start();
        } else {
            // If not selected, add to selected list
            if (selectedInterests.size() < 10) {  // Maximum 10 selections
                selectedInterests.add(interest);
                
                // Animation for selection
                tag.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(150)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            tag.setBackgroundResource(R.drawable.interest_tag_background_selected);
                            tag.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(150)
                                .setListener(null)
                                .start();
                        }
                    })
                    .start();
            }
        }
    }
} 