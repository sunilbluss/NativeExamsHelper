package com.grudus.nativeexamshelper;

import android.database.MatrixCursor;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.grudus.nativeexamshelper.activities.SubjectsListActivity;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.subjects.SubjectsContract;
import com.grudus.nativeexamshelper.pojos.Subject;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
public class SubjectsListActivityTest {


    private static final int RECYCLER_VIEW_ID = R.id.subjects_recycler_view;

    private final Subject SUBJECT_0 = Subject.subjectWithoutId("Math", "#123456");
    private final Subject SUBJECT_1 = Subject.subjectWithoutId("Physics", "#19a5ac");
    private final Subject SUBJECT_2 = Subject.subjectWithoutId("Computer Science", "#666666");

    private final Subject[] SUBJECTS = {SUBJECT_0, SUBJECT_1, SUBJECT_2};

    private String BUTTON_OK_TEXT;

    @Rule
    public ActivityTestRule<SubjectsListActivity> rule =
            new ActivityTestRule<>(SubjectsListActivity.class);



    @Mock
    private ExamsDbHelper dbHelper;

    private MatrixCursor matrixCursor;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

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

        when(dbHelper.removeAllExamsRelatedWithSubject(anyLong()))
                .then(mock -> Observable.empty());

        when(dbHelper.insertSubject(any()))
                .then(mock -> {
                    Subject sub = (Subject) mock.getArguments()[0];
                    matrixCursor.addRow(new Object[] {matrixCursor.getCount(), sub.getTitle(), sub.getColor(), 0});
                    return Observable.create(subscriber -> {
                        subscriber.onNext(1L);
                        subscriber.onCompleted();
                    });
                });

        ExamsDbHelper.setInstance(dbHelper);


        rule.getActivity().runOnUiThread(() -> {
            rule.getActivity().initDatabase();
            rule.getActivity().getAdapter().changeCursor(matrixCursor);
            rule.getActivity().getAdapter().notifyDataSetChanged();
        });

        BUTTON_OK_TEXT = rule.getActivity().getString(R.string.button_text_save);

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

    @Test
    public void addingNewSubjectTest() {
        assertEquals(3, matrixCursor.getCount());
        onView(nthChild(3)).check(doesNotExist());

        onView(is(instanceOf(FloatingActionButton.class))).perform(click());

        Subject newOne = Subject.subjectWithoutId("Babilon", "#32bbf2");

        onView(withId(R.id.edit_subject_title)).perform(typeText(newOne.getTitle()), closeSoftKeyboard());
        onView(allOf(is(instanceOf(Button.class)), withText(BUTTON_OK_TEXT))).perform(click());

        assertEquals(4, matrixCursor.getCount());
        onView(nthChild(3)).check(matches(isDisplayed()));
    }


}
