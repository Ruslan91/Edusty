package ru.edusty.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ru.edusty.android.R;

public class VkAuthActivity extends Activity {
    WebView vkWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_web);
        vkWeb = (WebView) findViewById(R.id.vkWeb);
        vkWeb.getSettings().setJavaScriptEnabled(true);
        vkWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                boolean isToken = url.contains("tokenID=");
                if (isToken) {
                    String token = url.substring(url.indexOf("=") + 1, url.indexOf("&"));
                    int newUser = Integer.parseInt(url.substring(url.lastIndexOf("=") + 1));
                    SharedPreferences sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("newUser", newUser);
                    editor.putString("token", token).apply();
                    if (newUser == 1) {
                        startActivity(new Intent(VkAuthActivity.this, SearchUniversityActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(VkAuthActivity.this, MainActivity.class));
                        finish();
                    }
                }
            }
        });
        vkWeb.loadUrl(
                "https://oauth.vk.com/authorize?client_id=4470041&display=touch&redirect_uri=http://edusty.azurewebsites.net/api/VkontakteAuth&response_type=code");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && vkWeb.canGoBack()) {
            vkWeb.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vk_auth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}