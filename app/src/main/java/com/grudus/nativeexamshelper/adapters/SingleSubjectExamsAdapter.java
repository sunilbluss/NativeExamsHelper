package com.grudus.nativeexamshelper.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.touchhelpers.SwipeToDeleteable;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.pojos.Subject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class SingleSubjectExamsAdapter extends RecyclerView.Adapter<SingleSubjectExamsAdapter.SingleSubjectExamsViewHolder> {

    private final Context context;
    private Cursor cursor;
    private String defaultInfo;
    private final ExamsDbHelper examsDbHelper;
    private Subject subject;

    private int cursorSize = 0;

    public SingleSubjectExamsAdapter(Context context, Cursor cursor, Subject subject) {
        this.context = context;
        this.cursor = cursor;
        defaultInfo = context.getResources().getString(R.string.sse_default_exam_info);
        examsDbHelper = ExamsDbHelper.getInstance(context);
        this.subject = subject;
        cursorSize = cursor.getCount();
    }

    @Override
    public SingleSubjectExamsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_single_subject_exams, parent, false);
        return new SingleSubjectExamsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SingleSubjectExamsViewHolder holder, int position) {

        cursor.moveToPosition(position);
        double grade = cursor.getDouble(ExamsContract.OldExamEntry.GRADE_COLUMN_INDEX);
        
        bindDate(holder);
        bindSubjectInfo(holder);
        bindColor(holder, grade);
        bindGrade(holder, grade);
    }

    private void bindDate(SingleSubjectExamsViewHolder holder) {
        final long date = cursor.getLong(ExamsContract.OldExamEntry.DATE_COLUMN_INDEX);
        holder.dateView.setText(
                DateHelper.getReadableDataFromLong(date));

    }

    private void bindSubjectInfo(SingleSubjectExamsViewHolder holder) {
        String subjectInfo = cursor.getString(ExamsContract.OldExamEntry.INFO_COLUMN_INDEX);
        boolean isEmpty = subjectInfo == null || subjectInfo.replaceAll("\\s+", "").isEmpty();
        holder.infoView.setText(isEmpty ? defaultInfo : subjectInfo);
    }

    private void bindGrade(SingleSubjectExamsViewHolder holder, double grade) {
        String gradeString;

        if (grade % 1 == 0)
            gradeString = String.valueOf((int)grade);
        else if (grade % 1 == 0.25)
            gradeString = String.valueOf((int)grade) + "+";
        else
            gradeString = String.valueOf((int)(grade+1)) + "-";

        holder.iconView.setText(gradeString);
    }

    private void bindColor(SingleSubjectExamsViewHolder holder, double grade) {
        GradientDrawable bg = (GradientDrawable) holder.iconView.getBackground();
        bg.setColor(Color.parseColor(context.getResources().getStringArray(R.array.gradesColors)[(int)(grade-0.5)]));
        holder.iconView.setBackground(bg);
    }

    @Override
    public int getItemCount() {
        return cursorSize;
    }
    
    public void closeCursor() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        cursorSize = 0;
    }

    private void deleteExamAtPosition(int adapterPosition) {
        cursor.moveToPosition(adapterPosition);
        final long time = cursor.getLong(ExamsContract.OldExamEntry.DATE_COLUMN_INDEX);
        final String subjectTitle = cursor.getString(ExamsContract.OldExamEntry.SUBJECT_COLUMN_INDEX);

        examsDbHelper.removeOldExam(time)
                .flatMap(o -> examsDbHelper.getSubjectGrades(subjectTitle))
                .flatMap(cursor -> {
                    changeCursor(cursor);
                    return examsDbHelper.setSubjectHasGrade(subject, cursor.moveToFirst());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(howMany -> {},
                        onError -> {},
                        () -> notifyItemRemoved(adapterPosition));
    }

    public void changeCursor(Cursor _new) {
        closeCursor();
        cursor = _new;
        cursorSize = cursor.getCount();
    }

    public class SingleSubjectExamsViewHolder extends RecyclerView.ViewHolder implements SwipeToDeleteable {

        @BindView(R.id.sse_list_item_icon) TextView iconView;
        @BindView(R.id.sse_list_item_info) TextView infoView;
        @BindView(R.id.sse_list_item_date) TextView dateView;
        @BindView(R.id.list_item_sse_layout) ViewGroup swipedLayout;
        
        public SingleSubjectExamsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        @Override
        public ViewGroup getSwipedLayout() {
            return swipedLayout;
        }

        @Override
        public void clearView() {
            swipedLayout.setX(0);
            swipedLayout.setTranslationX(0);
        }

        @Override
        public void delete(int position) {
            deleteExamAtPosition(position);
        }
    }
}
