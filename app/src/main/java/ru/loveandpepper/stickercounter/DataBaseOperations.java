package ru.loveandpepper.stickercounter;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseOperations extends SQLiteOpenHelper {
    public static final String DB_NAME = "Statisticsbase";
    public static final int DB_VERSION = 2;
    public static final String DEFAULT_TABLE = "tableone";
    public static final String CALLBACK_TABLE = "callbacktb";

    public DataBaseOperations(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + DEFAULT_TABLE + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "product TEXT, "
                + "price INTEGER, "
                + "quantity INTEGER, "
                + "date TEXT);");
        sqLiteDatabase.execSQL("CREATE TABLE " + CALLBACK_TABLE + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "phone TEXT, "
                + "name TEXT, "
                + "comment TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DEFAULT_TABLE);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CALLBACK_TABLE);
            onCreate(sqLiteDatabase);

    }

}
