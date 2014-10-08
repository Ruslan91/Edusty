package ru.edusty.android.Adapters;

/**
 * Created by Руслан on 16.09.2014.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import ru.edusty.android.ImageLoader;
import ru.edusty.android.R;

public class CreateMessageImageLoaderAdapter extends BaseAdapter {

    private final List<UUID> files;
    private final UUID token;
    private Context mContext;

    // Keep all Images in array

    // Constructor
    public CreateMessageImageLoaderAdapter(Context c, UUID token, List<UUID> files) {
        this.files = files;
        this.token = token;
        mContext = c;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return files.size(); // длина массива
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
        if (files.size() != 0)
        new ImageLoader(mContext).DisplayImage(mContext.getString(R.string.serviceUrl)
                + "File?tokenID=" + token + "&fileID=" + files.get(position), imageView);
        return imageView;
    }
}