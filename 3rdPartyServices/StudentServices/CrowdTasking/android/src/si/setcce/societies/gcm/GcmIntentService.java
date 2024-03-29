package si.setcce.societies.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.societies.thirdpartyservices.crowdtasking.MainActivity;
import org.societies.thirdpartyservices.crowdtasking.R;

import java.util.HashMap;
import java.util.Map;

import si.setcce.societies.crowdtasking.gcm.GcmMessage;

/**
 * Created by Simon on 21.10.2013.
 */
/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            Map<String, String> parameters = new HashMap<String, String>();
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                parameters.put(GcmMessage.PARAMETER_MESSAGE, "Send error: " + extras.toString());
                sendNotification(parameters);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                parameters.put(GcmMessage.PARAMETER_MESSAGE, "Deleted messages on server: " + extras.toString());
                sendNotification(parameters);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                parameters.put(GcmMessage.PARAMETER_MESSAGE, extras.getString(GcmMessage.PARAMETER_MESSAGE));
                //	            parameters.put(GcmMessage.PARAMETER_URL, extras.getString(GcmMessage.PARAMETER_URL));
                parameters.put(GcmMessage.PARAMETER_MEETING_SUBJECT, extras.getString(GcmMessage.PARAMETER_MEETING_SUBJECT));
                parameters.put(GcmMessage.PARAMETER_MEETING_ID, extras.getString(GcmMessage.PARAMETER_MEETING_ID));
                sendNotification(parameters, extras.getString(GcmMessage.PARAMETER_URL));
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Map<String, String> parameters) {
        sendNotification(parameters, "");
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Map<String, String> parameters, String downloadUrl) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        String msg = parameters.get(GcmMessage.PARAMETER_MESSAGE);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction("GCM");
        intent.putExtra(GcmMessage.PARAMETER_MESSAGE, msg);
        intent.putExtra(GcmMessage.PARAMETER_URL, downloadUrl);
        intent.putExtra(GcmMessage.PARAMETER_MEETING_SUBJECT, parameters.get(GcmMessage.PARAMETER_MEETING_SUBJECT));
        intent.putExtra(GcmMessage.PARAMETER_MEETING_ID, parameters.get(GcmMessage.PARAMETER_MEETING_ID));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("SCT Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }
}

