package fightingpit.VocabBuilder.Engine;


import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

import fightingpit.VocabBuilder.Engine.Services.ReminderBroadcastReceiver;
import fightingpit.VocabBuilder.Engine.Services.NotificationIntentService;
import fightingpit.VocabBuilder.R;
import fightingpit.VocabBuilder.SettingsActivity;

/**
 * Created by abhinavgarg on 31/12/16.
 *
 * A Class to handle user interaction with reminder settings.
 */
public class ReminderManager{


    private static String TAG = ReminderManager.class.getSimpleName();
    private static ReminderManager mInstance;
    private static Context mContext;
    private static SettingManager mSettingManager;
    private static String mReminderValue;
    private static Preference mReminderPreference;

    private ReminderManager(){}

    /**
     * Initialize ContextManager and SettingManager.
     * @return instance of ReminderManager.
     */
    public static ReminderManager getInstance(){
        mContext = ContextManager.getCurrentActivityContext();
        mSettingManager = new SettingManager();
        mReminderValue = "";

        if(mInstance == null){
            mInstance = new ReminderManager();
        }
        return mInstance;
    }

    /**
     * Handle the user interaction with reminder setting.
     * @param iPreference Reminder preference to modify summary
     */
    public void handleClickOnReminderSetting(Preference iPreference){
        if(mContext instanceof SettingsActivity)
        {
            mReminderPreference = iPreference;
            DialogFragment aTimePickerFragment = new TimePickerFragment();
            aTimePickerFragment.show(((SettingsActivity)
                    mContext).getFragmentManager(),mContext.getResources().getString(R.string
                    .frag_tag_time_picker));
        }
    }

    /**
     * Check if reminder is already set or not.
     * @return true if reminder is set.
     */
    private static boolean isReminderSet(){
        boolean aReturnValue = true;
        mReminderValue = mSettingManager.getValue(ContextManager.getCurrentActivityContext().getString(R
                .string.pref_reminder_value));
        if(mReminderValue.isEmpty()){
            aReturnValue = false;
        }
        return aReturnValue;
    }

    /**
     * Set daily reminder according to time selected by user
     * @param iHourOfDay hour
     * @param iMinute minute
     */
    private static void setReminder(int iHourOfDay, int iMinute){

        // Set Actual repeating notification reminder.
        Intent aReminderIntent = new Intent(mContext , NotificationIntentService.class);
        aReminderIntent.setAction(mContext.getResources().getString(R.string.reminder_action_code));
        PendingIntent aPendingIntent = PendingIntent.getService(mContext, 0, aReminderIntent, 0);

        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTimeInMillis(System.currentTimeMillis());
        aCalendar.set(Calendar.HOUR_OF_DAY, iHourOfDay);
        aCalendar.set(Calendar.MINUTE, iMinute);

        AlarmManager aAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        aAlarmManager.cancel(aPendingIntent);
        aAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, aCalendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, aPendingIntent);


        // All Alarms Cancel Automatically after reboot. Enable resetting reminders after boot.
        ComponentName aReceiver = new ComponentName(mContext, ReminderBroadcastReceiver.class);
        PackageManager aPackageManager = mContext.getPackageManager();

        aPackageManager.setComponentEnabledSetting(aReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


        // Set reminder Summary
        mReminderPreference.setSummary(CommonUtils.getFormattedReminderText(mReminderValue));

    }

    /**
     * Unset previously set reminder.
     */
    private static void unsetReminder(){

        mSettingManager.updateValue(ContextManager.getCurrentActivityContext().getResources().getString(R
                .string.pref_reminder_value), "");

        AlarmManager aAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent aReminderIntent = new Intent(mContext , NotificationIntentService.class);
        PendingIntent aPendingIntent = PendingIntent.getService(mContext, 0, aReminderIntent, 0);
        aAlarmManager.cancel(aPendingIntent);

        // Disable boot receiver
        ComponentName aReceiver = new ComponentName(mContext, ReminderBroadcastReceiver.class);
        PackageManager aPackageManager = mContext.getPackageManager();

        aPackageManager.setComponentEnabledSetting(aReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        // Set reminder Summary
        mReminderPreference.setSummary(mContext.getResources().getString(R.string
                .pref_summary_reminder_default));
    }

    /**
     * A TimePickerFragment Class for reminder setting.
     */
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            boolean aIsReminderAlreadySet = isReminderSet();

            if(aIsReminderAlreadySet)
            {
                // Set reminder values in picker
                String[] aHourMinute = mReminderValue.split(":");
                hour =  Integer.parseInt(aHourMinute[0]);
                minute = Integer.parseInt(aHourMinute[1]);
            }

            // Create a new instance of TimePickerDialog and return it
            TimePickerDialog aDialog = new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));

            if(aIsReminderAlreadySet)
            {
                // If reminder is set, add an unset option
                aDialog.setButton(TimePickerDialog.BUTTON_NEUTRAL, mContext.getResources().getString(R
                        .string.reminder_option_unset), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG,"Unset Reminder");
                        unsetReminder();
                    }
                });
            }
            return aDialog;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Set Reminder
            mReminderValue = Integer.toString(hourOfDay)+":"+ Integer
                    .toString(minute);
            mSettingManager.updateValue(mContext.getResources().getString(R
                    .string.pref_reminder_value), mReminderValue);
            Log.d(TAG,"Set Reminder for: " + mReminderValue);
            setReminder(hourOfDay, minute);
        }
    }


}
