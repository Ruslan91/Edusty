package ru.edusty.android.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import ru.edusty.android.Classes.EdustyRegisterUser;
import ru.edusty.android.Classes.EdustyUser;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.R;

/**
 * Created by Руслан on 13.10.2014.
 */
public class EdustyRegisterActivity extends Activity {

    private EditText etLogin;
    private EditText etPassword;
    private EditText etFirstname;
    private EditText etLastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edusty_registration);
        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etFirstname = (EditText) findViewById(R.id.etFirstname);
        etLastname = (EditText) findViewById(R.id.etLastname);
    }

    public void onClickBtnRegister(View view) {
        if (!etLogin.getText().toString().equals("") && !etPassword.getText().toString().equals(""))
            new EdustyRegister().execute(new EdustyRegisterUser(
                    etLogin.getText().toString(),
                    etPassword.getText().toString(),
                    etFirstname.getText().toString(),
                    etLastname.getText().toString()
            ));
        else
            Toast.makeText(this, "Поля Логин и Пароль не могут быть пустыми", Toast.LENGTH_SHORT).show();
    }

    //Регистрация через Edusty
    public class EdustyRegister extends AsyncTask<EdustyRegisterUser, Void, Response> {

        ProgressDialog progressDialog = new ProgressDialog(EdustyRegisterActivity.this);

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
                new EdustyAuth().execute(new EdustyAuthUser(
                        etLogin.getText().toString(),
                        etPassword.getText().toString()
                ));
            } else
                Toast.makeText(EdustyRegisterActivity.this, response.getStatus(), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        @Override
        protected Response doInBackground(EdustyRegisterUser... user) {
            EdustyRegisterUser registerUser = user[0];
            Response response = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost request = new HttpPost(getString(R.string.serviceUrl) + "EdustyUserRegister");
                StringEntity entity = new StringEntity(new Gson().toJson(registerUser),
                        HTTP.UTF_8);
                entity.setContentType("application/json");
                request.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(request);
                InputStreamReader reader = new InputStreamReader(httpResponse.getEntity().getContent());
                response = new Gson().fromJson(reader, Response.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    //Авторизация через Edusty
    public class EdustyAuth extends AsyncTask<EdustyAuthUser, Void, Response> {

        ProgressDialog progressDialog = new ProgressDialog(EdustyRegisterActivity.this);

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
                    startActivity(new Intent(EdustyRegisterActivity.this, SearchUniversityActivity.class));
                    finish();
                } else if (user.getNewUser() == 0) {
                    Intent intent = new Intent(EdustyRegisterActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            } else
                Toast.makeText(EdustyRegisterActivity.this, response.getStatus(), Toast.LENGTH_LONG).show();
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
