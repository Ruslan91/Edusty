package ru.edusty.android.Activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.internal.id;

import java.io.File;

import ru.edusty.android.FileCache;
import ru.edusty.android.ImageLoader;
import ru.edusty.android.R;
import ru.edusty.android.TouchImageView;

/**
 * Created by Руслан on 20.09.2014.
 */
public class ImageActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(R.drawable.action_bar_ic_launcher);
        getActionBar().setTitle("Изображение");
        TouchImageView imageView = (TouchImageView) findViewById(R.id.image);
        new ImageLoader(this).DisplayImage(getIntent().getStringExtra("fileURL"), imageView);
        //File file = new FileCache(this).getFile(getIntent().getStringExtra("fileURL"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
