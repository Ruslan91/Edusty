package ru.edusty.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import ru.edusty.android.R;

/**
 * Created by Руслан on 19.11.2014.
 */
public class PromoteActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promote);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void onClickBtnRate(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        startActivity(intent);
    }

    public void onClickBtnShare(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Приложение Edusty https://play.google.com/store/apps/details?id=ru.edusty.android&hl=ru");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void onClickBtnShareVk(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(
                "http://vk.com/share.php?url=http://edusty.ru&title=Edusty&description=Приложение для уведомления своих одногруппников.&noparse=true&image=https://pp.vk.me/c618525/v618525165/14eda/4MjNnRZYWVs.jpg"));
        startActivity(intent);
    }

    public void onClickBtnShareFacebook(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(
                "https://www.facebook.com/dialog/feed?app_id=739283496139194&display=popup&link=http://edusty.ru&redirect_uri=http://edusty.azurewebsites.net/&picture=https://pp.vk.me/c618525/v618525165/14eda/4MjNnRZYWVs.jpg"));
        startActivity(intent);
    }

    public void onClickBtnShareOK(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.odnoklassniki.ru/dk?st.cmd=addShare&st._surl=http://edusty.ru"));
        startActivity(intent);

    }

    public void onClickBtnShareGooglePlus(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://plus.google.com/share?url=http://edusty.ru&hl=ru"));
        startActivity(intent);
    }

    public void onClickBtnShareTwitter(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(("http://twitter.com/share?text=Приложение для уведомления своих одногруппников - http://edusty.ru")));
        startActivity(intent);
    }
}
