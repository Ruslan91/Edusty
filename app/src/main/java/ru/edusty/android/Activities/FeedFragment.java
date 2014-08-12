package ru.edusty.android.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import ru.edusty.android.Adapters.FeedAdapter;
import ru.edusty.android.Classes.Feed;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.R;
import ru.edusty.android.SwipeRefreshListFragment;

/**
 * Created by Руслан on 23.07.2014.
 */
public class FeedFragment extends SwipeRefreshListFragment implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {

    private String token;
    private int offset = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Feed[] feed;
    private ArrayList<Feed> feeds;
    private FeedAdapter feedAdapter;
    private boolean executed = false;
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;

    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
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
        token = getActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE).getString("token", "");
        new GetFeed().execute(offset);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(R.color.blue, R.color.green, R.color.yellow, R.color.red);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnScrollListener(this);
    }

    private void refresh(int offset) {
        new GetFeed().execute(offset);
        executed = true;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.feed_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_message:
                startActivity(new Intent(getActivity(), CreateMessageActivity.class));
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
        }, 5000);
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

    //Получение ленты сообщений
    public class GetFeed extends AsyncTask<Integer, Void, Response> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

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
                HttpGet request = new HttpGet(getString(R.string.serviceUrl) + "GroupMessage?tokenID=" + token + "&offset=" + offset);
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
}
