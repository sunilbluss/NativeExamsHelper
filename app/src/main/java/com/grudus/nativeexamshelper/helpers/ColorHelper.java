package com.grudus.nativeexamshelper.helpers;

import android.graphics.Color;

public class ColorHelper {

    public static int getNearestResColor(int color, String[] resColors) {
        int[] colors = new int[resColors.length];
        for (int i = 0; i < resColors.length; i++) colors[i] = Color.parseColor(resColors[i]);

        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        int nearest = colors[0];
        int minDistance = 3*255;
        int distance;

        for (int col : colors) {
            int r2 = Color.red(col);
            int g2 = Color.green(col);
            int b2 = Color.blue(col);
            distance = Math.abs(r - r2) + Math.abs(g - g2) + Math.abs(b - b2);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = col;
            }
        }

        return nearest;
    }

}
