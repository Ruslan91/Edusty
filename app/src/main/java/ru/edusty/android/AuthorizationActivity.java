package ru.edusty.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.UUID;

import ru.edusty.android.Activities.MainActivity;
import ru.edusty.android.Activities.SearchUniversityActivity;
import ru.edusty.android.Activities.VkAuthActivity;
import ru.edusty.android.Adapters.SearchUniversityAdapter;


public class AuthorizationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID token = UUID.fromString(getSharedPreferences("AppData", MODE_PRIVATE).getString("token", ""));
        int newUser = getSharedPreferences("AppData", MODE_PRIVATE).getInt("newUser", 1);
        if (token.compareTo(new UUID(0, 0)) != 0 && newUser == 0) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (newUser == 1){
            startActivity(new Intent(this, SearchUniversityActivity.class));
            finish();
        } else setContentView(R.layout.authorization);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void onClickBtnVk(View view) {
        Intent intent = new Intent(this, VkAuthActivity.class);
        startActivity(intent);
    }
}
