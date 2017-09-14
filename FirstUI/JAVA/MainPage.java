package com.example.asus.first_ui;

import java.util.*;

import android.graphics.Color;
import android.media.Image;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.database.DatabaseUtilsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup.*;
import android.content.*;
import android.widget.*;
import android.text.*;
import android.view.*;
import android.app.*;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.w3c.dom.Text;

public class MainPage extends AppCompatActivity {

    boolean Daytime = true;
    boolean PhotoOn = true;
    String SearchWord = "";
    HashMap<String, Integer> StI = new HashMap<String, Integer>();
    HashMap<Integer, String> ItS = new HashMap<Integer, String>();
    Button[] TagList = new Button[20];
    boolean[] TagShow = new boolean[20];
    NewsService.NewsBind binder;
    TextView[] NewsList = new TextView[1000];
    LinearLayout[] NewsBox = new LinearLayout[500];
    TextView[] InShadow = new TextView[500];
    PullToRefreshScrollView NewsScroll;
    int Cnt = 0;
    int Tot = 0;
    int NowPage  = 1;
    int NowTag = 0;
    HashSet<String> history_record = new HashSet<String>();
    HashSet<String> favorite_record = new HashSet<String>();
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            System.out.println("---$$$$$$$$$connected$$$$$$$$$$$$$$---");
            binder = (NewsService.NewsBind) iBinder;

            NewsRequest request = new NewsRequest(1, 0);
            NewsRespond respond = binder.get_news_digest(request);
            Vector<NewsDigest> answer = respond.get_news();
            ShowNews(answer);

            NewsListRespond history_respond = (NewsListRespond)binder.local_news(new NewsListRequest(DatabaseHelper.HISTORY));
            history_record = history_respond.get_answer();

