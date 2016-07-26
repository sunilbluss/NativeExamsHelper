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
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.database.subjects.SubjectsContract;
import com.grudus.nativeexamshelper.pojos.Subject;

import java.util.Arrays;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OldExamsAdapter extends RecyclerView.Adapter<OldExamsAdapter.OldExamsViewHolder> {

    public static final int HEADER_POSITION = 0;

    private Cursor cursor;
    private final ExamsDbHelper dbHelper;
    private ItemClickListener listener;

    private int headers;

    public OldExamsAdapter(Context context, Cursor cursor) {
        this.dbHelper = ExamsDbHelper.getInstance(context);
        this.cursor = cursor;
        headers = 1;
        Log.d("@@@", "OldExamsAdapter: konstruktor " + Arrays.toString(cursor.getColumnNames()) + ", " + cursor.getCount());
    }

    public void setListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public OldExamsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        dbHelper.openDBReadOnly();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_old_exam, parent, false);
        Log.d("@@@", "OldExamsAdapter: onCreateViewHolder");
        return new OldExamsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OldExamsViewHolder holder, int position) {
        Log.d("@@@", "OldExamsAdapter: onBind " + position);
        if (position == HEADER_POSITION)
            return; //header - already defined in xml
        position = position - getHeaderCount();


        cursor.moveToPosition(position);
        final String subjectTitle = cursor.getString(ExamsContract.OldExamEntry.SUBJECT_COLUMN_INDEX);

        dbHelper.findSubjectByTitle(subjectTitle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subjectObject -> {
                    if (subjectObject != null) {
                        GradientDrawable bg = (GradientDrawable) holder.iconView.getBackground();
                        bg.setColor(Color.parseColor(subjectObject.getColor()));
                        holder.iconView.setBackground(bg);
                    }
                });

        holder.iconView.setText(subjectTitle.substring(0, 1).toUpperCase());

        holder.subjectView.setText(subjectTitle);
    }

    public Subject getSubjectAtPosition(int adapterPosition) {
        cursor.moveToPosition(adapterPosition - getHeaderCount());
        String subjectTitle = cursor.getString(SubjectsContract.SubjectEntry.TITLE_COLUMN_INDEX);
        String color = cursor.getString(SubjectsContract.SubjectEntry.COLOR_COLUMN_INDEX);

        return new Subject(subjectTitle, color);
    }

    public void changeCursor(Cursor _new) {
        cursor.close();
        cursor = _new;
    }

    public void closeDatabase() {
        cursor.close();
        dbHelper.closeDB();
    }

    public int getHeaderCount() {
        return headers;
    }

    @Override
    public int getItemCount() {
        return cursor == null
                ? 0
                : cursor.getCount() + getHeaderCount();
    }

    public class OldExamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView subjectView, iconView;

        public OldExamsViewHolder(View itemView) {
            super(itemView);
            subjectView = (TextView) itemView.findViewById(R.id.list_item_old_exam_subject);
            iconView = (TextView) itemView.findViewById(R.id.list_item_old_exam_icon_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.itemClicked(v, getAdapterPosition());
        }
    }
}
