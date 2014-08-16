package ru.edusty.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import java.util.UUID;

import ru.edusty.android.R;


public class AuthorizationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (getIntent().getBooleanExtra("exit", false)) {
                finish();
            }
            SharedPreferences sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
            if (!sharedPreferences.getString("token", "").equals("")) {
                UUID token = UUID.fromString(getSharedPreferences("AppData", MODE_PRIVATE).getString("token", ""));
                int newUser = getSharedPreferences("AppData", MODE_PRIVATE).getInt("newUser", 1);
                if (token.compareTo(new UUID(0, 0)) != 0 && newUser == 0) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else if (newUser == 1) {
                    startActivity(new Intent(this, SearchUniversityActivity.class));
                    finish();
                }
            } else
                setContentView(R.layout.authorization);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickBtnVk(View view) {
        Intent intent = new Intent(this, VkAuthActivity.class);
        startActivity(intent);
    }
}
