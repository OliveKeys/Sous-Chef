package com.oliveskies.sous_chef;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

public class Utility {

    public int convertToDP(Context context, int dps)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }
    public static int calculateHeight(ListView list) {

        int height = 0;

        for (int i = 0; i < list.getCount(); i++) {
            View childView = list.getAdapter().getView(i, null, list);
            childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            height+= childView.getMeasuredHeight();
        }

        //dividers height
        height += list.getDividerHeight() * list.getCount();

        return height;
    }
}
