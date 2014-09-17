package ru.edusty.android.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import ru.edusty.android.Adapters.ImageAdapter;
import ru.edusty.android.Classes.PostMessage;
import ru.edusty.android.Classes.PutMessage;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.R;

/**
 * Created by Руслан on 04.08.2014.
 */
public class CreateMessageActivity extends Activity {
    private static final int GALLERY_REQUEST = 1;
    EditText etMessage;
    String message;
    String messageID;
    private UUID token;
    private SurfaceView surfaceView;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private List<UUID> files;
    private List<Bitmap> bitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_message);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        token = UUID.fromString(getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE).getString("token", ""));
        etMessage = (EditText) findViewById(R.id.etMessage);
        files = new ArrayList<UUID>();
        bitmaps = new ArrayList<Bitmap>();
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
                            new SendMessageToFeed().execute(new PostMessage(token, etMessage.getText().toString(), files));
                            return true;
                        } else {
                            new PutGroupMessage().execute(new PutMessage(token, etMessage.getText().toString(), UUID.fromString(messageID)));
                            return true;
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.enter_message_text), Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
            Bitmap galleryPic;
            switch(requestCode) {
                case GALLERY_REQUEST:
                    if(resultCode == RESULT_OK){
                        Uri selectedImage = imageReturnedIntent.getData();
                        try {
                            galleryPic = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                            if (galleryPic == null) break;
                            new PostFile().execute(galleryPic);
                            //LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llayout1);
                            //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            //lp.setMargins(4,4,4,4);
                            bitmaps.add(galleryPic);
                            GridView gridView = (GridView) findViewById(R.id.grid);
                            gridView.setAdapter(new ImageAdapter(this, bitmaps));
                            /*ImageView imageView = new ImageView(this);
                            imageView.setImageBitmap(galleryPic);
                            //imageView.setLayoutParams(lp);
                            imageView.setMaxHeight(100);
                            imageView.setMaxWidth(100);
                            gridView.addView(imageView);*/
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
            }
        }
    //Отправка сообщения в ленту
    public class SendMessageToFeed extends AsyncTask<PostMessage, Void, Response> {

        ProgressDialog progressDialog = new ProgressDialog(CreateMessageActivity.this);

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
            progressDialog.setMessage(getString(R.string.loading));
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

    //Отправка файла
    public class PostFile extends AsyncTask<Bitmap, Void, Response> {
        Bitmap file = null;
        Exception exception;
        ProgressDialog pdLoading = new ProgressDialog(CreateMessageActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage(getString(R.string.loading));
            pdLoading.show();
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (UUID.fromString((String) response.getItem()).compareTo(new UUID(0, 0)) != 0) {
                files.add(UUID.fromString((String) response.getItem()));
            }
            pdLoading.dismiss();
        }

        protected Response doInBackground(Bitmap... bitmaps) {
            Response response = null;
            file = bitmaps[0];
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost request = new HttpPost(getString(R.string.serviceUrl) + "File?tokenID=" + token);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                file.compress(Bitmap.CompressFormat.PNG, 100, stream);
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
}
