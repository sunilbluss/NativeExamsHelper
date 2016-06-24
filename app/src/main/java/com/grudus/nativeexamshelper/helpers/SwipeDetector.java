package com.grudus.nativeexamshelper.helpers;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import java.util.logging.Logger;

public class SwipeDetector implements View.OnTouchListener {

    public enum Action {
        LR, // Left to Right
        RL, // Right to Left
        None // when no action was detected
    }

    private static final String logTag = "SwipeDetector";
    private static final int MIN_DISTANCE = 100;
    private float downX, upX;
    private Action mSwipeDetected = Action.None;


    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                mSwipeDetected = Action.None;
                return false; // allow other events like Click to be processed
            }

            case MotionEvent.ACTION_MOVE: {
                upX = event.getX();

                float deltaX = downX - upX;

                // horizontal swipe detection
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        Log.d(logTag, "Swipe Left to Right " + deltaX);
                        mSwipeDetected = Action.LR;
                        return true;
                    }

/*                    if (deltaX > 0) {
                        Log.d(logTag, "Swipe Right to Left " + downX + " -> " + upX);
                        mSwipeDetected = Action.RL;
                        return true;
                    }*/
                }
            }
        }
        return false;
    }
}
