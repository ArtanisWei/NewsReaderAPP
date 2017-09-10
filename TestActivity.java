package com.example.victor.todaynews;

import android.app.Service;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.*;
import android.view.*;

import android.content.*;
import android.content.DialogInterface.*;

public class TestActivity extends AppCompatActivity {
    SQLiteDatabase db;
    Button start,contents,category,stop;
    //ScrapyService.MyBinder binder;
    NewsService.NewsBind binder;

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            System.out.println("---$$$$$$$$$connected$$$$$$$$$$$$$$---");

            binder = (NewsService.NewsBind) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            System.out.println("---disconnected---");
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

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //NewsRequest request = new NewsRequest(5);
                //Bundle bul = new Bundle();
                //System.out.println("start!");
                //bul.putSerializable("request", request);
                //bul.putString("content","start here");
                Intent intent = new Intent(TestActivity.this, NewsService.class);
                //intent.putExtras(bul);
                NewsRequest request = new NewsRequest(10);
                Bundle bul = new Bundle();
                bul.putSerializable("request",request);
                intent.putExtras(bul);

                bindService(intent, conn, Service.BIND_AUTO_CREATE);

            }
        });

        contents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("content!");
                Bundle bul = new Bundle();
                NewsRequest request = new NewsRequest(10);
                bul.putSerializable("request",request);

                Intent intent = new Intent(TestActivity.this, NewsService.class);
                intent.putExtras(bul);

                startService(intent);


                //System.out.println("in contents content is : " + binder.get_content());

                System.out.println("content!");
                System.out.println("now at " + binder.get_page());
                //System.out.println("now_url " + binder.get_url());
                int total = binder.get_number();
                if (total == 0) Toast.makeText(TestActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                else{
                  for (int i = 0; i < total; i++) {
                    System.out.println(binder.get_news().get(i));
                  }
                }

            }
        });
        category.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View view){
                System.out.println("category!");

                Bundle bul = new Bundle();
                NewsRequest request = new NewsRequest(10,"科技");
                bul.putSerializable("request",request);

                Intent intent = new Intent(TestActivity.this, NewsService.class);
                intent.putExtras(bul);

                startService(intent);
                System.out.println("now at " + binder.get_page());
              //System.out.println("now_url " + binder.get_url());
                int total = binder.get_number();
                if (total == 0) Toast.makeText(TestActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                else{
                      for (int i = 0; i < total; i++) {
                          System.out.println(binder.get_news().get(i));
                          System.out.println(binder.get_news().get(i).type);
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


