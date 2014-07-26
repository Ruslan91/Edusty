package ru.edusty.android.Activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.UUID;

import ru.edusty.android.Adapters.SearchGroupAdapter;
import ru.edusty.android.Classes.Group;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.R;

/**
 * Created by Руслан on 26.07.2014.
 */
public class SearchGroupActivity extends Activity {
    private ListView listView;
    private UUID universityID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        try {
            UUID token = UUID.fromString(getSharedPreferences("AppData", MODE_PRIVATE).getString("token", ""));
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
            Group[] responseItem = (Group[]) response.getItem();
            if (responseItem != null) {
                SearchGroupAdapter searchGroupAdapter = new SearchGroupAdapter(SearchGroupActivity.this, responseItem);
                listView.setAdapter(searchGroupAdapter);
                searchGroupAdapter.notifyDataSetChanged();
            } else {
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GetGroups extends AsyncTask<String, Void, Response> {

        Response response;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_university, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
