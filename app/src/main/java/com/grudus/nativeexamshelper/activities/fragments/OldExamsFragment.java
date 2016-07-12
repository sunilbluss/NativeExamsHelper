package com.grudus.nativeexamshelper.activities.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.SingleSubjectExamsActivity;
import com.grudus.nativeexamshelper.activities.UngradedExamsActivity;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.adapters.OldExamsCursorAdapter;
import com.grudus.nativeexamshelper.pojos.Subject;

/**
 * A simple {@link Fragment} subclass.
 */
public class OldExamsFragment extends Fragment {

    public final String TAG = "@@@" + this.getClass().getSimpleName();

    private ListView listView;
    private CursorAdapter cursorAdapter;
    private View header;


    public OldExamsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_old_exams, container, false);
        initViews(view);
        Log.d(TAG, "FRAGMENT 2 ON CREATE VIEW");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateList();
        setOnItemClickListener();
    }


    private void populateList() {
        ExamsDbHelper db = ExamsDbHelper.getInstance(getContext());
        db.openDB();


        cursorAdapter = new OldExamsCursorAdapter(getActivity(), db.getSubjectsWithGrade(), 0);
        setHeader();
        listView.setAdapter(cursorAdapter);
        db.closeDB();
    }

    private void setHeader() {
        header = getLayoutInflater(null).inflate(R.layout.list_item_old_exam, null);
        listView.addHeaderView(header);
    }

    private void initViews(View view) {
        listView = (ListView) view.findViewById(R.id.list_view_old_exams);
    }

    public void removeAll() {
        ExamsDbHelper db = ExamsDbHelper.getInstance(getContext());
        db.openDB();
        db.cleanAllRecords(ExamsContract.OldExamEntry.TABLE_NAME);
        db.resetSubjectGrades();
        cursorAdapter.changeCursor(null);
        db.closeDB();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "FRAGMENT 2 ON PAUSE");
        cursorAdapter.changeCursor(null);
    }

    private void setOnItemClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) cursorAdapter.getItem(position - listView.getHeaderViewsCount());
                String subjectTitle = c.getString(ExamsContract.OldExamEntry.SUBJECT_COLUMN_INDEX);
                ExamsDbHelper db = ExamsDbHelper.getInstance(getContext());

                db.openDB();
                Subject subject = db.findSubjectByTitle(subjectTitle);
                db.closeDB();

                if (subject == null) {
                    Log.e(TAG, "onItemClick: nie ma subjecta", new NullPointerException());
                    return;
                }

                Intent intent = new Intent(getContext(), SingleSubjectExamsActivity.class);
                intent.putExtra(SingleSubjectExamsActivity.INTENT_SUBJECT_TAG, subject);
                startActivity(intent);
                c.close();
            }
        });

        // TODO: 11.07.16 add animations
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityOptionsCompat anim = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
//                        header, getString(R.string.transition_list_item_to_toolbar));
                startActivity(new Intent(getActivity(), UngradedExamsActivity.class)
//                        , anim.toBundle()
                );
            }
        });
    }
}
