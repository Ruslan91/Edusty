package ru.edusty.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
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
                try {
                    boolean isToken = url.contains("tokenID=");
                    if (isToken) {
                        String[] splits = url.split("=");
                        String token = splits[1].substring(0, splits[1].indexOf("&"));
                        int newUser = Integer.parseInt(splits[2].substring(0, splits[2].indexOf("&")));
                        String userID = splits[3];
                        SharedPreferences sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("newUser", newUser);
                        editor.putString("userID", userID);
                        editor.putString("token", token).apply();
                        WebSettings ws = vkWeb.getSettings();
                        ws.setSaveFormData(true);
                        if (newUser == 1) {
                            startActivity(new Intent(VkAuthActivity.this, SearchUniversityActivity.class));
                            finish();
                        } else if (newUser == 0) {
                            Intent intent = new Intent(VkAuthActivity.this, MainActivity.class);
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
                "https://oauth.vk.com/authorize?client_id=4470041&display=touch&redirect_uri=http://edusty.azurewebsites.net/api/V1/VkontakteAuth&response_type=code");
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