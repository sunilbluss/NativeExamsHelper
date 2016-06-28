package com.grudus.nativeexamshelper.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.pojos.Subject;


public class SingleSubjectExamsCursorAdapter extends CursorAdapter {

    private static String defaultInfo;

    public SingleSubjectExamsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        defaultInfo = context.getResources().getString(R.string.sse_default_exam_info);
        Log.d("@@@", "SingleSubjectExamsCursorAdapter: constructor");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_single_subject_exams, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView iconView = (TextView) view.findViewById(R.id.sse_list_item_icon);
        TextView infoView = (TextView) view.findViewById(R.id.sse_list_item_info);
        TextView dateView = (TextView) view.findViewById(R.id.sse_list_item_date);

        dateView.setText(DateHelper.getReadableDataFromLong(cursor.getLong(ExamsContract.OldExamEntry.DATE_COLUMN_INDEX)));

        String subjectInfo = cursor.getString(ExamsContract.OldExamEntry.INFO_COLUMN_INDEX);
        boolean isEmpty = subjectInfo == null || subjectInfo.replaceAll("\\s+", "").isEmpty();
        infoView.setText(isEmpty ? defaultInfo : subjectInfo);

        double grade = cursor.getDouble(ExamsContract.OldExamEntry.GRADE_COLUMN_INDEX);
        String toSet;

        if (grade % 1 == 0) toSet = String.valueOf((int)grade);
        else if (grade % 1 == 0.25) toSet = String.valueOf((int)grade) + "+";
        else toSet = String.valueOf((int)(grade+1)) + "-";

        GradientDrawable bg = (GradientDrawable) iconView.getBackground();
        bg.setColor(Color.parseColor(context.getResources().getStringArray(R.array.gradesColors)[(int)(grade-0.5)]));
        iconView.setBackground(bg);

        iconView.setText(toSet);
    }
}
