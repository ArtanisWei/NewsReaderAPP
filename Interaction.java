package com.example.victor.todaynews;

/**
 * Created by victor on 07/09/2017.
 */
import java.io.Serializable;
import java.security.KeyException;
import java.util.*;

public interface Interaction extends Serializable{
    static final HashMap<String,Integer> from_String_to_Integer = new HashMap<String, Integer>(){
        {
            put("科技",1);
            put("教育",2);
            put("军事",3);
            put("国内",4);
            put("社会",5);
            put("文化",6);
            put("汽车",7);
            put("国际",8);
            put("体育",9);
            put("财经",10);
            put("健康",11);
            put("娱乐",12);
        }
    };
    static final HashMap<Integer,String> from_Integer_to_String = new HashMap<Integer,String>(){
        {
            put(1,"科技");
            put(2,"教育");
            put(3,"军事");
            put(4,"国内");
            put(5,"社会");
            put(6,"文化");
            put(7,"汽车");
            put(8,"国际");
            put(9,"体育");
            put(10,"财经");
            put(11,"健康");
            put(12,"娱乐");
        }
    };
    public ArrayList _get();
    public boolean _set(ArrayList para);
}

class NewsRequest implements Interaction{
    int page;//想看哪一页
    int type;//传入需要的种类，如果无种类的特殊需求则传入0

    NewsRequest(){
        page = 0;
        type = 0;
    }
    NewsRequest(int _num){
        page = _num;
        type = 0;
    }
    NewsRequest(int _num, String _type){
        page = _num;
        type = from_String_to_Integer.get(_type);
    }
    NewsRequest(int _num, int _type){
        page = _num;
        type = _type;
    }

    public ArrayList _get(){
        ArrayList temp_list = new ArrayList();
        temp_list.add(page);
        temp_list.add(type);
        return temp_list;
    }

