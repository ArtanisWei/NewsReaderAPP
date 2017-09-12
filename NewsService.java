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
               //System.out.println("in run function: " + web_content);
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
        //in.read();
        while((line = in.readLine()) != null){
            //System.out.println("$$$$$$$$$$$line is$$$$$$$$$$$$$" + line);
            result += line;
        }
        in.close();
        //System.out.println("reasult is " + result);

        return result;
    }
}


public class NewsService extends Service {
    final private int max_size = 10;
    private NewsBind binder = new NewsBind();
    private NewsDatabase database;
    //private SQLiteDatabase database = new SQLiteDatabase(this);


    final private String LATEST_NEWS = "http://166.111.68.66:2042/news/action/query/latest";
    final private String SEARCH_NEWS = "http://166.111.68.66:2042/news/action/query/search";
    final private String DETAIL_NEWS = "http://166.111.68.66:2042/news/action/query/detail";


    class NewsBind extends Binder{
       // public int get_page(){return current_page;}

        public NewsRespond get_news_digest(NewsRequest request){
            System.out.println("request news");
            int page = (int)request._get().get(0);
            int type = (int)request._get().get(1);

            if (page >= max_size) {
                System.out.println("no more news, page back to the end");
                page = 1;
            }

            String param = "pageNo=" + page + "&pageSize=" + max_size;

            if (type != 0) param += "&category=" + type;

            ReadThread webThread  = new ReadThread(LATEST_NEWS+ "?" + param, true);
            webThread.start();
            try{webThread.join();}catch(Exception e){};
            Vector<NewsDigest> temp = webThread.get_digests();
            NewsRespond respond  = new NewsRespond(temp.size(), temp);

            if (respond.number < max_size) {System.out.println("$$$$$$$$error$$$$$$$$$ less number!");}
            return respond;
        }

        public NewsSearchRespond search_news(NewsSearchRequest request){
            System.out.println("serarch news");
            String keyword = (String)request.get_keyword();

            String param = "keyword=" + keyword;

            ReadThread webThread = new ReadThread(SEARCH_NEWS + "?" + param, true);
            webThread.start();
            try{webThread.join();}catch(Exception e){};

            Vector<NewsDigest> temp = webThread.get_digests();
            NewsSearchRespond respond = new NewsSearchRespond(temp);

            if (temp.size() == 0) {System.out.println("can't find!");}
            return respond;
        }

        public NewsContentRespond news_content(NewsContentRequest request){
            System.out.println("news detail");
            String news_id = request.get_id();
            String param = "newsId=" + news_id;

            ReadThread webThread = new ReadThread(DETAIL_NEWS + "?" + param, false);
            webThread.start();
            try{webThread.join();}catch(Exception e){};

            NewsDigest digest = new NewsDigest(request.newstitle, request.newsintro);
            NewsContent content = webThread.get_content();
            boolean success = false;
            if (!content.content.equals("")) success = true;


            if (success) {
                //NewsInsertRequest insert_request = new NewsInsertRequest(DatabaseHelper.HISTORY, digest,content);
                //local_news(insert_request);
                database.insert(new NewsDatabaseObject(digest, content), DatabaseHelper.FAVORITE);
            }

            NewsContentRespond respond = new NewsContentRespond(success, content);
            return respond;
        }

        public LocalNewsRespond local_news(LocalNewsRequest request){
            if (request instanceof NewsInsertRequest){
                NewsDatabaseObject object = new NewsDatabaseObject(((NewsInsertRequest) request).digest,((NewsInsertRequest) request).content);
                database.insert(object,request.table_name);
                return new LocalNewsRespond();
            }
            if (request instanceof NewsDeleteRequest){
                boolean _success = database.delete(((NewsDeleteRequest) request).news_id, request.table_name);
                return new LocalNewsRespond(_success);
            }
            if (request instanceof AllNewsRequest){
                HashMap<String, NewsDatabaseObject> map = database.getAllNews(request.table_name);
                return new AllNewsRespond(map);
            }
            if (request instanceof NewsListRequest){
                HashSet<String> set = database.getNewsList(request.table_name);
                return new NewsListRespond(set);
            }
            System.out.println("error");
            return new LocalNewsRespond();
        }

    }

    @Override
    public IBinder onBind(Intent it){
        System.out.println("Service Bind");
        database = new NewsDatabase(this);
        if (database != null) System.out.println("database has been set up");
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
       // database = new NewsDatabase(this);
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
