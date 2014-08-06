package ru.edusty.android.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.edusty.android.Classes.Feed;
import ru.edusty.android.R;

/**
 * Created by Руслан on 04.08.2014.
 */
public class FeedAdapter extends BaseAdapter {
    private final Feed[] feeds;
    private final LayoutInflater lInflater;
    private URL url;

    static class ViewHolder {
        TextView name;
        TextView date;
        TextView message;
        ImageView image;
    }

    public FeedAdapter(Context context, Feed[] feed) {
        this.feeds = feed;
        this.lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return feeds.length;
    }

    @Override
    public Object getItem(int position) {
        return feeds;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        View v = convertView;
        if (v == null) {
            v = lInflater.inflate(R.layout.feed_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) v.findViewById(R.id.tvName);
            viewHolder.date = (TextView) v.findViewById(R.id.tvDate);
            viewHolder.message = (TextView) v.findViewById(R.id.tvMessage);
            viewHolder.image = (ImageView) v.findViewById(R.id.image);

            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }
        try {
            viewHolder.name.setText(feeds[position].getUser().getFirstName() + " " + feeds[position].getUser().getLastName());
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(feeds[position].getMessageDate());
            viewHolder.date.setText(new SimpleDateFormat("EEE, dd MMMM yyyy, HH:mm").format(date));
            viewHolder.message.setText(feeds[position].getMessage());
            if (viewHolder.image != null) {
                new DownloadImageTask(viewHolder.image).execute(feeds[position].getUser().getPictureUrl());
            }
/*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    viewHolder.image.setImageBitmap(bmp);
                    //viewHolder.image.setImageDrawable(Drawable.createFromStream((InputStream) new URL(feeds[position].getUser().getPictureUrl()).getContent(), "src"));
                }
            }).start();*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}