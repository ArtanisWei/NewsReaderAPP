package com.example.victor.todaynews;

/**
 * Created by victor on 11/09/2017.
 */
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.Cursor;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

class DatabaseHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "NewsDatabase";
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    public static final String HISTORY = "HISTORY";
    public static final String FAVORITE = "LOVE";
    public static final String _TITLE = "news_title";
    public static final String _INTRO = "news_intro";
    public static final String _ID = "news_id";
    public static final String _TYPE = "news_type";
    public static final String _PICTURES_PATH = "news_picture";
    public static final String _CONTENT = "news_content";
    public static final String CUTTER = "$$$";
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE_HISTORY="CREATE TABLE "+ HISTORY + "("
                +"_id INTEGER PRIMARY KEY AUTOINCREMENT , "
                +_TITLE+" TEXT, "
                +_INTRO+" TEXT, "
                +_ID+" TEXT, "
                +_TYPE+" INTEGER, "
                +_PICTURES_PATH+" TEXT, "
                +_CONTENT+" TEXT)";
        String CRATE_TABLE_FAVORITE="CREATE TABLE "+ FAVORITE + "("
                +"_id INTEGER PRIMARY KEY AUTOICREMENT , "
                +_TITLE+" TEXT, "
                +_INTRO+" TEXT, "
                +_ID+" TEXT, "
                +_TYPE+" INTEGER, "
                +_PICTURES_PATH+" TEXT, "
                +_CONTENT+" TEXT)";
        db.execSQL(CREATE_TABLE_HISTORY);
        db.execSQL(CRATE_TABLE_FAVORITE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS HISTORY");
        db.execSQL("DROP TABLE IF EXISTS LOVE");
    }
}

public class NewsDatabase {
    private DatabaseHelper helper;
    public NewsDatabase(Context context){
        helper = new DatabaseHelper(context);
    }
    public void insert(NewsDatabaseObject object , String table_name){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper._TITLE, object.title);
        values.put(DatabaseHelper._TYPE,object.type);
        values.put(DatabaseHelper._ID,object.id);
        values.put(DatabaseHelper._CONTENT,object.content);
        values.put(DatabaseHelper._INTRO,object.intro);
        values.put(DatabaseHelper._PICTURES_PATH,object.picture_path);
        db.insert(table_name, null, values);
        db.close();
    }
    public boolean delete(String target_id, String table_name){
        SQLiteDatabase db = helper.getWritableDatabase();
        if (table_name == DatabaseHelper.HISTORY) {
            db.execSQL("TRUNCATE TABLE " + DatabaseHelper.HISTORY);
            return true;
        }
        int cons = db.delete(table_name, DatabaseHelper._ID + "=?", new String[]{String.valueOf(target_id)});
        db.close();
        if (cons == 0) return false;
        return true;
    }
    public HashMap<String, NewsDatabaseObject> getAllNews(String table_name){
        SQLiteDatabase db = helper.getReadableDatabase();
        String selectQuery = "SELECT " + DatabaseHelper._ID + "," + DatabaseHelper._TITLE + "," + DatabaseHelper._INTRO + "," +
                DatabaseHelper._PICTURES_PATH + "," + DatabaseHelper._CONTENT + "," + DatabaseHelper._TYPE + " FROM"+
                table_name;
        HashMap<String, NewsDatabaseObject> map = new HashMap<String, NewsDatabaseObject>();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do{

                map.put(cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID)), new NewsDatabaseObject(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper._TITLE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper._INTRO)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper._TYPE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper._PICTURES_PATH)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper._CONTENT))
                ));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return map;
    }
    public HashSet<String> getNewsList(String table_name){
        SQLiteDatabase db = helper.getReadableDatabase();
        String selectQuery = "SELECT " + DatabaseHelper._ID + " FROM" + table_name;
        HashSet<String> news_id = new HashSet<String>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do{
                news_id.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID)));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return news_id;
    }
}