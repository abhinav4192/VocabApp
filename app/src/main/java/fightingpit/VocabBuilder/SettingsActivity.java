package fightingpit.VocabBuilder;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import fightingpit.VocabBuilder.Adapter.SetAdapter;
import fightingpit.VocabBuilder.Engine.ContextManager;
import fightingpit.VocabBuilder.Engine.Database.DatabaseMethods;
import fightingpit.VocabBuilder.Model.SetDetails;

public class SettingsActivity extends AppCompatPreferenceActivity{
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        ContextManager.setCurrentActivityContext(this);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("pref_max_words_in_quiz"));
            bindPreferenceSummaryToValue(findPreference("pref_filter_status"));

            Preference connectToNewComputer= findPreference("pref_set_selection");

            connectToNewComputer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("ABG", preference.getKey());

                    showSetSelectorAlert();

                    return false;
                }
            });

        }

        private void showSetSelectorAlert(){

            final DatabaseMethods aDatabaseMethods = new DatabaseMethods();
            final ArrayList<SetDetails> aSets = aDatabaseMethods.getAllSets(false);
            final int aSetSize = aSets.size();


            final SetAdapter aSetAdapter = new SetAdapter(aSets);

            final AlertDialog aAlertDialog = new AlertDialog.Builder(ContextManager
                    .getCurrentActivityContext
                            (), R.style.ThemeAlertDialog)
                    .setTitle("Select sets")
                    .setAdapter(aSetAdapter, null)
                    .setPositiveButton("OK", null)
                    .setNegativeButton("CANCEL",null)
                    .setNeutralButton("Clear All", null)
                    .create();
            aAlertDialog.show();

            aAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(aSetAdapter.updateSelectedSetInDB()){
                        aAlertDialog.dismiss();
                    }else {
                        Toast.makeText(ContextManager.getCurrentActivityContext(), "Please select" +
                                " at least one set.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            aAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aAlertDialog.dismiss();
                }
            });

            aAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i=0;i < aSetSize; i++) {
                        aSets.get(i).setSelected(false);
                    }
                    aSetAdapter.notifyDataSetChanged();
                }
            });
        }


    }
}
