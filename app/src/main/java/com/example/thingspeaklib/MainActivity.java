package com.example.thingspeaklib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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

import com.example.thingspeaklib.tsChannelExtra.Channel;
import com.example.thingspeaklib.tsChannelExtra.tsChannelList;
import com.google.gson.Gson;
import com.macroyau.thingspeakandroid.ThingSpeakChannel;
import com.macroyau.thingspeakandroid.ThingSpeakLineChart;
import com.macroyau.thingspeakandroid.model.Feed;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
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

    private List<Channel> channelList;
    private tsChannelList channel;
    private ThingSpeakChannel tsChannel;
    private ThingSpeakLineChart tsChart;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MyListAdapter myListAdapter;
    private static final int ACTIVITY_REPORT = 1000;
    private static final String FILE_NAME = "device.json";
    int time_set = 1;
    int water_set = 0; //default "%"
    private NotificationManagerCompat notificationManager;
    public static final String CHANNEL_1_ID = "channel1";

    LinkedList<HashMap<String, String>> fieldList; // recycleview 传送资料

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = NotificationManagerCompat.from(this);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = findViewById(R.id.recyclerViewItem);


        listener();
        load_data();
        reflesh_timer();
    }





    private void listener(){
        ImageView imageAddMain = findViewById(R.id.imageAdd);
        imageAddMain.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, add_menu.class);
            startActivityForResult(intent, ACTIVITY_REPORT);
        });

        ImageView imageListMain = findViewById(R.id.imageList);
        imageListMain.setOnClickListener(v -> {
            if(channelList.size() == 0) {
                addDeviceHintShow();
                return;
            }
            Intent intent2 = new Intent(MainActivity.this, list_menu.class);
            startActivity(intent2);
        });
    }







    private void load_feed() {
        fieldList = new LinkedList<>();
        for (int f = 0; f < channelList.size(); f++) {
            tsChannel = new ThingSpeakChannel(channelList.get(f).Id);
            String Api = channelList.get(f).Api;
            int tsDepth = channelList.get(f).MaxDepth;
            tsChannel.loadChannelFeed();
            tsChannel.setChannelFeedUpdateListener((channelId, fieldId, channelFeed) -> {        // Set listener for Channel feed update events
                List<Feed> data = channelFeed.getFeeds();       // Catch Feed // Get Last Entry Data from Feed

                for (int i = 1; i < 9; i++) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    if (i % 2 != 0 && data.get(data.size() - 1).getField(i) != null) {
                        hashMap.put("Field", String.valueOf(data.get(data.size() - 1).getField(i)));
                        hashMap.put("Field_id", String.valueOf(channelId));
                        hashMap.put("Depth", String.valueOf(tsDepth));
                        hashMap.put("Battery", String.valueOf(data.get(data.size() - 1).getField(i + 1)));
                        fieldList.add(hashMap);
                    }
                  }alm_check();
                recyclerView.setLayoutManager(new LinearLayoutManager(this));     // 控制recyclerView
                myListAdapter = new MyListAdapter(fieldList, tsChart);                  // 控制recyclerView
                recyclerView.setAdapter(myListAdapter);                           // 控制recyclerView
            });
        }
        if(channelList.size() > 0){}

        else {
            recyclerView.setAdapter(null);                                     // 控制recyclerView, 清除內容
            addDeviceHintShow();
        }
        swipeRefreshLayout.setRefreshing(false);// 隐藏刷新圈

    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {     //画面回到来的时候就进行提取
        super.onActivityResult(requestCode, resultCode, data);
        String text = "";
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTIVITY_REPORT) {
                Bundle bundle = data.getExtras();
                //String field_id = bundle.getString("Field_id");
               // String api_key = bundle.getString("Api_key");
                Boolean water_sw = bundle.getBoolean("Water_sw");
                Integer count_time = bundle.getInt("Time_set");
                Integer water_alm = bundle.getInt("water_alm");

                if (count_time != time_set && count_time > 0) {
                    time_set = count_time;
                    text += "时间更新为 " + time_set +"分钟" ;
                }
                if (water_sw == true){
                    if (water_set != water_alm){
                        water_set = water_alm ;
                        if (text != "") text += "\n";
                        text += "水量设定值为 " + water_set + "%";
                }}
                if (text != "")
                Toast.makeText(MainActivity.this, text , Toast.LENGTH_LONG ).show();
            }
        }
        onRefresh();
    }







    private void alm_check(){  //预警通知
        NotificationChannel Channel1 = new NotificationChannel(CHANNEL_1_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = getSystemService(NotificationManager.class);

        for (int i = 0 ; i < fieldList.size(); i++){
            int current_depth = Integer.parseInt(fieldList.get(i).get("Field"));
            int depth = Integer.parseInt(fieldList.get(i).get("Depth"));
            int water = (((depth - current_depth)*100)/depth);

            if ( water_set >= water){
                manager.createNotificationChannel(Channel1);
                Notification notification = new NotificationCompat.Builder(this,CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.ic_add)
                        .setContentTitle("水塔編號  "+fieldList.get(i).get("Field_id"))
                        .setContentText("水量低于设定值" + water_set + "%" + "\n" +"目前水量为 " + water + "%"+" 请注意用水量")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();
                notificationManager.notify(i, notification);
            }
        }
    }





    @Override
    public void onRefresh() {
        load_data();

    }



    public void reflesh_timer() {
        
        refreash_prog(60000);

    }



    private void refreash_prog(int milliseconds) {      //背景执行时间计算
        final Handler handler = new Handler(); // 助手
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {     //要跑的程序
                reflesh_timer();
                if(channelList != null)
                load_feed();
            }
        };
       // if (time_set != milliseconds)handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, milliseconds*time_set);
    }






    private void addDeviceHintShow() {
        // 找不到檔案，顯示提示
        Toast.makeText(this,
                "請點選下方按鈕新增裝置!", Toast.LENGTH_LONG).show();
    }






    public void load_data() {
        //  https://thumbb13555.pixnet.net/blog/post/327006144-thread%E3%80%81handler%EF%BC%86asynctask
        new Thread(() -> {
            // TODO 檔案處理 讀取設定檔
            try {
                FileInputStream fin = openFileInput(FILE_NAME);
                BufferedInputStream buffin = new BufferedInputStream(fin);
                String bufftmp = new String();
                do {
                    byte[] buffbyte = new byte[1];
                    if(buffin.read(buffbyte) == -1)
                        break;
                    else
                        bufftmp += new String(buffbyte);  // 將buffer存至byte array
                } while(true);
                buffin.close();

                if(bufftmp.isEmpty())
                    // 找不到檔案，顯示提示
                    addDeviceHintShow();
                else {
                    // 轉換Json 至 List
                    Gson gson = new Gson();
                    channel = gson.fromJson(bufftmp, tsChannelList.class);
                    channelList = channel.GetChannelList();
                    runOnUiThread(() -> {
                        load_feed();
                    });
                }

            } catch (FileNotFoundException e) {
                // 創立新檔案
                /*try {
                    FileOutputStream fout = openFileOutput(FILE_NAME, MODE_PRIVATE);
                    BufferedOutputStream buffout = new BufferedOutputStream(fout);
                    buffout.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }*/
                // 找不到檔案，顯示提示
                runOnUiThread(() -> {
                    addDeviceHintShow();
                });
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    
}
