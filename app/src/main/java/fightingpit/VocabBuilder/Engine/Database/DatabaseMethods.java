package fightingpit.VocabBuilder.Engine.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import fightingpit.VocabBuilder.Engine.ContextManager;
import fightingpit.VocabBuilder.Engine.SettingManager;
import fightingpit.VocabBuilder.Model.SetDetails;
import fightingpit.VocabBuilder.Model.WordWithDetails;
import fightingpit.VocabBuilder.R;

/**
 * Created by abhinavgarg on 30/06/16.
 */
public class DatabaseMethods extends DatabaseHelper {

    Set<WordWithDetails> mWordSet = new HashSet<>();
    private ArrayList<WordWithDetails> mWordList = new ArrayList<>();

    public ArrayList<WordWithDetails> getWordList() {
        return mWordList;
    }

    public void clearWordList() {
        mWordList.clear();
        mWordSet.clear();
    }

    /**
     * Updates the Word list with words based on current settings.
     * Settings Considered:-
     * 1) Sets Selected. Multiple selected sets allowed.
     * 2) Favorite Filter
     * 3) Progress Filter
     */
    public void updateWordList() {

        clearWordList();
        SettingManager aSettingManager = new SettingManager();
        ArrayList<SetDetails> aAllSelectedSets = getAllSets(true);

        for (SetDetails aSet : aAllSelectedSets) {
            if (aSet.getNameOfSet().equalsIgnoreCase(ContextManager.getCurrentActivityContext()
                    .getResources().getString(R.string.all_words))) {
                updateWordListByAllWords(true, aSettingManager.getFilterStatus());
            } else if (Pattern.matches("Alphabet [A-Z]]", aSet.getNameOfSet())) {
                updateWordListByAlphabet(aSet.getNameOfSet().split(" ")[1], true, aSettingManager
                        .getFilterStatus());
            } else {
                updateWordListBySets(aSet.getNumberOfSet().toString(), true, aSettingManager
                        .getFilterStatus());
            }
        }
        Collections.sort(mWordList);
    }

