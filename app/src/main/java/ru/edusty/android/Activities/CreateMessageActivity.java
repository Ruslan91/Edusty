package ru.edusty.android.Activities;

import android.app.Activity;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.InputStreamReader;
import java.util.UUID;

import ru.edusty.android.Classes.PostMessage;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.R;

/**
 * Created by Руслан on 04.08.2014.
 */
public class CreateMessageActivity extends Activity {
    private EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_message);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        etMessage = (EditText) findViewById(R.id.etMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_message_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                if (!etMessage.getText().toString().equals("")) {
                    UUID token = UUID.fromString(getSharedPreferences("AppData", MODE_PRIVATE).getString("token", ""));
                    new SendMessageToFeed().execute(new PostMessage(token, etMessage.getText().toString()));
                } else Toast.makeText(this, "Введите текст сообщения!", Toast.LENGTH_SHORT).show();
            case android.R.id.home:
                    finish();
                    return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Отправка сообщения в ленту
    public class SendMessageToFeed extends AsyncTask<PostMessage, Void, Response> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response.getItem().equals(true)) {
                finish();
            }
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
}
