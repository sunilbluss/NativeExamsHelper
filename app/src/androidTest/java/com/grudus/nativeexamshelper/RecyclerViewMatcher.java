package com.grudus.nativeexamshelper;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;


public class RecyclerViewMatcher {

    public static Matcher<View> nthRecyclerViewChild(final int index, final int recyclerViewId) {
        return new TypeSafeMatcher<View>() {

            View childView;

            @Override
            protected boolean matchesSafely(View view) {
                if (index < 0)
                    return false;
                if (childView == null) {
                    RecyclerView recyclerView =
                            (RecyclerView) view.getRootView().findViewById(recyclerViewId);
                    if (recyclerView != null && recyclerView.getId() == recyclerViewId && index < recyclerView.getChildCount()) {
                        childView = recyclerView.findViewHolderForAdapterPosition(index).itemView;
                    }
                    else {
                        return false;
                    }
                }

                return view == childView;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("index index " + index);
            }
        };
    }
}
