package ru.edusty.android.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ru.edusty.android.R;

/**
 * Created by Руслан on 27.09.2014.
 */
public class GooglePlusActivity extends Activity {
    WebView vkWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_web);
        final ProgressDialog progressDialog = ProgressDialog.show(GooglePlusActivity.this, "", getString(R.string.loading), true);
        vkWeb = (WebView) findViewById(R.id.vkWeb);
        WebSettings webSettings = vkWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        vkWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.show();
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
                        String userID = splits[3];
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("newUser", newUser);
                        editor.putString("userID", userID);
                        editor.putString("token", token);
                        editor.putString("profile", "google+");
                        editor.apply();
                        if (newUser == 1) {
                            startActivity(new Intent(GooglePlusActivity.this, SearchUniversityActivity.class));
                            finish();
                        } else if (newUser == 0) {
                            Intent intent = new Intent(GooglePlusActivity.this, MainActivity.class);
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
                "https://accounts.google.com/o/oauth2/auth?client_id=8428975111-04rqi70kf744m4v83q1t11plrtdcrgav.apps.googleusercontent.com&redirect_uri=http://edusty.azurewebsites.net/api/V2/GoogleAuth&response_type=code&scope=https://www.googleapis.com/auth/plus.login");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && vkWeb.canGoBack()) {
            vkWeb.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
