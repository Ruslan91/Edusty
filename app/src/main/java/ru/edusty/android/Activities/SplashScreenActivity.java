package ru.edusty.android.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import com.yandex.metrica.Counter;

import ru.edusty.android.R;

public class SplashScreenActivity extends Activity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context = getApplicationContext();
        Counter.initialize(context);
        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE);
                if (sharedPreferences.getString("token", "").equals("")) {
                    Intent mainIntent = new Intent(SplashScreenActivity.this, AuthorizationActivity.class);
                    SplashScreenActivity.this.startActivity(mainIntent);
                    SplashScreenActivity.this.finish();
                } else {
                    if (sharedPreferences.getInt("newUser", 1) != 1) {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    } else startActivity(new Intent(SplashScreenActivity.this, SearchUniversityActivity.class));
                    /*else if (sharedPreferences.getString("profile", "").equals("facebook")) {
                        startActivity(new Intent(SplashScreenActivity.this, FacebookAuthActivity.class));
                    } else if (sharedPreferences.getString("profile", "").equals("odnoklassniki")) {
                        startActivity(new Intent(SplashScreenActivity.this, OdnoklassnikiActivity.class));
                    } else if (sharedPreferences.getString("profile", "").equals("google+")) {
                        startActivity(new Intent(SplashScreenActivity.this, GooglePlusActivity.class));
                    } else if (sharedPreferences.getString("profile", "").equals("edusty") ) {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    } else if (sharedPreferences.getString("profile", "").equals("")) {
                        startActivity(new Intent(SplashScreenActivity.this, AuthorizationActivity.class));
                    }*/
                    SplashScreenActivity.this.finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
