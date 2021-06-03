package com.example.thingspeaklib;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.util.Random;

public class set_color {

    public static ColorDrawable[] vibrantLightColorList =
            {
                new ColorDrawable(Color.parseColor("#ffe06c")),
                new ColorDrawable(Color.parseColor("#93cfb3")),
                new ColorDrawable(Color.parseColor("#fdaaaa")),
                new ColorDrawable(Color.parseColor("#faca5f")),
                new ColorDrawable(Color.parseColor("#a5dedf")),
                new ColorDrawable(Color.parseColor("#ffab72")),
                new ColorDrawable(Color.parseColor("#e0eaad")),
                new ColorDrawable(Color.parseColor("#a5d0ff")),
                new ColorDrawable(Color.parseColor("#d570ff")),
                new ColorDrawable(Color.parseColor("#b5dfb7"))
            };

    public static ColorDrawable getRandomDrawbleColor() {
        int idx = new Random().nextInt(vibrantLightColorList.length);
        return vibrantLightColorList[idx];
    }
}
