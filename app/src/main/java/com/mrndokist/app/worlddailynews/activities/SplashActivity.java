package com.mrndokist.app.worlddailynews.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mrndokist.app.worlddailynews.R;

public class SplashActivity extends Activity implements Animation.AnimationListener {

    Animation animFadeIn;
    RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        View decorView = getWindow().getDecorView();

        Typeface localTypeface1 = Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Bold.ttf");
        ((TextView) findViewById(R.id.tv_splash)).setTypeface(localTypeface1);

        //hide the status bar
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        //Load animation
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_fade_in);

        //set animation listener
        animFadeIn.setAnimationListener(this);

        //animation for image
        mRelativeLayout = findViewById(R.id.splashLayout);

        //start the animation
        mRelativeLayout.setVisibility(View.VISIBLE);
        mRelativeLayout.startAnimation(animFadeIn);


    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
