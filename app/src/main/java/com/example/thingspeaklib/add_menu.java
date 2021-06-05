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

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class add_menu extends AppCompatActivity {
    private EditText deviceId,deviceApi,maxDepth;  //fieldnum 可能会换选单模式
    private RelativeLayout waterset;
    private SeekBar sb_waterlv;
    private int field_id ,water_lv,time_set,water_alm ;
    private String api_key;
   // private static final String FILE_NAME = "example.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_menu);

        deviceId =(EditText)findViewById(R.id.fieldid);
        deviceApi = (EditText)findViewById(R.id.edtApiKey);
        maxDepth = (EditText)findViewById(R.id.edtTowerHeight);


        waterset=(RelativeLayout)findViewById(R.id.waterset);
        waterset.setVisibility(View.GONE);

        Switch watersw = (Switch) findViewById(R.id.watersw);
        watersw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    waterset.setVisibility(View.VISIBLE);
                }else waterset.setVisibility(View.GONE);
            }
        });


        Spinner spinner2 = (Spinner) findViewById(R.id.chktime);   //spinner2
        ArrayAdapter<CharSequence>adapter2 = ArrayAdapter.createFromResource(this,
                R.array.chktimenum,
                android.R.layout.simple_spinner_dropdown_item);
       // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setSelection(0,false);
        spinner2.setBackgroundColor(Color.parseColor("#434343"));
        spinner2.setOnItemSelectedListener(spnOnItemSelected2);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageView imageSave = findViewById(R.id.imageDone);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (deviceId.getText().toString().isEmpty() == false && maxDepth.getText().toString().isEmpty() == false)
                    save_data();
                if (deviceId.getText().toString().isEmpty() == false && maxDepth.getText().toString().isEmpty() == true) {
                    Toast.makeText(add_menu.this, "请填入水位高度", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent();
                    // intent.setClass(add_menu.this, MainActivity.class);

                    Bundle bundle = new Bundle();
                    //  bundle.putString("Field_id",fieldid.getText().toString());
                    // bundle.putString("Api_key",edtApiKey.getText().toString());
                    bundle.putInt("Water_lv", water_lv);
                    bundle.putInt("Time_set", time_set);
                    bundle.putInt("water_alm", water_alm);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    add_menu.this.finish();
                }
            }

        });
    }



    private AdapterView.OnItemSelectedListener spnOnItemSelected2 = new AdapterView.OnItemSelectedListener() {   //spinner2
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //选好的时候
            Integer[] time_sec = new Integer[]{1,3,5,10,15,20 };
            time_set = time_sec[position];
            //spinner2.setSelection(0,false);
            //spnfield =  parent.getSelectedItem().toString();

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //没选的时候
            time_set =60000;
        }
    };
   /* private void bindViews() {   //伸缩条设定
        sb_waterlv = (SeekBar) findViewById(R.id.seekBar);
       // txt_cur = (TextView) findViewById(R.id.txt_cur);
        sb_normal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txt_cur.setText("当前进度值:" + progress + "  / 100 ");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(mContext, "触碰SeekBar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(mContext, "放开SeekBar", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    public void save_data(){
        // TODO 檔案處理 寫入設定檔
        try {
            FileOutputStream fout = openFileOutput("device.txt", MODE_APPEND);
            BufferedOutputStream buffout = new BufferedOutputStream(fout);
            String device_id = deviceId.getText().toString();
//                String channel_api = deviceApi.getText().toString();
            String max_depth = maxDepth.getText().toString();
//                String str = device_id + ',' + channel_api + ',' + max_depth + '\n';
            String str = device_id + ',' + max_depth + '\n';
            buffout.write(str.getBytes());
            buffout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}