package com.grudus.nativeexamshelper.database;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.grudus.nativeexamshelper.DateHelper;
import com.grudus.nativeexamshelper.R;


public class ExamsCursorAdapter extends CursorAdapter {

    public ExamsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context)
                .inflate(R.layout.list_item_adding_exam_content, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView subjectView = (TextView) view.findViewById(R.id.list_item_adding_exam_subject);
        TextView dateView = (TextView) view.findViewById(R.id.list_item_adding_exam_date);

        String subject = cursor.getString(cursor.getColumnIndex(ExamsContract.ExamEntry.SUBJECT_COLUMN));
        long dateLong = cursor.getLong(cursor.getColumnIndex(ExamsContract.ExamEntry.DATE_COLUMN));

        String readableDate = DateHelper.getReadableDataFromLong(dateLong);

        subjectView.setText(subject);
        dateView.setText(readableDate);
    }
}
