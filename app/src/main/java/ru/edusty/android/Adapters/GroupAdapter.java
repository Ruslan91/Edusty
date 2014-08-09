package ru.edusty.android.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.edusty.android.Classes.User;
import ru.edusty.android.ImageDownloaderTask;
import ru.edusty.android.ImageLoader;
import ru.edusty.android.R;

/**
 * Created by Руслан on 04.08.2014.
 */
public class GroupAdapter extends BaseAdapter {
    private final User[] users;
    private final LayoutInflater lInflater;
    private final ImageLoader imageLoader;

    static class ViewHolder {
        TextView name;
        ImageView image;
    }

    public GroupAdapter(Context context, User[] users) {
        this.users = users;
        this.lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return users.length;
    }

    @Override
    public Object getItem(int position) {
        return users;
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
            v = lInflater.inflate(R.layout.group_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) v.findViewById(R.id.tvName);
            viewHolder.image = (ImageView) v.findViewById(R.id.image);

            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }
        try {
            User user = users[position];
            viewHolder.name.setText(user.getFirstName() + " " + user.getLastName());
            if (viewHolder.image != null) {
                imageLoader.DisplayImage(user.getPictureUrl(),viewHolder.image);
                //new ImageDownloaderTask(viewHolder.image).execute(user.getPictureUrl());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }
}