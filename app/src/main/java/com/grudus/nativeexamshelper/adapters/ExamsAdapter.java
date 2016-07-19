package com.grudus.nativeexamshelper.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.helpers.AnimationHelper;
import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.helpers.TimeHelper;
import com.grudus.nativeexamshelper.pojos.Subject;


public class ExamsAdapter extends RecyclerView.Adapter<ExamsAdapter.ExamsViewHolder> {

    private Cursor cursor;

    private static final int ANIMATION_DURATION = 400;
    private static final float SCALE_TO_RESIZE = 1.4f;

    private final ExamsDbHelper dbHelper;

    static {
        AnimationHelper.setDuration(ANIMATION_DURATION);
    }

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

    public void closeDatabase() {
        cursor.close();
        dbHelper.closeDB();
    }

    public class ExamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView iconView, dateView, subjectView;
        private boolean expanded;
        private boolean selected;

        private RelativeLayout expandedLayout;


        public ExamsViewHolder(View itemView) {
            super(itemView);
            subjectView = (TextView) itemView.findViewById(R.id.list_item_adding_exam_subject);
            dateView = (TextView) itemView.findViewById(R.id.list_item_adding_exam_date);
            iconView = (TextView) itemView.findViewById(R.id.list_item_icon_text);
            expandedLayout = (RelativeLayout) itemView.findViewById(R.id.exams_expanded_list_item_layout);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            expanded = false;
            selected = false;

        }

        @Override
        public void onClick(View v) {
            if (!expanded) {
                expanded = true;
                AnimationHelper.expand(expandedLayout);
                startIconResizeAnimation(SCALE_TO_RESIZE);
                startInfoAlphaAnimation(1f);
            }
            else {
                expanded = false;
                AnimationHelper.collapse(expandedLayout);
                startIconResizeAnimation(1f);
                startInfoAlphaAnimation(0f);

            }

        }

        private void startIconResizeAnimation(float scale) {
            iconView.animate().setDuration(ANIMATION_DURATION).scaleY(scale).scaleX(scale).start();
        }

        private void startInfoAlphaAnimation(float alpha) {
            expandedLayout.animate().setDuration(ANIMATION_DURATION).alpha(alpha).start();
        }

        @Override
        public boolean onLongClick(View v) {
            if (!selected) {
                itemView.setBackgroundColor(0xffeeeeee);
                selected = true;
                iconView.animate().rotationY(360).setDuration(ANIMATION_DURATION).start();
            }
            else {
                selected = false;
                itemView.setBackgroundColor(0xffffffff);
                iconView.animate().rotationY(0).setDuration(ANIMATION_DURATION).start();
            }
            return true;
        }
    }

}
