package ru.edusty.android.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.UUID;

import ru.edusty.android.Classes.PutUser;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.Classes.User;
import ru.edusty.android.ImageLoader;
import ru.edusty.android.R;

public class ProfileActivity extends Activity {

    private static final int GALLERY_REQUEST = 1;
    private ImageView image;
    private TextView tvName;
    private TextView tvUniversityGroup;

    private ImageLoader imageLoader;
    private User user;
    private UUID token;
    private String userID;
    private Button btnVkProfile;
    private Button btnFacebookProfile;
    private Button btnOdnoklassnikiProfile;
    private Button btnGooglePlusProfile;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private String pictureUrl;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        image = (ImageView) findViewById(R.id.image);
        imageLoader = new ImageLoader(this);
        tvName = (TextView) findViewById(R.id.tvName);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        tvUniversityGroup = (TextView) findViewById(R.id.tvUniversity_Group);
        btnVkProfile = (Button) findViewById(R.id.btnVkProfile);
        btnFacebookProfile = (Button) findViewById(R.id.btnFacebookProfile);
        btnOdnoklassnikiProfile = (Button) findViewById(R.id.btnOdnoklassnikiProfile);
        btnGooglePlusProfile = (Button) findViewById(R.id.btnGooglePlusProfile);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        token = UUID.fromString(getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE).getString("token", ""));
        if (getIntent().getStringExtra("userID") != null) {
            userID = getIntent().getStringExtra("userID");
        }
        new GetProfile().execute();
        if (getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE).getString("userID", "").equals(userID))
            btnChangePassword.setVisibility(View.VISIBLE);
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

    public void onClickBtnFacebookProfile(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://facebook.com/" + user.getFacebookID()));
        startActivity(intent);
    }

    public void onClickBtnOdnoklassnikiProfile(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://ok.ru/profile/" + user.getFacebookID()));
        startActivity(intent);
    }

    public void onClickBtnGooglePlusProfile(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://plus.google.com/" + user.getGoogleID()));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (user != null) {
            if (getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE).getString("userID", "").equals(userID))
                menu.getItem(0).setVisible(true);
            else menu.getItem(0).setVisible(false);
        } else menu.getItem(0).setVisible(false);
        if (etFirstName.getVisibility() == View.VISIBLE) {
            menu.getItem(1).setVisible(true);
            menu.getItem(0).setVisible(false);
        } else {
            menu.getItem(1).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_edit:
                image.setClickable(true);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                        } catch (ActivityNotFoundException e) {
                            // Выводим сообщение об ошибке
                            String errorMessage = "Ваше устройство не поддерживает съемку";
                            Toast toast = Toast
                                    .makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
                etFirstName.setVisibility(View.VISIBLE);
                etFirstName.setText(user.getFirstName());
                etLastName.setVisibility(View.VISIBLE);
                etLastName.setText(user.getLastName());
                etEmail.setVisibility(View.VISIBLE);
                if (user.getEMail() != null) etEmail.setText(user.getEMail());
                tvName.setVisibility(View.GONE);
                tvUniversityGroup.setVisibility(View.GONE);
                this.invalidateOptionsMenu();
                break;
            case R.id.action_accept:
                String picture;
                if (pictureUrl != null) picture = pictureUrl;
                else picture = user.getPictureUrl();
                new PutUserTask().execute(new PutUser(
                        token,
                        etFirstName.getText().toString(),
                        etLastName.getText().toString(),
                        picture,
                        etEmail.getText().toString()));
        }
        return super.onOptionsItemSelected(item);
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

    public void onClickBtnChangePassword(View view) {
        startActivity(new Intent(this, ChangePasswordActivity.class));
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
                    image.setLayoutParams(new LinearLayout.LayoutParams(320, 320));
                    if (user.getPictureUrl() != null) {
                        if (!user.getPictureUrl().equals("")) {
                            new ImageLoader(getApplicationContext()).DisplayImage(user.getPictureUrl(), image);
                        }
                    }
                    tvName.setText(user.getFirstName() + " " + user.getLastName());
                    tvUniversityGroup.setText(user.getUniversityTitle() + ", " + user.getGroupTitle());
                    if (!user.getVkontakteID().equals(0)) btnVkProfile.setVisibility(View.VISIBLE);
                    if (user.getFacebookID() != null)
                        btnFacebookProfile.setVisibility(View.VISIBLE);
                    if (user.getOdnoklassnikiID() != null)
                        btnOdnoklassnikiProfile.setVisibility(View.VISIBLE);
                    if (user.getGoogleID() != null)
                        btnGooglePlusProfile.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
                ProfileActivity.this.invalidateOptionsMenu();
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

    //Редактирование профиля
    public class PutUserTask extends AsyncTask<PutUser, Void, Response> {

        ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);

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
                new GetProfile().execute(UUID.fromString(userID));
                etFirstName.setVisibility(View.GONE);
                etLastName.setVisibility(View.GONE);
                etEmail.setVisibility(View.GONE);
                tvName.setVisibility(View.VISIBLE);
                tvUniversityGroup.setVisibility(View.VISIBLE);
            } else
                Toast.makeText(ProfileActivity.this, response.getStatus(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            ProfileActivity.this.invalidateOptionsMenu();
        }

        Response response;

        @Override
        protected Response doInBackground(PutUser... params) {
            try {
                PutUser putUser = params[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPut request = new HttpPut(getString(R.string.serviceUrl) + "User");
                StringEntity stringEntity = new StringEntity(new Gson().toJson(putUser), HTTP.UTF_8);
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

    //Отправка файла
    public class PostFile extends AsyncTask<Bitmap, Void, Response> {
        Bitmap file = null;
        Exception exception;
        ProgressDialog pdLoading = new ProgressDialog(ProfileActivity.this);

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
                pictureUrl = "http://192.168.1.33/api/V3/File?fileID=" + response.getItem().toString();
                new ImageLoader(getApplicationContext()).DisplayImage(
                        pictureUrl, image
                );
            } else
                Toast.makeText(ProfileActivity.this, response.getStatus(), Toast.LENGTH_SHORT).show();
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
}
