package ru.edusty.android.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

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
    private int preLast;

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
        View view = inflater.inflate(R.layout.list_default, container, false);
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
        final int lastItem = firstVisibleItem + visibleItemCount;
        if(lastItem == totalItemCount) {
            new GetFeed().execute(totalItemCount);
        }
    }

    public class GetFeed extends AsyncTask<Integer, Void, Response> {
        //ProgressDialog progressDialog = new ProgressDialog(getActivity());
        ArrayList<Feed> feeds;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
/*            progressDialog.setMessage("Загрузка...");
            progressDialog.show();*/
        }

        @Override
        protected void onPostExecute(Response response) {
            try {
                if (feeds.size() == 0)
                    feeds = (ArrayList<Feed>) Arrays.asList((Feed[]) response.getItem());
                else feeds.add((Feed) response.getItem());
                if (feeds.size() == 0) setListAdapter(null);
                else {
                    setListAdapter(new FeedAdapter(getActivity(), feeds));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //progressDialog.dismiss();
        }

        @Override
        protected Response doInBackground(Integer... params) {
            Response response = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                Gson gson = new Gson();
                HttpGet request = new HttpGet(getString(R.string.serviceUrl) + "GroupMessage?tokenID=" + token + "&offset" + offset);
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
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
