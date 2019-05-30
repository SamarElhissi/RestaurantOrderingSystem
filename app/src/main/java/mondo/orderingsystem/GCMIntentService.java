package mondo.orderingsystem;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GCMIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1000;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    String branch_id="";
    String msg = "";
    String title = "";
    public GCMIntentService() {
        super(GCMIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if (!extras.isEmpty()) {

            // read extras as sent from server
             branch_id = extras.getString("branch_id");
             msg = extras.getString("gcm.notification.body");
             title = extras.getString("gcm.notification.title");

            sendNotification(msg, title );


        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg ,String title) {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        // intent.putExtra("url", "https://www.restaurantlogin.com/mobile/menu?company_uid=cd6801cb-200c-495d-9b31-66637e177162");//+IdsList.get(position));
        intent.putExtra("msg", msg);
        intent.putExtra("title", title);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext()).setSmallIcon(R.drawable.mondo_logo)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
