package ru.edusty.android.Activities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.UUID;

import ru.edusty.android.Adapters.SuggestedGroupsAdapter;
import ru.edusty.android.Classes.PostUser;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.Classes.SuggestedGroups;
import ru.edusty.android.R;

/**
 * Created by Руслан on 24.10.2014.
 */
public class SuggestedGroupsActivity extends ListActivity {
    private Button btnAccept;
    private Button btnSearch;
    private UUID suggestedGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_groups);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnAccept = (Button) findViewById(R.id.btnAccept);
        new GetSuggestedGroupsTask().execute(getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE).getString("token", ""));
    }

    public void onClickBtnSearch(View view) {
        startActivity(new Intent(this, SearchUniversityActivity.class));

    }

    public void onClickBtnAccept(View view) {
        new PostUserInfoTask().execute(new PostUser(
                UUID.fromString(getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE).getString("token", "")),
                suggestedGroup
        ));
    }

    public class GetSuggestedGroupsTask extends AsyncTask<String, Void, Response> {
        ProgressDialog progressDialog = new ProgressDialog(SuggestedGroupsActivity.this);
        Response response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(final Response response) {
            super.onPostExecute(response);
            if (response.getStatus().equals("ок")) {
                final SuggestedGroups[] suggestedGroups = (SuggestedGroups[]) response.getItem();
                String header = "Возможно, среди этих групп есть Ваша:";
                TextView textView = new TextView(SuggestedGroupsActivity.this);
                textView.setText(header);
                getListView().addHeaderView(textView, null, false);
                SuggestedGroupsAdapter adapter = new SuggestedGroupsAdapter(SuggestedGroupsActivity.this, (suggestedGroups));
                getListView().setAdapter(adapter);
                getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        btnAccept.setClickable(true);
                        suggestedGroup = suggestedGroups[position - 1].getID();
                    }
                });
            } else
                Toast.makeText(getApplicationContext(), response.getStatus(), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                String query = params[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet request = new HttpGet(getString(R.string.serviceUrl) + "SuggestedGroups?tokenID=" + query);
                HttpResponse httpResponse = httpClient.execute(request);
                InputStreamReader reader = new InputStreamReader(httpResponse.getEntity()
                        .getContent(), HTTP.UTF_8);
                Type fooType = new TypeToken<Response<SuggestedGroups[]>>() {
                }.getType();
                response = new Gson().fromJson(reader, fooType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    //Добавление информации о группе пользователю
    public class PostUserInfoTask extends AsyncTask<PostUser, Void, Response> {

        ProgressDialog progressDialog = new ProgressDialog(SuggestedGroupsActivity.this);
        Response response;

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
            } else
                Toast.makeText(getApplicationContext(), response.getStatus(), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        @Override
        protected Response doInBackground(PostUser... params) {
            try {
                PostUser user = params[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(getString(R.string.serviceUrl) + "User");
                StringEntity stringEntity = new StringEntity(new Gson().toJson(user));
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
