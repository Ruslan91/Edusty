package ru.edusty.android.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import ru.edusty.android.Adapters.FeedAdapter;
import ru.edusty.android.Classes.Feed;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.FileCache;
import ru.edusty.android.R;
import ru.edusty.android.SwipeRefreshListFragment;

/**
 * Created by Руслан on 23.07.2014.
 */
public class FeedFragment extends SwipeRefreshListFragment implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener, ActionMode.Callback {

    private String token;
    private int offset = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Feed> feeds;
    private FeedAdapter feedAdapter;
    private boolean executed = false;
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    private UUID messageID;
    private String userID;
    private String message;
    private FileCache fileCache;
    private Feed[] feed;

    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        fileCache = new FileCache(getActivity());
    }

    @Override
    public ListView getListView() {
        return super.getListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_feed, container, false);
        setRetainInstance(true);
        token = getActivity().getSharedPreferences(getString(R.string.app_data), Context.MODE_PRIVATE).getString("token", "");
        userID = getActivity().getSharedPreferences(getString(R.string.app_data), Context.MODE_PRIVATE).getString("userID", "");
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(R.color.green, R.color.yellow, R.color.red, R.color.blue);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isOnline()) {
            //refresh(0);
            getListView().setOnScrollListener(this);
            getListView().setOnItemLongClickListener(new AbsListView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (feeds.get(position).getUser().getUserID().toString().equals(userID)) {
                        messageID = feeds.get(position).getMessageID();
                        message = feeds.get(position).getMessage();
                        getListView().startActionMode(FeedFragment.this);
                    }
                    return true;
                }
            });
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), MessageActivity.class);
                    intent.putExtra("messageID", feeds.get(position).getMessageID().toString());
                    startActivity(intent);
                }
            });
        } else {
            if (readFile() != null) {
                try {
                    feeds = readFile();
                    feedAdapter = new FeedAdapter(getActivity(), feeds);
                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(feedAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(getListView());
                    setListAdapter(swingBottomInAnimationAdapter);
                    executed = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else setListAdapter(null);
            Toast.makeText(getActivity(), "Соединение с интернетом отсутствует.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        //refresh(0);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isOnline())
            writeFile(feeds);
    }

    private void refresh(int offset) {
        new GetFeed().execute(offset);
        executed = true;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean writeFile(ArrayList<Feed> feeds) {
        try {
            FileOutputStream fos = getActivity().openFileOutput("feeds", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(feeds);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public ArrayList<Feed> readFile() {
        try {
            FileInputStream fis = getActivity().openFileInput("feeds");
            ObjectInputStream ois = new ObjectInputStream(fis);
            feeds = (ArrayList<Feed>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return feeds;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.feed_menu, menu);
        if (!isOnline()) menu.getItem(0).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_message:
                startActivity(new Intent(getActivity(), SendMessageActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh(0);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastItem = firstVisibleItem + visibleItemCount;
        if (visibleItemCount > 0 && ++firstVisibleItem + visibleItemCount == totalItemCount && lastItem != 0 && !executed) {
            new GetFeed().execute(totalItemCount);
            executed = true;
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (isOnline())
            mode.getMenuInflater().inflate(R.menu.action_mode_feed_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                new DeleteMessage().execute(messageID);
                mode.finish();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(getActivity(), SendMessageActivity.class);
                intent.putExtra("message", message);
                intent.putExtra("messageID", messageID.toString());
                startActivity(intent);
                mode.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    //Получение ленты сообщений
    public class GetFeed extends AsyncTask<Integer, Void, Response> {

/*        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Загрузка...");
            progressDialog.show();
        }*/

        @Override
        protected void onPostExecute(Response response) {
            try {
                if (offset == 0 && feeds != null) feeds = null;
                feed = (Feed[]) response.getItem();
                if (feed.length != 0 && feeds == null) {
                    feeds = new ArrayList<Feed>(Arrays.asList(feed));
                    feedAdapter = new FeedAdapter(getActivity(), feeds);
                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(feedAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(getListView());
                    setListAdapter(swingBottomInAnimationAdapter);
                } else if (feed.length != 0 && feeds.size() != 0) {
                    for (int i = 0; i < feed.length; i++) {
                        feeds.add(offset + i, feed[i]);
                    }
                } else if (feeds == null && feed.length == 0) setListAdapter(null);
                feedAdapter.notifyDataSetChanged();
                executed = false;
                //progressDialog.dismiss();*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Response doInBackground(Integer... params) {
            Response response = null;
            try {
                offset = params[0];
                HttpClient httpclient = new DefaultHttpClient();
                Gson gson = new Gson();
                HttpGet request = new HttpGet(getString(R.string.serviceUrl) + "GroupMessages?tokenID=" + token + "&offset=" + offset);
                HttpResponse httpResponse = httpclient.execute(request);
                InputStreamReader reader = new InputStreamReader(httpResponse.getEntity()
                        .getContent(), HTTP.UTF_8);
                Type fooType = new TypeToken<Response<Feed[]>>() {
                }.getType();
                response = gson.fromJson(reader, fooType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    //Удаление сообщения
    public class DeleteMessage extends AsyncTask<UUID, Void, Response> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response response) {
            try {
                if (response.getItem().equals(true)) {
                    refresh(0);
                } else
                    Toast.makeText(getActivity(), response.getStatus(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Response doInBackground(UUID... params) {
            Response response = null;
            try {
                messageID = params[0];
                HttpClient httpclient = new DefaultHttpClient();
                Gson gson = new Gson();
                HttpDelete request = new HttpDelete(getString(R.string.serviceUrl) + "GroupMessage?tokenID=" + token + "&messageID=" + messageID);
                HttpResponse httpResponse = httpclient.execute(request);
                InputStreamReader reader = new InputStreamReader(httpResponse.getEntity()
                        .getContent(), HTTP.UTF_8);
                response = gson.fromJson(reader, Response.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

    }
}
