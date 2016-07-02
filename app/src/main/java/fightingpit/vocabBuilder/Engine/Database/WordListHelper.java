package fightingpit.vocabBuilder.Engine.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import fightingpit.vocabBuilder.Engine.ContextManager;
import fightingpit.vocabBuilder.Engine.SettingManager;
import fightingpit.vocabBuilder.Model.WordWithDetails;
import fightingpit.vocabBuilder.R;

/**
 * Created by abhinavgarg on 30/06/16.
 */
public class WordListHelper extends DatabaseHelper {

    static ArrayList<WordWithDetails> mWordList = new ArrayList<>();


    /**
     * Updates the Word list with words based on current settings.
     * Settings Considered:-
     * 1) Set Number
     * 2) Favorite Filter
     * 3) Progress Filter
     */
    void updateWordList() {
        mWordList.clear();
        // Update Word list on basis of current preferences.

        SettingManager aSettingManager = new SettingManager();
        SQLiteDatabase aReadableDatabase = getReadableDatabase();

        String aSetNumberString = aSettingManager.getCurrentSetNumber();
        Integer aSetNumberInteger = Integer.parseInt(aSetNumberString);
        Cursor c;
        // 1) Applying Set Number Filter
        if (aSetNumberInteger >= 2 && aSetNumberInteger <= 27) {
            // Reaching here means user have alphabet as sets
            // Setting Alphabet from set number
            String aGetSetNameQuery = "SELECT " + DatabaseContract.SetNameNumber.SET_NAME + " " +
                    "FROM " + DatabaseContract.SetNameNumber.TABLE_NAME + " WHERE " +
                    DatabaseContract.SetNameNumber.SET_NUMBER + "=?";
            String[] aSelectionArgsSetName = {aSetNumberString};
            Cursor aCursor = aReadableDatabase.rawQuery(aGetSetNameQuery, aSelectionArgsSetName);
            aCursor.moveToFirst();
            String aSetName = aCursor.getString(aCursor.getColumnIndexOrThrow(DatabaseContract
                    .SetNameNumber
                    .SET_NAME));
            aCursor.close();
            String aAlphabet = aSetName.split(" ")[1];

            // Setting all words stating with alphabet;
            String aQuery = "SELECT * FROM " + DatabaseContract.WordMeaning.TABLE_NAME + " WHERE " +
                    DatabaseContract.WordMeaning.WORD + " LIKE '?%'";
            String[] aSelectionArgs = {aAlphabet};
            c = aReadableDatabase.rawQuery(aQuery, aSelectionArgs);
        } else {
            String aQuery = "SELECT * FROM " + DatabaseContract.WordMeaning.TABLE_NAME + " INNER " +
                    "JOIN" +
                    " " + DatabaseContract.WordSet.TABLE_NAME + " WHERE " + DatabaseContract.WordSet
                    .SET_NUMBER + "=? AND" + DatabaseContract.WordMeaning.WORD + "=" +
                    DatabaseContract.WordSet.WORD + " ORDER BY " + DatabaseContract.WordMeaning
                    .WORD;
            String[] aSelectionArgs = {aSetNumberString};
            c = aReadableDatabase.rawQuery(aQuery, aSelectionArgs);
        }
        c.moveToFirst();
        while (c.isAfterLast()) {

            WordWithDetails aWordWithDetails = new WordWithDetails();

            // 2) Applying Favorite Filter
            if ((c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordMeaning.FAVOURITE)))
                    .equalsIgnoreCase("Y")) {
                aWordWithDetails.setFavourite(true);
            } else {
                if (aSettingManager.showOnlyFavorites()) {
                    c.moveToNext();
                    continue;
                } else {
                    aWordWithDetails.setFavourite(false);
                }
            }

            // 3) Apply Progress Filter
            int aProgress = c.getInt(c.getColumnIndexOrThrow(DatabaseContract
                    .WordMeaning.PROGRESS));
            String aFilterStatus = aSettingManager.getFilterStatus();
            if (aFilterStatus.equalsIgnoreCase(ContextManager.getCurrentActivityContext()
                    .getResources().getString(R.string.filter_learning)) && aProgress ==
                    ContextManager
                            .getCurrentActivityContext().getResources().getInteger(R.integer
                            .MAX_PROGRESS)) {
                c.moveToNext();
                continue;
            }
            if (aFilterStatus.equalsIgnoreCase(ContextManager.getCurrentActivityContext()
                    .getResources().getString(R.string.filter_mastered)) && aProgress !=
                    ContextManager
                            .getCurrentActivityContext().getResources().getInteger(R.integer
                            .MAX_PROGRESS)) {
                c.moveToNext();
                continue;
            }

            // Adding word to list.
            aWordWithDetails.setWord(c.getString(c.getColumnIndexOrThrow(DatabaseContract
                    .WordMeaning.WORD)));
            aWordWithDetails.setMeaning(c.getString(c.getColumnIndexOrThrow(DatabaseContract
                    .WordMeaning.MEANING)));
            aWordWithDetails.setSentence(c.getString(c.getColumnIndexOrThrow(DatabaseContract
                    .WordMeaning.SENTENCE)));
            aWordWithDetails.setProgress(aProgress);

            if ((c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordMeaning.ORIGINAL)))
                    .equalsIgnoreCase("Y")) {
                aWordWithDetails.setOriginal(true);
            } else {
                aWordWithDetails.setOriginal(false);
            }
            mWordList.add(aWordWithDetails);
            c.moveToNext();
        }
        c.close();

    }

}
