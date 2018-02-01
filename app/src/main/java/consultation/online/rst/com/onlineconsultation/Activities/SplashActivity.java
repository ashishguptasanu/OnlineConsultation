package consultation.online.rst.com.onlineconsultation.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import consultation.online.rst.com.onlineconsultation.R;


public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DISPLAY_LENGTH = 3000;
    SharedPreferences mSharedPreferences, sharedPreferences;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        fixClassLoaderIssue();
        View yourView = findViewById(R.id.your_view);
        sharedPreferences = SplashActivity.this.getPreferences(Context.MODE_PRIVATE);
        mSharedPreferences.edit().putString("order_id_firebase","").apply();
        mSharedPreferences.edit().putString("reject_status","").apply();
        sharedPreferences.edit().putInt("ignored",1).apply();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (yourView != null) {
                yourView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }

        }, SPLASH_DISPLAY_LENGTH);
    }
    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = SplashActivity.this.getPreferences(Context.MODE_PRIVATE);
    }
    private static void fixClassLoaderIssue() {
        ClassLoader myClassLoader = SplashActivity.class.getClassLoader();
        Thread.currentThread().setContextClassLoader(myClassLoader);
    }
}
