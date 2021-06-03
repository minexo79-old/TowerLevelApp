package com.example.thingspeaklib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.macroyau.thingspeakandroid.ThingSpeakChannel;
import com.macroyau.thingspeakandroid.ThingSpeakLineChart;
import com.macroyau.thingspeakandroid.model.Feed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import lecho.lib.hellocharts.view.LineChartView;
/*   ------ write by chiseng --------*/
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ThingSpeakChannel tsChannel;
    private ThingSpeakLineChart tsChart;
    private LineChartView chartView;
    private TextView textviewRes, textviewId;    //item
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MyListAdapter myListAdapter;
    String Api_Key = "J27YPQ7GWCKIVB82";

    int time_set = 60000;
   // int cardnum = 0;
    LinkedList<HashMap<String, String>> fieldList; // recycleview 传送资料
    LinkedList<Integer> field_id_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // LoadFeed();

        field_id_List = new LinkedList<>();
        field_id_List.add(1383016);
        field_id_List.add(950541);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = findViewById(R.id.recyclerViewItem);


        Bundle bundle = getIntent().getExtras();
       //  int cardnum = bundle.getInt("Cardnum");


       // time_set = bundle.getInt("time_set");




        ImageView imageAddMain = findViewById(R.id.imageAdd);
        imageAddMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, add_menu.class);
               // intent.putExtra("cardnum", cardnum);
                startActivityForResult(intent, 2);
            }
        });
        reflesh_timer();
      //  LoadFeedTest();

    }

    private void LoadFeedTest(){

        fieldList = new LinkedList<>();

        for(int f = 0 ; f < field_id_List.size() ; f ++){
        tsChannel = new ThingSpeakChannel(field_id_List.get(f));
        tsChannel.loadChannelFeed();

        tsChannel.setChannelFeedUpdateListener((channelId, fieldId, channelFeed) -> {        // Set listener for Channel feed update events

            List<Feed> data = channelFeed.getFeeds();       // Catch Feed // Get Last Entry Data from Feed

            int n = 0;
            for (int i = 1; i < 9; i++) {
                //  if (data.get(data.size() - 1).getField(i) != null ) {
                ;
                HashMap<String, String> hashMap = new HashMap<>();
                if(i % 2 != 0 && data.get(data.size() - 1).getField(i) != null) {
                    hashMap.put("Field", String.valueOf(data.get(data.size() - 1).getField(i)));
                    hashMap.put("Field_id", String.valueOf(channelId));
                    hashMap.put("Battery", String.valueOf(data.get(data.size() - 1).getField(i+1)));
                    fieldList.add(hashMap);
                }
                //else  Toast.makeText(getApplicationContext(), String.valueOf(i), Toast.LENGTH_SHORT).show();  //尝试是否读取到其他栏位 （可以不要）
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(this));       // 控制recyclerView
            myListAdapter = new MyListAdapter(fieldList , tsChart);                     // 控制recyclerView
            recyclerView.setAdapter(myListAdapter);                                     // 控制recyclerView
            swipeRefreshLayout.setRefreshing(false);     //隐藏刷新圈
    });}

    }


    private void LoadFeed() {

        tsChannel = new ThingSpeakChannel(field_id_List.get(1));
        tsChannel.loadChannelFeed();


        tsChannel.setChannelFeedUpdateListener((channelId, fieldId, channelFeed) -> {        // Set listener for Channel feed update events


            List<Feed> data = channelFeed.getFeeds();       // Catch Feed // Get Last Entry Data from Feed

            fieldList = new LinkedList<>();
            int n = 0;
            for (int i = 1; i < 9; i++) {
              //  if (data.get(data.size() - 1).getField(i) != null ) {
;
                    HashMap<String, String> hashMap = new HashMap<>();
                    if(i % 2 != 0 && data.get(data.size() - 1).getField(i) != null) {
                        hashMap.put("Field", String.valueOf(data.get(data.size() - 1).getField(i)));
                        hashMap.put("Field_id", String.valueOf(channelId));
                        hashMap.put("Battery", String.valueOf(data.get(data.size() - 1).getField(i+1)));
                        fieldList.add(hashMap);


            }
            //else  Toast.makeText(getApplicationContext(), String.valueOf(i), Toast.LENGTH_SHORT).show();  //尝试是否读取到其他栏位 （可以不要）

            }
            recyclerView.setLayoutManager(new LinearLayoutManager(this));       // 控制recyclerView
            myListAdapter = new MyListAdapter(fieldList , tsChart);                     // 控制recyclerView
            recyclerView.setAdapter(myListAdapter);                                     // 控制recyclerView
            swipeRefreshLayout.setRefreshing(false);     //隐藏刷新圈
        });

    }



    @Override
    public void onRefresh() {
        //LoadFeed();
        LoadFeedTest();
    }

    public void reflesh_timer(){
        LoadFeedTest();   //要跑的程序
        Toast.makeText(MainActivity.this, String.valueOf(time_set), Toast.LENGTH_LONG).show();
        //count ++ ;
        refreash_prog(time_set); // 30 sec
    }

    private void refreash_prog(int milliseconds){
        final Handler handler = new Handler(); // 助手

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                reflesh_timer();
            }
        };
        handler.postDelayed(runnable,milliseconds);
    }

}