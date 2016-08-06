package fightingpit.VocabBuilder.Engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import fightingpit.VocabBuilder.R;

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

    public Integer getIntegerValue(String key) {
        return mSharedPref.getInt(key, -1);
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

    public void updateValue(String key, Integer value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    /**
     * @return true if sorting is shuffled.
     */
    public boolean toShuffle() {
        return mDefaultSharedPref.getBoolean(ContextManager.getCurrentActivityContext()
                .getResources().getString(R.string.pref_key_shuffle), false);
    }


    /**
     * @return true if only favorite words to be showed.
     */
    public Boolean showOnlyFavorites() {
        return mDefaultSharedPref.getBoolean(ContextManager.getCurrentActivityContext()
                .getResources().getString(R.string.pref_key_filter_favorite), false);
    }

    /**
     * @return Filter based on Learning Progress
     * "1", if to show All words
     * "2", if to show only Learning words
     * "3", if to show only Mastered words
     */
    public String getFilterStatus() {
        String aFilterStatus = mDefaultSharedPref.getString(ContextManager
                .getCurrentActivityContext().getResources().getString(R.string
                        .pref_key_filter_status), "1");
        return aFilterStatus;
    }

    /**
     * @return true if the meanings of the words are to be shown by default.
     */

    public boolean showMeanings() {
        return mDefaultSharedPref.getBoolean(ContextManager
                .getCurrentActivityContext().getResources().getString(R.string
                        .pref_key_show_meaning_word_list), true);
    }


}
