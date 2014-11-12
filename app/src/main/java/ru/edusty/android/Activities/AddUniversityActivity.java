package ru.edusty.android.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import ru.edusty.android.Classes.PostUniversity;
import ru.edusty.android.Classes.PostUser;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.R;

public class AddUniversityActivity extends Activity {

    private Context context;
    private EditText etTitle;
    private EditText etCountry;
    private EditText etCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_university);
        context = getApplicationContext();
        etTitle = (EditText) findViewById(R.id.etTitle);
        etCountry = (EditText) findViewById(R.id.etCountry);
        etCity = (EditText) findViewById(R.id.etCity);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_university, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_accept:
                new PostUniversityTask().execute(new PostUniversity(
                        UUID.fromString(getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE).getString("token", "")),
                        etTitle.getText().toString(),
                        etCountry.getText().toString(),
                        etCity.getText().toString()
                ));
        }
        return super.onOptionsItemSelected(item);
    }

    //    Добавление ВУЗа
    public class PostUniversityTask extends AsyncTask<PostUniversity, Void, Response> {
        ProgressDialog progressDialog = new ProgressDialog(AddUniversityActivity.this);
        Response response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response.getStatus().equals("ок")) {
                Intent intent = new Intent(context, CreateGroupActivity.class);
                intent.putExtra("universityID", response.getItem().toString());
                intent.putExtra("university", etTitle.getText().toString());
                intent.putExtra("new", 1);
                startActivity(intent);
            } else
                Toast.makeText(context, response.getStatus(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        @Override
        protected Response doInBackground(PostUniversity... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(getString(R.string.serviceUrl) + "University");
                StringEntity stringEntity = new StringEntity(new Gson().toJson(params[0]));
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
