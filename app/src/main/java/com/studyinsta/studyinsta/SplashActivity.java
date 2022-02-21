package com.studyinsta.studyinsta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 1200;
    private FirebaseAuth firebaseAuth;

    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();

        //Animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.splash_top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.splash_bottom_animation);

        //Hooks
        image = findViewById(R.id.logo_in_splash_activity);
        logo = findViewById(R.id.app_name_in_splash);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if (getIntent().getStringExtra("PRODUCT_ID") == null) {
                    if (currentUser == null) {
                        Intent registerintent = new Intent(SplashActivity.this, RegisterActivity.class);
                        startActivity(registerintent);
                        finish();

                    } else {
                        Intent mainintent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(mainintent);
                        finish();

                    }
                }else {
                    String productId = getIntent().getStringExtra("PRODUCT_ID");
                    Intent productIntent = new Intent(SplashActivity.this, ProductDetailsActivity.class);
                    productIntent.putExtra("PRODUCT_ID", productId);
                    startActivity(productIntent);
                    finish();
                }
            }
        },SPLASH_SCREEN);
    }
}
