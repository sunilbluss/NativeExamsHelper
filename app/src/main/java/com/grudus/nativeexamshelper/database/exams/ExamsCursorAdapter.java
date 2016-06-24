package com.grudus.nativeexamshelper.database.exams;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.pojos.Subject;


public class ExamsCursorAdapter extends CursorAdapter {

    private ExamsDbHelper dbHelper;

    public ExamsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        dbHelper = new ExamsDbHelper(context);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context)
                .inflate(R.layout.list_item_exam, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView subjectView = (TextView) view.findViewById(R.id.list_item_adding_exam_subject);
        TextView dateView = (TextView) view.findViewById(R.id.list_item_adding_exam_date);
        TextView iconView = (TextView) view.findViewById(R.id.list_item_icon_text);

        String subjectTitle = cursor.getString(ExamsContract.ExamEntry.SUBJECT_COLUMN_INDEX);
        long dateLong = cursor.getLong(ExamsContract.ExamEntry.DATE_COLUMN_INDEX);

        String readableDate = DateHelper.getReadableDataFromLong(dateLong);

        dbHelper.openDB();
        Subject subjectObiect = dbHelper.findSubjectByTitle(subjectTitle);
        dbHelper.closeDB();

        if (subjectObiect != null) {
            GradientDrawable bg = (GradientDrawable) iconView.getBackground();
            bg.setColor(Color.parseColor(subjectObiect.getColor()));
            iconView.setBackground(bg);
        }

        iconView.setText(subjectTitle.substring(0, 1).toUpperCase());
        subjectView.setText(subjectTitle);
        dateView.setText(readableDate);
    }
}
