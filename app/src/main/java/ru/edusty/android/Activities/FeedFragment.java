package ru.edusty.android.Activities;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

import ru.edusty.android.Adapters.FeedAdapter;
import ru.edusty.android.Classes.Feed;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.R;

/**
 * Created by Руслан on 23.07.2014.
 */
public class FeedFragment extends ListFragment {

    private String token;

    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_default, container, false);
        token = getActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE).getString("token", "");
        new GetFeed().execute(UUID.fromString(token));
        setRetainInstance(true);
        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.feed_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_message:
                startActivity(new Intent(getActivity(), CreateMessageActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class GetFeed extends AsyncTask<UUID, Void, Response> {

        //ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
/*            progressDialog.setMessage("Загрузка...");
            progressDialog.show();*/
        }

        @Override
        protected void onPostExecute(Response response) {
            try {
                //setData(response);
                Feed[] feed = (Feed[]) response.getItem();
                FeedAdapter feedAdapter;
                if (feed.length == 0) setListAdapter(null);
                else {
                    feedAdapter = new FeedAdapter(getActivity(), feed);
                    setListAdapter(feedAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //progressDialog.dismiss();
        }

        @Override
        protected Response doInBackground(UUID... params) {
            Response response = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                Gson gson = new Gson();
                HttpGet request = new HttpGet(getString(R.string.serviceUrl) + "GroupMessage?tokenID=" + token);
                HttpResponse httpResponse = httpclient.execute(request);
                InputStreamReader reader = new InputStreamReader(httpResponse.getEntity()
                        .getContent(), HTTP.UTF_8);
                Type fooType = new TypeToken<Response<Feed[]>>() {
                }.getType();
                response = gson.fromJson(reader, fooType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

    }
}
