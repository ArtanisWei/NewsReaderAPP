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
import java.util.Vector;

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
        String CREATE_TABLE_HISTORY="CREATE TABLE if not exists "+ HISTORY + "("
                +"__id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +_TITLE+" TEXT, "
                +_INTRO+" TEXT, "
                +_ID+" TEXT, "
                +_TYPE+" TEXT, "
                +_PICTURES_PATH+" TEXT, "
                +_CONTENT+" TEXT)";
        String CREATE_TABLE_FAVORITE="CREATE TABLE if not exists "+ FAVORITE + "("
                +"_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +_TITLE+" TEXT, "
                +_INTRO+" TEXT, "
                +_ID+" TEXT, "
                +_TYPE+" TEXT, "
                +_PICTURES_PATH+" TEXT, "
                +_CONTENT+" TEXT)";
        db.execSQL(CREATE_TABLE_FAVORITE);
        db.execSQL(CREATE_TABLE_HISTORY);
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
        String sql = "DELETE FROM " + table_name + " WHERE "+ DatabaseHelper._ID + " = " + "'" + target_id + "'";
        db.execSQL(sql);
        db.close();
        return true;
    }

    public boolean clear(String _table_name){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE From " + _table_name + " where 1=1");
        return true;
    }
    public HashMap<String, NewsDatabaseObject> getAllNews(String table_name){
        SQLiteDatabase db = helper.getReadableDatabase();
        String selectQuery = "SELECT " + DatabaseHelper._ID + "," + DatabaseHelper._TITLE + "," + DatabaseHelper._INTRO + "," +
                DatabaseHelper._PICTURES_PATH + "," + DatabaseHelper._CONTENT + "," + DatabaseHelper._TYPE + " FROM "+
                table_name;
        HashMap<String, NewsDatabaseObject> map = new HashMap<String, NewsDatabaseObject>();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do{
                map.put(cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID)), new NewsDatabaseObject(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper._TITLE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper._INTRO)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper._TYPE)),
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
        //System.out.println("now table is " + table_name);
        SQLiteDatabase db = helper.getReadableDatabase();
        String selectQuery = "SELECT " + DatabaseHelper._ID + " FROM " + table_name;
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
    public Vector<NewsDigest> getNewsTitle(String table_name){
        System.out.println("now table is " + table_name);
        SQLiteDatabase db = helper.getReadableDatabase();
        String selectQuery = "SELECT " + DatabaseHelper._TITLE + "," + DatabaseHelper._INTRO + "," + DatabaseHelper._ID + " FROM " + table_name;
        Vector<NewsDigest> digests = new Vector<NewsDigest>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do{
                NewsDigest digest = new NewsDigest(cursor.getString(cursor.getColumnIndex(DatabaseHelper._TITLE)), cursor.getString(cursor.getColumnIndex(DatabaseHelper._INTRO)));
                digest.id = cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID));
                digests.add(digest);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return digests;
    }
    public NewsDatabaseObject getNewsByid(String target_id, String table_name){
        SQLiteDatabase db = helper.getReadableDatabase();
        String selectQuery="SELECT " + DatabaseHelper._ID + "," + DatabaseHelper._INTRO + "," + DatabaseHelper._TITLE + "," + DatabaseHelper._CONTENT + ","
                + DatabaseHelper._PICTURES_PATH + "," + DatabaseHelper._TYPE + " FROM "+ table_name + " WHERE "
                +DatabaseHelper._ID + "=?";
        NewsDatabaseObject object = new NewsDatabaseObject();
        Cursor cursor = db.rawQuery(selectQuery,new String[]{String.valueOf(target_id)});
        if (cursor.moveToFirst()){
            do{
                object.title = cursor.getString(cursor.getColumnIndex(DatabaseHelper._TITLE));
                object.intro = cursor.getString(cursor.getColumnIndex(DatabaseHelper._INTRO));
                object.picture_path = cursor.getString(cursor.getColumnIndex(DatabaseHelper._PICTURES_PATH));
                object.content = cursor.getString(cursor.getColumnIndex(DatabaseHelper._CONTENT));
                object.id = cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID));
                object.type = cursor.getString(cursor.getColumnIndex(DatabaseHelper._TYPE));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return object;
    }
    public Vector<String> getType(String table_name){
        SQLiteDatabase db = helper.getReadableDatabase();
        String selectQuery = "SELECT " + DatabaseHelper._TYPE + " FROM " + table_name + " WHERE 1=1";
        Vector<String> all_types = new Vector<String>();

        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do{
                all_types.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper._TYPE)));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return all_types;
    }
}