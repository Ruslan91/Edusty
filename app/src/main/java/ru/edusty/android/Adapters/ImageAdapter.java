package ru.edusty.android.Adapters;

/**
 * Created by Руслан on 16.09.2014.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import ru.edusty.android.R;

public class ImageAdapter extends BaseAdapter {

    private final List<Bitmap> bitmaps;
    private Context mContext;

    // Keep all Images in array

    // Constructor
    public ImageAdapter(Context c, List<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
        mContext = c;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return bitmaps.size(); // длина массива
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return bitmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(bitmaps.get(position));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(120, 110));
        return imageView;
    }
}