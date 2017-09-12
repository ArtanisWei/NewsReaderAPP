package com.example.victor.todaynews;

import java.io.Serializable;
import java.util.*;
/**
 * Created by victor on 07/09/2017.
 */

public class News implements Serializable{
    String type;
    String id;
    String url;
    Date time;
    String source;
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
    News(){
        type = "";
        id = "";
        url = "";
        time = new Date();
        source = "";
    };
    News(String _type, String _id, String _url, Date _time, String _source){
        type = _type; id = _id; url = _url; time = _time; source = _source;
    }
}

class NewsDatabaseObject extends News{
    String title;
    String intro;
    String id;
    String type;
    String picture_path;
    String content;
    NewsDatabaseObject(){
    }

    NewsDatabaseObject(NewsDigest digest, NewsContent _content){
        title = digest.title;
        intro = digest.intro;
        id = digest.id;
        type = digest.type;
        picture_path = "";
        Vector<String> pictures = _content.picture_urls;
        for(String s:pictures){
            String _target = s + DatabaseHelper.CUTTER;
            picture_path = picture_path + _target;
        }
        content = _content.content;
    }

    NewsDatabaseObject(String _title, String _intro, String _id, String _type, String _picture_path, String _content){
        title = _title;
        intro = _intro;
        id = _id;
        type = _type;
        picture_path = _picture_path;
        content = _content;
    }
    @Override
    public String toString(){
        return ("\ntitle: " + title + "\nintro" + intro + "\ntype" + type
                + "\npicture_start: \n" + picture_path + "\npicture_end\n" + content);
    }
}
class NewsDigest extends News{
    String title;
    String intro;
    NewsDigest(){
        super();
        title = "";
        intro = "";
    }
    NewsDigest(String _title, String _intro){
        super();
        title = _title;
        intro = _intro;
    }
    NewsDigest(String _type, String _id, String _url, Date _time, String _source, String _title, String _intro){
        super(_type,_id,_url,_time,_source);

        title = _title;
        intro = _intro;
    }
    @Override
    public String toString(){
        return title + "\n" + intro;
    }
}

class NewsContent extends News{
    //String title;
    HashSet<String> special_words;
    String content;
    Vector<String> picture_urls;
    public String _content(){
        return content;
    }
    public HashSet<String> _getwords(){
        return special_words;
    }
    public Vector<String> _getpicture(){
        return picture_urls;
    }
    NewsContent(){
        super();
        content = "";
        special_words = new HashSet<String>();
        picture_urls = new Vector<String>();
    }
    NewsContent(NewsDigest dig, String _content, Vector<String> pic, HashSet<String> sp){
        super(dig.type, dig.id, dig.url, dig.time, dig.source);
        special_words = sp;
        content = _content;
        picture_urls = pic;
    }

    NewsContent(String _type, String _id, String _url, Date _time, String _source, String _title, String _content, HashSet<String> _special_words, Vector<String> _picture_urls){
        super(_type,_id,_url,_time,_source);
        special_words = _special_words;
        picture_urls = _picture_urls;
    }
    NewsContent(HashSet<String> special, Vector<String> picture, String _content){
        super();
        special_words = special;
        picture_urls = picture;
        content = _content;
    }
    public String toString(){
        String result = content;
        result += "\n";
        result += "picture start: \n";
        for (int i = 0; i < picture_urls.size(); i++){
            result += picture_urls.get(i);
            result += "\n";
        }
        result += "picture end \n";
        result += "words start: \n";
        for (String s: special_words){
            result += s;
            result += "\n";
        }
        result += "word end \n";
        return result;
    }

}