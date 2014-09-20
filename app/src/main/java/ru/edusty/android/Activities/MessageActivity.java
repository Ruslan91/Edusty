package ru.edusty.android.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
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

import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import ru.edusty.android.Adapters.CommentAdapter;
import ru.edusty.android.Classes.Comment;
import ru.edusty.android.Classes.Message;
import ru.edusty.android.Classes.Response;
import ru.edusty.android.FileCache;
import ru.edusty.android.ImageLoader;
import ru.edusty.android.R;

public class MessageActivity extends Activity implements ActionMode.Callback {

    private UUID token;
    private String messageID;
    private ListView listComments;
    private ImageView image;
    private TextView tvName;
    private TextView tvDate;
    private TextView tvMessage;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Comment[] comments;
    private Message message;
    private UUID commentID;
    private UUID userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        token = UUID.fromString(getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE).getString("token", ""));
        userID = UUID.fromString(getSharedPreferences(getString(R.string.app_data), Context.MODE_PRIVATE).getString("userID", ""));
        messageID = getIntent().getStringExtra("messageID");
        listComments = (ListView) findViewById(R.id.listComments);
        listComments.setOnItemLongClickListener(new AbsListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (comments[position].getUser().getUserID().equals(userID)) {
                    commentID = comments[position].getID();
                    startActionMode(MessageActivity.this);
                }
                return true;
            }
        });

        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("userID", message.getUser().getUserID().toString());
                startActivity(intent);
            }
        });
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        image = (ImageView) findViewById(R.id.image);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        mSwipeRefreshLayout.setColorScheme(R.color.green, R.color.yellow, R.color.red, R.color.blue);
        new GetComments().execute();
        new GetMessage().execute();

    }
/*    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }*/
    @Override
    protected void onRestart() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        new GetComments().execute();
    }

    //Получение записи
    public class GetMessage extends AsyncTask<UUID, Void, Response> {

        ProgressDialog progressDialog = new ProgressDialog(MessageActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Загрузка...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Response response) {
            try {
                if (response.getItem() != null) {
                    message = (Message) response.getItem();
                    tvName.setText(message.getUser().getFirstName() + " " + message.getUser().getLastName());
                    tvMessage.setText(message.getMessage());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = dateFormat.parse(message.getMessageDate());
                    TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
                    Calendar calendar = Calendar.getInstance();
                    Date time = calendar.getTime();
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    if (dateFormat.format(date).equals(dateFormat.format(time))) {
                        dateFormat = new SimpleDateFormat("сегодня в HH:mm");
                        dateFormat.setTimeZone(timeZone);
                        tvDate.setText(dateFormat.format(date));
                    } else {
                        dateFormat = new SimpleDateFormat("dd");
                        if (Integer.parseInt(dateFormat.format(date)) + 1 == Integer.parseInt(dateFormat.format(time))) {
                            dateFormat = new SimpleDateFormat("вчера в HH:mm");
                            dateFormat.setTimeZone(timeZone);
                            tvDate.setText(dateFormat.format(date));
                        } else {
                            dateFormat = new SimpleDateFormat("dd MMMM yyyy в HH:mm");
                            dateFormat.setTimeZone(timeZone);
                            tvDate.setText(dateFormat.format(date));
                        }
                    }
                    new ImageLoader(getApplicationContext()).DisplayImage(message.getUser().getPictureUrl(), image);

                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llayout);
                    if (message.getFiles() != null) {
                        for (int i = 0; i < message.getFiles().size(); i++) {
                            final ImageView imageView = new ImageView(MessageActivity.this);
                            imageView.setTag(i);
                            imageView.setPadding(4, 4, 4, 4);
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            imageView.setLayoutParams(new LinearLayout.LayoutParams(150,150));
                            new ImageLoader(getApplicationContext())
                                    .DisplayImage(getString(R.string.serviceUrl) + "File?tokenID=" + token + "&fileID=" + message.getFiles().get(i), imageView);
                            linearLayout.addView(imageView);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                                    intent.putExtra("fileURL", getString(R.string.serviceUrl)
                                            + "File?tokenID=" + token
                                            + "&fileID=" + message.getFiles().get((Integer) imageView.getTag()));
                                    startActivity(intent);

/*                                    File file = new FileCache(getApplicationContext())
                                            .getFile(getString(R.string.serviceUrl)
                                                    + "File?tokenID=" + token
                                                    + "&fileID=" + message.getFiles().get((Integer) imageView.getTag()));
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(file), "image*//*");
                                    startActivity(intent);*/
                                }
                            });
                        }
                    }
                }
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Response doInBackground(UUID... params) {
            Response response = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                Gson gson = new Gson();
                HttpGet request = new HttpGet(getString(R.string.serviceUrl) + "GroupMessage?tokenID=" + token + "&messageID=" + messageID);
                HttpResponse httpResponse = httpclient.execute(request);
                InputStreamReader reader = new InputStreamReader(httpResponse.getEntity()
                        .getContent(), HTTP.UTF_8);
                Type fooType = new TypeToken<Response<Message>>() {
                }.getType();
                response = gson.fromJson(reader, fooType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

    }

    //Получение комментариев
    public class GetComments extends AsyncTask<Integer, Void, Response> {

        private CommentAdapter commentAdapter;

        @Override
        protected void onPostExecute(Response response) {
            try {
                comments = (Comment[]) response.getItem();
                if (comments.length != 0) {
                    commentAdapter = new CommentAdapter(MessageActivity.this, new ArrayList<Comment>(Arrays.asList(comments)));
                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(commentAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listComments);
                    listComments.setAdapter(swingBottomInAnimationAdapter);
                } else listComments.setAdapter(null);
                commentAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Response doInBackground(Integer... params) {
            Response response = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                Gson gson = new Gson();
                HttpGet request = new HttpGet(getString(R.string.serviceUrl) + "Comments?tokenID=" + token + "&messageID=" + messageID);
                HttpResponse httpResponse = httpclient.execute(request);
                InputStreamReader reader = new InputStreamReader(httpResponse.getEntity()
                        .getContent(), HTTP.UTF_8);
                Type fooType = new TypeToken<Response<Comment[]>>() {
                }.getType();
                response = gson.fromJson(reader, fooType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

    }

    public class DeleteComment extends AsyncTask<UUID, Void, Response> {
        ProgressDialog progressDialog = new ProgressDialog(MessageActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response.getItem().equals(true)) {
                refresh();
            } else
                Toast.makeText(MessageActivity.this, response.getStatus(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        @Override
        protected Response doInBackground(UUID... params) {
            Response response = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                Gson gson = new Gson();
                HttpDelete request = new HttpDelete(getString(R.string.serviceUrl) + "Comment?tokenID=" + token + "&commentID=" + commentID);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_add:
                Intent intent = new Intent(MessageActivity.this, CreateCommentActivity.class);
                intent.putExtra("messageID", messageID);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.action_mode_comments_list, menu);

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
                new DeleteComment().execute();
                mode.finish();
                return true;
/*            case R.id.action_edit:
                Intent intent = new Intent(getActivity(), CreateMessageActivity.class);
                intent.putExtra("message", message);
                intent.putExtra("messageID", messageID.toString());
                startActivity(intent);
                mode.finish();
                return true;*/
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}
