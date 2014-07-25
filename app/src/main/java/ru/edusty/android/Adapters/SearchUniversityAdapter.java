package ru.edusty.android.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Picture;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.UUID;

import ru.edusty.android.Classes.University;
import ru.edusty.android.R;

/**
 * Created by Руслан on 25.07.2014.
 */
public class SearchUniversityAdapter extends BaseAdapter {
    private final University[] results;
    private final LayoutInflater lInflater;

    public SearchUniversityAdapter(Activity activity, University[] searchResult) {
        results = searchResult;
        lInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public static class ViewHolder {
        TextView Title;
        UUID ID;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        View v = convertView;
        if (v == null) {
            v = lInflater.inflate(R.layout.search_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.Title = (TextView) v.findViewById(R.id.Title);

            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();

        }
        viewHolder.Title.setText(results[position].getTitle());
        return v;
    }
}