    public void updateWordListByAllWords(boolean aApplyFavouriteFilter, String
            aApplyProgressFilter) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                DatabaseContract.WordMeaning.TABLE_NAME, null, null, null,
                null, null, null);
        applyFiltersOnWordList(c, aApplyFavouriteFilter, aApplyProgressFilter);
    }


    public void updateWordListByAlphabet(String iAlphabet, boolean aApplyFavouriteFilter, String
            aApplyProgressFilter) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = DatabaseContract.WordMeaning.WORD + " like '?%";
        String[] selectionArgs = {iAlphabet};
        Cursor c = db.query(
                DatabaseContract.WordMeaning.TABLE_NAME, null, selection, selectionArgs,
                null, null, null);
        applyFiltersOnWordList(c, aApplyFavouriteFilter, aApplyProgressFilter);
    }

    public void updateWordListBySets(String iSetNumber, boolean aApplyFavouriteFilter, String
            aApplyProgressFilter) {
        SQLiteDatabase db = getReadableDatabase();
        String aQuery = "SELECT * FROM " + DatabaseContract.WordMeaning.TABLE_NAME + " INNER JOIN" +
                " " +
                DatabaseContract.WordSet.TABLE_NAME + " WHERE " + DatabaseContract.WordSet
                .SET_NUMBER + "=? AND " + DatabaseContract.WordMeaning.TABLE_NAME + "." +
                DatabaseContract.WordMeaning.WORD + "=" + DatabaseContract.WordSet.TABLE_NAME
                + "." + DatabaseContract.WordSet.WORD + " ORDER BY " + DatabaseContract.WordMeaning
                .WORD;
        String[] aSelectionArgs = {iSetNumber};
        Cursor c = db.rawQuery(aQuery, aSelectionArgs);
        applyFiltersOnWordList(c, aApplyFavouriteFilter, aApplyProgressFilter);
    }

    private void applyFiltersOnWordList(Cursor c, boolean aApplyFavouriteFilter, String
            aApplyProgressFilter) {

        c.moveToFirst();
        while (!c.isAfterLast()) {
            WordWithDetails aWordWithDetails = new WordWithDetails();

            // Apply Favourite Filter
            if ((c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordMeaning.FAVOURITE))) == 1) {
                aWordWithDetails.setFavourite(true);
            } else {
                if (aApplyFavouriteFilter) {
                    c.moveToFirst();
                    continue;
                } else {
                    aWordWithDetails.setFavourite(false);
                }
            }

            // Apply Progress Filter
            int aProgress = c.getInt(c.getColumnIndexOrThrow(DatabaseContract
                    .WordMeaning.PROGRESS));
            if (aApplyProgressFilter.equalsIgnoreCase(ContextManager.getCurrentActivityContext()
                    .getResources().getString(R.string.filter_learning)) && aProgress ==
                    ContextManager
                            .getCurrentActivityContext().getResources().getInteger(R.integer
                            .MAX_PROGRESS)) {
                c.moveToNext();
                continue;
            }
            if (aApplyProgressFilter.equalsIgnoreCase(ContextManager.getCurrentActivityContext()
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
            aWordWithDetails.setProgress(c.getInt(c.getColumnIndexOrThrow(DatabaseContract
                    .WordMeaning.PROGRESS)));
            aWordWithDetails.setProgress(aProgress);
            if ((c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordMeaning.ORIGINAL))) == 0) {
                aWordWithDetails.setOriginal(true);
            } else {
                aWordWithDetails.setOriginal(false);
            }

            // To prevent duplicate words in list.
            if (mWordSet.add(aWordWithDetails))
                mWordList.add(aWordWithDetails);
        }
        c.close();

    }

    public ArrayList<SetDetails> getAllSets(boolean iGetOnlySelected) {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<SetDetails> aList = new ArrayList<>();

        Cursor c = db.query(
                DatabaseContract.SetNameNumber.TABLE_NAME, null, null, null,
                null, null, DatabaseContract.SetNameNumber.SET_NAME);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            SetDetails aSetDetails = new SetDetails();

            if (c.getInt(c.getColumnIndexOrThrow(DatabaseContract.SetNameNumber.SELECTED)) == 1) {
                aSetDetails.setSelected(true);
            } else {
                if (iGetOnlySelected) {
                    c.moveToNext();
                    continue;
                } else {
                    aSetDetails.setSelected(false);
                }
            }
            aSetDetails.setNameOfSet(c.getString(c.getColumnIndexOrThrow(DatabaseContract
                    .SetNameNumber.SET_NAME)));
            aSetDetails.setNumberOfSet(c.getInt(c.getColumnIndexOrThrow(DatabaseContract
                    .SetNameNumber.SET_NUMBER)));
            aList.add(aSetDetails);
        }
        c.close();
        return aList;
    }

    public String getSetNameFromSetNumber(String setNumber) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {DatabaseContract.SetNameNumber.SET_NAME};
        String selection = DatabaseContract.SetNameNumber.SET_NUMBER + "=?";
        String[] selectionArgs = {setNumber};

        Cursor c = db.query(
                DatabaseContract.SetNameNumber.TABLE_NAME, projection, selection, selectionArgs,
                null, null, null);
        String aReturnValue = null;
        if (c.getCount() != 0) {
            c.moveToFirst();
            aReturnValue = c.getString(c.getColumnIndexOrThrow(DatabaseContract.SetNameNumber
                    .SET_NAME));
        }
        c.close();
        return aReturnValue;
    }

    //
    //    public String getSetNameFromSetNumber(Integer setNumber) {
    //        return getSetNameFromSetNumber(setNumber.toString());
    //    }

    public Integer getSetNumberFromSetName(String setName) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {DatabaseContract.SetNameNumber.SET_NUMBER};
        String selection = DatabaseContract.SetNameNumber.SET_NAME + "=?";
        String[] selectionArgs = {setName};

        Cursor c = db.query(
                DatabaseContract.SetNameNumber.TABLE_NAME, projection, selection, selectionArgs,
                null, null, null);
        Integer aReturnValue = null;
        if (c.getCount() != 0) {
            c.moveToFirst();
            aReturnValue = c.getInt(c.getColumnIndexOrThrow(DatabaseContract.SetNameNumber
                    .SET_NUMBER));
        }
        c.close();
        return aReturnValue;
    }

    public void updateFavourite(final String iWord, final String iMeaning, final boolean
            iFavourite) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase aReadableDatabase = getWritableDatabase();

                // New value for one column
                ContentValues values = new ContentValues();
                if (iFavourite) {
                    values.put(DatabaseContract.WordMeaning.FAVOURITE, 1);
                } else {
                    values.put(DatabaseContract.WordMeaning.FAVOURITE, 0);
                }

                String selection = DatabaseContract.WordMeaning.WORD + "=? AND " +
                        DatabaseContract.WordMeaning.MEANING + "=?";
                String[] selectionArgs = {iWord, iMeaning};

                aReadableDatabase.update(
                        DatabaseContract.WordMeaning.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                aReadableDatabase.close();
            }
        });
    }

}
