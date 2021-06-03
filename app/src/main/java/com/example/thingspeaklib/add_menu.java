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

public class add_menu extends AppCompatActivity {
    private EditText fieldid,edtApiKey;  //fieldnum 可能会换选单模式
    private RelativeLayout waterset;
    private SeekBar sb_waterlv;
    int cardnum ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_menu);
        Intent intent = getIntent();
       cardnum = intent.getIntExtra("cardnum",cardnum);

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

     /*   Spinner spinner = (Spinner) findViewById(R.id.spfilednum);   //spinner 1   //没用到
        ArrayAdapter<CharSequence>adapter = ArrayAdapter.createFromResource(this,
                R.array.fieldnum,
                android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(1,false);
        spinner.setBackgroundColor(Color.parseColor("#434343"));
        spinner.setOnItemSelectedListener(spnOnItemSelected);*/

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

                Intent intent = new Intent();
                intent.setClass(add_menu.this, MainActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt("Cardnum",cardnum);
                intent.putExtras(bundle);
                startActivity(intent);
                //onBackPressed();
                // finish();
            }
        });
    }


   /* private AdapterView.OnItemSelectedListener spnOnItemSelected = new AdapterView.OnItemSelectedListener() {  //spinner1  占时没用
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //选好的时候
            int spnfield = position;
            String spnselected = parent.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
                //没选的时候
        }
    };*/

    private AdapterView.OnItemSelectedListener spnOnItemSelected2 = new AdapterView.OnItemSelectedListener() {   //spinner2
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //选好的时候
            int spnfield = position;
            String spnselected = parent.getSelectedItem().toString();
            Intent intent = new Intent();
            intent.setClass(add_menu.this, MainActivity.class);
            intent.putExtra("time_set",spnfield*10000);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //没选的时候
        }
    };
    /*private void bindViews() {   //伸缩条设定
        sb_normal = (SeekBar) findViewById(R.id.sb_normal);
        txt_cur = (TextView) findViewById(R.id.txt_cur);
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
}