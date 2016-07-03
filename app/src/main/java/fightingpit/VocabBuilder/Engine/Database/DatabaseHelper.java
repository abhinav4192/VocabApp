package fightingpit.vocabBuilder.Engine.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fightingpit.vocabBuilder.Engine.ContextManager;
import fightingpit.vocabBuilder.R;

/**
 * Created by abhinavgarg on 02/07/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    //Context DB_CONTEXT;

    public DatabaseHelper() {
        super(ContextManager.getCurrentActivityContext(), DatabaseContract.DATABASE_NAME, null, DatabaseContract
                .DATABASE_VERSION);
    }


    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Creating Table
        for(int i=0; i < DatabaseContract.SQL_CREATE_TABLE_ARRAY.length; i++) {
            db.execSQL(DatabaseContract.SQL_CREATE_TABLE_ARRAY[i]);
        }

//        // Inserting Data into table
//        InputStream aInitQuery = DB_CONTEXT.getResources().openRawResource(R.raw.barrons_db_insert);
//        BufferedReader aInsertReader = new BufferedReader(new InputStreamReader(aInitQuery));
//
//        // Iterate through lines (assuming each insert has its own line and there is no other stuff)
//        try {
//            while (aInsertReader.ready()) {
//                String aInsertStatement = aInsertReader.readLine();
//                db.execSQL(aInsertStatement);
//            }
//            aInsertReader.close();
//        }
//        catch(IOException e)
//        {
//            Log.d("ABG", "DB Insert Failed");
//        }


    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i=0; i < DatabaseContract.SQL_DROP_TABLE_ARRAY.length; i++) {
            db.execSQL(DatabaseContract.SQL_DROP_TABLE_ARRAY[i]);
        }
        onCreate(db);
    }

}