package ru.edusty.android.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import ru.edusty.android.Classes.SuggestedGroups;
import ru.edusty.android.R;

/**
 * Created by Руслан on 24.10.2014.
 */
public class SuggestedGroupsAdapter extends BaseAdapter {
    private final SuggestedGroups[] results;
    private final LayoutInflater lInflater;

    public SuggestedGroupsAdapter(Activity activity, SuggestedGroups[] suggestedGroups) {
        this.lInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.results = suggestedGroups;
    }

    @Override
    public int getCount() {
        return results.length;
    }

    @Override
    public Object getItem(int position) {
        return results[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder {
        TextView GroupTitle;
        TextView UniversityTitle;
        TextView MembersCount;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ((ListView) parent).setItemChecked(position, true);
        final ViewHolder viewHolder;
        View v = convertView;
        if (v == null) {
            v = lInflater.inflate(R.layout.suggested_groups_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.GroupTitle = (TextView) v.findViewById(R.id.tvGroupTitle);
            viewHolder.UniversityTitle = (TextView) v.findViewById(R.id.tvUniversityTitle);
            viewHolder.MembersCount = (TextView) v.findViewById(R.id.tvMembers);

            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }
        viewHolder.GroupTitle.setText(results[position].getGroupTitle());
        if (results[position].getCountry() != null && results[position].getCity() != null) {
            viewHolder.UniversityTitle.setText(
                    results[position].getUniversityTitle() + " (" + results[position].getCountry() + ", " + results[position].getCity() + ")");
        }
        viewHolder.MembersCount.setText("Участников: " + results[position].getMembersCount().toString());
        return v;
    }
}
