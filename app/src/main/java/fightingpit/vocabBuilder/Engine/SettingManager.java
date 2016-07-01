package fightingpit.vocabBuilder.Engine;

import android.content.Context;
import android.content.SharedPreferences;

import fightingpit.vocabBuilder.R;

/**
 * Created by abhinavgarg on 02/07/16.
 */
public class SettingManager {

    SharedPreferences mSharedPref;
    SharedPreferences.Editor mEditor;

    private static final String ALPHA_OR_SHUFFLE = "ALPHA_OR_SHUFFLE";

    public SettingManager() {
        mSharedPref = ContextManager.getCurrentActivityContext()
                .getSharedPreferences(ContextManager.getCurrentActivityContext().getResources()
                        .getString(R.string.shared_preferences_file), Context.MODE_PRIVATE);
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
     * @return true is sorting is alphabetical.
     */
    public boolean sortIsAlphabetical() {
        return mSharedPref.getBoolean(ALPHA_OR_SHUFFLE, true);
    }

    /**
     * @return true is sorting is shuffled.
     */
    public boolean sortIsShuffled() {
        return !sortIsAlphabetical();
    }

    /**
     * Set sort as Alphabetical.
     */
    public void sortSetAlphabetical() {
        mEditor.putBoolean(ALPHA_OR_SHUFFLE, true);
    }

    /**
     * Set sort as Shuffled.
     */
    public void sortSetShuffled() {
        mEditor.putBoolean(ALPHA_OR_SHUFFLE, false);
    }


}
