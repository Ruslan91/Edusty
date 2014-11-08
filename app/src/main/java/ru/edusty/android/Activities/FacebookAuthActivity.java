package ru.edusty.android.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ru.edusty.android.R;

/**
 * Created by Руслан on 24.09.2014.
 */
public class FacebookAuthActivity extends Activity {
    WebView vkWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_web);
        final ProgressDialog progressDialog = ProgressDialog.show(FacebookAuthActivity.this, "", getString(R.string.loading), true);
        vkWeb = (WebView) findViewById(R.id.vkWeb);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE);
        if (!isOnline() && !sharedPreferences.getString("token", "").equals("") && sharedPreferences.getInt("newUser", 1) != 1) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            vkWeb.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    try {
                        progressDialog.dismiss();
                        boolean isToken = url.contains("tokenID=");
                        if (isToken) {
                            String[] splits = url.split("=");
                            String token = splits[1].substring(0, splits[1].indexOf("&"));
                            int newUser = Integer.parseInt(splits[2].substring(0, splits[2].indexOf("&")));
                            String userID = splits[3].substring(0, splits[3].indexOf("#"));
                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("newUser", newUser);
                            editor.putString("userID", userID);
                            editor.putString("token", token);
                            editor.putString("profile", "facebook");
                            editor.apply();
                            if (newUser == 1) {
                                startActivity(new Intent(FacebookAuthActivity.this, SearchUniversityActivity.class));
                                finish();
                            } else if (newUser == 0) {
                                Intent intent = new Intent(FacebookAuthActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            vkWeb.loadUrl(
                    "https://www.facebook.com/dialog/oauth?client_id=739283496139194&redirect_uri=http://edusty.azurewebsites.net/api/V2/FacebookAuth");
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && vkWeb.canGoBack()) {
            vkWeb.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) FacebookAuthActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
