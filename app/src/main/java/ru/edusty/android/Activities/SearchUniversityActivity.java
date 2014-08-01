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
import android.widget.Button;
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

import ru.edusty.android.Adapters.SearchUniversityAdapter;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.Classes.University;
import ru.edusty.android.R;

public class SearchUniversityActivity extends Activity {

    private ListView listView;
    private University[] responseItem;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        try {
            btnNext = (Button) findViewById(R.id.button);
            btnNext.setVisibility(View.INVISIBLE);
            btnNext.setText("Далее");
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
                    new GetUniversities().execute(newText);
                    return true;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(Response response) {
        try {
            responseItem = (University[]) response.getItem();
            SearchUniversityAdapter searchUniversityAdapter = new SearchUniversityAdapter(SearchUniversityActivity.this, responseItem);
            if (responseItem != null) {
                listView.setAdapter(searchUniversityAdapter);
                searchUniversityAdapter.notifyDataSetChanged();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        btnNext.setVisibility(View.VISIBLE);
                    }
                });
            } else listView.setAdapter(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onClickBtnNext(View view) {
        int position = listView.getCheckedItemPosition();
        Intent intent = new Intent(SearchUniversityActivity.this, SearchGroupActivity.class);
        intent.putExtra("universityID", responseItem[position].getID().toString());
        startActivity(intent);
    }
    public class GetUniversities extends AsyncTask<String, Void, Response> {

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
                HttpGet request = new HttpGet(getString(R.string.serviceUrl) + "SearchUniversity?query=" + query);
                HttpResponse httpResponse = httpClient.execute(request);
                InputStreamReader reader = new InputStreamReader(httpResponse.getEntity()
                        .getContent(), HTTP.UTF_8);
                Type fooType = new TypeToken<Response<University[]>>() {
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
