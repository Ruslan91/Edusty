package ru.edusty.android.Activities;

import android.app.Fragment;
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
import android.widget.EditText;
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

import ru.edusty.android.Adapters.GroupAdapter;
import ru.edusty.android.Classes.Feed;
import ru.edusty.android.Classes.Group;
import ru.edusty.android.Classes.PostGroup;
import ru.edusty.android.Classes.PostUser;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.Classes.User;
import ru.edusty.android.R;

/**
 * Created by Руслан on 23.07.2014.
 */
public class GroupFragment extends ListFragment {
    private TextView tvTitle;
    private EditText etTitle;

    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_group, container, false);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        etTitle = (EditText) view.findViewById(R.id.etTitle);
        new GetGroup().execute(UUID.fromString(getActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE).getString("token", "")));
        setRetainInstance(true);
        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.group_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (tvTitle.isCursorVisible()) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                etTitle.setText(tvTitle.getText().toString());
                tvTitle.setVisibility(View.INVISIBLE);
                etTitle.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_accept:
                new PostGroup().execute(new ru.edusty.android.Classes.PostGroup(
                        UUID.fromString(getActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE).getString("token", "")),
                        etTitle.getText().toString()));
                new GetGroup().execute(UUID.fromString(getActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE).getString("token", "")));
                tvTitle.setVisibility(View.VISIBLE);
                etTitle.setVisibility(View.INVISIBLE);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public class GetGroup extends AsyncTask<UUID, Void, Response> {
        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Group group = (Group) response.getItem();
            tvTitle.setText(group.getTitle());
            User[] users = group.getUsers();
                if (users.length != 0) {
                    GroupAdapter groupAdapter = new GroupAdapter(getActivity(), users);
                    setListAdapter(groupAdapter);
                } else setListAdapter(null);
            }

        @Override
        protected Response doInBackground(UUID... params) {
            Response response = null;
            try {
                String query = params[0].toString();
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(getString(R.string.serviceUrl) + "Group?tokenID=" + query);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                InputStreamReader inputStreamReader = new InputStreamReader(httpResponse.getEntity().getContent(), HTTP.UTF_8);
                Type fooType = new TypeToken<Response<Group>>() {
                }.getType();
                response = new Gson().fromJson(inputStreamReader, fooType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
    }
    public class PostGroup extends AsyncTask<ru.edusty.android.Classes.PostGroup, Void, Response> {
        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response.getItem().equals(true)) {
            } else
                Toast.makeText(getActivity(), response.getStatus(), Toast.LENGTH_SHORT).show();
        }

        Response response;

        @Override
        protected Response doInBackground(ru.edusty.android.Classes.PostGroup... params) {
            try {
                ru.edusty.android.Classes.PostGroup user = params[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(getString(R.string.serviceUrl) + "GroupEdit");
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

