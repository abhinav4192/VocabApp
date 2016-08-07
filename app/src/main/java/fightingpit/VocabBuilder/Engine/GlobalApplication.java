package fightingpit.VocabBuilder.Engine;

import android.app.Application;

import fightingpit.VocabBuilder.Engine.Database.DatabaseMethods;

/**
 * Created by abhinavgarg on 31/07/16.
 * This class will be used to instantiate object that are required across the application in
 * multiple activities, fragments.
 */
public class GlobalApplication extends Application {
    DatabaseMethods mDatabaseMethods = null;
    TextToSpeechManager mTextToSpeechManager = null;

    public void init()
    {
        mDatabaseMethods = new DatabaseMethods();
        mTextToSpeechManager = new TextToSpeechManager();
        mTextToSpeechManager.init();
    }

    public DatabaseMethods getDatabaseMethods() {
        return mDatabaseMethods;
    }

    public TextToSpeechManager getTextToSpeechManager() {
        return mTextToSpeechManager;
    }

}
