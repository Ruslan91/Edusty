package ru.edusty.android.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

import ru.edusty.android.Classes.GetGroups;
import ru.edusty.android.R;

/**
 * Created by Руслан on 25.07.2014.
 */
public class SearchGroupAdapter extends BaseAdapter {
    private final List<GetGroups> results;
    private final LayoutInflater lInflater;

    public SearchGroupAdapter(Context context, List<GetGroups> searchResult) {
        this.lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.results = searchResult;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder {
        TextView Title;
        TextView Members;
        UUID ID;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ((ListView) parent).setItemChecked(position, true);
        final ViewHolder viewHolder;
        View v = convertView;
        if (v == null) {
            v = lInflater.inflate(R.layout.search_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.Title = (TextView) v.findViewById(R.id.tvTitle);
            viewHolder.Members = (TextView) v.findViewById(R.id.tvMembers);

            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();

        }
        viewHolder.Title.setText(results.get(position).getTitle());
        if ((results.get(position).getMembersCount() - results.get(position).getMembers().size()) != 0) {
            String members = "";
            for (int i = 0; i < results.get(position).getMembers().size(); i++) {
                if (i != results.get(position).getMembers().size() - 1) {
                    members = members + results.get(position).getMembers().get(i) + ", ";
                } else
                    members = members + results.get(position).getMembers().get(i) + " и ещё " + (results.get(position).getMembersCount() - results.get(position).getMembers().size()) + " участник(ов)";
            };
            viewHolder.Members.setText(members);
        } else viewHolder.Members.setText("В группе нет участников");

        return v;
    }
}
