package ru.edusty.android.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.InputStreamReader;
import java.util.UUID;

import ru.edusty.android.Classes.PostMessage;
import ru.edusty.android.Classes.PutMessage;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.R;

/**
 * Created by Руслан on 04.08.2014.
 */
public class CreateMessageActivity extends Activity {
    EditText etMessage;
    String message;
    String messageID;
    private UUID token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_message);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        token = UUID.fromString(getSharedPreferences("AppData", MODE_PRIVATE).getString("token", ""));
        etMessage = (EditText) findViewById(R.id.etMessage);

        if (getIntent().getStringExtra("message") != null) {
            getActionBar().setTitle("Редактирование сообщения");
            message = getIntent().getStringExtra("message");
            messageID = getIntent().getStringExtra("messageID");
            etMessage.setText(message);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_message_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.action_send:
                    if (!etMessage.getText().toString().equals("")) {
                        if (message == null) {
                            new SendMessageToFeed().execute(new PostMessage(token, etMessage.getText().toString()));
                            return true;
                        } else {
                            new PutGroupMessage().execute(new PutMessage(token, etMessage.getText().toString(), UUID.fromString(messageID)));
                            return true;
                        }
                    } else {
                        Toast.makeText(this, "Введите текст сообщения!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                case android.R.id.home:
                    finish();
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    //Отправка сообщения в ленту
    public class SendMessageToFeed extends AsyncTask<PostMessage, Void, Response> {

        ProgressDialog progressDialog = new ProgressDialog(CreateMessageActivity.this);

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
                finish();
            } else
                Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        @Override
        protected Response doInBackground(PostMessage... messages) {
            PostMessage postMessage = messages[0];
            Response response = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost request = new HttpPost(getString(R.string.serviceUrl) + "GroupMessage");
                StringEntity entity = new StringEntity(new Gson().toJson(postMessage),
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

    //Редактирование сообщения ленты
    public class PutGroupMessage extends AsyncTask<PutMessage, Void, Response> {

        ProgressDialog progressDialog = new ProgressDialog(CreateMessageActivity.this);

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
                finish();
            } else
                Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        @Override
        protected Response doInBackground(PutMessage... messages) {
            PutMessage putMessage = messages[0];
            Response response = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPut request = new HttpPut(getString(R.string.serviceUrl) + "GroupMessage");
                StringEntity entity = new StringEntity(new Gson().toJson(putMessage),
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
}
