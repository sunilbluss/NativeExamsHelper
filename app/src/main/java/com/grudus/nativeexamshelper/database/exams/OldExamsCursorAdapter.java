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

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.sliding.OldExamsFragment;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.pojos.OldExam;
import com.grudus.nativeexamshelper.pojos.Subject;

import java.util.Random;

public class OldExamsCursorAdapter extends CursorAdapter {

    private ExamsDbHelper dbHelper;

    public OldExamsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        dbHelper = ExamsDbHelper.getInstance(context);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context)
                .inflate(R.layout.list_item_old_exam, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView subjectView = (TextView) view.findViewById(R.id.list_item_old_exam_subject);
//        TextView infoView = (TextView) view.findViewById(R.id.list_item_old_exam_info);
        TextView iconView = (TextView) view.findViewById(R.id.list_item_old_exam_icon_text);

        String subjectTitle = cursor.getString(ExamsContract.OldExamEntry.SUBJECT_COLUMN_INDEX);


        dbHelper.openDB();
        Subject subjectObiect = dbHelper.findSubjectByTitle(subjectTitle);
        dbHelper.closeDB();

        if (subjectObiect != null) {
            GradientDrawable bg = (GradientDrawable) iconView.getBackground();
            bg.setColor(Color.parseColor(subjectObiect.getColor()));
            iconView.setBackground(bg);
        }

//        if (info != null) infoView.setText(info);

        iconView.setText(subjectTitle.substring(0, 1).toUpperCase());


        subjectView.setText(subjectTitle);
    }
}