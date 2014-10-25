package ru.edusty.android.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.edusty.android.Classes.User;
import ru.edusty.android.R;

/**
 * Created by Руслан on 25.10.2014.
 */
public class NavigationDrawerAdapter extends BaseAdapter {
    private final LayoutInflater lInflater;
    String[] titles;
    int[] images;
    static class ViewHolder {
        TextView title;
        ImageView image;
    }

    public NavigationDrawerAdapter(Context context, String[] titles, int[] images) {
        this.lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.titles = titles;
        this.images = images;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return titles;
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
            v = lInflater.inflate(R.layout.navigation_drawer_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) v.findViewById(R.id.tvTitle);
            viewHolder.image = (ImageView) v.findViewById(R.id.image);

            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }
        try {
            viewHolder.image.setImageResource(images[position]);
            viewHolder.title.setText(titles[position]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }
}
