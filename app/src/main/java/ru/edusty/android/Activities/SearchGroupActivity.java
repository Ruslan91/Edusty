package ru.edusty.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import ru.edusty.android.Classes.Group;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.Classes.User;
import ru.edusty.android.R;

/**
 * Created by Руслан on 26.07.2014.
 */
public class SearchGroupActivity extends Activity {
    private ListView listView;
    private UUID universityID;
    private Group[] responseItem;
    private Response response;
    private UUID token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        try {
            token = UUID.fromString(getSharedPreferences("AppData", MODE_PRIVATE).getString("token", ""));
            universityID = UUID.fromString(getIntent().getExtras().getString("universityID"));
            SearchView searchView = (SearchView) findViewById(R.id.searchView);
            searchView.setIconified(false);
            listView = (ListView) findViewById(R.id.listView);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.d("QUERY", "Query " + query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.d("QUERY", "New text is " + newText);
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
            responseItem = (Group[]) response.getItem();
            SearchGroupAdapter searchGroupAdapter = new SearchGroupAdapter(SearchGroupActivity.this, responseItem);
            if (responseItem != null) {
                listView.setAdapter(searchGroupAdapter);
                searchGroupAdapter.notifyDataSetChanged();
            } else {
                listView.setAdapter(null);
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    new PostUserInfo().execute(new User(token, responseItem[position].getID()));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                Type fooType = new TypeToken<Response<Group[]>>() {
                }.getType();
                response = new Gson().fromJson(reader, fooType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    public class PostUserInfo extends AsyncTask<User, Void, Response> {
        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response.getItem().equals(true)) {
                startActivity(new Intent(SearchGroupActivity.this, MainActivity.class));
                finish();
            } else
                Toast.makeText(getApplicationContext(), response.getStatus(), Toast.LENGTH_SHORT).show();
        }

        Response response;

        @Override
        protected Response doInBackground(User... params) {
            try {
                User user = params[0];
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
/*        responseItem = (Group[]) response.getItem();
        if (responseItem[0].getId() == null) {
            menu.getItem(0).setVisible(true);
        } else menu.getItem(0).setVisible(false);*/
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create) {
            Intent intent = new Intent(this, CreateGroupActivity.class);
            intent.putExtra("universityID", universityID);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