            NewsListRespond favorite_respond = (NewsListRespond)binder.local_news(new NewsListRequest(DatabaseHelper.FAVORITE));
            favorite_record = favorite_respond.get_answer();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            System.out.println("---disconnected---");
        }
    };

    public void EndRefresh(){
        NewsScroll.onRefreshComplete();
    }

    protected void TextTemp(String Text) {
        TextView K1 = (TextView) findViewById(R.id.TempShow);
        K1.setEnabled(true);
        K1.setText(Text + "  DT:" + (Daytime ? "Y" : "N") + " PO:" + (PhotoOn ? "Y" : "N"));
        K1.setEnabled(false);
    }

    public void SetHashMap() {
        StI.put("鏈€鏂?, 0);
        StI.put("鎺ㄨ崘", -1);
        StI.put("鏀惰棌", -2);
        StI.put("绉戞妧", 1);
        StI.put("鏁欒偛", 2);
        StI.put("鍐涗簨", 3);
        StI.put("鍥藉唴", 4);
        StI.put("绀句細", 5);
        StI.put("鏂囧寲", 6);
        StI.put("姹借溅", 7);
        StI.put("鍥介檯", 8);
        StI.put("浣撹偛", 9);
        StI.put("璐㈢粡", 10);
        StI.put("鍋ュ悍", 11);
        StI.put("濞变箰", 12);

        ItS.put(0, "鏈€鏂?);
        ItS.put(-1, "鎺ㄨ崘");
        ItS.put(-2, "鏀惰棌");
        ItS.put(1, "绉戞妧");
        ItS.put(2, "鏁欒偛");
        ItS.put(3, "鍐涗簨");
        ItS.put(4, "鍥藉唴");
        ItS.put(5, "绀句細");
        ItS.put(6, "鏂囧寲");
        ItS.put(7, "姹借溅");
        ItS.put(8, "鍥介檯");
        ItS.put(9, "浣撹偛");
        ItS.put(10, "璐㈢粡");
        ItS.put(11, "鍋ュ悍");
        ItS.put(12, "濞变箰");
    }

    public void SetDayNight(){
        int BackGround_N = Color.parseColor("#303030");
        int BackGround_D = Color.parseColor("#FFFAFA");
        if (Daytime){
            LinearLayout MainPage = (LinearLayout) findViewById(R.id.MainZone);
            MainPage.setBackgroundColor(BackGround_D);
        }else{
            LinearLayout MainPage = (LinearLayout) findViewById(R.id.MainZone);
            MainPage.setBackgroundColor(BackGround_N);
        }
        int BackG = 0;
        int TextG = 0;
        if (Daytime) {
            BackG = Color.parseColor("#FFFFFF");
            TextG = Color.parseColor("#000000");
        }else{
            BackG = Color.parseColor("#3F3F3C");
            TextG = Color.parseColor("#C4C3CB");
        }
        for (int i = 0 ; i < Tot ; ++i)
            NewsBox[i].setBackgroundColor(BackG);
        for (int i = 0 ; i < Cnt ; ++i)
            NewsList[i].setTextColor(TextG);

    }

    public void ShowNews(Vector<NewsDigest> answer){
        LinearLayout NewsZone = (LinearLayout) findViewById(R.id.NewsZone);
        for (int i = 0 ; i < Tot ; ++i) {
            NewsBox[i].setVisibility(View.GONE);
            InShadow[i].setVisibility(View.GONE);
        }
        Cnt = Tot = 0;
        int BackG = 0;
        int TextG = 0;
        int RTextG = 0;
        if (Daytime) {
            BackG = Color.parseColor("#FFFFFF");
            TextG = Color.parseColor("#000000");
            RTextG = Color.parseColor("#767676");
        }else{
            BackG = Color.parseColor("#3F3F3C");
            TextG = Color.parseColor("#C4C3CB");
            RTextG = Color.parseColor("#A1A1A1");
        }
        for (int i = 0 ; i < answer.size() ; ++i){
            final String NID = answer.get(i).id;
            final String NTitle = answer.get(i).title;
            final String NIntro = answer.get(i).intro;
            final String NType = answer.get(i).type;
            NewsBox[i] = new LinearLayout(this);
            NewsBox[i].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            NewsBox[i].setOrientation(LinearLayout.VERTICAL);
            NewsBox[i].setPadding(0, 5, 0, 10);
            NewsBox[i].setBackgroundColor(BackG);
            NewsBox[i].setClickable(true);
            final int j = i;
            NewsBox[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NewsContent content = new NewsContent();
                    NewsContentRequest request = new NewsContentRequest(NID, NTitle, NIntro);
                    NewsContentRespond respond = binder.news_content(request);
                    if (respond._success){
                        NewsList[2*j].setTextColor(Color.parseColor(Daytime?"#767676":"#A1A1A1"));
                        content = respond.get_answer();
                        Intent intent = new Intent();
                        intent.setClass(MainPage.this, NewsPage.class);
                        intent.putExtra("Night", (Daytime ? 0 : 1));
                        intent.putExtra("Photo", (PhotoOn ? 0 : 1));
                        intent.putExtra("Title", NTitle);
                        intent.putExtra("IDDDD", NID);
                        intent.putExtra("Content", content);

                        boolean OldFavorite = favorite_record.contains(NID);
                        intent.putExtra("Favorite", (OldFavorite ? 0 : 1));

                        if (history_record.contains(NID)){
                            TextTemp("Already...");
                        }else {
                            NewsDigest digest = new NewsDigest(NTitle, NIntro);
                            digest.type = NType;
                            digest.id = NID;
                            NewsInsertRequest insert_request = new NewsInsertRequest(DatabaseHelper.HISTORY,digest, content);
                            binder.local_news(insert_request);
                            TextTemp(NID);
                        }
                        history_record.add(NID);
                        startActivityForResult(intent, 0);
                    }
                    else{
                        Toast.makeText(MainPage.this, "鏃犵綉缁滆繛鎺ワ紒", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            NewsList[Cnt] = new TextView(this);
            NewsList[Cnt].setText(answer.get(i).title);
            NewsList[Cnt].setGravity(Gravity.LEFT);
            NewsList[Cnt].getPaint().setFakeBoldText(true);
            NewsList[Cnt].setTextSize(20);
            if (history_record.contains(NID))
                NewsList[Cnt].setTextColor(RTextG);
            else NewsList[Cnt].setTextColor(TextG);
            NewsBox[i].addView(NewsList[Cnt++]);

            NewsList[Cnt] = new TextView(this);
            NewsList[Cnt].setText(answer.get(i).intro);
            NewsList[Cnt].setGravity(Gravity.LEFT);
            NewsList[Cnt].getPaint().setFakeBoldText(false);
            NewsList[Cnt].setTextSize(16);
            NewsList[Cnt].setPadding(0, 5, 0, 10);
            NewsList[Cnt].setTextColor(TextG);
            NewsBox[i].addView(NewsList[Cnt++]);

            NewsZone.addView(NewsBox[Tot++]);

            InShadow[i] = new TextView(this);
            InShadow[i].setText("");
            InShadow[i].setTextSize(10);
            InShadow[i].setEnabled(false);
            NewsZone.addView(InShadow[i]);
        }
    }

    public void AddNews(Vector<NewsDigest> answer){
        LinearLayout NewsZone = (LinearLayout) findViewById(R.id.NewsZone);
        int BackG = Color.parseColor((Daytime?"#FFFFFF":"#3F3F3C"));
        int TextG = Color.parseColor((Daytime?"#000000":"#C4C3CB"));
        int RTextG = Color.parseColor(Daytime?"#767676":"#A1A1A1");
        for (int i = 0 ; i < answer.size() ; ++i){
            final String NID = answer.get(i).id;
            final String NTitle = answer.get(i).title;
            final String NIntro = answer.get(i).intro;
            final String NType = answer.get(i).type;

            NewsBox[Tot] = new LinearLayout(this);
            NewsBox[Tot].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            NewsBox[Tot].setOrientation(LinearLayout.VERTICAL);
            NewsBox[Tot].setPadding(0, 5, 0, 10);
            NewsBox[Tot].setBackgroundColor(BackG);
            NewsBox[Tot].setClickable(true);
            final int j = Tot;
            NewsBox[Tot].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NewsContent content = new NewsContent();
                    NewsContentRequest request = new NewsContentRequest(NID, NTitle, NIntro);
                    NewsContentRespond respond = binder.news_content(request);
                    if (respond._success){
                        content = respond.get_answer();
                        Intent intent = new Intent();
                        intent.setClass(MainPage.this, NewsPage.class);
                        intent.putExtra("Night", (Daytime ? 0 : 1));
                        intent.putExtra("Photo", (PhotoOn ? 0 : 1));
                        intent.putExtra("Title", NTitle);
                        intent.putExtra("IDDDD", NID);
                        intent.putExtra("Content", content);

                        boolean OldFavorite = favorite_record.contains(NID);
                        intent.putExtra("Favorite", (OldFavorite ? 0 : 1));
                        NewsList[2*j].setTextColor(Color.parseColor(Daytime?"#767676":"#A1A1A1"));

                        if (history_record.contains(NID)){
                            TextTemp("Already...");
                        }else {
                            NewsDigest digest = new NewsDigest(NTitle, NIntro);
                            digest.type = NType;
                            digest.id = NID;
                            NewsInsertRequest insert_request = new NewsInsertRequest(DatabaseHelper.HISTORY,digest, content);
                            binder.local_news(insert_request);
                            TextTemp(NID);
                        }
                        history_record.add(NID);
                        startActivityForResult(intent, 0);
                    }
                    else{
                        Toast.makeText(MainPage.this, "鏃犵綉缁滆繛鎺ワ紒", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            NewsList[Cnt] = new TextView(this);
            NewsList[Cnt].setText(answer.get(i).title);
            NewsList[Cnt].setGravity(Gravity.LEFT);
            NewsList[Cnt].getPaint().setFakeBoldText(true);
            NewsList[Cnt].setTextSize(20);
            if (history_record.contains(NID))
                NewsList[Cnt].setTextColor(RTextG);
            else NewsList[Cnt].setTextColor(TextG);
            NewsBox[Tot].addView(NewsList[Cnt++]);

            NewsList[Cnt] = new TextView(this);
            NewsList[Cnt].setText(answer.get(i).intro);
            NewsList[Cnt].setGravity(Gravity.LEFT);
            NewsList[Cnt].getPaint().setFakeBoldText(false);
            NewsList[Cnt].setTextSize(16);
            NewsList[Cnt].setPadding(0, 5, 0, 10);
            NewsList[Cnt].setTextColor(TextG);
            NewsBox[Tot].addView(NewsList[Cnt++]);

            NewsZone.addView(NewsBox[Tot]);

            InShadow[Tot] = new TextView(this);
            InShadow[Tot].setText("");
            InShadow[Tot].setTextSize(10);
            InShadow[Tot].setEnabled(false);
            NewsZone.addView(InShadow[Tot++]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Fresco.initialize(this);

        Intent intent = new Intent(MainPage.this, NewsService.class);
        bindService(intent, conn, Service.BIND_AUTO_CREATE);

        ImageButton ClearMemory = (ImageButton) findViewById(R.id.ClearMemory);
        ClearMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                history_record = new HashSet<String>();
                //NewsDeleteRequest delete_request = new NewsDeleteRequest("",DatabaseHelper.HISTORY);
                //binder.local_news(delete_request);
                NewsClearRequest clear_request = new NewsClearRequest(DatabaseHelper.HISTORY);
                Toast.makeText(MainPage.this, "宸叉竻绌虹紦瀛?, Toast.LENGTH_SHORT).show();
                binder.local_news(clear_request);
            }
        });


        EditText SearchInfo = (EditText) findViewById(R.id.SearchText);
        SearchInfo.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                SearchWord = s.toString();
            }
        });
        ImageButton SearchButton = (ImageButton) findViewById(R.id.SearchButton);
        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextTemp(SearchWord);
                NewsSearchRequest request = new NewsSearchRequest(SearchWord);
                NewsSearchRespond respond= binder.search_news(request);
                Vector<NewsDigest> answer = respond.get_news();
                ShowNews(answer);
            }
        });

        LinearLayout SL = (LinearLayout) findViewById(R.id.ScrollLayout);
        SetHashMap();

        NewsScroll = (PullToRefreshScrollView) findViewById(R.id.NewsScroll);
        NewsScroll.setMode(PullToRefreshBase.Mode.BOTH);
        NewsScroll.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                EndRefresh();
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                TextTemp("Refreshing 2...");
                if (NowTag > -1) {
                    NowPage = NowPage + 1;
                    NewsRequest request = new NewsRequest(NowPage, NowTag);
                    NewsRespond respond = binder.get_news_digest(request);
                    Vector<NewsDigest> answer = respond.get_news();
                    AddNews(answer);
                }
                EndRefresh();
            }
        });

        int taghash = 4095;
        SharedPreferences preferences = getSharedPreferences("TagsInfo", Context.MODE_PRIVATE);
        taghash = preferences.getInt("Tags", 4095);
        TextTemp(taghash + "SS");
        for (int i = 1; i < 13; ++i) {
            TagShow[i] = ((taghash % 2) == 1 ? true : false);
            taghash = taghash / 2;
        }
        for (int i = -2; i < 13; ++i) {
            final int Num = i;
            TagList[i + 2] = new Button(this);
            TagList[i + 2].setText(ItS.get(i));
            TagList[i + 2].setVisibility(View.VISIBLE);
            if ((i > 0) && (!TagShow[i]))
                TagList[i + 2].setVisibility(View.GONE);
            TagList[i + 2].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Num > -1){
                        NowPage = 1; NowTag = Num;
                        NewsRequest request = new NewsRequest(1, Num);
                        NewsRespond respond = binder.get_news_digest(request);
                        Vector<NewsDigest> answer = respond.get_news();
                        ShowNews(answer);
                    }else{
                        if (Num == -2){
                            NewsTitleRequest title_request = new NewsTitleRequest(DatabaseHelper.FAVORITE);
                            NewsTitleRespond title_respond = (NewsTitleRespond)binder.local_news(title_request);
                            Vector<NewsDigest> answer = title_respond.get_answer();
                            for (int i = 0 ; i < answer.size() ; ++i)
                                System.out.println(answer.get(i).title);
                            System.out.println(favorite_record.size());
                            ShowNews(answer);
                        }
                        else {
                            NewsRecommendRequest request = new NewsRecommendRequest();
                            NewsRecommendRespond respond = binder.news_recommand(request);
                            Vector<NewsDigest> answer = respond.get_answer();
                            ShowNews(answer);
                        }
                    }
                    NewsScroll.scrollTo(0, 0);
                }

            });
            SL.addView(TagList[i + 2]);
        }

        ImageButton TagManager = (ImageButton) findViewById(R.id.TagManager);
        TagManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextTemp("Tag manager is on!!");
                Intent intent = new Intent();
                intent.setClass(MainPage.this, TagPage.class);
                intent.putExtra("Tags", TagShow);     //Change it later
                intent.putExtra("Night", (Daytime? 0 : 1));
                startActivity(intent);
                onPause();
            }
        });
        final LinearLayout LeftDrawer = (LinearLayout) findViewById(R.id.LeftDrawer);
        final ImageButton NightMode = (ImageButton) findViewById(R.id.NightMode);
        NightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Daytime = !Daytime;
                if (Daytime) {
                    NightMode.setImageDrawable(getResources().getDrawable(R.drawable.nightmode_on));
                    LeftDrawer.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }else{
                    NightMode.setImageDrawable(getResources().getDrawable(R.drawable.nightmode_off));
                    LeftDrawer.setBackgroundColor(Color.parseColor("#303030"));
                }
                SetDayNight();
            }
        });
        final ImageButton OfflineMode = (ImageButton) findViewById(R.id.OfflineMode);
        OfflineMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoOn = !PhotoOn;
                if (PhotoOn)
                    OfflineMode.setImageDrawable(getResources().getDrawable(R.drawable.nophotomode_on));
                else
                    OfflineMode.setImageDrawable(getResources().getDrawable(R.drawable.nophonomode_off));
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("TagsInfo", Context.MODE_PRIVATE);
        int taghash = preferences.getInt("Tags", 4095);
        for (int i = 1; i < 13; ++i) {
            TagShow[i] = ((taghash % 2) == 1 ? true : false);
            taghash = taghash / 2;
        }
        for (int i = 1 ; i < 13; ++i)
            TagList[i + 2].setVisibility((TagShow[i] ? View.VISIBLE : View.GONE));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Result){
        int OldF = Result.getIntExtra("OldF", 0);
        int NewF = Result.getIntExtra("NewF", 0);
        String NID = "";
        NID = Result.getStringExtra("IDDDD");
        if (OldF + NewF == 1){
            if (OldF == 0){
                favorite_record.remove(NID);
                TextTemp("Old King is Dead");

                NewsDeleteRequest NDR = new NewsDeleteRequest(NID, DatabaseHelper.FAVORITE);
                binder.local_news(NDR);
            }
            else{
                favorite_record.add(NID);
                TextTemp("Long live the king");

                NewsInsertByid NIR = new NewsInsertByid(DatabaseHelper.FAVORITE, NID);
                binder.local_news(NIR);
            }
        }
    }
}
