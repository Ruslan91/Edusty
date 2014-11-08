package ru.edusty.android.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
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

import ru.edusty.android.Adapters.SearchGroupAdapter;
import ru.edusty.android.Classes.PostUser;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.R;

/**
 * Created by Руслан on 26.07.2014.
 */
public class SearchGroupActivity extends Activity {
    private ListView listView;
    private UUID universityID;
    private ru.edusty.android.Classes.GetGroups[] responseItem;
    private Response response;
    private UUID token;
    private Button btnNext;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = getApplicationContext();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            btnNext = (Button) findViewById(R.id.btnNext);
            btnNext.setVisibility(View.INVISIBLE);
//            btnNext.setClickable(false);
            btnNext.setText(getString(R.string.complete));
            token = UUID.fromString(getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE).getString("token", ""));
            universityID = UUID.fromString(getIntent().getExtras().getString("universityID"));
            SearchView searchView = (SearchView) findViewById(R.id.searchView);
            searchView.setIconified(false);
            listView = (ListView) findViewById(R.id.listView);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    new GetGroups().execute(newText);
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(Response response) {

        try {
            responseItem = (ru.edusty.android.Classes.GetGroups[]) response.getItem();
            SearchGroupAdapter searchGroupAdapter = new SearchGroupAdapter(SearchGroupActivity.this, responseItem);
            if (responseItem != null) {
                this.invalidateOptionsMenu();
                listView.setAdapter(searchGroupAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        btnNext.setVisibility(View.VISIBLE);
//                        btnNext.setClickable(true);
                    }
                });
            } else {
                this.invalidateOptionsMenu();
                listView.setAdapter(null);
                btnNext.setVisibility(View.INVISIBLE);
//                btnNext.setClickable(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickBtnNext(View view) {
        int position = listView.getCheckedItemPosition();
        try {
            new PostUserInfo().execute(new PostUser(token, responseItem[position].getID()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//Получение списка групп
    public class GetGroups extends AsyncTask<String, Void, Response> {

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            setData(response);
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                String query = params[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet request = new HttpGet(getString(R.string.serviceUrl) + "SearchGroup?universityID=" + universityID + "&query=" + query);
                HttpResponse httpResponse = httpClient.execute(request);
                InputStreamReader reader = new InputStreamReader(httpResponse.getEntity()
                        .getContent(), HTTP.UTF_8);
                Type fooType = new TypeToken<Response<ru.edusty.android.Classes.GetGroups[]>>() {
                }.getType();
                response = new Gson().fromJson(reader, fooType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
    }

//Добавление информации о группе пользователю
    public class PostUserInfo extends AsyncTask<PostUser, Void, Response> {
        ProgressDialog progressDialog = new ProgressDialog(SearchGroupActivity.this);
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
            if (response.getItem().equals(true)) {
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("newUser", 0).apply();
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else
                Toast.makeText(context, response.getStatus(), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_group, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (responseItem != null) {
            if (responseItem.length != 0) {
                menu.getItem(0).setVisible(false);
            } else menu.getItem(0).setVisible(true);
        } else menu.getItem(0).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create) {
            Intent intent = new Intent(this, CreateGroupActivity.class);
            intent.putExtra("universityID", universityID.toString());
            intent.putExtra("university", getIntent().getStringExtra("university"));
            startActivity(intent);
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
