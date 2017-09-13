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
            return;
        }
    }

    private String connect_internet(String _url) throws Exception{
        URL realUrl = new URL(_url);

        URLConnection connection = realUrl.openConnection();
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(2000);

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
            int size = request.size;
            if (size == -1) size = max_size;

            if (page >= size) {
                System.out.println("no more news, page back to the end");
                page = 1;
            }

            String param = "pageNo=" + page + "&pageSize=" + size;

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

            NewsContent content = webThread.get_content();
            boolean success = false;
            if (!content.content.equals("")) success = true;

            if (!success) {
                NewsGetByidRequest database_request = new NewsGetByidRequest(news_id, DatabaseHelper.HISTORY);
                NewsGetByidRespond respond = (NewsGetByidRespond)local_news(database_request);
                success = respond.success;
                content = respond.content;
            }

            NewsContentRespond respond = new NewsContentRespond(success, content);
            return respond;
        }

        public LocalNewsRespond local_news(LocalNewsRequest request){
            if (request instanceof NewsInsertByid){
                NewsDatabaseObject object = database.getNewsByid(((NewsInsertByid) request).news_id, DatabaseHelper.HISTORY);
                database.insert(object, ((NewsInsertByid) request).table_name);
                return new LocalNewsRespond();
            }
            if (request instanceof NewsInsertRequest){
                NewsDatabaseObject object = new NewsDatabaseObject(((NewsInsertRequest) request).digest,((NewsInsertRequest) request).content);
                database.insert(object,request.table_name);
                return new LocalNewsRespond();
            }
            if (request instanceof NewsClearRequest){
                System.out.println("table_name is : " + request.table_name);
                boolean _success = database.clear(request.table_name);
                return new LocalNewsRespond(_success);
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
            if (request instanceof NewsTitleRequest){
                Vector<NewsDigest> vet = database.getNewsTitle(request.table_name);
                return new NewsTitleRespond(vet);
            }
            if (request instanceof NewsGetByidRequest){
                NewsDatabaseObject object = database.getNewsByid(((NewsGetByidRequest) request).news_id, ((NewsGetByidRequest) request).table_name);
                if (object.content.equals("")) {
                    return new NewsGetByidRespond(false);
                }
                Vector<String> picture_path = new Vector<String>();
                String[] pictures = object.picture_path.split(DatabaseHelper.CUTTER);
                for (int i = 0; i < pictures.length; i++){
                    picture_path.add(pictures[i]);
                }
                NewsContent content = new NewsContent(new HashSet<String>(), picture_path, object.content);
                return new NewsGetByidRespond(true, content);

            }
            if (request instanceof NewsTypeRequest){
                String table = ((NewsTypeRequest) request).table_name;
                Vector<String> all_type = database.getType(table);
                return new NewsTypeRespond(all_type);
            }

            System.out.println("error_type");
            return new LocalNewsRespond();
        }


        public NewsRecommendRespond news_recommand(NewsRecommendRequest request){
            NewsTypeRespond histor_respond  = (NewsTypeRespond)local_news(new NewsTypeRequest(DatabaseHelper.HISTORY));
            Vector<Integer> history_max_type = histor_respond.get_answer();
            NewsTypeRespond favorite_respond = (NewsTypeRespond)local_news(new NewsTypeRequest(DatabaseHelper.FAVORITE));
            Vector<Integer> favorite_max_type = favorite_respond.get_answer();
            boolean[] total_type = new boolean[13];
            for (int i = 0; i < total_type.length;i++){
                total_type[i] = false;
            }
            for (int i : history_max_type){
                total_type[i] = true;
            }
            for (int i:favorite_max_type){
                total_type[i] = true;
            }

            Vector<NewsDigest> answer = new Vector<NewsDigest>();

          //  for (int i = 0;i < total_type.length;i++){
          //      if (total_type[i]){
          //          Vector<NewsDigest> all_news = get_news_digest(new NewsRequest(1,i)).get_news();
          //          for (NewsDigest d:all_news){
          //              answer.add(d);
          //          }
          //      }
           // }
            for(int start_page = 1; answer.size() < 20; start_page++){
                Vector<NewsDigest> all_news = get_news_digest(new NewsRequest(start_page,200,0)).get_news();
                for (NewsDigest d: all_news){
                    int num = request.from_String_to_Integer.get(d.type);
                    if (total_type[num]) answer.add(d);
                }
            }

            return new NewsRecommendRespond(answer);
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
