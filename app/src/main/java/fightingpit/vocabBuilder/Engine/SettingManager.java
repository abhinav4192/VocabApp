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


    public SettingManager() {
        mSharedPref = ContextManager.getCurrentActivityContext()
                .getSharedPreferences(ContextManager.getCurrentActivityContext().getResources()
                        .getString(R.string.shared_preferences_file), Context.MODE_PRIVATE);
        mEditor = mSharedPref.edit();
    }

    public String getValue(String key) {
        return mSharedPref.getString(key, "");
    }

    public void updateValue(String key, String value){
        mEditor.putString(key,value);
        mEditor.commit();
    }

}
