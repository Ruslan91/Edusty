package ru.edusty.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import java.util.UUID;

import ru.edusty.android.R;


public class AuthorizationActivity extends Activity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization);
        sharedPreferences = getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE);
    }

    public void onClickBtnVk(View view) {
        Intent intent = new Intent(this, VkAuthActivity.class);
        startActivity(intent);
    }

    public void onClickBtnFacebook(View view) {
        Intent intent = new Intent(this, FacebookAuthActivity.class);
        startActivity(intent);
    }

    public void onClickBtnOdnoklassniki(View view) {
        Intent intent = new Intent(this, OdnoklassnikiActivity.class);
        startActivity(intent);
    }
    public void onClickBtnGooglePlus(View view) {
        Intent intent = new Intent(this, GooglePlusActivity.class);
        startActivity(intent);
    }

    public void onClickBtnEdustyPlus(View view) {
        startActivity(new Intent(this, EdustyAuthActivty.class));
    }

    public void onClickTvTermsOfService(View view) {
        startActivity(new Intent(this, TermsOfServiceActivity.class));
    }
}
