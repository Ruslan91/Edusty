package ru.edusty.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.InputStreamReader;
import java.util.UUID;

import ru.edusty.android.Classes.CreateGroup;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.R;

public class CreateGroupActivity extends Activity {

    private EditText etTitle;
    private UUID universityID;
    private UUID token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        token = UUID.fromString(getSharedPreferences("AppData", MODE_PRIVATE).getString("token", ""));
        universityID = UUID.fromString(getIntent().getStringExtra("universityID"));
        etTitle = (EditText) findViewById(R.id.etTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_accept) {
            new PostGroup().execute(new CreateGroup(etTitle.getText().toString(), token, universityID));
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Создание группы
    public class PostGroup extends AsyncTask<CreateGroup, Void, Response> {
        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response.getItem().equals(true)) {
                SharedPreferences sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("newUser", 0).apply();
                startActivity(new Intent(CreateGroupActivity.this, MainActivity.class));
                finish();
            } else
                Toast.makeText(getApplicationContext(), response.getStatus(), Toast.LENGTH_SHORT).show();
        }

        Response response;

        @Override
        protected Response doInBackground(CreateGroup... params) {
            try {
                CreateGroup group = params[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(getString(R.string.serviceUrl) + "Group");
                StringEntity stringEntity = new StringEntity(new Gson().toJson(group));
                stringEntity.setContentType("application/json");
                request.setEntity(stringEntity);
                HttpResponse httpResponse = httpClient.execute(request);
                InputStreamReader reader = new InputStreamReader(httpResponse.getEntity()
                        .getContent(), HTTP.UTF_8);
                response = new Gson().fromJson(reader, Response.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
    }
}
