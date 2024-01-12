package com.example.weatherp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Removing status bar [Full Screen]
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Setting Splash
        splashScreen();
    }

    private void splashScreen() {
        int SPLASH_TIME = 3000;
        new Handler().postDelayed(() -> {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);

            startActivity(intent);
            finish();
        }, SPLASH_TIME);
    }
}