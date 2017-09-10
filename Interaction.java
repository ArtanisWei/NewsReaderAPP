package com.example.victor.todaynews;

/**
 * Created by victor on 07/09/2017.
 */
import java.io.Serializable;
import java.security.KeyException;
import java.util.*;

public interface Interaction extends Serializable{
    final HashMap<String,Integer> from_String_to_Integer = new HashMap<String, Integer>(){
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
    final HashMap<Integer,String> from_Integer_to_String = new HashMap<Integer,String>(){
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
    int number;//需要的新闻数量
    int type;//传入需要的种类，如果无种类的特殊需求则传入0

    NewsRequest(){
        number = 0;
        type = 0;
    }
    NewsRequest(int _num){
        number = _num;
        type = 0;
    }
    NewsRequest(int _num, String _type){
        number = _num;
        type = from_String_to_Integer.get(_type);
    }
    NewsRequest(int _num, int _type){
        number = _num;
        type = _type;
    }

    public ArrayList _get(){
        ArrayList temp_list = new ArrayList();
        temp_list.add(number);
        temp_list.add(type);
        return temp_list;
    }

    public boolean _set(ArrayList para){
        try{
            number = (Integer)para.get(0);
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
    NewsSearchRespond(int _num, Vector<NewsDigest> _n){
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

class NewsFavouriteRequest implements Interaction{
    String news_id;//目标新闻id
    int operator;//操作码，1表示添加新闻上述新闻id, 2表示删除上述新闻id, 3表示返回上述新闻具体内容，4表示列出列表上述新闻id无效
    NewsFavouriteRequest(String id, int _o){
        news_id = id;
        operator = _o;
    }
    public ArrayList _get(){
        ArrayList temp = new ArrayList();
        temp.add(news_id);
        temp.add(operator);
        return temp;
    }
    public boolean _set(ArrayList temp){
        try{
            news_id = (String)temp.get(0);
            operator = (int)temp.get(1);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

class NewsContentRequest implements Interaction{
    String newsid;//需要查看详情的新闻id
    NewsContentRequest(String id){
        newsid = id;
    }
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

class NewsFavourtieRespond implements Interaction{
    boolean success;//返回对缓存的这次操作是否成功
    Vector<NewsDigest> list;//返回缓存列表（如果需要的话）
    NewsContent content;//返回指定新闻的内容


    NewsFavourtieRespond(boolean _success){
        success = _success;
        list = new Vector<NewsDigest>();
        content = new NewsContent();
    }
    public ArrayList _get(){
        ArrayList temp = new ArrayList();
        temp.add(success);
        temp.add(list);
        temp.add(content);
        return temp;
    }
    public boolean _set(ArrayList temp){//弃置不用
        return true;
    } //由于这个回应内容太过复杂，所以可以直接修改内部变量
}

class NewsContentRespond implements Interaction{
    boolean _success;//是否成功找到了新闻详情
    NewsContent answer;//具体新闻详情
    NewsContentRespond(boolean __success, NewsContent _ans){
        _success = __success;
        answer = _ans;
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
