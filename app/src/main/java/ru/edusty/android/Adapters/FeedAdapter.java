package ru.edusty.android.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ru.edusty.android.Classes.Feed;
import ru.edusty.android.ImageLoader;
import ru.edusty.android.R;

/**
 * Created by Руслан on 04.08.2014.
 */
public class FeedAdapter extends BaseAdapter {
    private ArrayList<Feed> feeds;
    private final LayoutInflater lInflater;
    private final ImageLoader imageLoader;

    static class ViewHolder {
        TextView name;
        TextView date;
        TextView message;
        ImageView image;
    }

    public FeedAdapter(Context context, ArrayList<Feed> feeds) {
        this.feeds = feeds;
        this.lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return feeds.size();
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
            Feed feed = feeds.get(position);
            viewHolder.name.setText(feed.getUser().getFirstName() + " " + feed.getUser().getLastName());
            viewHolder.message.setText(feed.getMessage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = dateFormat.parse(feed.getMessageDate());
            TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
            Calendar calendar = Calendar.getInstance();
            Date time = calendar.getTime();
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (dateFormat.format(date).equals(dateFormat.format(time))) {
                dateFormat = new SimpleDateFormat("сегодня в HH:mm");
                dateFormat.setTimeZone(timeZone);
                viewHolder.date.setText(dateFormat.format(date));
            } else {
                dateFormat = new SimpleDateFormat("dd");
                if (Integer.parseInt(dateFormat.format(date)) + 1 == Integer.parseInt(dateFormat.format(time))) {
                    dateFormat = new SimpleDateFormat("вчера в HH:mm");
                    dateFormat.setTimeZone(timeZone);
                    viewHolder.date.setText(dateFormat.format(date));
                } else {
                    dateFormat = new SimpleDateFormat("dd MMMM yyyy в HH:mm");
                    dateFormat.setTimeZone(timeZone);
                    viewHolder.date.setText(dateFormat.format(date));
                }
            }
            if (viewHolder.image != null) {
                imageLoader.DisplayImage(feed.getUser().getPictureUrl(), viewHolder.image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }
}