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
    News(){
        type = "";
        id = "";
        url = "";
        time = new Date();
        source = "";
    };
    News(String _type, String _id, String _url, Date _time, String _source){
        type = _type; _id = id; url = _url; time = _time; source = _source;
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

}