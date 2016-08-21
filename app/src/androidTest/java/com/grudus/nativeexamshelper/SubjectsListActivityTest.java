package com.grudus.nativeexamshelper;

import android.database.MatrixCursor;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.test.espresso.core.deps.guava.base.Strings;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.grudus.nativeexamshelper.activities.SubjectsListActivity;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.subjects.SubjectsContract;
import com.grudus.nativeexamshelper.pojos.Subject;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.grudus.nativeexamshelper.RecyclerViewMatcher.nthRecyclerViewChild;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
public class SubjectsListActivityTest {

    private static final String REGEX_ANY_CHAR = ".+";

    private static final int RECYCLER_VIEW_ID = R.id.subjects_recycler_view;
    private static final int FLOATING_BUTTON_ID = R.id.floating_button_add_subject;

    private final Subject SUBJECT_0 = new Subject("Math", "#123456");
    private final Subject SUBJECT_1 = new Subject("Physics", "#19a5ac");
    private final Subject SUBJECT_2 = new Subject("Computer Science", "#666666");

    private final Subject[] SUBJECTS = {SUBJECT_0, SUBJECT_1, SUBJECT_2};


    @Rule
    public ActivityTestRule<SubjectsListActivity> rule =
            new ActivityTestRule<>(SubjectsListActivity.class);



    @Mock
    private ExamsDbHelper dbHelper;

    private MatrixCursor matrixCursor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        ExamsDbHelper.setInstance(dbHelper);

        matrixCursor = new MatrixCursor(SubjectsContract.SubjectEntry.ALL_COLUMNS);
        for (int i = 0; i < SUBJECTS.length; i++) {
            final Subject subject = SUBJECTS[i];
            matrixCursor.addRow(new Object[] {i, subject.getTitle(), subject.getColor(), 1});
        }

        when(dbHelper.getAllSubjectsSortByTitle())
                .then(mock -> Observable.create(subscriber -> {
                    subscriber.onNext(matrixCursor);
                    subscriber.onCompleted();
                }));

        when(dbHelper.removeSubject(anyString()))
                .then(mock -> {
                    final String input = (String) mock.getArguments()[0];
                    matrixCursor.close();
                    matrixCursor = new MatrixCursor(SubjectsContract.SubjectEntry.ALL_COLUMNS);
                    for (int i = 0; i < SUBJECTS.length; i++) {
                        final Subject subject = SUBJECTS[i];
                        if (subject.getTitle().equals(input))
                            continue;
                        matrixCursor.addRow(new Object[] {i, subject.getTitle(), subject.getColor(), 1});
                    }

                    return Observable.create(sub -> {
                        sub.onNext(1);
                        sub.onCompleted();
                    });
                });

        when(dbHelper.removeAllExamsRelatedWithSubject(anyString()))
                .then(mock -> Observable.empty());

        rule.getActivity().setExamsDbHelper(dbHelper);
    }

    @After
    public void clean() {
        matrixCursor.close();
    }

    private static Matcher<View> nthChild(final int index) {
        return nthRecyclerViewChild(index, RECYCLER_VIEW_ID);
    }



    @Test
    public void allViewsAreDisplayed() {
        onView(is(instanceOf(RecyclerView.class))).check(matches(isDisplayed()));
        onView(is(instanceOf(FloatingActionButton.class))).check(matches(isDisplayed()));

        for (int i = 0; i < SUBJECTS.length; i++)
            onView(nthChild(i)).check(matches(isDisplayed()));

        onView(nthChild(SUBJECTS.length)).check(doesNotExist());
    }

    @Test
    public void clickOnListItemOpenDialog() {
        onView(withId(R.id.dialog_edit_subject_colorview)).check(doesNotExist());

        onView(nthChild(0)).perform(click());

        onView(withId(R.id.dialog_edit_subject_colorview)).check(matches(isDisplayed()));
    }

    @Test
    public void fieldsInEditSubjectDialogReflectSubjectState() {
        onView(nthChild(1)).perform(click());

        onView(withId(R.id.edit_subject_title))
                .check(matches(withText(SUBJECT_1.getTitle())));

        final int color = Color.parseColor(SUBJECT_1.getColor());
        final int red = Color.red(color);
        final int green = Color.green(color);
        final int blue = Color.blue(color);

        onView(allOf(is(instanceOf(TextView.class)), withText(String.valueOf(red))))
                .check(matches(isDisplayed()));
        onView(allOf(is(instanceOf(TextView.class)), withText(String.valueOf(green))))
                .check(matches(isDisplayed()));
        onView(allOf(is(instanceOf(TextView.class)), withText(String.valueOf(blue))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void swipeToRightDeleteItem() {
        assertEquals(3, matrixCursor.getCount());

        onView(nthChild(1)).perform(swipeRight());

        assertEquals(2, matrixCursor.getCount());
    }


}
