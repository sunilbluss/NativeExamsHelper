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

import java.util.ArrayList;
import java.util.Date;

public class UngradedExamsAdapter extends RecyclerView.Adapter<UngradedExamsAdapter.UngradedExamViewHolder> {

    private Cursor cursor;
    private ItemClickListener listener;

    public UngradedExamsAdapter(Cursor cursor, ItemClickListener itemClickListener) {
        this.cursor = cursor;
        this.listener = itemClickListener;
    }

    @Override
    public UngradedExamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_exam, parent, false);
        UngradedExamViewHolder vh = new UngradedExamViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(UngradedExamViewHolder holder, int position) {
        cursor.moveToPosition(position);

        String subject = cursor.getString(ExamsContract.ExamEntry.SUBJECT_COLUMN_INDEX);
        String info = cursor.getString(ExamsContract.ExamEntry.INFO_COLUMN_INDEX);

        holder.textView.setText(subject);
        holder.infoView.setText(info);
        holder.iconView.setText(subject.substring(0,1).toUpperCase());

    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }


    public void examHasGrade(int position, Cursor newCursor) {
        notifyItemRemoved(position);
        cursor.close();
        cursor = newCursor;
    }

    public Exam getExamByPosition(int position) {
        cursor.moveToPosition(position);
        String subject = cursor.getString(ExamsContract.ExamEntry.SUBJECT_COLUMN_INDEX);
        String info = cursor.getString(ExamsContract.ExamEntry.INFO_COLUMN_INDEX);
        Date date = new Date(cursor.getLong(ExamsContract.ExamEntry.DATE_COLUMN_INDEX));

        return new Exam(subject, info, date);
    }

    public class UngradedExamViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView iconView, textView, infoView;

        public UngradedExamViewHolder(View itemView) {
            super(itemView);
            iconView = (TextView) itemView.findViewById(R.id.list_item_icon_text);
            textView = (TextView) itemView.findViewById(R.id.list_item_adding_exam_subject);
            infoView = (TextView) itemView.findViewById(R.id.list_item_adding_exam_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.itemClicked(v, getAdapterPosition());
        }
    }
}
