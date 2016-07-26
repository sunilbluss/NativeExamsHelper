package com.grudus.nativeexamshelper.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.helpers.AnimationHelper;
import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.helpers.TimeHelper;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ExamsAdapter extends RecyclerView.Adapter<ExamsAdapter.ExamsViewHolder> {

    private Cursor cursor;

    private static final int ANIMATION_DURATION = 400;
    private static final float SCALE_TO_RESIZE = 1.4f;

    private final ExamsDbHelper dbHelper;
    private final Context context;

    static {
        AnimationHelper.setDuration(ANIMATION_DURATION);
    }

    public ExamsAdapter(Context context, Cursor cursor) {
        this.cursor = cursor;
        this.dbHelper = ExamsDbHelper.getInstance(context);
        this.context = context;
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
        holder.dateView.setText(readableDate);

        String info = cursor.getString(ExamsContract.ExamEntry.INFO_COLUMN_INDEX);
        ((TextView)holder.expandedLayout.findViewById(R.id.list_item_expanded_time)).setText(TimeHelper.getFormattedTime(dateLong));
        ((TextView)holder.expandedLayout.findViewById(R.id.list_item_expanded_info)).setText(info);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    private void deleteRowAtPosition(final int adapterPosition) {
        cursor.moveToPosition(adapterPosition);
        dbHelper.openDB();
        long millis = cursor.getLong(ExamsContract.ExamEntry.DATE_COLUMN_INDEX);

        dbHelper.removeExam(millis)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap(function -> dbHelper.getAllIncomingExamsSortByDate())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(cursor -> {
                    changeCursor(cursor);
                    notifyItemRemoved(adapterPosition);
                }, error -> Log.e("@@@" + this.getClass().getSimpleName(), "populateRecyclerView: ERRRRRRR", error));


    }


    public void changeCursor(Cursor _new) {
        cursor.close();
        cursor = _new;
    }

    public void closeDatabase() {
        cursor.close();
        dbHelper.closeDB();
    }

    public class ExamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView iconView, dateView, subjectView;
        private ImageView binIcon;
        private boolean expanded;
        private boolean selected;

        private RelativeLayout expandedLayout;


        public ExamsViewHolder(View itemView) {
            super(itemView);
            binIcon = (ImageView) itemView.findViewById(R.id.list_item_image_under_icon);
            subjectView = (TextView) itemView.findViewById(R.id.list_item_adding_exam_subject);
            dateView = (TextView) itemView.findViewById(R.id.list_item_adding_exam_date);
            iconView = (TextView) itemView.findViewById(R.id.list_item_icon_text);
            expandedLayout = (RelativeLayout) itemView.findViewById(R.id.exams_expanded_list_item_layout);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            binIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteRowAtPosition(getAdapterPosition());
                }
            });

            expanded = false;
            selected = false;

        }

        @Override
        public void onClick(View v) {
            if (!expanded) {
                expanded = true;
                AnimationHelper.expand(expandedLayout);
                startIconResizeAnimation(SCALE_TO_RESIZE);
            }
            else {
                expanded = false;
                AnimationHelper.collapse(expandedLayout);
                startIconResizeAnimation(1f);

            }

        }

        private void startIconResizeAnimation(float scale) {
            if (selected) return;
            iconView.animate().setDuration(ANIMATION_DURATION).scaleY(scale).scaleX(scale).start();
        }

        private void startInfoAlphaAnimation(float alpha) {
            expandedLayout.animate().setDuration(ANIMATION_DURATION).alpha(alpha).start();
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d("@@@@", "onLongClick: selected = " + selected);
            if (expanded) return false;
            if (!selected) {
                itemView.setBackgroundColor(0xffeeeeee);
                selected = true;
                iconView.animate()
                        .rotationY(90)
                        .setDuration(ANIMATION_DURATION/2)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                iconView.setVisibility(View.INVISIBLE);
                                binIcon.setRotationY(90);
                                binIcon.setVisibility(View.VISIBLE);
                                binIcon.animate().setDuration(ANIMATION_DURATION/2).rotationY(180).setListener(null).start();

                            }
                        })
                        .start();
            }
            else {
                selected = false;
                itemView.setBackgroundColor(0xffffffff);
                binIcon.animate()
                        .rotationY(90)
                        .setDuration(ANIMATION_DURATION/2)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                binIcon.setVisibility(View.INVISIBLE);
                                iconView.setRotationY(90);
                                iconView.setVisibility(View.VISIBLE);
                                iconView.animate().setDuration(ANIMATION_DURATION/2).rotationY(0).setListener(null).start();
                            }
                        })
                        .start();
            }
            return true;
        }


    }

}
