package ru.edusty.android.Activities;

import android.app.Activity;
import android.content.Intent;
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
        token = UUID.fromString(getSharedPreferences("AppData", MODE_PRIVATE).getString("token", ""));
        if (getIntent().getStringExtra("userID") != null)
            userID = getIntent().getStringExtra("userID");
        new GetProfile().execute();
    }


    public void onClickBtnVkProfile(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://vk.com/id" + user.getVkontakteID()));
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Получение профиля пользователя
    public class GetProfile extends AsyncTask<UUID, Void, Response> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response response) {
            try {
                if (response.getStatus().equals("ок")) {
                    user = (User) response.getItem();
                    if (!user.getPictureUrl().equals("")) {
                        imageLoader.DisplayImage(user.getPictureUrl(),image);
                    }
                    tvName.setText(user.getFirstName() + " " + user.getLastName());
                    tvUniversityGroup.setText(user.getUniversityTitle() + ", " + user.getGroupTitle());
                }
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
