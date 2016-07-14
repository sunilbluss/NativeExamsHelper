package com.grudus.nativeexamshelper.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.helpers.TimeHelper;
import com.grudus.nativeexamshelper.pojos.Subject;


public class ExamsAdapter extends RecyclerView.Adapter<ExamsAdapter.ExamsViewHolder> {

    private Cursor cursor;
    private final ExamsDbHelper dbHelper;

    public ExamsAdapter(Context context, Cursor cursor) {
        this.cursor = cursor;
        this.dbHelper = ExamsDbHelper.getInstance(context);
    }


    @Override
    public ExamsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        dbHelper.openDBReadOnly();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_exam, parent, false);
        ExamsViewHolder vh = new ExamsViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ExamsViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String subjectTitle = cursor.getString(ExamsContract.ExamEntry.SUBJECT_COLUMN_INDEX);
        long dateLong = cursor.getLong(ExamsContract.ExamEntry.DATE_COLUMN_INDEX);

        String readableDate = DateHelper.getReadableDataFromLong(dateLong);

        dbHelper.openDB();
        Subject subjectObject = dbHelper.findSubjectByTitle(subjectTitle);
        dbHelper.closeDB();

        if (subjectObject != null) {
            GradientDrawable bg = (GradientDrawable) holder.iconView.getBackground();
            bg.setColor(Color.parseColor(subjectObject.getColor()));
            holder.iconView.setBackground(bg);
        }

        holder.iconView.setText(subjectTitle.substring(0, 1).toUpperCase());
        holder.subjectView.setText(subjectTitle);
        holder.dateView.setText(readableDate);

        String info = cursor.getString(ExamsContract.ExamEntry.INFO_COLUMN_INDEX);
        ((TextView)holder.expandedLayout.findViewById(R.id.list_item_expanded_time)).setText(TimeHelper.getFormattedTime(dateLong));
        ((TextView)holder.expandedLayout.findViewById(R.id.list_item_expanded_info)).setText(info);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public void changeCursor(Cursor _new) {
        cursor.close();
        cursor = _new;
        notifyDataSetChanged();
    }



    public class ExamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView iconView, dateView, subjectView;
        private boolean expanded;
        private RelativeLayout expandedLayout;

        public ExamsViewHolder(View itemView) {
            super(itemView);
            subjectView = (TextView) itemView.findViewById(R.id.list_item_adding_exam_subject);
            dateView = (TextView) itemView.findViewById(R.id.list_item_adding_exam_date);
            iconView = (TextView) itemView.findViewById(R.id.list_item_icon_text);
            expandedLayout = (RelativeLayout) itemView.findViewById(R.id.exams_expanded_list_item_layout);

            itemView.setOnClickListener(this);
            expanded = false;

        }

        @Override
        public void onClick(View v) {
            if (!expanded) {
                expanded = true;
                expandedLayout.setAlpha(0f);
                expandedLayout.setVisibility(View.VISIBLE);
                expandedLayout.animate().setDuration(500).alpha(1f).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                    }
                }).start();
            }
            else {
                expanded = false;
                expandedLayout.animate().setDuration(500).alpha(0f).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        expandedLayout.setVisibility(View.GONE);
                    }
                }).start();

            }


        }
    }

}