    public boolean _set(ArrayList para){
        try{
            page = (Integer)para.get(0);
            type = (Integer)para.get(1);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

}

class NewsSearchRequest implements Interaction{
    String keyword;//搜索关键词
    NewsSearchRequest(){
        keyword = "";
    }
    NewsSearchRequest(String _keyword){
        keyword = _keyword;
    }
    public ArrayList _get(){
        ArrayList temp = new ArrayList();
        temp.add(keyword);
        return temp;
    }
    public String get_keyword(){
        return keyword;
    }
    public boolean _set(ArrayList pa){
        try{
            keyword = (String)pa.get(0);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
class NewsSearchRespond implements Interaction{
    Vector<NewsDigest> news;//所有新闻摘要构成的向量
    NewsSearchRespond(){
        news = new Vector<NewsDigest>();
    }
    NewsSearchRespond(Vector<NewsDigest> _n){
        news = _n;
    }
    public ArrayList _get(){
        ArrayList temp = new ArrayList();
        temp.add(news);
        return temp;
    }
    public Vector<NewsDigest> get_news(){
        return news;
    }
    public boolean _set(ArrayList pa){
        try{
            news = (Vector<NewsDigest>)pa.get(0);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

class LocalNewsRequest implements Interaction{
    String table_name;
    LocalNewsRequest(String _name){
        table_name = _name;
    }
    LocalNewsRequest(){
        table_name = "";
    }
    public ArrayList _get(){
        return new ArrayList();
    }
    public boolean _set(ArrayList para){
        return true;
    }
}
class NewsTitleRequest extends LocalNewsRequest{
    String _table_name;
    NewsTitleRequest(String _name){table_name = _name;}
}
class NewsInsertRequest extends LocalNewsRequest{
    NewsDigest digest;
    NewsContent content;
    NewsInsertRequest(){
    }
    NewsInsertRequest(String table_name, NewsDigest _digest, NewsContent _content){
        super(table_name);
        digest = _digest;
        content = _content;
    }
}
class NewsInsertByid extends NewsInsertRequest{
    String news_id;
    String table_name;
    NewsInsertByid(String _name, String id){
        table_name = _name;
        news_id = id;
    }
}

class NewsGetByidRequest extends LocalNewsRequest{
    String news_id;
    String table_name;
    NewsGetByidRequest(String _id, String _table_name){
        news_id = _id;
        table_name = _table_name;
    }
}
class NewsGetByidRespond extends LocalNewsRespond{
    NewsContent content;
    NewsGetByidRespond(NewsContent _content){
        content = _content;
    }
    public NewsContent get_content(){return content;}
}

class NewsDeleteRequest extends LocalNewsRequest{
    String news_id;
    NewsDeleteRequest(String _id, String table_name){
        super(table_name);
        news_id = _id;
    }
}
class NewsListRequest extends LocalNewsRequest{
    NewsListRequest(String table_name){
        super(table_name);
    }
}
class AllNewsRequest extends LocalNewsRequest{
    AllNewsRequest(String table_name){
        super(table_name);
    }
}

class LocalNewsRespond implements Interaction{
    boolean is_success;
    LocalNewsRespond(){

    }
    LocalNewsRespond(boolean _success){
        is_success = _success;
    }
    public ArrayList _get(){
        return new ArrayList();
    }
    public boolean _set(ArrayList para){
        return true;
    }
}
class AllNewsRespond extends LocalNewsRespond{
    HashMap<String, NewsDatabaseObject>  map = new HashMap<String, NewsDatabaseObject>();
    HashMap<String, NewsDatabaseObject> get_answer(){
        return map;
    }
    AllNewsRespond(HashMap<String, NewsDatabaseObject> mp){
        map = mp;
    }
}
class NewsListRespond extends LocalNewsRespond{
    HashSet<String> set = new HashSet<String>();
    HashSet<String> get_answer(){
        return set;
    }
    NewsListRespond(HashSet<String> _set){
        set = _set;
    }
}
class NewsTitleRespond extends LocalNewsRespond{
    Vector<NewsDigest> answer = new Vector<NewsDigest>();
    NewsTitleRespond(Vector<NewsDigest> vet){answer = vet;}
    Vector<NewsDigest> get_answer(){return answer;}
}


class NewsContentRequest implements Interaction{
    String newsid;//需要查看详情的新闻id
    String newstitle;
    String newsintro;
    NewsContentRequest(String id, String title, String intro){
        newsid = id;newstitle = title; newsintro = intro;
    }
    NewsContentRequest(String id){
        newsid = id;
    }
    public String get_id(){return newsid;}
    public ArrayList _get(){
        ArrayList temp = new ArrayList();
        temp.add(newsid);
        return temp;
    }
    public boolean _set(ArrayList temp){
        try{
            newsid = (String)temp.get(0);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

class NewsRespond implements Interaction{
    int number;//获得的新闻数目
    Vector<NewsDigest> news;//所有新闻摘要构成的向量

    NewsRespond(){
        number = 0;
        news = new Vector<NewsDigest>();
    }
    NewsRespond(int _num, Vector<NewsDigest> n){
        number = _num;
        news = n;
    }

    public ArrayList _get(){
        ArrayList temp = new ArrayList();
        temp.add(number);
        temp.add(news);
        return temp;
    }

    public Vector<NewsDigest> get_news(){
        return news;
    }

    public boolean _set(ArrayList temp){
        try{
            number = (int)temp.get(0);
            news = (Vector<NewsDigest>)temp.get(1);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

class NewsContentRespond implements Interaction{
    boolean _success;//是否成功找到了新闻详情
    NewsContent answer;//具体新闻详情
    NewsContentRespond(boolean __success, NewsContent _ans){
        _success = __success;
        answer = _ans;
    }
    NewsContent get_answer(){
        return answer;
    }
    boolean is_success(){
        return _success;
    }
    public ArrayList _get(){
        ArrayList temp = new ArrayList();
        temp.add(_success);
        temp.add(answer);
        return temp;
    }
    public boolean _set(ArrayList temp){
        try{
            _success = (boolean)temp.get(0);
            answer = (NewsContent)temp.get(1);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
