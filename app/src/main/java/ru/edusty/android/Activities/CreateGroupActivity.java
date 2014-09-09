package ru.edusty.android.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yandex.metrica.Counter;

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
        Counter.initialize(getApplicationContext());
        token = UUID.fromString(getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE).getString("token", ""));
        universityID = UUID.fromString(getIntent().getStringExtra("universityID"));
        String university = getIntent().getStringExtra("university");
        TextView tvTitle = (TextView) findViewById(R.id.tvName);
        tvTitle.setText("В \"" + university + "\" нет вашей группы? Введите название группы, которую хотите добавить.");
        etTitle = (EditText) findViewById(R.id.etTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_accept:
                if (!etTitle.getText().toString().equals(""))
                new PostGroup().execute(new CreateGroup(etTitle.getText().toString(), token, universityID));
                else Toast.makeText(CreateGroupActivity.this, getString(R.string.enter_group_name), Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Создание группы
    public class PostGroup extends AsyncTask<CreateGroup, Void, Response> {

        ProgressDialog progressDialog = new ProgressDialog(CreateGroupActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response.getItem().equals(true)) {
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("newUser", 0).apply();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }

        Response response;

        @Override
        protected Response doInBackground(CreateGroup... params) {
            try {
                CreateGroup group = params[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(getString(R.string.serviceUrl) + "Group");
                StringEntity stringEntity = new StringEntity(new Gson().toJson(group), HTTP.UTF_8);
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
