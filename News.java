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

class NewsDegest extends News{
    String title;
    String intro;
    NewsDegest(){
        super();
        title = "";
        intro = "";
    }
    NewsDegest(String _type, String _id, String _url, Date _time, String _source, String _title, String _intro){
        super(_type,_id,_url,_time,_source);
        title = _title;
        intro = _intro;
    }

}

class NewsContent extends News{
    String title;
    String[] special_words;
    String content;
    String[] picture_urls;
    NewsContent(){
        super();
        title = "";content = "";
        special_words = null;
        picture_urls = null;
    }
    NewsContent(String _type, String _id, String _url, Date _time, String _source, String _title, String _content, String[] _special_words, String[] _picture_urls){
        super(_type,_id,_url,_time,_source);
        title = _title;
        special_words = _special_words;
        picture_urls = _picture_urls;
    }

}
