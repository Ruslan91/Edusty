package ru.edusty.android.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.edusty.android.Activities.UserFeedFragment;
import ru.edusty.android.Classes.Feed;
import ru.edusty.android.Classes.Group;
import ru.edusty.android.R;

/**
 * Created by Руслан on 04.08.2014.
 */
public class FeedAdapter extends BaseAdapter {
    private final Feed[] feeds;
    private final LayoutInflater lInflater;
    private final Context context;
    static class ViewHolder {
        TextView name;
        TextView date;
        TextView message;
        ImageView image;
    }

    public FeedAdapter(Context context, Feed[] feed) {
        this.context = context;
        this.feeds = feed;
        this.lInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
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
            viewHolder.image = (ImageView) v.findViewById(R.id.imageView);

            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }
        try {
            viewHolder.name.setText(feeds[position].getUser().getFirstName() + " " + feeds[position].getUser().getLastName());
            /*Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(feed[position].getMessageDate());
            viewHolder.date.setText(new SimpleDateFormat("EEE, dd MMMM yyyy, HH:mm").format(date));*/
            viewHolder.message.setText(feeds[position].getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }
}