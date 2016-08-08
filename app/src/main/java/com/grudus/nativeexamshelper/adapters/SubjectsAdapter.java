package com.grudus.nativeexamshelper.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.touchhelpers.SwipeToDeleteable;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.subjects.SubjectsContract;
import com.grudus.nativeexamshelper.pojos.Subject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectsViewHolder> {

    private Cursor cursor;
    private ItemClickListener itemClickListener;
    private final Context context;

    public SubjectsAdapter(Cursor cursor, Context context, ItemClickListener listener) {
        this.cursor = cursor;
        this.itemClickListener = listener;
        this.context = context;
    }


    @Override
    public SubjectsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_subject, parent, false);
        SubjectsViewHolder vh = new SubjectsViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(SubjectsViewHolder holder, int position) {
        cursor.moveToPosition(position);

        String titleText = cursor.getString(SubjectsContract.SubjectEntry.TITLE_COLUMN_INDEX);

        bindTitleText(holder, titleText);
        bindColor(holder);
        bindIconView(holder, titleText);

    }

    private void bindTitleText(SubjectsViewHolder holder, String title) {
        holder.titleView.setText(title);
    }

    private void bindColor(SubjectsViewHolder holder) {
        int color = Color.parseColor(cursor.getString(SubjectsContract.SubjectEntry.COLOR_COLUMN_INDEX));

        GradientDrawable bgShape = (GradientDrawable) holder.iconView.getBackground();
        bgShape.setColor(color);
        holder.iconView.setBackground(bgShape);
    }

    private void bindIconView(SubjectsViewHolder holder, String titleText) {
        holder.iconView.setText(titleText.substring(0, 1).toUpperCase());
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public Subject getItem(int position) {
        cursor.moveToPosition(position);
        String color = cursor.getString(SubjectsContract.SubjectEntry.COLOR_COLUMN_INDEX);
        String titleText = cursor.getString(SubjectsContract.SubjectEntry.TITLE_COLUMN_INDEX);
        return new Subject(titleText, color);
    }

    public void changeCursor(Cursor _new) {
        closeCursor();
        cursor = _new;
    }


    public void closeCursor() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    public void removeItemFromList(int adapterPosition) {
        notifyItemRemoved(adapterPosition);
    }


    public void removeFromDbAndChangeCursor(int adapterPosition) {
        ExamsDbHelper db = ExamsDbHelper.getInstance(context);
        cursor.moveToPosition(adapterPosition);

        db.removeSubject(cursor.getString(SubjectsContract.SubjectEntry.TITLE_COLUMN_INDEX))
                .flatMap(howManyDeleted -> {
                    if (howManyDeleted == 0) return Observable.empty();
                    return db.getAllSubjectsSortByTitle();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::changeCursor,
                        onError -> {},
                        () -> notifyItemRemoved(adapterPosition));

        db.removeAllExamsRelatedWithSubject(cursor.getString(SubjectsContract.SubjectEntry.TITLE_COLUMN_INDEX))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }


    public class SubjectsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, SwipeToDeleteable {

        TextView titleView, iconView;
        LinearLayout invisibleView;
        LinearLayout linearLayout;

        public SubjectsViewHolder(View itemView) {
            super(itemView);

            bindViews(itemView);

            itemView.setOnClickListener(this);
            linearLayout.setOnClickListener(this);
        }

        private void bindViews(View itemView) {
            titleView = (TextView) itemView.findViewById(R.id.list_item_subject_text);
            iconView = (TextView) itemView.findViewById(R.id.list_item_subject_icon_text);
            invisibleView = (LinearLayout) itemView.findViewById(R.id.invisible);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.list_item_adding_exam_linear);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.itemClicked(v, getAdapterPosition());
            }
        }

        public String getSubject() {
            return titleView.getText().toString();
        }


        @Override
        public ViewGroup getSwipedLayout() {
            return linearLayout;
        }

        @Override
        public void clearView() {
            linearLayout.setX(0);
            linearLayout.setTranslationX(0);
        }

        @Override
        public void delete(int position) {
            removeFromDbAndChangeCursor(position);
        }

    }

}
