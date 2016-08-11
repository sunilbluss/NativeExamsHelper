package com.grudus.nativeexamshelper.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ExamsAdapter extends RecyclerView.Adapter<ExamsAdapter.ExamsViewHolder> {

    private Cursor cursor;

    private static final int ANIMATION_DURATION = AnimationHelper.DEFAULT_ANIMATION_DURATION;
    private static final float SCALE_TO_RESIZE = 1.4f;

    private int selectedItemBackgroundColor;
    private int normalItemBackgroundColor;

    private final ExamsDbHelper dbHelper;
    private final Context context;

    private int cursorSize = 0;


    public ExamsAdapter(Context context, Cursor cursor) {
        this.cursor = cursor;
        this.dbHelper = ExamsDbHelper.getInstance(context);
        this.context = context;
        cursorSize = cursor.getCount();

        setUpBackgroundColors();
    }

    private void setUpBackgroundColors() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();

        theme.resolveAttribute(R.attr.selectedListItemColor, typedValue, true);
        selectedItemBackgroundColor = typedValue.data;

        theme.resolveAttribute(R.attr.background, typedValue, true);
        normalItemBackgroundColor = typedValue.data;
    }


    @Override
    public ExamsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        dbHelper.openDBReadOnly();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_exam, parent, false);
        return new ExamsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExamsViewHolder holder, int position) {
        cursor.moveToPosition(position);

        String subjectTitle = cursor.getString(ExamsContract.ExamEntry.SUBJECT_COLUMN_INDEX);
        long dateLong = cursor.getLong(ExamsContract.ExamEntry.DATE_COLUMN_INDEX);

        findSubjectAndBindColor(holder, subjectTitle);
        bindIconView(holder, subjectTitle);
        bindSubjectView(holder, subjectTitle);
        bindDateView(holder, dateLong);

        bindExpandedLayout(holder, dateLong);

    }

    private void bindExpandedLayout(ExamsViewHolder holder, long dateLong) {
        String info = cursor.getString(ExamsContract.ExamEntry.INFO_COLUMN_INDEX);
        ((TextView)holder.expandedLayout.findViewById(R.id.list_item_expanded_time)).setText(TimeHelper.getFormattedTime(dateLong));
        ((TextView)holder.expandedLayout.findViewById(R.id.list_item_expanded_info)).setText(info);
    }

    private void findSubjectAndBindColor(ExamsViewHolder holder, String subjectTitle) {
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
    }

    private void bindIconView(ExamsViewHolder holder, String subjectTitle) {
        holder.iconView.setText(subjectTitle.substring(0, 1).toUpperCase());
    }

    private void bindSubjectView(ExamsViewHolder holder, String subjectTitle) {
        holder.subjectView.setText(subjectTitle);
    }

    private void bindDateView(ExamsViewHolder holder, long dateLong) {
        String readableDate = DateHelper.getReadableDataFromLong(dateLong);
        holder.dateView.setText(readableDate);
    }

    @Override
    public int getItemCount() {
        return cursorSize;
    }

    private void deleteRowAtPosition(final int adapterPosition) {
        cursor.moveToPosition(adapterPosition);
        dbHelper.openDB();
        long millis = cursor.getLong(ExamsContract.ExamEntry.DATE_COLUMN_INDEX);

        dbHelper.removeExam(millis)
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
        cursorSize = cursor.getCount();
    }

    public void closeDatabase() {
        if (cursor != null)
            cursor.close();
        cursorSize = 0;
        dbHelper.closeDB();
    }

    public class ExamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @BindView(R.id.list_item_icon_text) TextView iconView;
        @BindView(R.id.list_item_adding_exam_date) TextView dateView;
        @BindView(R.id.list_item_adding_exam_subject) TextView subjectView;
        @BindView(R.id.list_item_image_under_icon) ImageView binIcon;
        @BindView(R.id.exams_expanded_list_item_layout) RelativeLayout expandedLayout;

        private boolean expanded;
        private boolean selected;

        public ExamsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            binIcon.setOnClickListener(v -> deleteRowAtPosition(getAdapterPosition()));

            expanded = false;
            selected = false;
            AnimationHelper.setDuration(ANIMATION_DURATION);
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
            iconView.animate()
                    .setDuration(ANIMATION_DURATION)
                    .scaleY(scale)
                    .scaleX(scale)
                    .start();
        }


        @Override
        public boolean onLongClick(View v) {
            if (expanded) return false;
            if (!selected) {
                itemView.setBackgroundColor(selectedItemBackgroundColor);
                selected = true;
                AnimationHelper.rotateToReceiveBinIcon(iconView, binIcon, ANIMATION_DURATION/2)
                        .start();
            }
            else {
                selected = false;
                itemView.setBackgroundColor(normalItemBackgroundColor);
                AnimationHelper.rotateToDisposeBinIcon(iconView, binIcon, ANIMATION_DURATION/2)
                        .start();
            }
            return true;
        }


    }

}
