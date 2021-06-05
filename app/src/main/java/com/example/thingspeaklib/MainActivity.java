package com.example.thingspeaklib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import lecho.lib.hellocharts.view.LineChartView;

/*   ------ writed by chiseng --------*/
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ThingSpeakChannel tsChannel;
    private ThingSpeakLineChart tsChart;
    private LineChartView chartView;
    private TextView textviewRes, textviewId;    //item
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MyListAdapter myListAdapter;
    String Api_Key = "J27YPQ7GWCKIVB82"; // 没用了
    private static final int ACTIVITY_REPORT = 1000;
    private static final String FILE_NAME = "example.txt";
    int time_set = 1;

    LinkedList<HashMap<String, String>> fieldList; // recycleview 传送资料
    LinkedList<List<Integer>> channel_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = findViewById(R.id.recyclerViewItem);

        ImageView imageAddMain = findViewById(R.id.imageAdd);
        imageAddMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, add_menu.class);
                startActivityForResult(intent, ACTIVITY_REPORT);
            }
        });
        //reflesh_timer();
            load_data();

    }


    private void LoadFeedTest() {

        fieldList = new LinkedList<>();

        for (int f = 0; f < channel_List.size(); f++) {
            tsChannel = new ThingSpeakChannel(channel_List.get(f).get(0));
            int tsDepth = channel_List.get(f).get(1);
            tsChannel.loadChannelFeed();

            tsChannel.setChannelFeedUpdateListener((channelId, fieldId, channelFeed) -> {        // Set listener for Channel feed update events

                List<Feed> data = channelFeed.getFeeds();       // Catch Feed // Get Last Entry Data from Feed

                int n = 0;
                for (int i = 1; i < 9; i++) {

                    HashMap<String, String> hashMap = new HashMap<>();
                    if (i % 2 != 0 && data.get(data.size() - 1).getField(i) != null) {
                        hashMap.put("Field", String.valueOf(data.get(data.size() - 1).getField(i)));
                        hashMap.put("Field_id", String.valueOf(channelId));
                        hashMap.put("Depth", String.valueOf(tsDepth));
                        hashMap.put("Battery", String.valueOf(data.get(data.size() - 1).getField(i + 1)));
                        fieldList.add(hashMap);
                    }
                    //else  Toast.makeText(getApplicationContext(), String.valueOf(i), Toast.LENGTH_SHORT).show();  //尝试是否读取到其他栏位 （可以不要）
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(this));       // 控制recyclerView
                myListAdapter = new MyListAdapter(fieldList, tsChart);                      // 控制recyclerView
                recyclerView.setAdapter(myListAdapter);                                     // 控制recyclerView

            });
        }
        swipeRefreshLayout.setRefreshing(false);// 隐藏刷新圈
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ACTIVITY_REPORT) {
                Bundle bundle = data.getExtras();
                //String field_id = bundle.getString("Field_id");
               // String api_key = bundle.getString("Api_key");
                String water_lv = bundle.getString("Water_lv");
                Integer count_time = bundle.getInt("Time_set");
                String water_alm = bundle.getString("water_alm");

                /*if (field_id.isEmpty() == false) {
                    for (int i = 0; i < channel_List.size(); i++) {
                        if (Integer.valueOf(field_id) == channel_List.get(i)) {
                            return;
                        }
                    }
                    channel_List.add(Integer.valueOf(field_id));
                    //Toast.makeText(MainActivity.this, "Saved" + "", Toast.LENGTH_SHORT).show();
                }*/
                if (count_time != time_set && count_time != 0) {
                    time_set = count_time;
                    Toast.makeText(MainActivity.this, time_set + "分钟 更新完成", Toast.LENGTH_SHORT).show();
                } else if (count_time == 0){ time_set = 1;  // 不知为什么会变0
                    Toast.makeText(MainActivity.this, time_set + "分钟 更新完成", Toast.LENGTH_SHORT).show();}
            }
        }
        onRefresh();
    }


    @Override
    public void onRefresh() {
        load_data();
        // Toast.makeText(MainActivity.this,String.valueOf(time_set), Toast.LENGTH_LONG).show();
        //LoadFeedTest();
    }

    public void reflesh_timer() {
        LoadFeedTest();   //要跑的程序

        //count ++ ;
        refreash_prog(time_set * 60000); // 30 sec
    }

    private void refreash_prog(int milliseconds) {
        final Handler handler = new Handler(); // 助手

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                reflesh_timer();
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }

    public void load_data() {

        channel_List = new LinkedList<>();
        // List<Integer> data = new ArrayList<Integer>();
        // data.add(1383016);     // Xin You TowerLevel
        //  data.add(100);
        //      field_id_List.add(950541);    // Zhi Cheng
        //channel_List.add(data);

        // TODO 檔案處理 讀取設定檔
        try {
            FileInputStream fin = openFileInput("device.txt");

            BufferedInputStream buffin = new BufferedInputStream(fin);

            String bufftmp = new String();
            do {

                byte[] buffbyte = new byte[1];
                int flag = buffin.read(buffbyte);
                if(flag == -1)
                    break;
                else
                    bufftmp += new String(buffbyte);  // 獲取ID
            } while(true);

            buffin.close();

            // newline分割

            String[] buff_parameter = bufftmp.split("\n");
            for (String s : buff_parameter) {
                if(s.isEmpty()) break;
                else {
                    String[] sp = s.split(",");
                    List<Integer> data = new ArrayList<Integer>();
                    data.add(Integer.parseInt(sp[0]));   // 裝置ID
                    data.add(Integer.parseInt(sp[1]));   // 水塔深度

                    channel_List.add(data);
                }
            }
        } catch (FileNotFoundException e) {
            try {
                FileOutputStream fout = openFileOutput("device.txt", MODE_PRIVATE);
                BufferedOutputStream buffout = new BufferedOutputStream(fout);
                buffout.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            // 找不到檔案，顯示提示
            Toast.makeText(this,
                    "請點選下方按鈕新增裝置!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }LoadFeedTest();
    }
}