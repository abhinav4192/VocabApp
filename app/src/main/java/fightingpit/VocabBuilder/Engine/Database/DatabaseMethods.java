package fightingpit.VocabBuilder.Engine.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
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

    private HashSet<WordWithDetails> mWordSet = new HashSet<>();
    private ArrayList<WordWithDetails> mWordList = new ArrayList<>();
    private ArrayList<Integer> mLearningList = new ArrayList<>();
    private Random mRandomGenerator = new Random();
    private Integer mRandomIndex = -1;

    public Integer getRandomWordListIndex(){
        if(mRandomIndex >= 0){
            Log.d("++ABG++", "mRandomIndex > 0");
            Integer test = mLearningList.get(mRandomIndex);
            Log.d("++ABG++", "mRandomIndex > 0 : " + test);
            return mLearningList.get(mRandomIndex);
        }
        else{
            Log.d("++ABG++", "mRandomIndex < 0");
            return -1;
        }

    }

    public ArrayList<WordWithDetails> getWordList() {
        return mWordList;
    }

    private void clearWordList() {
        mWordList.clear();
        mWordSet.clear();
        mLearningList.clear();
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
        for(SetDetails s: aAllSelectedSets){
            Log.d("ABG", "Selected:" + s.getNameOfSet());
        }

        for (SetDetails aSet : aAllSelectedSets) {
            if (aSet.getNameOfSet().equalsIgnoreCase(ContextManager.getCurrentActivityContext()
                    .getResources().getString(R.string.all_words))) {
                updateWordListByAllWords(aSettingManager.showOnlyFavorites(), aSettingManager.getFilterStatus());
            } else if (Pattern.matches("Alphabet [A-Z]", aSet.getNameOfSet())) {
                updateWordListByAlphabet(aSet.getNameOfSet().split(" ")[1], aSettingManager.showOnlyFavorites(), aSettingManager
                        .getFilterStatus());
            } else {
                updateWordListBySets(aSet.getNumberOfSet().toString(), aSettingManager.showOnlyFavorites(), aSettingManager
                        .getFilterStatus());
            }
        }
        Collections.sort(mWordList);
        createLearningList();
    }

    private void updateWordListByAllWords(boolean aApplyFavouriteFilter, String
            aApplyProgressFilter) {

        Log.d("ABG","updateWordListByAllWords");

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                DatabaseContract.WordMeaning.TABLE_NAME, null, null, null,
                null, null, null);
        applyFiltersOnWordList(c, aApplyFavouriteFilter, aApplyProgressFilter);
    }


    private void updateWordListByAlphabet(String iAlphabet, boolean aApplyFavouriteFilter, String
            aApplyProgressFilter) {

        Log.d("ABG","updateWordListByAlphabet:" + iAlphabet);
        SQLiteDatabase db = getReadableDatabase();
        String selection = DatabaseContract.WordMeaning.WORD + " LIKE ?";
        String[] selectionArgs = {iAlphabet+"%"};
        Cursor c = db.query(
                DatabaseContract.WordMeaning.TABLE_NAME, null, selection, selectionArgs,
                null, null, null);
        applyFiltersOnWordList(c, aApplyFavouriteFilter, aApplyProgressFilter);
    }

    private void updateWordListBySets(String iSetNumber, boolean aApplyFavouriteFilter, String
            aApplyProgressFilter) {
        Log.d("ABG","updateWordListBySets:" + iSetNumber);
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

        Log.d("ABG","applyFiltersOnWordList");
        c.moveToFirst();
        while (!c.isAfterLast()) {
            //Log.d("ABG","in while");
            WordWithDetails aWordWithDetails = new WordWithDetails();

            // Apply Favourite Filter
            if ((c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordMeaning.FAVOURITE))) == 1) {
                aWordWithDetails.setFavourite(true);
            } else {
                if (aApplyFavouriteFilter) {
                    c.moveToNext();
                    continue;
                } else {
                    aWordWithDetails.setFavourite(false);
                }
            }

            // Apply Progress Filter
            int aProgress = c.getInt(c.getColumnIndexOrThrow(DatabaseContract
                    .WordMeaning.PROGRESS));
            if (aApplyProgressFilter.equalsIgnoreCase(ContextManager.getCurrentActivityContext()
                    .getResources().getString(R.string.filter_learning)) && aProgress >=
                    ContextManager
                            .getCurrentActivityContext().getResources().getInteger(R.integer
                            .MAX_PROGRESS)) {
                c.moveToNext();
                continue;
            }
            if (aApplyProgressFilter.equalsIgnoreCase(ContextManager.getCurrentActivityContext()
                    .getResources().getString(R.string.filter_mastered)) && aProgress <
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
            c.moveToNext();
        }
        c.close();

    }

    public ArrayList<SetDetails> getAllSets(boolean iGetOnlySelected) {
        Log.d("ABG","getAllSets");
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
            c.moveToNext();
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

    public void updateSetSelected(Integer iSetNumber, boolean iIsSelected){

        SQLiteDatabase aWritableDatabase = getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        if (iIsSelected) {
            values.put(DatabaseContract.SetNameNumber.SELECTED, 1);
        } else {
            values.put(DatabaseContract.SetNameNumber.SELECTED, 0);
        }

        String selection = DatabaseContract.SetNameNumber.SET_NUMBER + "=?";
        String[] selectionArgs = {iSetNumber.toString()};

        aWritableDatabase.update(
                DatabaseContract.SetNameNumber.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        aWritableDatabase.close();

    }

    public void updateFavourite(final String iWord, final String iMeaning, final boolean
            iFavourite) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase aWritableDatabase = getWritableDatabase();

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

                aWritableDatabase.update(
                        DatabaseContract.WordMeaning.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                aWritableDatabase.close();
            }
        });
    }


    private void createLearningList(){
        for(Integer i=0;i<mWordList.size();++i){
            if(mWordList.get(i).getProgress() < ContextManager.getCurrentActivityContext()
                    .getResources().getInteger(R.integer
                    .MAX_PROGRESS)){
                mLearningList.add(i);
            }
        }
        Log.d("++ABG++", "mLearningList size:" + mLearningList.size());
        updateRandomIndex();
    }


    private void updateRandomIndex(){
        Integer learningListSize = mLearningList.size();
        if(mLearningList.size()<=0){
            mRandomIndex = -1;
        } else{
            Integer newRandomIndex = mRandomGenerator.nextInt(learningListSize);
            if(newRandomIndex == mRandomIndex && learningListSize > 1){
                updateRandomIndex();
            } else {
                mRandomIndex = newRandomIndex;
            }
        }
        Log.d("++ABG++", "mRandomIndex value:" + mRandomIndex);

    }
    public void updateWordProgress(final Integer iWordListIndex, final boolean iIsCorrectAnswer) {
        Integer progress = mWordList.get(iWordListIndex).getProgress();
        Log.d("++ABG++","iWordListIndex:" + iWordListIndex + " progress:"+progress);
        if (iIsCorrectAnswer) {
            if (progress < ContextManager.getCurrentActivityContext()
                    .getResources().getInteger(R.integer.MAX_PROGRESS)) {
                mWordList.get(iWordListIndex).setProgress(++progress);
            }
        } else {
            progress = 0;
            mWordList.get(iWordListIndex).setProgress(0);
        }
        if(progress >= ContextManager.getCurrentActivityContext().getResources().getInteger(R
                .integer.MAX_PROGRESS)){
            mLearningList.remove(mRandomIndex.intValue()); // Integer to int to remove by index.
        }
        updateRandomIndex();
        updateWordProgress(mWordList.get(iWordListIndex).getWord(), mWordList.get(iWordListIndex)
                .getMeaning(), progress);

    }

    private void updateWordProgress(final String iWord, final String iMeaning, final Integer
            iProgress){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase aWritableDatabase = getWritableDatabase();
                // New value for one column
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.WordMeaning.PROGRESS, iProgress);
                String selection = DatabaseContract.WordMeaning.WORD + "=? AND " +
                        DatabaseContract.WordMeaning.MEANING + "=?";
                String[] selectionArgs = {iWord, iMeaning};

                aWritableDatabase.update(
                        DatabaseContract.WordMeaning.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                aWritableDatabase.close();
            }
        });
    }

}
