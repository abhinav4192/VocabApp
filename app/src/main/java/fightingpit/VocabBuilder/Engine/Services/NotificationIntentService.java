package fightingpit.VocabBuilder.Engine.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import fightingpit.VocabBuilder.MainActivity;
import fightingpit.VocabBuilder.R;

/**
 * A service to display reminder notification to user.
 */
public class NotificationIntentService extends IntentService {

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String aAction = intent.getAction();
            if(getResources().getString(R.string.reminder_action_code).equalsIgnoreCase(aAction))
            {
                displayNotification();
            }

        }
    }

    private void displayNotification() {
        Intent aIntent = new Intent(this, MainActivity.class);
        int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent aPendingIntent = PendingIntent.getActivity(this, requestID, aIntent, flags);

        // TODO: Change icons and colour.
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(R.drawable.star_filled)
                        .setColor(Color.parseColor("#009688"))
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(getResources().getString(R.string
                                .reminder_notification_text))
                        .setContentIntent(aPendingIntent);

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }

}
