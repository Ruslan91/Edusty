package ru.edusty.android.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.InputStreamReader;
import java.lang.reflect.Type;

import ru.edusty.android.Classes.EdustyAuthUser;
import ru.edusty.android.Classes.EdustyUser;
import ru.edusty.android.Classes.PostComment;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.R;

public class EdustyAuthActivty extends Activity {

    private EditText etLogin;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edusty_auth);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);

    }

    public void onClickBtnSignIn(View view) {
        if (!etLogin.getText().toString().equals("") && !etPassword.getText().toString().equals(""))
            new EdustyAuth().execute(new EdustyAuthUser(etLogin.getText().toString(), etPassword.getText().toString()));
        else Toast.makeText(this, "Логин и Пароль не могут быть пустыми", Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnRegister(View view) {
        startActivity(new Intent(this, EdustyRegisterActivity.class));
    }

    public void onClickTvForgotPassword(View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

//Авторизация через Edusty
    public class EdustyAuth extends AsyncTask<EdustyAuthUser, Void, Response> {

        ProgressDialog progressDialog = new ProgressDialog(EdustyAuthActivty.this);

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
            if (response.getStatus().equals("ок")) {
                EdustyUser user = (EdustyUser) response.getItem();
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("newUser", user.getNewUser());
                editor.putString("userID", user.getUserID().toString());
                editor.putString("token", user.getTokenID().toString());
                editor.putString("profile", "edusty");
                editor.apply();
                if (user.getNewUser() == 1) {
                    startActivity(new Intent(EdustyAuthActivty.this, SearchUniversityActivity.class));
                    finish();
                } else if (user.getNewUser() == 0) {
                    Intent intent = new Intent(EdustyAuthActivty.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            } else Toast.makeText(EdustyAuthActivty.this, response.getStatus(), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        @Override
        protected Response doInBackground(EdustyAuthUser... user) {
            EdustyAuthUser authUser = user[0];
            Response response = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost request = new HttpPost(getString(R.string.serviceUrl) + "EdustyAuth");
                StringEntity entity = new StringEntity(new Gson().toJson(authUser),
                        HTTP.UTF_8);
                entity.setContentType("application/json");
                request.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(request);
                InputStreamReader reader = new InputStreamReader(httpResponse.getEntity().getContent());
                Type fooType = new TypeToken<Response<EdustyUser>>() {
                }.getType();
                response = new Gson().fromJson(reader, fooType);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
    }
}
