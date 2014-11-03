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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.InputStreamReader;
import java.util.UUID;

import ru.edusty.android.Classes.PostComment;
import ru.edusty.android.Classes.PostMessage;
import ru.edusty.android.Classes.PutMessage;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.R;

/**
 * Created by Руслан on 15.09.2014.
 */
public class CreateCommentActivity extends Activity {
    private UUID token;
    private EditText etMessage;
    private UUID messageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_message);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        token = UUID.fromString(getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE).getString("token", ""));
        messageID = UUID.fromString(getIntent().getStringExtra("messageID"));
        etMessage = (EditText) findViewById(R.id.etMessage);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_message_menu, menu);
        menu.getItem(0).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.action_send:
                    if (!etMessage.getText().toString().equals("")) {
                            new SendComment().execute(new PostComment(messageID, etMessage.getText().toString(), token));
                            return true;
                    } else {
                        Toast.makeText(this, getString(R.string.enter_message_text), Toast.LENGTH_SHORT).show();
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
    public class SendComment extends AsyncTask<PostComment, Void, Response> {

        ProgressDialog progressDialog = new ProgressDialog(CreateCommentActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response.getItem().equals(true)) {
                finish();
            } else Toast.makeText(CreateCommentActivity.this, response.getStatus(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        @Override
        protected Response doInBackground(PostComment... messages) {
            PostComment postComment = messages[0];
            Response response = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost request = new HttpPost(getString(R.string.serviceUrl) + "Comment");
                StringEntity entity = new StringEntity(new Gson().toJson(postComment),
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
