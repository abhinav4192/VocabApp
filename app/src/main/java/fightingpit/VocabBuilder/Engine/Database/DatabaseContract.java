package fightingpit.VocabBuilder.Engine.Database;
import android.provider.BaseColumns;

/**
 * Created by abhinavgarg on 29/06/16.
 */

public final class DatabaseContract {

    public static final  int    DATABASE_VERSION    = 1;
    public static final  String DATABASE_NAME       = "database.db";
    private static final String PRIMARY_KEY         = "PRIMARY KEY";
    private static final String UNIQUE              = "UNIQUE";
    private static final String COMMA_SEP           = ", ";
    
    public static final String[] SQL_CREATE_TABLE_ARRAY = {
            WordMeaning.CREATE_TABLE,
            WordSet.CREATE_TABLE,
            SetNameNumber.CREATE_TABLE,
    };

    public static final String[] SQL_DROP_TABLE_ARRAY = {
            WordMeaning.DROP_TABLE,
            WordSet.DROP_TABLE,
            SetNameNumber.DROP_TABLE,
    };


    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty private constructor.
    private DatabaseContract() {}

    public static abstract class WordMeaning implements BaseColumns {

        public static final String TABLE_NAME = "WORD_LIST";
        public static final String WORD     = "WORD";
        public static final String MEANING   = "MEANING";
        public static final String SENTENCE   = "SENTENCE";
        public static final String PROGRESS = "PROGRESS";
        public static final String FAVOURITE = "FAVOURITE";
        public static final String ORIGINAL = "ORIGINAL";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                WORD + " TEXT PRIMARY KEY NOT NULL" + COMMA_SEP +
                MEANING + " TEXT NOT NULL" + COMMA_SEP +
                SENTENCE + " TEXT NOT NULL" + COMMA_SEP +
                PROGRESS + " INTEGER NOT NULL" + COMMA_SEP +
                FAVOURITE + " INTEGER NOT NULL" + COMMA_SEP +
                ORIGINAL + " INTEGER NOT NULL" +
                " )";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class WordSet implements BaseColumns {

        public static final String TABLE_NAME     = "WORD_SET";
        public static final String WORD           = "WORD";
        public static final String SET_NUMBER     = "SET_NUMBER";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                WORD + " TEXT NOT NULL" + COMMA_SEP +
                SET_NUMBER + " INTEGER NOT NULL" + COMMA_SEP +
                " PRIMARY KEY (" + WORD + COMMA_SEP + SET_NUMBER + ")" +
                " )";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


    public static abstract class SetNameNumber implements BaseColumns {

        public static final String TABLE_NAME     = "SET_NAME_NUMBER";
        public static final String SET_NUMBER     = "SET_NUMBER";
        public static final String SET_NAME       = "SET_NAME";
        public static final String SELECTED     = "SELECTED";

//        public static final String CREATE_TABLE = "CREATE TABLE " +
//                TABLE_NAME + " (" +
//                SET_NUMBER + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
//                SET_NAME + " TEXT NOT NULL" + COMMA_SEP +
//                UNIQUE + " (" + SET_NAME + ")" +
//                " )";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                SET_NUMBER + " INTEGER PRIMARY KEY" + COMMA_SEP +
                SET_NAME + " TEXT NOT NULL" + COMMA_SEP +
                SELECTED + " INTEGER NOT NULL" + COMMA_SEP +
                UNIQUE + " (" + SET_NAME + ")" +
                " )";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
