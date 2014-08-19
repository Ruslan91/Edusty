package ru.edusty.android.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import ru.edusty.android.Classes.Group;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.Classes.User;
import ru.edusty.android.R;

/**
 * Created by Руслан on 23.07.2014.
 */
public class GroupFragment extends ListFragment {
    private EditText etTitle;
    private User[] users;
    private Button btnAccept;
    private UUID token;
    private String defaultText;

    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_group, container, false);
        setRetainInstance(true);
        token = UUID.fromString(getActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE).getString("token", ""));
        etTitle = (EditText) view.findViewById(R.id.etTitle);
        btnAccept = (Button) view.findViewById(R.id.btnAccept);
        etTitle.setVisibility(View.INVISIBLE);
        btnAccept.setVisibility(View.INVISIBLE);
        new GetGroup().execute(token);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tv = new TextView(getActivity());
        etTitle.setVisibility(View.VISIBLE);
        defaultText = etTitle.getText().toString();
        //btnAccept.setVisibility(View.VISIBLE);
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals(defaultText)) {
                    btnAccept.setVisibility(View.VISIBLE);
                } else btnAccept.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostGroup().execute(new ru.edusty.android.Classes.PostGroup(token, etTitle.getText().toString()));
            }
        });
        tv.setText("Участники");
        getListView().addHeaderView(tv);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("userID", users[position - 1].getUserID().toString());
                startActivity(intent);
            }
        });
    }

    //Получение информации о группе
    public class GetGroup extends AsyncTask<UUID, Void, Response> {

        @Override
        protected void onPostExecute(Response response) {
            try {
                Group group = (Group) response.getItem();
                etTitle.setText(group.getTitle());
                users = group.getUsers();
                if (users.length != 0) {
                    setListAdapter(new GroupAdapter(getActivity(), users));
                } else setListAdapter(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    //Измененние названия группы
    public class PostGroup extends AsyncTask<ru.edusty.android.Classes.PostGroup, Void, Response> {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Загрузка...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response.getItem().equals(true)) {
                progressDialog.dismiss();
            } else
                Toast.makeText(getActivity(), response.getStatus(), Toast.LENGTH_SHORT).show();
        }

        Response response;

        @Override
        protected Response doInBackground(ru.edusty.android.Classes.PostGroup... params) {
            try {
                ru.edusty.android.Classes.PostGroup group = params[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(getString(R.string.serviceUrl) + "GroupEdit");
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

