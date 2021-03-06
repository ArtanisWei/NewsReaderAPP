package com.example.victor.todaynews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
/**
 * Created by victor on 08/09/2017.
 */
class NewsParserException extends Exception{
    String raw;
    NewsParserException(){
        raw = "";
    }
    NewsParserException(String raw_new){
        raw = raw_new;
    };
    @Override
    public String toString(){
        return "News Parse failed at: " + raw;
    }
}
public class NewsParser {
    NewsParser(){
    }
    static Pattern news_regex = Pattern.compile("\\{(.*?)\\}");
    static Pattern digest_feature_regex = Pattern.compile("(.*?):\"(.*)\"");
    static Pattern content_feature_regex = Pattern.compile("(.*?) : \"(.*)\"");
    static Pattern words_regex = Pattern.compile("\"word\" : \"(.*?)\"");
    static Pattern major_pattern = Pattern.compile("\"list\":\\[(.*)\\],\"pageNo\"");
    static Pattern picture_pattern = Pattern.compile("http://.*?\\.(jpg|jpeg|png|gif)");

    private static String get_digest_info(String raw) throws NewsParserException{
        Matcher map = digest_feature_regex.matcher(raw);
        map.find();
        try{
            return map.group(2);
        }catch(Exception e){
            System.out.println("here");
            System.out.println(raw);
            throw new NewsParserException();
        }
    }
    private static String get_content(String raw) throws NewsParserException{
        try{
            String sub_raw = raw.substring(raw.indexOf("\"news_Content\""), raw.indexOf("\"news_ID\"") - 2);
            Matcher map = content_feature_regex.matcher(sub_raw);
            map.find();
            return map.group(2);
        }catch(Exception e){
            throw new NewsParserException();
        }
    }
    private static HashSet<String> get_special_words(String raw) throws NewsParserException{
        try{
            HashSet<String> answer = new HashSet<String>();
            String sub_person = raw.substring(raw.indexOf("\"persons\""), raw.indexOf("\"repeat_ID\""));
            String sub_location = raw.substring(raw.indexOf("\"locations\""), raw.indexOf("\"newsClassTag\""));
            String sub_orgnization = raw.substring(raw.indexOf("\"organizations\""), raw.indexOf("\"persons\""));
            Matcher person = words_regex.matcher(sub_person);
            Matcher location = words_regex.matcher(sub_location);
            Matcher orgnization = words_regex.matcher(sub_orgnization);
            while(person.find()){
                answer.add(person.group(1));
            }
            while(location.find()){
                answer.add(location.group(1));
            }
            while(orgnization.find()){
                answer.add(orgnization.group(1));
            }
            return answer;
        }catch(Exception e){
            throw new NewsParserException();
        }
    }

    private static Vector<String> get_pictures(String raw) throws NewsParserException{
        try{
            Vector<String> answer = new Vector<String>();
            //System.out.println(raw.indexOf("\"news_Pictures\"") + " " + raw.indexOf("\"news_Source\""));
            String sub_picture = raw.substring(raw.indexOf("\"news_Pictures\""), raw.indexOf("\"news_Source\""));
            //System.out.println("sub_picture is: " + sub_picture);
            Matcher picture_match = picture_pattern.matcher(sub_picture);
            while(picture_match.find()){
                answer.add(sub_picture.substring(picture_match.start(),picture_match.end()));
            }
            return answer;
        }catch(Exception e){
            //System.out.println("raw: " + raw);
            throw new NewsParserException();
        }
    }

    private static Date get_date(String raw) throws NewsParserException{
        Matcher date_map = digest_feature_regex.matcher(raw);
        date_map.find();
        try{
            DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            return format.parse(date_map.group(2));
        }catch(Exception e){
            System.out.println("Date wrong at " + raw);
            return new Date();
            //throw new NewsParserException();
        }
    }

    public static Vector<NewsDigest> parse_digest(String info) throws NewsParserException{
        Vector<NewsDigest> answer = new Vector<NewsDigest>();
        try{
            Matcher major_matcher = major_pattern.matcher(info);
            major_matcher.find();
            String result = major_matcher.group(1);
            //System.out.println("news is : " + result);

            Matcher news_matcher = news_regex.matcher(result);
            while(news_matcher.find()){
                //System.out.println("news start");
                //System.out.println("temp_news is ----->" + result.substring(news_matcher.start(),news_matcher.end()));
                String temp_news = news_matcher.group(1);

                //String[] feature_list = temp_news.split(",");

                String _type = get_digest_info(temp_news.substring(temp_news.indexOf("\"newsClassTag\""), temp_news.indexOf("\"news_Author\"") - 1));
                //System.out.println("type" + _type);
                String _id = get_digest_info(temp_news.substring(temp_news.indexOf("\"news_ID\""), temp_news.indexOf("\"news_Pictures\"") - 1));

                String _url = get_digest_info(temp_news.substring(temp_news.indexOf("\"news_URL\""), temp_news.indexOf("\"news_Video\"") - 1));

                Date _date = get_date(temp_news.substring(temp_news.indexOf("\"news_Time\""), temp_news.indexOf("\"news_Title\"") - 1));
                //System.out.println("date" + _date);
                String _source = get_digest_info(temp_news.substring(temp_news.indexOf("\"news_Source\""),temp_news.indexOf("\"news_Time\"") - 1));
                String _title = get_digest_info(temp_news.substring(temp_news.indexOf("\"news_Title\""),temp_news.indexOf("\"news_URL\"") - 1));
                String _intro = get_digest_info(temp_news.substring(temp_news.indexOf("\"news_Intro\"")));

                answer.add(new NewsDigest(_type, _id, _url, _date,_source,_title,_intro));
                //System.out.println("news finish!");
            }
            return answer;
        }catch(Exception e){
            System.out.println(e);
            throw new NewsParserException(info);
        }
    }
    public static NewsContent parse_content(String info) throws NewsParserException{
        String content = get_content(info);
        HashSet<String> special_words = get_special_words(info);
        Vector<String> pictures = get_pictures(info);
        return new NewsContent(special_words, pictures, content);
    }
}
