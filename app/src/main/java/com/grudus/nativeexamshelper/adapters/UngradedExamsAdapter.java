package com.grudus.nativeexamshelper.adapters;


import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.pojos.Exam;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UngradedExamsAdapter extends RecyclerView.Adapter<UngradedExamsAdapter.UngradedExamViewHolder> {

    private Cursor cursor;
    private ItemClickListener listener;

    private int cursorSize = 0;

    public UngradedExamsAdapter(Cursor cursor, ItemClickListener itemClickListener) {
        this.cursor = cursor;
        this.listener = itemClickListener;

        cursorSize = cursor.getCount();
    }

    @Override
    public UngradedExamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_exam, parent, false);
        return new UngradedExamViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UngradedExamViewHolder holder, int position) {
        cursor.moveToPosition(position);

        String subject = String.valueOf(cursor.getLong(ExamsContract.ExamEntry.SUBJECT_ID_COLUMN_INDEX));

        bindTextView(holder, subject);
        bindInfoView(holder);
        bindIcon(holder, subject);
    }

    private void bindTextView(UngradedExamViewHolder holder, String subject) {
        holder.textView.setText(subject);
    }

    private void bindInfoView(UngradedExamViewHolder holder) {
        String info = cursor.getString(ExamsContract.ExamEntry.INFO_COLUMN_INDEX);
        holder.infoView.setText(info);
    }

    private void bindIcon(UngradedExamViewHolder holder, String subject) {
        holder.iconView.setText(subject.substring(0,1).toUpperCase());
    }

    @Override
    public int getItemCount() {
        return cursorSize;
    }


    public void examHasGrade(int position, Cursor newCursor) {
        notifyItemRemoved(position);
        cursor.close();
        cursor = newCursor;
        cursorSize = cursor.getCount();
    }

    public Exam getExamByPosition(int position) {
        cursor.moveToPosition(position);
        Long id = cursor.getLong(ExamsContract.ExamEntry.INDEX_COLUMN_INDEX);
        Long subjectId = cursor.getLong(ExamsContract.ExamEntry.SUBJECT_ID_COLUMN_INDEX);
        String info = cursor.getString(ExamsContract.ExamEntry.INFO_COLUMN_INDEX);
        Date date = new Date(cursor.getLong(ExamsContract.ExamEntry.DATE_COLUMN_INDEX));

        return new Exam(id, subjectId, info, date);
    }

    public void closeCursor() {
        cursor.close();
        cursorSize = 0;
    }

    public class UngradedExamViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.list_item_icon_text) TextView iconView;
        @BindView(R.id.list_item_adding_exam_subject) TextView textView;
        @BindView(R.id.list_item_adding_exam_date) TextView infoView;

        public UngradedExamViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.itemClicked(v, getAdapterPosition());
        }
    }
}
