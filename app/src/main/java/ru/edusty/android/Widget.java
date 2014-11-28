package ru.edusty.android;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import ru.edusty.android.Activities.ImageActivity;
import ru.edusty.android.Activities.MessageActivity;
import ru.edusty.android.Adapters.FeedAdapter;
import ru.edusty.android.Classes.Feed;
import ru.edusty.android.Classes.Message;
import ru.edusty.android.Classes.Response;

/**
 * Created by Руслан on 22.11.2014.
 */
public class Widget extends AppWidgetProvider {

    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
    private Context context;
    private String token;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //вызываем переопределенный метод родительского класса
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //пишем в лог имя данного метода для отслеживания
        Log.d("kkk", "onUpdate");
        this.context = context;
        token = context.getSharedPreferences(context.getString(R.string.app_data), Context.MODE_PRIVATE).getString("token","");
        //Создаем новый RemoteViews
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        new GetFeed(remoteViews,appWidgetManager,appWidgetIds).execute(0);
        //Создаем строку с текущей датой и временем
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String now = dateFormat.format(date);
        //обновляем данные в TextView виджета
        remoteViews.setTextViewText(R.id.tvMessage, now);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //вызываем переопределенный метод родительского класса
        super.onReceive(context, intent);
        //пишем в лог имя текущего метода
        Log.d("kkk", "onReceive");

        //Ловим наш Broadcast
        final String action = intent.getAction();
        if (ACTION_WIDGET_RECEIVER.equals(action)) {

        }
    }

    //    Получение ленты сообщений
    public class GetFeed extends AsyncTask<Integer, Void, Response> {
        private RemoteViews remoteViews;
        private AppWidgetManager appWidgetManager;
        private int[] appWidgetIds;

        public GetFeed(RemoteViews remoteViews, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            this.remoteViews = remoteViews;
            this.appWidgetManager = appWidgetManager;
            this.appWidgetIds = appWidgetIds;
        }

        @Override
        protected void onPostExecute(Response response) {
            try {
                Feed[] feeds = (Feed[]) response.getItem();
                remoteViews.setTextViewText(R.id.tvName, feeds[0].getUser().getFirstName() + " " + feeds[0].getUser().getLastName());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = dateFormat.parse(feeds[0].getMessageDate());
                TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
                Calendar calendar = Calendar.getInstance();
                Date time = calendar.getTime();
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if (dateFormat.format(date).equals(dateFormat.format(time))) {
                    dateFormat = new SimpleDateFormat("сегодня в HH:mm");
                    dateFormat.setTimeZone(timeZone);
                    remoteViews.setTextViewText(R.id.tvDate, dateFormat.format(date));
                } else {
                    dateFormat = new SimpleDateFormat("dd");
                    if (Integer.parseInt(dateFormat.format(date)) + 1 == Integer.parseInt(dateFormat.format(time))) {
                        dateFormat = new SimpleDateFormat("вчера в HH:mm");
                        dateFormat.setTimeZone(timeZone);
                        remoteViews.setTextViewText(R.id.tvDate, dateFormat.format(date));
                    } else {
                        dateFormat = new SimpleDateFormat("dd MMMM yyyy в HH:mm");
                        dateFormat.setTimeZone(timeZone);
                        remoteViews.setTextViewText(R.id.tvDate, dateFormat.format(date));
                    }
                }
                remoteViews.setTextViewText(R.id.tvMessage, feeds[0].getMessage());
                appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            progressDialog.dismiss();
        }

        @Override
        protected Response doInBackground(Integer... params) {
            Response response = null;
            try {
                Integer offset = params[0];
                HttpClient httpclient = new DefaultHttpClient();
                Gson gson = new Gson();
                HttpGet request = new HttpGet(context.getString(R.string.serviceUrl) + "GroupMessages?tokenID=" + token + "&offset=" + offset);
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