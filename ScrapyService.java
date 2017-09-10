package com.example.victor.todaynews;
import android.app.*;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.*;
import android.content.*;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Created by victor on 05/09/2017.
 */



public class ScrapyService extends Service{

    private String content;
    //private Vector<String> titles;
    private MyBinder binder = new MyBinder();
    class MyBinder extends Binder{
        public String get_content(){
            return content;
        }
        //public Vector<String> gettitles(){return titles;}
    }
    @Override
    public  IBinder onBind(Intent it){
        System.out.println("Service is Binded");

        content = it.getStringExtra("content");
        //System.out.println("In service content is : "  + content);

        return binder;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        content = "This is a news";
        //titles.clear();
        System.out.println("Service is created");

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        System.out.println("Service is restarted");
        Bundle temp = intent.getExtras();
        String _content = temp.getString("content");
        content = _content;
        System.out.println("In service content is: " + content);
        return START_STICKY;
    }
    @Override
    public void  onDestroy(){
        super.onDestroy();
        System.out.println("Service is destroyed");
    }

    @Override
    public boolean onUnbind(Intent it){
        super.onUnbind(it);
        System.out.println("Service is unbinded");
        return true;
    }

}
