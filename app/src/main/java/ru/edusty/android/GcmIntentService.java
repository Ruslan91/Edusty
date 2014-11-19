package ru.edusty.android;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import ru.edusty.android.Activities.AboutActivity;
import ru.edusty.android.Activities.MainActivity;
import ru.edusty.android.Activities.PromoteActivity;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static final String TAG = "GCM";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString(), 0);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " + extras.toString(),0);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // This loop represents the service doing some work.
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification(extras.getString("alert"), extras.getString("badge"), extras.getString("type"));
                Log.i(TAG, "Received: " + extras);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg, String badge, String type) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = null;
        if (type.equals("0")) {
            Intent intent = new Intent(this, MainActivity.class);
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_content_email)
                    .setContentTitle(msg.substring(0, msg.indexOf(":")))
                    .setContentText(msg.substring(msg.indexOf(":") + 1))
                    .setTicker(msg)
                    .setContentInfo(badge)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setAutoCancel(true)
                            //.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentIntent(PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_CANCEL_CURRENT));
        } else if (type.equals("1")) {
            Intent intent = new Intent(this, PromoteActivity.class);
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_system_push)
                    .setContentTitle(msg.substring(0, msg.indexOf(":")))
                    .setContentText(msg.substring(msg.indexOf(":") + 1))
                    .setTicker(msg)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_CANCEL_CURRENT));
        }
        mNotificationManager.cancelAll();
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
