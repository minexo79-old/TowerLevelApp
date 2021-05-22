package com.example.thingspeaklib;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.macroyau.thingspeakandroid.ThingSpeakChannel;
import com.macroyau.thingspeakandroid.ThingSpeakLineChart;
import com.macroyau.thingspeakandroid.model.ChannelFeed;
import com.macroyau.thingspeakandroid.model.Feed;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity {

    private ThingSpeakChannel tsChannel;
    private ThingSpeakLineChart tsChart;
    private LineChartView chartView;
    private TextView textviewRes, textviewId;
    //ThingSpeakChannel tsPrivateChannel = new ThingSpeakChannel(CHANNEL_ID, READ_API_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textviewRes = (TextView)findViewById(R.id.txtres);
        textviewId  = (TextView)findViewById(R.id.txtid);

        LoadFeed();
        Loadgraf();
    }

   /* private void Bargrap(){
        ColumnChartView barChart = findViewById(R.id.barchart);

        ArrayList<ColumnChartData> data = new ArrayList<>();
        data.add(new ColumnChartData(100));

    }*/

    private void LoadFeed() {
        // Connect to ThinkSpeak Channel 9
        tsChannel = new ThingSpeakChannel(1394850, "2K2DS74416P4B6V1");
        // Set listener for Channel feed update events
        tsChannel.setChannelFieldFeedUpdateListener((channelId, fieldId, channelFieldFeed) -> {
            // Catch Feed
            List<Feed> data = channelFieldFeed.getFeeds();
            // Get Last Entry Data from Feed
            textviewId.setText("水塔編號：" + channelId);
            textviewRes.setText("目前水位：" + data.get(data.size() - 1).getField1() + "cm");
        });

        tsChannel.loadChannelFieldFeed(1);
    }

    private void Loadgraf() {
        // Fetch the specific Channel feed
        tsChannel.loadChannelFeed();

        // Create a Calendar object dated 5 minutes ago
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);

        // Configure LineChartView
        chartView = findViewById(R.id.chart);
        chartView.setZoomEnabled(false);
        chartView.setValueSelectionEnabled(true);

        // Create a line chart from Field1 of ThinkSpeak Channel
        tsChart = new ThingSpeakLineChart( 1394850, 1, "2K2DS74416P4B6V1");

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
            chartView.setLineChartData(lineChartData);
            // Set scrolling bounds of the chart
            chartView.setMaximumViewport(maxViewport);
            // Set the initial chart bounds
            chartView.setCurrentViewport(initialViewport);
        });
        // Load chart data asynchronously
        tsChart.loadChartData();
    }
}