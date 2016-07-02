package com.grudus.nativeexamshelper.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.helpers.DateHelper;


public class SingleSubjectExamsAdapter extends RecyclerView.Adapter<SingleSubjectExamsAdapter.SingleSubjectExamsViewHolder> {

    private Context context;
    private Cursor cursor;
    private static String defaultInfo;

    public SingleSubjectExamsAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        Log.i("@@@@@@@", "SingleSubjectExamsAdapter: adapter count " + cursor.getCount());
        defaultInfo = context.getResources().getString(R.string.sse_default_exam_info);
    }

    @Override
    public SingleSubjectExamsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_single_subject_exams, parent, false);
        SingleSubjectExamsViewHolder vh = new SingleSubjectExamsViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(SingleSubjectExamsViewHolder holder, int position) {

        cursor.moveToPosition(position);
        
        Log.i("@@@@@@@", "onBindViewHolder: opened " + position);
        holder.dateView.setText(DateHelper.getReadableDataFromLong(cursor.getLong(ExamsContract.OldExamEntry.DATE_COLUMN_INDEX)));

        String subjectInfo = cursor.getString(ExamsContract.OldExamEntry.INFO_COLUMN_INDEX);
        boolean isEmpty = subjectInfo == null || subjectInfo.replaceAll("\\s+", "").isEmpty();
        holder.infoView.setText(isEmpty ? defaultInfo : subjectInfo);

        double grade = cursor.getDouble(ExamsContract.OldExamEntry.GRADE_COLUMN_INDEX);
        String toSet;

        if (grade % 1 == 0) toSet = String.valueOf((int)grade);
        else if (grade % 1 == 0.25) toSet = String.valueOf((int)grade) + "+";
        else toSet = String.valueOf((int)(grade+1)) + "-";

        GradientDrawable bg = (GradientDrawable) holder.iconView.getBackground();
        bg.setColor(Color.parseColor(context.getResources().getStringArray(R.array.gradesColors)[(int)(grade-0.5)]));
        holder.iconView.setBackground(bg);

        holder.iconView.setText(toSet);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }
    
    public void closeCursor() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    public void changeCursor(Cursor _new) {
        closeCursor();
        cursor = _new;
        notifyDataSetChanged();
    }

    public static class SingleSubjectExamsViewHolder extends RecyclerView.ViewHolder {

        TextView iconView, infoView, dateView;
        
        public SingleSubjectExamsViewHolder(View view) {
            super(view);
            this.iconView = (TextView) view.findViewById(R.id.sse_list_item_icon);
            this.infoView = (TextView) view.findViewById(R.id.sse_list_item_info);
            this.dateView = (TextView) view.findViewById(R.id.sse_list_item_date);
        }
    }
}
