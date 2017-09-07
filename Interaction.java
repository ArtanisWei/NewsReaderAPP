package com.example.victor.todaynews;

/**
 * Created by victor on 07/09/2017.
 */
import java.security.KeyException;
import java.util.*;

public interface Interaction {
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
    public ArrayList _get();
    public boolean _set(ArrayList para);
}

class NewsRequest implements Interaction{
    int number;//需要的新闻数量
    boolean[] type_bitmap;//需要类型的bitmap,需要则为true,否则为false

    NewsRequest(int _num, String[]need_type){
        number = _num;
        type_bitmap = new boolean[13];
        for (int i = 0; i < type_bitmap.length; i++){
            type_bitmap[i] = false;
        }
        for (int i = 0; i < need_type.length; i++){
            try{
                int temp = from_String_to_Integer.get(need_type[i]);
                type_bitmap[temp] = true;
            }catch(Exception e){
                System.out.println("不存在" + need_type[i] + "类型");
            }
        }
    }

    public ArrayList _get(){
        ArrayList temp_list = new ArrayList();
        temp_list.add(number);
        temp_list.add(type_bitmap);
        return temp_list;
    }

    public boolean _set(ArrayList para){
        try{
            number = (Integer)para.get(0);
            type_bitmap = (boolean[])para.get(1);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

}

class NewsSearchRequest implements Interaction{
    int number;//需要的数量
    String keyword;//搜索关键词
    NewsSearchRequest(int _num, String _keyword){
        keyword = _keyword;
        number = _num;
    }
    public ArrayList _get(){
        ArrayList temp = new ArrayList();
        temp.add(number);
        temp.add(keyword);
        return temp;
    }
    public boolean _set(ArrayList pa){
        try{
            keyword = (String)pa.get(1);
            number = (Integer)pa.get(0);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
class NewsSearchRespond implements Interaction{
    int number;//最终找到新闻的数目
    Vector<NewsDegest> news;//所有新闻摘要构成的向量
    NewsSearchRespond(int _num, Vector<NewsDegest> _n){
        number = _num;
        news = _n;
    }
    public ArrayList _get(){
        ArrayList temp = new ArrayList();
        temp.add(number);
        temp.add(news);
        return temp;
    }
    public boolean _set(ArrayList pa){
        try{
            number = (int)pa.get(0);
            news = (Vector<NewsDegest>)pa.get(1);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

class NewsCacheRequest implements Interaction{
    String news_id;//目标新闻id
    int operator;//操作码，1表示添加新闻上述新闻id, 2表示删除上述新闻id, 3表示返回上述新闻具体内容，4表示列出列表上述新闻id无效
    NewsCacheRequest(String id, int _o){
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
    Vector<NewsDegest> news;//所有新闻摘要构成的向量

    NewsRespond(int _num, Vector<NewsDegest> n){
        number = _num;
        news = n;
    }
    public ArrayList _get(){
        ArrayList temp = new ArrayList();
        temp.add(number);
        temp.add(news);
        return temp;
    }
    public boolean _set(ArrayList temp){
        try{
            number = (int)temp.get(0);
            news = (Vector<NewsDegest>)temp.get(1);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

class NewsCacheRespond implements Interaction{
    boolean success;//返回对缓存的这次操作是否成功
    Vector<NewsDegest> list;//返回缓存列表（如果需要的话）
    NewsContent content;//返回指定新闻的内容

    NewsCacheRespond(boolean _success){
        success = _success;
        list = new Vector<NewsDegest>();
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
    }
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
