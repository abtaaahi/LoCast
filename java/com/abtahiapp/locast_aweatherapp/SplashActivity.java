package com.abtahiapp.locast_aweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_TIME_OUT = 2000; // 2 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get a reference to the ImageView
        ImageView imageView = findViewById(R.id.imageView);
        // Load the animation
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        // Apply the animation to the ImageView
        imageView.startAnimation(fadeInAnimation);
        // Set up a timer to transition to the main activity after SPLASH_TIME_OUT milliseconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity and close the splash activity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}