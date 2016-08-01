package fightingpit.VocabBuilder;

import android.app.Application;

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
    WordListHelper mWordListHelper = new WordListHelper();

    public WordListHelper getWordListHelper() {

        // Test Code for WordList to be used throughout appplication.
        for(Integer i=0;i<50;i++)
        {
            WordWithDetails aWord = new WordWithDetails();
            aWord.setWord("Word " + i.toString());
            aWord.setMeaning("Word Meaning " + i.toString());
            aWord.setSentence("Sentence for the meaning. Long sentence. Really Long. " + i.toString
                    ());
            mWordListHelper.getWordList().add(aWord);
        }
        return mWordListHelper;
    }

}
