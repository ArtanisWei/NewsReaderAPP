package com.example.victor.todaynews;

import android.app.Service;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.TestLooperManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.*;
import android.view.*;

import android.content.*;
import android.content.DialogInterface.*;
import java.util.*;

public class TestActivity extends AppCompatActivity {
    SQLiteDatabase db;
    Button start,contents,category,search, stop;

    NewsService.NewsBind news_binder;
    ScrapyService.MyBinder test_binder;

    ServiceConnection conn_news = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            System.out.println("---$$$$$$$$$connected$$$$$$$$$$$$$$---");

            news_binder = (NewsService.NewsBind) iBinder;


            System.out.println("in ServiceConnection bind finish");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            System.out.println("---disconnected---");
        }
    };

    ServiceConnection conn_test = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            test_binder = (ScrapyService.MyBinder) iBinder;
            System.out.println(test_binder.get_content("connect"));
            //System.out.println("in ServiceConnection bind finish");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            System.out.println("---disconnected---");
        }
    };
    ServiceConnection conn_search = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //search_binder = (NewsSearchService.NewsSearchBind)iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // final Intent intent = new Intent(this, ScrapyService.class);
        setContentView(R.layout.activity_test);

        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);

        contents = (Button) findViewById(R.id.contents);
        category = (Button) findViewById(R.id.category);
        search = (Button) findViewById(R.id.search);

        Intent _intent = new Intent(this, NewsService.class);
        //_intent.putExtra("content","test");
        //_intent.putExtras(new Bundle().putSerializable("request", new NewsRequest(20)))

        bindService(_intent, conn_news, Service.BIND_AUTO_CREATE);
       // System.out.println("bind finish");

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(TestActivity.this, ScrapyService.class);
                //intent.putExtra("content","start");
                //startService(intent);
                System.out.println(test_binder.get_content("start"));
            }
        });

        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Intent intent = new Intent(TestActivity.this, ScrapyService.class);
                //intent.putExtra("content","search");
                //startService(intent);
                System.out.println(test_binder.get_content("content"));
            }
        });

        contents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("content!");
                Bundle bul = new Bundle();
                NewsRequest request = new NewsRequest(10);

                System.out.println("now at " + news_binder.get_page());
                //System.out.println("now_url " + binder.get_url());
                Vector<NewsDigest> answer = news_binder.get_news_digest(request).get_news();
                System.out.println("now at " + news_binder.get_page());

                int total = answer.size();
                if (total == 0) Toast.makeText(TestActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                else{
                  for (int i = 0; i < total; i++) {
                    System.out.println(answer.get(i));
                  }
                }

            }
        });
        category.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View view){
              System.out.println("content!");
              Bundle bul = new Bundle();
              NewsRequest request = new NewsRequest(10,1);

              System.out.println("now at " + news_binder.get_page());
              //System.out.println("now_url " + binder.get_url());
              Vector<NewsDigest> answer = news_binder.get_news_digest(request).get_news();
              System.out.println("now at " + news_binder.get_page());

              int total = answer.size();
              if (total == 0) Toast.makeText(TestActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
              else{
                  for (int i = 0; i < total; i++) {
                      System.out.println(answer.get(i));
                  }
              }
          }
        });

        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                System.out.println("stop!");
                Intent intent = new Intent(TestActivity.this, NewsService.class);
                stopService(intent);

            }

        });

    }
    @Override
    protected void onDestroy(){
        Intent intent = new Intent(this, NewsService.class);
        stopService(intent);
        super.onDestroy();
    }
}


