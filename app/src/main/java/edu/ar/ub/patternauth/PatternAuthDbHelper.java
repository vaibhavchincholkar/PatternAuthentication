package edu.ar.ub.patternauth;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PatternAuthDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="PatternAuth.db";
    private static final int DATABASE_VERSION=4;
    public PatternAuthDbHelper( Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.TABLE_NAME + " (" +
                        DBContract._ID + " INTEGER PRIMARY KEY," +
                        DBContract.USERNAME + " TEXT," +
                        DBContract.XCORD + " FLOAT," +
                        DBContract.YCORD + " FLOAT)";
        final String SQL_CREATE_ENTRIES2 =
                "CREATE TABLE " + DBContract.SCORE_TABLE + " (" +
                        DBContract._ID + " INTEGER PRIMARY KEY," +
                        DBContract.UNAME + " TEXT," +
                        DBContract.SCORE + " INTEGER)";
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.SCORE_TABLE);
        onCreate(db);
    }
}
