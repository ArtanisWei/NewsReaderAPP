package com.example.asus.first_ui;


import android.graphics.Color;
import android.support.design.internal.BaselineLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.content.*;
import java.util.*;

public class TagPage extends AppCompatActivity {

    Switch[] SwitchList = new Switch[20];
    boolean[] IsShowed = new boolean[20];
    HashMap<String, Integer> StI = new HashMap<String, Integer>();
    HashMap<Integer, String> ItS = new HashMap<Integer, String>();
    public void SetHashMap(){
        StI.put("最新", 0); StI.put("推荐",-1); StI.put("收藏",-2);
        StI.put("科技", 1); StI.put("教育", 2); StI.put("军事", 3);
        StI.put("国内", 4); StI.put("社会", 5); StI.put("文化", 6);
        StI.put("汽车", 7); StI.put("国际", 8); StI.put("体育", 9);
        StI.put("财经",10); StI.put("健康",11); StI.put("娱乐",12);

        ItS.put(0 ,"最新"); ItS.put(-1,"推荐"); ItS.put(-2,"收藏");
        ItS.put(1 ,"科技"); ItS.put(2 ,"教育"); ItS.put(3 ,"军事");
        ItS.put(4 ,"国内"); ItS.put(5 ,"社会"); ItS.put(6 ,"文化");
        ItS.put(7 ,"汽车"); ItS.put(8 ,"国际"); ItS.put(9 ,"体育");
        ItS.put(10,"财经"); ItS.put(11,"健康"); ItS.put(12,"娱乐");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_page);

        SetHashMap();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        IsShowed = bundle.getBooleanArray("Tags");
        int Time = bundle.getInt("Night");
        int BackGroundC;
        int TextC;
        if (Time == 0){
            BackGroundC = Color.parseColor("#FFFAFA");
            TextC = Color.parseColor("#000000");
        }else{
            BackGroundC = Color.parseColor("#303030");
            TextC = Color.parseColor("#C4C3CB");
        }
        LinearLayout TM = (LinearLayout) findViewById(R.id.TagManage);
        //LinearLayout TMain = (LinearLayout) findViewById(R.id.TagMain);
        //TMain.setBackgroundColor(BackGroundC);
        BaselineLayout BLL = (BaselineLayout) findViewById(R.id.BLL);
        BLL.setBackgroundColor(BackGroundC);
        for (int i = 1 ; i < 13 ; ++i){
            final int Number = i;
            SwitchList[i] = new Switch(this);
            SwitchList[i].setText(ItS.get(i));
            SwitchList[i].setTextColor(TextC);
            SwitchList[i].setChecked(IsShowed[i]);
            SwitchList[i].setTextSize(20);
            SwitchList[i].setPadding(50, 15, 20, 15);         //L, T, R, B
            SwitchList[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    IsShowed[Number] = isChecked;
                }
            });
            TM.addView(SwitchList[i]);
        }

        ImageButton BackButton = (ImageButton) findViewById(R.id.BackButton);
        BackButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SharedPreferences preferences = getSharedPreferences("TagsInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                int hasher = 0;
                for (int i = 12 ; (i > 0) ; --i)
                    hasher = hasher * 2 + (IsShowed[i] ? 1 : 0);
                editor.putInt("Tags", hasher);
                editor.commit();
                finish();
            }
        });
    }
}
