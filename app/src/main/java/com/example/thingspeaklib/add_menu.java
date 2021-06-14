package com.example.thingspeaklib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class add_menu extends AppCompatActivity {
    private EditText deviceId, deviceApi, maxDepth;  //fieldnum 可能会换选单模式
    private int field_id, time_set, water_alm;
    private Boolean water_sw = false;
    private String api_key;
    private List<Channel> channelList;
    private tsChannelList channel;
    private static final String FILE_NAME = "device.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_menu);

        deviceId = (EditText) findViewById(R.id.fieldid);
        deviceApi = (EditText) findViewById(R.id.edtApiKey);
        maxDepth = (EditText) findViewById(R.id.edtTowerHeight);


        load_data();
        click_listener();
        seekbar_get();
        spinner_loop();
        switch_listener();

    }





    private void switch_listener(){
        RelativeLayout waterset = findViewById(R.id.waterset);
        waterset.setVisibility(View.GONE);
        Switch watersw = findViewById(R.id.watersw);
        water_sw = false;
        watersw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    waterset.setVisibility(View.VISIBLE);
                    water_sw = true;
                } else waterset.setVisibility(View.GONE);
            }
        });
    }




    private void click_listener(){
        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());

        ImageView imageSave = findViewById(R.id.imageDone);
        imageSave.setOnClickListener(v -> {

            String Warning = new String();
            // 欄位強制輸入檢查
            if(maxDepth.getText().toString().isEmpty() == false && deviceId.getText().toString().isEmpty() == false)save_data();
            else if (deviceId.getText().toString().isEmpty() == false ) {
                if (maxDepth.getText().toString().isEmpty())
                    Warning += "最高水位"; }
            else if (maxDepth.getText().toString().isEmpty() == false) {
                if (deviceId.getText().toString().isEmpty())
                    Warning += "裝置ID"; }

            if (Warning.isEmpty() != true) {
                Toast.makeText(this, "請填入" + Warning, Toast.LENGTH_SHORT).show();
            } else {

                    Intent intent = new Intent();
                    // intent.setClass(add_menu.this, MainActivity.class);

                    Bundle bundle = new Bundle();
                    //  bundle.putString("Field_id",fieldid.getText().toString());
                    //  bundle.putString("Api_key",edtApiKey.getText().toString());
                    bundle.putBoolean("Water_sw", water_sw);
                    bundle.putInt("Time_set", time_set);
                    bundle.putInt("water_alm", water_alm);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    add_menu.this.finish();
                    //Toast.makeText(this, Warning, Toast.LENGTH_LONG).show();

            }
        });
    }





    private void spinner_loop(){
        Spinner spinner2 = findViewById(R.id.chktime);   //spinner2
        final Integer[] time_sec = new Integer[]{0,1, 3, 5, 10, 15, 20,25};
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter(add_menu.this,
                android.R.layout.simple_dropdown_item_1line,time_sec);
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setSelection(0, false);
        spinner2.setBackgroundColor(Color.parseColor("#434343"));

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                time_set = time_sec[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                time_set = 0;
            }
        });
    }



    private void seekbar_get() {

        TextView showValue = findViewById(R.id.Maxwater);
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                water_alm = progress / 2;
                showValue.setText("" + water_alm + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }







    public void save_data() {
        // TODO 檔案處理 寫入設定檔
        // 檢查重複資訊
        for (Channel channel : channelList) {
            if (channel.Id == Integer.parseInt(deviceId.getText().toString())) {
                // 顯示警告
                Toast.makeText(this, "記錄檔內已有重複的資訊，請重新輸入。", Toast.LENGTH_SHORT).show();
               // return false;   // 回傳 false
            }
        }

        try {
            int id = Integer.parseInt(deviceId.getText().toString());
            int depth = Integer.parseInt(maxDepth.getText().toString());
            String api = deviceApi.getText().toString();

            channel.AddChannel(id, api, depth);

            FileOutputStream fout = openFileOutput(FILE_NAME, MODE_PRIVATE);
            BufferedOutputStream buffout = new BufferedOutputStream(fout);

            Gson gson = new Gson();
            String str = gson.toJson(channel);

            buffout.write(str.getBytes());
            buffout.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
       // return true;   // 回傳true
    }







    private void load_data(){
        new Thread(() -> {
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
                Gson gson = new Gson();
                channel = gson.fromJson(bufftmp, tsChannelList.class);
                channelList = channel.GetChannelList();
            }
        }).start();
    }





}