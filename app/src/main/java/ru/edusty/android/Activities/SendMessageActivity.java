package ru.edusty.android.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.edusty.android.Adapters.CreateMessageImageLoaderAdapter;
import ru.edusty.android.Classes.Message;
import ru.edusty.android.Classes.PostMessage;
import ru.edusty.android.Classes.PutMessage;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.R;

/**
 * Created by Руслан on 04.08.2014.
 */
public class SendMessageActivity extends Activity implements ActionMode.Callback {
    private static final int GALLERY_REQUEST = 1;
    EditText etMessage;
    String messageID;
    private UUID token;
    private List<UUID> files;
    private List<Bitmap> bitmaps;
    private Integer imageNum;
    private GridView gridView;
    private CreateMessageImageLoaderAdapter createMessageImageLoaderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_message);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        token = UUID.fromString(getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE).getString("token", ""));
        etMessage = (EditText) findViewById(R.id.etMessage);
        gridView = (GridView) findViewById(R.id.grid);
        files = new ArrayList<UUID>();
        bitmaps = new ArrayList<Bitmap>();
        if (getIntent().getStringExtra("messageID") != null) {
            getActionBar().setTitle("Редактирование сообщения");
            messageID = getIntent().getStringExtra("messageID");
            new GetMessage().execute();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        gridView.setLongClickable(true);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                imageNum = position;
                if (getIntent().getStringExtra("messageID") != null)
                    gridView.startActionMode(SendMessageActivity.this);
                return true;
            }
        });
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
                    if (messageID == null) {
                        new SendMessageToFeed().execute(new PostMessage(token, etMessage.getText().toString(), files));
                        return true;
                    } else {
                        new PutGroupMessage().execute(new PutMessage(token, etMessage.getText().toString(), UUID.fromString(messageID), files));
                        return true;
                    }
            case R.id.action_add_picture:
                try {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                } catch (ActivityNotFoundException e) {
                    // Выводим сообщение об ошибке
                    String errorMessage = "Ваше устройство не поддерживает съемку";
                    Toast toast = Toast
                            .makeText(this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
    }

    catch(
    Exception e
    )

    {
        e.printStackTrace();
    }

    return super.

    onOptionsItemSelected(item);
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Bitmap galleryPic;
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        galleryPic = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        if (galleryPic == null) break;
                        new PostFile().execute(galleryPic);
                        //bitmaps.add(galleryPic);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.action_mode_image_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                files.remove(files.get(imageNum));
                createMessageImageLoaderAdapter.notifyDataSetChanged();
                mode.finish();
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

//    Отправка сообщения в ленту
public class SendMessageToFeed extends AsyncTask<PostMessage, Void, Response> {

    ProgressDialog progressDialog = new ProgressDialog(SendMessageActivity.this);

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
        if (response.getItem().equals(true)) {
            finish();
        } else
            Toast.makeText(SendMessageActivity.this, response.getStatus(), Toast.LENGTH_SHORT).show();
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

//    Редактирование сообщения ленты
public class PutGroupMessage extends AsyncTask<PutMessage, Void, Response> {

    ProgressDialog progressDialog = new ProgressDialog(SendMessageActivity.this);

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
        if (response.getItem().equals(true)) {
            finish();
        } else
            Toast.makeText(SendMessageActivity.this, response.getStatus(), Toast.LENGTH_SHORT).show();
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

//    Отправка файла
public class PostFile extends AsyncTask<Bitmap, Void, Response> {
    Bitmap file = null;
    Exception exception;
    ProgressDialog pdLoading = new ProgressDialog(SendMessageActivity.this);

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pdLoading.setMessage(getString(R.string.loading));
        pdLoading.show();
        pdLoading.setCancelable(false);
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        if (UUID.fromString((String) response.getItem()).compareTo(new UUID(0, 0)) != 0) {
            files.add(UUID.fromString((String) response.getItem()));
            gridView.setAdapter(new CreateMessageImageLoaderAdapter(SendMessageActivity.this, token, files));
        } else
            Toast.makeText(SendMessageActivity.this, response.getStatus(), Toast.LENGTH_SHORT).show();
        pdLoading.dismiss();
    }

    protected Response doInBackground(Bitmap... bitmaps) {
        Response response = null;
        file = bitmaps[0];
        float width = file.getWidth();
        int newWidth;
        float height = file.getHeight();
        int newHeight;
        float ratio = 1920 / width;
        if (ratio < 1) {
            newWidth = (int) (width * ratio);
            newHeight = (int) (height * ratio);
        } else {
            newWidth = (int) width;
            newHeight = (int) height;
        }
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(file, newWidth,
                newHeight, false);
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost request = new HttpPost(getString(R.string.serviceUrl) + "File?tokenID=" + token);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            request.setEntity(new ByteArrayEntity(byteArray));
            HttpResponse httpResponse = httpclient.execute(request);
            InputStreamReader reader = new InputStreamReader(httpResponse.getEntity().getContent());
            response = new Gson().fromJson(reader, Response.class);

        } catch (Exception e) {
            this.exception = e;
        }
        return response;
    }
}

//    Получение записи
public class GetMessage extends AsyncTask<UUID, Void, Response> {

    ProgressDialog progressDialog = new ProgressDialog(SendMessageActivity.this);

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Response response) {
        try {
            if (response.getItem() != null) {
                Message message = (Message) response.getItem();
                etMessage.setText(message.getMessage());
                if (message.getFiles() != null) {
                    files.clear();
                    files.addAll(message.getFiles());
                    createMessageImageLoaderAdapter =
                            new CreateMessageImageLoaderAdapter(SendMessageActivity.this, token, files);
                    gridView.setAdapter(createMessageImageLoaderAdapter);
                }
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
            HttpGet request = new HttpGet(getString(R.string.serviceUrl) + "GroupMessage?tokenID=" + token + "&messageID=" + messageID);
            HttpResponse httpResponse = httpclient.execute(request);
            InputStreamReader reader = new InputStreamReader(httpResponse.getEntity()
                    .getContent(), HTTP.UTF_8);
            Type fooType = new TypeToken<Response<Message>>() {
            }.getType();
            response = gson.fromJson(reader, fooType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
}
