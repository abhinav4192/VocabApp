package fightingpit.vocabBuilder.Engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import fightingpit.vocabBuilder.R;

/**
 * Created by abhinavgarg on 02/07/16.
 */
public class SettingManager {

    SharedPreferences mSharedPref;
    SharedPreferences mDefaultSharedPref;
    SharedPreferences.Editor mEditor;

    private static final String ALPHA_OR_SHUFFLE = "ALPHA_OR_SHUFFLE";

    public SettingManager() {
        mSharedPref = ContextManager.getCurrentActivityContext()
                .getSharedPreferences(ContextManager.getCurrentActivityContext().getResources()
                        .getString(R.string.shared_preferences_file), Context.MODE_PRIVATE);
        mDefaultSharedPref = PreferenceManager.getDefaultSharedPreferences(ContextManager
                .getCurrentActivityContext());
        mEditor = mSharedPref.edit();
    }

    /**
     * Generic Method to return Shared Preference for a given key.
     *
     * @param key for which value has to be returned.
     * @return value for input key.
     */
    public String getValue(String key) {
        return mSharedPref.getString(key, "");
    }

    /**
     * Generic Method to update Shared Preference. Shared Preference will we stored as String.
     *
     * @param key   key to be updated
     * @param value value to be put for the key.
     */
    public void updateValue(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    // Sort Methods.

    /**
     * @return true if sorting is alphabetical.
     */
    public boolean sortIsAlphabetical() {
        return mSharedPref.getBoolean(ALPHA_OR_SHUFFLE, true);
    }

    /**
     * @return true if sorting is shuffled.
     */
    public boolean sortIsShuffled() {
        return !sortIsAlphabetical();
    }

    /**
     * Set sort as Alphabetical.
     */
    public void sortSetAlphabetical() {
        mEditor.putBoolean(ALPHA_OR_SHUFFLE, true);
        mEditor.commit();
    }

    /**
     * Set sort as Shuffled.
     */
    public void sortSetShuffled() {
        mEditor.putBoolean(ALPHA_OR_SHUFFLE, false);
        mEditor.commit();
    }

    /**
     * @return the number of current set user is learning.
     */
    public String getCurrentSetNumber(){
        // TODO: Implement this.
        return "1";
    }

    /**
     * @return true if only favorite words to be showed.
     */
    public Boolean showOnlyFavorites(){
        return mDefaultSharedPref.getBoolean(ContextManager.getCurrentActivityContext()
                .getResources().getString(R.string.pref_key_filter_favorite),false);
    }

    /**
     * @return Filter based on Learning Progress
     *          "1", if to show All words
     *          "2", if to show only Learning words
     *          "3", if to show only Mastered words
     */
    public String getFilterStatus(){
        Integer aFilterStatus = mDefaultSharedPref.getInt(ContextManager
                .getCurrentActivityContext().getResources().getString(R.string
                        .pref_key_filter_status),1);
        return aFilterStatus.toString();
    }


}
