package com.grudus.nativeexamshelper.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.subjects.SubjectsContract;


public class SubjectsCursorAdapter extends CursorAdapter {


    public SubjectsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context)
                .inflate(R.layout.list_item_subject, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleView = (TextView) view.findViewById(R.id.list_item_subject_text);
        TextView iconView = (TextView) view.findViewById(R.id.list_item_subject_icon_text);

        String titleText = cursor.getString(SubjectsContract.SubjectEntry.TITLE_COLUMN_INDEX);
        titleView.setText(titleText);

        int color = Color.parseColor(cursor.getString(SubjectsContract.SubjectEntry.COLOR_COLUMN_INDEX));


        GradientDrawable bgShape = (GradientDrawable) iconView.getBackground();
        bgShape.setColor(color);
        iconView.setBackground(bgShape);

        iconView.setText(titleText.substring(0, 1).toUpperCase());
    }
}
