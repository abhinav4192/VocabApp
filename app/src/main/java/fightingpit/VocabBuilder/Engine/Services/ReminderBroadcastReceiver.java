package fightingpit.VocabBuilder.Engine.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import fightingpit.VocabBuilder.Engine.SettingManager;
import fightingpit.VocabBuilder.R;

/**
 * A BroadcastReceiver to reset reminder after reboot.
 */
public class ReminderBroadcastReceiver extends BroadcastReceiver {
    public ReminderBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // Set Actual repeating notification reminder.
        Intent aReminderIntent = new Intent(context , NotificationIntentService.class);
        aReminderIntent.setAction(context.getResources().getString(R.string.reminder_action_code));
        PendingIntent aPendingIntent = PendingIntent.getService(context, 0, aReminderIntent, 0);

        SettingManager aSettingManager = new SettingManager();
        String[] aHourMinute = aSettingManager.getValue(context.getResources().getString(R.string
                .pref_reminder_value)).split(":");

                Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTimeInMillis(System.currentTimeMillis());
        aCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(aHourMinute[0]));
        aCalendar.set(Calendar.MINUTE, Integer.parseInt(aHourMinute[1]));

        AlarmManager aAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        aAlarmManager.cancel(aPendingIntent);
        aAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, aCalendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, aPendingIntent);

    }
}
