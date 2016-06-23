package com.grudus.nativeexamshelper;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import com.grudus.nativeexamshelper.activities.AddingExamMainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class AddingExamMainActivityTest {


    @Rule
    public final ActivityTestRule<AddingExamMainActivity> main
            = new ActivityTestRule<AddingExamMainActivity>(AddingExamMainActivity.class);


    // TODO: 23.06.16 I'll be back 
    @Test
    public void shouldBeAbleToLaunchMainScreen() {
        
    }








}
