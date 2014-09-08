package ru.edusty.android.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

import ru.edusty.android.Classes.Response;
import ru.edusty.android.Classes.User;
import ru.edusty.android.ImageDownloaderTask;
import ru.edusty.android.ImageLoader;
import ru.edusty.android.R;

public class ProfileActivity extends Activity {

    private ImageView image;
    private TextView tvName;
    private TextView tvUniversityGroup;

    private ImageLoader imageLoader;
    private User user;
    private UUID token;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        image = (ImageView) findViewById(R.id.image);
        imageLoader = new ImageLoader(this);
        tvName = (TextView) findViewById(R.id.tvName);
        tvUniversityGroup = (TextView) findViewById(R.id.tvUniversity_Group);
        token = UUID.fromString(getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE).getString("token", ""));
        if (getIntent().getStringExtra("userID") != null)
            userID = getIntent().getStringExtra("userID");
        new GetProfile().execute();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void onClickBtnVkProfile(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://vk.com/id" + user.getVkontakteID()));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //Получение профиля пользователя
    public class GetProfile extends AsyncTask<UUID, Void, Response> {
        ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Response response) {
            try {
                if (response.getStatus().equals("ок")) {
                    user = (User) response.getItem();
                    if (!user.getPictureUrl().equals("")) {
                        new ImageDownloaderTask(image).execute(user.getPictureUrl());
                    }
                    tvName.setText(user.getFirstName() + " " + user.getLastName());
                    tvUniversityGroup.setText(user.getUniversityTitle() + ", " + user.getGroupTitle());
                }
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Response doInBackground(UUID... params) {
            Response response = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                Gson gson = new Gson();
                HttpGet request = new HttpGet(getString(R.string.serviceUrl) + "User?tokenID=" + token + "&userID=" + userID);
                HttpResponse httpResponse = httpclient.execute(request);
                InputStreamReader reader = new InputStreamReader(httpResponse.getEntity()
                        .getContent(), HTTP.UTF_8);
                Type fooType = new TypeToken<Response<User>>() {
                }.getType();
                response = gson.fromJson(reader, fooType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

    }
}
