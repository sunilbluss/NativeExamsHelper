package com.grudus.nativeexamshelper.activities.sliding;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grudus.nativeexamshelper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OldExamsFragment extends Fragment {


    public OldExamsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_old_exams, container, false);
    }

}
