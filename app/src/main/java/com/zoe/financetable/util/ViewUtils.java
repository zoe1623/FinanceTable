package com.zoe.financetable.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;

import com.zoe.financetable.R;

public class ViewUtils {

    private static LinearLayout.LayoutParams vParams;
    private static LinearLayout.LayoutParams hParams;
    public static View getVLine(Context context) {
        Resources resources = context.getResources();
        if(vParams == null) vParams = new LinearLayout.LayoutParams((int) (resources.getDisplayMetrics().density + 0.5f), -1);
        View line = new View(context);
        line.setLayoutParams(vParams);
        line.setBackgroundColor(resources.getColor(R.color.list_divider_color));
        return line;
    }

    public static View getHLine(Context context) {
        Resources resources = context.getResources();
        if(hParams == null) hParams = new LinearLayout.LayoutParams(-1,(int) (resources.getDisplayMetrics().density + 0.5f));
        View line = new View(context);
        line.setLayoutParams(hParams);
        line.setBackgroundColor(resources.getColor(R.color.list_divider_color));
        return line;
    }
}
