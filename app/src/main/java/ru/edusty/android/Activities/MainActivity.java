package ru.edusty.android.Activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.yandex.metrica.Counter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import ru.edusty.android.Classes.Push;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.R;

public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    static final String TAG = "GCMDemo";

    private CharSequence mTitle;
    private GoogleCloudMessaging gcm;
    Context context;

    String SENDER_ID = "821944378740";
    private UUID token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        if (getSharedPreferences(getString(R.string.app_data),MODE_PRIVATE).getInt("newUser", 1) == 1) startActivity(new Intent(this, SearchUniversityActivity.class));
        token = UUID.fromString(getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE).getString("token", ""));
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            new GetRegistrationID().execute();
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Counter.sharedInstance().onPauseActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Counter.sharedInstance().onResumeActivity(this);
        checkPlayServices();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void onClickItemSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public class GetRegistrationID extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                String regid = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regid;
                sendRegistrationIdToBackend(regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {

        }

    }

    private void sendRegistrationIdToBackend(String registrationID) {
        new SendRegIdToServer().execute(new Push(token, registrationID, 0));
    }

    public class SendRegIdToServer extends AsyncTask<Push, Void, Response> {

        @Override
        protected Response doInBackground(Push... params) {
            Response response = null;
            try {
                Push push = params[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(getString(R.string.serviceUrl) + "Push");
                StringEntity stringEntity = new StringEntity(new Gson().toJson(push), HTTP.UTF_8);
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
    public void onNavigationDrawerItemSelected(int position) {

        Fragment[] frags = new Fragment[2];
        frags[0] = new FeedFragment();
        frags[1] = new GroupFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, frags[position]).commit();
        onSectionAttached(position + 1);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
}
