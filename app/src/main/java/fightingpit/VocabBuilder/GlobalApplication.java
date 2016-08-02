package fightingpit.VocabBuilder;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;

import fightingpit.VocabBuilder.Engine.ContextManager;
import fightingpit.VocabBuilder.Engine.Database.WordListHelper;
import fightingpit.VocabBuilder.Engine.SettingManager;
import fightingpit.VocabBuilder.Model.WordWithDetails;

/**
 * Created by abhinavgarg on 31/07/16.
 * This class will be used to instantiate object that are required across the application in
 * multiple activities, fragments.
 */
public class GlobalApplication extends Application {
    WordListHelper mWordListHelper = null;

    public void init()
    {
        mWordListHelper = new WordListHelper();
    }

    public WordListHelper getWordListHelper() {
        return mWordListHelper;
    }

}
