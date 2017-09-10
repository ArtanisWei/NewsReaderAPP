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
    private NewsRespond _respond;
    private String web_content;
    private String target_url;

    public NewsRespond get_respond() {
        return _respond;
    }

    public String get_web_content() {
        return web_content;
    }

    ReadThread(String _url) {
        target_url = _url;
    }

    public void run() {
        System.out.println("Thread begin!");
        try {
            web_content = connect_internet(target_url);
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
    private NewsRequest request;
    private NewsRespond respond;

    private int current_page = 0;
    private int last_type = 0;

    final private int max_size = 20;
    private NewsBind binder = new NewsBind();
    final private String urlString = "http://166.111.68.66:2042/news/action/query/latest";

    class NewsBind extends Binder{
        public int get_page(){return current_page;}
        public NewsRespond get_respond(){
            return respond;
        }
        public Vector<NewsDigest> get_news(){return (Vector<NewsDigest>) respond._get().get(1);}
        public int get_number(){return (int)respond._get().get(0);}
    }


    public IBinder onBind(Intent it){
        System.out.println("News Service Bind");
        request = (NewsRequest)it.getSerializableExtra("request");
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

        ReadThread webThread  = new ReadThread(urlString + "?" + param);
        webThread.start();
        try{webThread.join();}catch(Exception e){};

        final String web_content = webThread.get_web_content();
        //System.out.println(web_content);

        respond._set(new ArrayList() {
            {
                try{
                    Vector<NewsDigest> temp = NewsParser.parse_digest(web_content);
                    add(temp.size());
                    add(temp);
                }catch(Exception e){
                    System.out.println(e);
                    Vector<NewsDigest> temp = new Vector<NewsDigest>();
                    add(0);
                    add(temp);
                }
            }
        });

        if (respond.number < num) {System.out.println("$$$$$$$$error$$$$$$$$$ less number!");}
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        System.out.println("request news");
        request = (NewsRequest)intent.getSerializableExtra("request");
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

        ReadThread webThread  = new ReadThread(urlString + "?" + param);
        webThread.start();
        try{webThread.join();}catch(Exception e){};

        final String web_content = webThread.get_web_content();
        //System.out.println(web_content);

        respond._set(new ArrayList() {
            {
                try{
                    Vector<NewsDigest> temp = NewsParser.parse_digest(web_content);
                    add(temp.size());
                    add(temp);
                }catch(Exception e){
                    System.out.println(e);
                    Vector<NewsDigest> temp = new Vector<NewsDigest>();
                    add(0);
                    add(temp);
                }
            }
        });

        if (respond.number < num) {System.out.println("$$$$$$$$error$$$$$$$$$ less number!");}
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        request = new NewsRequest();
        respond = new NewsRespond();
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
