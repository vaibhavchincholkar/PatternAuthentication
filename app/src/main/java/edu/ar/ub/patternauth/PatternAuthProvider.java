package edu.ar.ub.patternauth;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class PatternAuthProvider extends ContentProvider {
    private PatternAuthDbHelper PADbHelper;
    @Override
    public boolean onCreate() {
        PADbHelper= new PatternAuthDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = PADbHelper.getReadableDatabase();
        Cursor cursor=null;

       if(String.valueOf(uri).equals("content://edu.ar.ub.patternauth.provider/UsersData")) {
           if(selection.equals("username"))
           {
               String select="select DISTINCT "+DBContract.USERNAME+" from "+DBContract.TABLE_NAME+"";
               cursor =db.rawQuery(select, null);
               return cursor;
           }
           else
           {
               cursor=db.query(DBContract.TABLE_NAME,null,DBContract.USERNAME + " = ?", new String[]{selection},null,null,null);
               //cursor =db.rawQuery(getCord, null);
               return cursor;
           }
       }
       else {
           Log.d("Query","inside scoreUri");
           if(selection.equals(null))
           {
               String select="select MAX("+DBContract.SCORE+") from "+DBContract.SCORE_TABLE+"";
               cursor =db.rawQuery(select, null);
               return cursor;
           }
           else
           {
               cursor=db.query(DBContract.SCORE_TABLE,null,DBContract.UNAME + " = ?", new String[]{selection},null,null,null);
               return cursor;
           }
       }
    }


    @Override
    public String getType( Uri uri) {
        return null;
    }


    @Override
    public Uri insert( Uri uri, ContentValues values) {
        SQLiteDatabase db = PADbHelper.getWritableDatabase();
        long insrt=-1;
        db.beginTransaction();
        if(String.valueOf(uri).equals("content://edu.ar.ub.patternauth.provider/UsersData")) {

            Log.d(" insert", "inside main");
            try {
                insrt = db.insert(DBContract.TABLE_NAME, null, values);
            } catch (Exception e) {
                Log.d("Content Provider insert", "failed to insert because" + e);
            }
            if (insrt == -1) {
                db.endTransaction();
                Log.d("Content Provider insert", "failed to insert");
            } else {
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        }else
        {
                Log.d(" insert","inside score");
                try
                {
                    insrt=db.insert(DBContract.SCORE_TABLE,null,values);
                }
                catch (Exception e)
                {
                    Log.d("Content Provider insert","failed to insert because"+e);
                }
                if(insrt==-1)
                {
                    db.endTransaction();
                    Log.d("Content Provider insert","failed to insert");
                }
                else
                {
                    db.setTransactionSuccessful();
                    db.endTransaction();
                }
        }
        return uri;
    }

    @Override
    public int delete( Uri uri,  String selection, String[] selectionArgs) {
        SQLiteDatabase db = PADbHelper.getWritableDatabase();
        db.delete(DBContract.TABLE_NAME,null,null);
        db.close();
        return 0;
    }

    @Override
    public int update( Uri uri,  ContentValues values,  String selection, String[] selectionArgs) {
        return 0;
    }
}

