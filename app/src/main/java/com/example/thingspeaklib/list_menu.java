package com.example.thingspeaklib;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thingspeaklib.tsChannelExtra.Channel;
import com.example.thingspeaklib.tsChannelExtra.tsChannelList;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class list_menu extends AppCompatActivity {

    private ListView listDevice;
    private List<Channel> channelList;
    private tsChannelList channel;
    private static final String FILE_NAME = "device.json";
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_menu);


        String bufftmp = "";
        byte[] buffbyte = new byte[1];
        // TODO 檔案讀取
        try {
            FileInputStream fin = openFileInput(FILE_NAME);
            BufferedInputStream buffin = new BufferedInputStream(fin);
            do {
                int flag = buffin.read(buffbyte);
                if (flag == -1)
                    break;
                else
                    bufftmp += new String(buffbyte);  // 將buffer存至byte array
            } while (true);

            buffin.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // 判斷是否為新檔案
        if (bufftmp.length() == 0) {
            channelList = new ArrayList<Channel>();
            channel = new tsChannelList();
        } else {
            gson = new Gson();
            channel = gson.fromJson(bufftmp, tsChannelList.class);
            channelList = channel.GetChannelList();
        }

        listDevice = (ListView)findViewById(R.id.ListDevice);

        List<String> device = new ArrayList<String>() {{
            for (Channel channels : channelList)
                add("裝置號碼：" + channels.Id);
        }};

        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, device);

        listDevice.setAdapter(deviceAdapter);

        listDevice.setOnItemClickListener(llClick);

        // 顯示操作訊息
        Toast.makeText(list_menu.this,
                "請選擇你要刪除的裝置!", Toast.LENGTH_SHORT).show();

    }

    private ListView.OnItemClickListener llClick = (parent, view, position, id) -> {
        new AlertDialog.Builder(this)
                .setTitle("訊息")
                .setMessage("請問是否要刪除裝置" + channelList.get(position).Id + "?")
                .setPositiveButton("確定", (dialog, which) -> {
                    // TODO 刪除此筆資料，並且更改檔案
                    channel.RemoveChannel(channelList.get(position).Id);
                    String json = gson.toJson(channel);

                    FileOutputStream fout = null;

                    try {
                        fout = openFileOutput(FILE_NAME, MODE_PRIVATE);
                        BufferedOutputStream buffout = new BufferedOutputStream(fout);

                        buffout.write(json.getBytes());
                        buffout.close();

                        list_menu.this.finish();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    // 忽略操作, 關閉視窗
                    dialog.cancel();
                }).show();
    };
}