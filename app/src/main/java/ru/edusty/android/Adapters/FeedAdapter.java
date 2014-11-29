package ru.edusty.android.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ru.edusty.android.Activities.ImageActivity;
import ru.edusty.android.Activities.ProfileActivity;
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
    private Feed feed;
    private Context context;
    private ViewHolder viewHolder;

    static class ViewHolder {
        TextView name;
        TextView date;
        TextView message;
        ImageView image;
        LinearLayout linearLayout;
        ImageView imageView;
        Button comments;
    }

    public FeedAdapter(Context context, ArrayList<Feed> feeds) {
        this.feeds = feeds;
        this.context = context;
        this.lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return feeds.size();
    }

    @Override
    public Object getItem(int position) {
        return feeds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View v = convertView;
        System.out.println("getView " + position + " " + convertView);
        feed = feeds.get(position);
        if (v == null) {
            v = lInflater.inflate(R.layout.feed_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) v.findViewById(R.id.tvName);
            viewHolder.date = (TextView) v.findViewById(R.id.tvDate);
            viewHolder.message = (TextView) v.findViewById(R.id.tvMessage);
            viewHolder.image = (ImageView) v.findViewById(R.id.image);
            viewHolder.linearLayout = (LinearLayout) v.findViewById(R.id.llayout);
            viewHolder.imageView = (ImageView) v.findViewById(R.id.imageView);
            viewHolder.comments = (Button) v.findViewById(R.id.btnCommentsCount);
            v.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) v.getTag();
        viewHolder.name.setTag(feed.getUser().getUserID().toString());
        try {
                viewHolder.name.setText(feed.getUser().getFirstName() + " " + feed.getUser().getLastName());
                if (isOnline()) {
                    viewHolder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int i = position;
                            Intent intent = new Intent(context, ProfileActivity.class);
                            intent.putExtra("userID", feeds.get(i).getUser().getUserID().toString());
                            context.startActivity(intent);
                        }
                    });
                }
                viewHolder.message.setText(feed.getMessage());
                viewHolder.comments.setText(feed.getCommentsCount().toString());
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
                if (feed.getFiles().size() != 0) {
                    viewHolder.linearLayout.removeAllViews();
                    for (int i = 0; i < feed.getFiles().size(); i++) {
                        final ImageView imageView;
                        imageView = new ImageView(context);
                        imageView.setTag(i);
                        imageView.setPadding(4, 4, 4, 4);
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
                        imageView.setClickable(true);
                        imageView.setContentDescription(feed.getFiles().get(i).toString());
                        new ImageLoader(context)
                                .DisplayImage(context.getString(R.string.serviceUrl) + "File?fileID=" + feed.getFiles().get(i), imageView);
                        viewHolder.linearLayout.addView(imageView);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                int image_position = (Integer) imageView.getTag();
//                                int item_position = (Integer) viewHolder.name.getTag();
                                Intent intent = new Intent(context, ImageActivity.class);
                                intent.putExtra("fileURL", context.getString(R.string.serviceUrl)
                                        + "File?fileID=" + imageView.getContentDescription());
                                        context.startActivity(intent);
                            }
                        });
                    }
                } else viewHolder.linearLayout.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return v;
        }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}