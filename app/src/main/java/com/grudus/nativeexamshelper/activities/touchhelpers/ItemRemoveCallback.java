package com.grudus.nativeexamshelper.activities.touchhelpers;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.LinearLayout;

import com.grudus.nativeexamshelper.adapters.SubjectsAdapter;

public class ItemRemoveCallback extends ItemTouchHelper.SimpleCallback {

    private LinearLayout linearLayout;

    public ItemRemoveCallback(int dragDirs, int swipeDirs, Context context) {
        super(dragDirs, swipeDirs);

    }


    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            SubjectsAdapter.SubjectsViewHolder holder = ((SubjectsAdapter.SubjectsViewHolder) viewHolder);
            linearLayout = holder.getLinearLayout();

            linearLayout.setTranslationX(dX);
        }

        else super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        ((SubjectsAdapter.SubjectsViewHolder) viewHolder).clearView();
        Log.d("@@@@", "clearView: ");
        super.clearView(recyclerView, viewHolder);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();
        Log.d("@@@@@@@", "onSwiped: swiped " + pos);

        ((SubjectsAdapter.SubjectsViewHolder)viewHolder).getAdapter().removeFromDbAndChangeCursor(pos);
    }


}
