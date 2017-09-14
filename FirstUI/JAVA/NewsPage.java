package com.example.asus.first_ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Picture;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.*;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.GenericDraweeView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.net.URL;
import java.security.Key;
import java.util.*;

public class NewsPage extends AppCompatActivity {
    boolean IsDay;
    boolean ShowPhoto;
    String Title;
    int BackGroundC, TextC;

    Intent Result = new Intent();
    int NewF = 0;
    int OldF = 0;
    private TextToSpeech mSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);

        mSpeech = new TextToSpeech(this, new OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS)
                    mSpeech.setLanguage(Locale.CHINESE);
            }
        });

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        IsDay = (bundle.getInt("Night") == 0);
        ShowPhoto = (bundle.getInt("Photo") == 0);
        Title = bundle.getString("Title");
        NewsContent content = (NewsContent) bundle.getSerializable("Content");


        BackGroundC = Color.parseColor(IsDay?"#FFFAFA":"#303030");
        TextC = Color.parseColor(IsDay?"#000000":"#C4C3CB");

        LinearLayout MZ = (LinearLayout) findViewById(R.id.NewsPageMain);
        LinearLayout NHZ = (LinearLayout) findViewById(R.id.NewsHead);
        MZ.setBackgroundColor(BackGroundC);
        LinearLayout NewsInputer = (LinearLayout) findViewById(R.id.NewsInputer);
        int OldF = bundle.getInt("Favorite");

        EditText NewsTitle = new EditText(this);
        NewsTitle.setText(Title);
        NewsTitle.setTextColor(TextC);
        NewsTitle.setTextSize(24);
        NewsTitle.setEnabled(false);
        NHZ.addView(NewsTitle);

        if (content.special_words.size() > 0){
            TextView Keyword = new TextView(this);
            String Keys = "新闻关键词 :\n" +  " ";
            for(String key: content.special_words){
                if (key.length() < 6)
                    Keys = Keys + key + " ";
            }
            SpannableString ss = new SpannableString(Keys);
            String Words = "";
            int head = 0;
            int tail = 0;
            for (int i = 6 ; i < Keys.length() ; ++i){
                if (Keys.charAt(i) == ' ') {
                    Words = "";
                    head = i + 1;
                    i = i + 1;
                    if (i >= Keys.length()) break;
                    while (Keys.charAt(i) != ' ') {
                        Words = Words + Keys.charAt(i);
                        i = i + 1;
                    }
                    tail = i;
                    i = i - 1;
                    ss.setSpan(new URLSpan("https://baike.baidu.com/item/" + Words), head, tail, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            Keyword.setText(ss);
            Keyword.setMovementMethod(LinkMovementMethod.getInstance());
            Keyword.setClickable(true);
            Keyword.setTextColor(TextC);
            Keyword.setTextSize(20);
            NewsInputer.addView(Keyword);
        }

        int cnt = content._getpicture().size();
        SimpleDraweeView[] Pictures = new SimpleDraweeView[cnt];
        if (ShowPhoto){
            for (int i = 0 ; i < content._getpicture().size() ; ++i){
                Pictures[i] = new SimpleDraweeView(this);
                Pictures[i].setImageURI(content._getpicture().get(i));
                GenericDraweeHierarchy PicInfo = Pictures[i].getHierarchy();
                PicInfo = Pictures[i].getHierarchy();
                PicInfo.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
                Pictures[i].setHierarchy(PicInfo);

                Pictures[i].setMinimumHeight(600);
                //Pictures[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            if (content._getpicture().size() > 0)
                NewsInputer.addView(Pictures[0]);
        }

        EditText NewsCon = new EditText(this);
        NewsCon.setText(content._content());
        NewsCon.setTextSize(18);
        NewsCon.setTextColor(TextC);
        NewsCon.setEnabled(false);
        NewsInputer.addView(NewsCon);

        if (ShowPhoto) {
            for (int i = 1; i < content._getpicture().size(); ++i)
                NewsInputer.addView(Pictures[i]);
        }

        final ImageButton LikeIt = (ImageButton) findViewById(R.id.LikeButton);
        if (OldF == 0)
            LikeIt.setImageDrawable(getResources().getDrawable(R.drawable.notlikeit));
        else
            LikeIt.setImageDrawable(getResources().getDrawable(R.drawable.likeit));
        NewF = OldF;
        LikeIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewF = 1 - NewF;
                if (NewF == 0)
                    LikeIt.setImageDrawable(getResources().getDrawable(R.drawable.notlikeit));
                else
                    LikeIt.setImageDrawable(getResources().getDrawable(R.drawable.likeit));
                Result.putExtra("NewF", NewF);
            }
        });
        ImageButton ReadIt = (ImageButton) findViewById(R.id.ReadButton);
        ReadIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpeech.speak(Title, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        ImageButton ShareIt = (ImageButton) findViewById(R.id.ShareButton);
        ShareIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsClearRequest clear_request = new NewsClearRequest(DatabaseHelper.HISTORY);
                Toast.makeText(NewsPage.this, "抱歉，分享功能尚未实现", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton TalkIt = (ImageButton) findViewById(R.id.TalkButton);
        TalkIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsClearRequest clear_request = new NewsClearRequest(DatabaseHelper.HISTORY);
                Toast.makeText(NewsPage.this, "抱歉，评论功能尚未实现", Toast.LENGTH_SHORT).show();
            }
        });

        String IDDDD = bundle.getString("IDDDD");
        Result.putExtra("IDDDD", IDDDD);
        Result.putExtra("OldF", OldF);
        Result.putExtra("NewF", NewF);

        this.setResult(0, Result);
        ImageButton BackButton = (ImageButton) findViewById(R.id.NewsBack);
        BackButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });
    }

    @Override
    protected void onDestroy(){
        if (mSpeech != null){
            mSpeech.stop();
            mSpeech.shutdown();
        }
        super.onDestroy();
    }
}
