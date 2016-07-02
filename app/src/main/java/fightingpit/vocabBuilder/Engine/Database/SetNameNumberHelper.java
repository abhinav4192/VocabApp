package fightingpit.vocabBuilder.Engine.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by abhinavgarg on 03/07/16.
 */
public class SetNameNumberHelper extends DatabaseHelper {

    public String getSetNameFromSetNumber(String setNumber){
        SQLiteDatabase aReadableDatabase = getReadableDatabase();
        String aQuery = "SELECT " + DatabaseContract.SetNameNumber.SET_NAME + " FROM " +
                DatabaseContract.SetNameNumber.TABLE_NAME + " WHERE " + DatabaseContract
                .SetNameNumber.SET_NUMBER + "=?";
        String[] aSelectionArgs = {setNumber};
        Cursor c = aReadableDatabase.rawQuery(aQuery, aSelectionArgs);
        c.moveToFirst();
        String aSetName = c.getString(c.getColumnIndexOrThrow(DatabaseContract.SetNameNumber
                .SET_NAME));
        c.close();
        return aSetName;
    }

    public String getSetNameFromSetNumber(Integer setNumber){
        SQLiteDatabase aReadableDatabase = getReadableDatabase();
        String aQuery = "SELECT " + DatabaseContract.SetNameNumber.SET_NAME + " FROM " +
                DatabaseContract.SetNameNumber.TABLE_NAME + " WHERE " + DatabaseContract
                .SetNameNumber.SET_NUMBER + "=?";
        String[] aSelectionArgs = {setNumber.toString()};
        Cursor c = aReadableDatabase.rawQuery(aQuery, aSelectionArgs);
        c.moveToFirst();
        String aSetName = c.getString(c.getColumnIndexOrThrow(DatabaseContract.SetNameNumber
                .SET_NAME));
        c.close();
        return aSetName;
    }
}
