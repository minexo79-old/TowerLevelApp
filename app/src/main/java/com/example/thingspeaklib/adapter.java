package com.example.thingspeaklib;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.macroyau.thingspeakandroid.ThingSpeakLineChart;

import java.sql.DriverPropertyInfo;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

import lecho.lib.hellocharts.view.LineChartView;

import static android.content.ContentValues.TAG;

class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {

    LinkedList<HashMap<String, String>> fieldList;
    private ThingSpeakLineChart tsChart;
    public LineChartView chartView;
    int last_id = 0, count;
    int last_check = 0;
    ColorDrawable colorget;

    //帶入ArrayList作為資料
    public MyListAdapter(LinkedList<HashMap<String, String>> fieldList, ThingSpeakLineChart tsChart) {
        this.fieldList = fieldList;
        this.tsChart = tsChart;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { //(background colour) to do something

        holder.textviewId.setText("水塔編號   " + fieldList.get(position).get("Field_id"));
        holder.textviewRes.setText("目前水位：" + fieldList.get(position).get("Field"));
        holder.progressbar.setProgress(Integer.valueOf(fieldList.get(position).get("Field")),true);    //set max = 180
        holder.textvolume.setText((String.format("%.1f",(Integer.valueOf(fieldList.get(position).get("Field")))*0.555))+"%");   //max value = 100/180

        if (String.valueOf(fieldList.get(position).get("Battery")) == "null") {
            holder.textviewbty.setText((String.valueOf(fieldList.get(position).get("Battery"))));
            // holder.battery_img.setBackgroundResource(R.drawable.ic_unknown);
        } else
            holder.textviewbty.setText((String.format("%.1f", (Double.valueOf(fieldList.get(position).get("Battery")) - 3) * 83.3)) + "% ");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);


        holder.chartView.setZoomEnabled(false);
        holder.chartView.setValueSelectionEnabled(true);


        if (position != 0 && Integer.valueOf(fieldList.get(position).get("Field_id")) == last_check) {
            last_check = Integer.valueOf(fieldList.get(position).get("Field_id"));
            last_id = last_id + 2;
            holder.background_color.setBackground(colorget);
        } else if (position == 0) {
            last_check = Integer.valueOf(fieldList.get(position).get("Field_id"));
            last_id = 1;
            colorget = set_color.getRandomDrawbleColor();
            holder.background_color.setBackground(colorget);
        } else if (last_check != Integer.valueOf(fieldList.get(position).get("Field_id"))) {
            last_check = Integer.valueOf(fieldList.get(position).get("Field_id"));
            last_id = 1;
            colorget = set_color.getRandomDrawbleColor();
            holder.background_color.setBackground(colorget);
        }


        // Create a line chart from Field1 of ThinkSpeak Channel
        tsChart = new ThingSpeakLineChart(Integer.valueOf(fieldList.get(position).get("Field_id")), last_id);

        // Get 200 entries at maximum 显示多少比数据
        tsChart.setNumberOfEntries(10);
        // Set value axis labels on 10-unit interval
        tsChart.setValueAxisLabelInterval(10);
        // Set date axis labels on 5-minute interval
        tsChart.setDateAxisLabelInterval(1);
        // Show the line as a cubic spline
        tsChart.useSpline(true);
        // Set the line color
        tsChart.setLineColor(Color.parseColor("#0086D3"));
        // Set the axis color
        tsChart.setAxisColor(Color.parseColor("#FFFFFF"));
        // Set the starting date (5 minutes ago) for the default viewport of the chart
        tsChart.setChartStartDate(calendar.getTime());
        // Set listener for chart data update
        tsChart.setListener((channelId, fieldId, title, lineChartData, maxViewport, initialViewport) -> {
            // Set chart data to the LineChartView
            holder.chartView.setLineChartData(lineChartData);
            // Set scrolling bounds of the chart
            holder.chartView.setMaximumViewport(maxViewport);
            // Set the initial chart bounds
            holder.chartView.setCurrentViewport(initialViewport);
        });
        // Load chart data asynchronously
        tsChart.loadChartData();

    }


    @Override
    public int getItemCount() {  //设定要制造多少个item
        return fieldList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textviewId, textviewRes, textviewbty,textvolume;
        public LineChartView chartView;
        public ImageView background_color, battery_img;
        public ProgressBar progressbar;

        public ViewHolder(@NonNull View holder) { //呼叫传送过去的item  //綁定元件ID
            super(holder);
            textviewRes = (TextView) holder.findViewById(R.id.txtres);
            textviewId = (TextView) holder.findViewById(R.id.txtid);
            textviewbty = (TextView) holder.findViewById(R.id.bty_txt);
            chartView = (LineChartView) holder.findViewById(R.id.chart);
            background_color = (ImageView) holder.findViewById(R.id.img);
            battery_img = (ImageView) holder.findViewById(R.id.battery_icon);
            progressbar = (ProgressBar) holder.findViewById(R.id.progressbar);
            textvolume = (TextView) holder.findViewById(R.id.wtr_volume);

        }
    }


}