package com.example.victor.todaynews;

import android.app.*;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.*;
import android.content.*;
import android.support.annotation.RequiresPermission;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Created by victor on 08/09/2017.
 */
class ReadThread extends Thread {
    private String web_content;
    private String target_url;
    private boolean is_digest;// true shows degist and false shows content
    private NewsContent _content;
    private Vector<NewsDigest> _digests;

    public String get_web_content() {
        return web_content;
    }

    public NewsContent get_content(){
        return _content;
    }
    public Vector<NewsDigest> get_digests(){
        return _digests;
    }

    ReadThread(String _url,boolean _digest) {
        _content = new NewsContent();
        _digests = new Vector<NewsDigest> ();
        target_url = _url;
        is_digest = _digest;
    }

    public void run() {
        System.out.println("Thread begin!");
        try {
            web_content = connect_internet(target_url);
            if (is_digest) {
                _digests = NewsParser.parse_digest(web_content);
            }
            else{
                _content = NewsParser.parse_content(web_content);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String connect_internet(String _url) throws Exception{
        URL realUrl = new URL(_url);

        URLConnection connection = realUrl.openConnection();

        System.out.println("ready to connect to: " + _url);
        connection.connect();
        System.out.println("successful connect to " + _url);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String result = "";
        String line = "";
        while((line = in.readLine()) != null){
            result += line;
        }
        in.close();


        return result;
    }
}

public class NewsService extends Service {

    private int current_page = 0;
    private int last_type = 0;

    final private int max_size = 20;
    private NewsBind binder = new NewsBind();
    final private String urlString = "http://166.111.68.66:2042/news/action/query/latest";

    class NewsBind extends Binder{
        public int get_page(){return current_page;}

        public NewsRespond get_news_digest(NewsRequest request){
            System.out.println("request news");
            int num = (int)request._get().get(0);
            int type = (int)request._get().get(1);
            if (type == last_type) current_page++;
            else{
                current_page = 1;
                last_type = type;
            }
            if (num <= current_page) num = max_size;
            String param = "pageNo=" + current_page + "&pageSize=" + num;
            if (type != 0) param += "&category=" + type;

            ReadThread webThread  = new ReadThread(urlString + "?" + param, true);
            webThread.start();
            try{webThread.join();}catch(Exception e){};
            NewsRespond respond  = new NewsRespond(webThread.get_digests().size(), webThread.get_digests());

            if (respond.number < num) {System.out.println("$$$$$$$$error$$$$$$$$$ less number!");}
            return respond;
        }
    }

    @Override
    public IBinder onBind(Intent it){
        System.out.println("Service Bind");
        return binder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        System.out.println("News Service create");
        super.onCreate();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        System.out.println("Service is end");
    }
    @Override
    public boolean onUnbind(Intent intent){
        super.onUnbind(intent);
        System.out.println("Service is Unbinded");
        return true;
    }
}
