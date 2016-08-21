package com.grudus.nativeexamshelper;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;
import android.widget.EditText;

import com.grudus.nativeexamshelper.activities.AddExamActivity;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.helpers.CalendarDialogHelper;
import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.pojos.Exam;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
public class AddExamTest {

    private static final int[] IMAGE_IDS = {R.id.add_exam_image_view_1, R.id.add_exam_image_view_2, R.id.add_exam_image_view_3, R.id.add_exam_image_view_4};
    private static final int BUTTON_ID = R.id.add_exam_button;
    private static final int[] TEXT_IDS = {R.id.add_exam_subject_input, R.id.add_exam_extras_input, R.id.add_exam_time_input, R.id.add_exam_date_input};

    private static final String REGEX_ANY_CHAR = ".+";


    private Calendar calendar;
    private Date date;

    private String buttonCancelText;
    private String buttonOkText;


    @Rule
    public ActivityTestRule<AddExamActivity> rule =
            new ActivityTestRule<>(AddExamActivity.class);


    @Mock
    private ExamsDbHelper dbHelper;
    @Mock
    private CalendarDialogHelper calendarDialogHelper;

    private ArrayList<Exam> database;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        rule.getActivity().startTesting();
        initCalendarAndDate();
        initCalendarDialogHelperBehaviour();
        initTimeDialogHelperBehaviour();

        database = new ArrayList<>();

        when(dbHelper.insertExam(any()))
                .then(mock -> {
                    database.add((Exam)mock.getArguments()[0]);
                    return Observable.empty();
                });

        rule.getActivity().setDatabase(dbHelper);
    }

    @Before
    public void initStrings() {
        buttonCancelText = rule.getActivity().getString(R.string.button_text_back);
        buttonOkText = rule.getActivity().getString(R.string.button_text_save);
    }

    private void initCalendarAndDate() {
        calendar = Calendar.getInstance();
        calendar.set(2016, 10, 10, 9, 11);
        date = calendar.getTime();
    }

    private void initCalendarDialogHelperBehaviour() {
        when(calendarDialogHelper.getDate()).then(a -> date);
        when(calendarDialogHelper.getCalendar()).then(a -> calendar);
    }

    private void initTimeDialogHelperBehaviour() {
        when(calendarDialogHelper.getDate()).then(a -> date);
        when(calendarDialogHelper.getCalendar()).then(a -> calendar);
    }

    @After
    public void clean() {
        database.clear();
        database = null;

        for (int id : TEXT_IDS)
            onView(withId(id)).perform(clearText());
    }

    @Test
    public void allViewsAreDisplayed() {
        final int length = TEXT_IDS.length;
        assertEquals("All images have input view", length, IMAGE_IDS.length);

        for (int i = 0; i < length; i++) {
            onView(withId(TEXT_IDS[i])).check(matches(isDisplayed()));
            onView(withId(IMAGE_IDS[i])).check(matches(isDisplayed()));
        }

        onView(withId(BUTTON_ID)).check(matches(isDisplayed()));
    }



    @Test
    public void clickOnSubjectInputViewOpensSubjectListDialog() {
        onView(withId(R.id.subjects_recycler_view)).check(doesNotExist());

        onView(withId(TEXT_IDS[0])).perform(click());

        onView(withId(R.id.subjects_recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnSubjectListDialogElementPrintsSubject() {

        EditText editText = (EditText) rule.getActivity().findViewById(TEXT_IDS[0]);
        assertNotNull(editText);

        String text = editText.getText().toString();
        assertFalse(text.matches(REGEX_ANY_CHAR));

        onView(withId(TEXT_IDS[0])).perform(click());
        onView(withId(R.id.subjects_recycler_view))
                .perform(actionOnItemAtPosition(0, click()));


        String newText = editText.getText().toString();
        assertTrue(newText.matches(REGEX_ANY_CHAR));
    }

    @Test
    public void clickOnInfoInputViewOpensEnterTextDialog() {
        onView(withId(R.id.dialog_edit_text)).check(doesNotExist());

        onView(withId(TEXT_IDS[1])).perform(click());

        onView(withId(R.id.dialog_edit_text)).check(matches(isDisplayed()));
    }

    @Test
    public void cancelEnterTextDialogDoesNotChangeView() {
        onView(withId(TEXT_IDS[1])).check(matches(withText("")));
        onView(withId(TEXT_IDS[1])).perform(click());

        onView(allOf(is(instanceOf(Button.class)), withText(buttonCancelText))).perform(click());

        onView(withId(TEXT_IDS[1])).check(matches(withText("")));
    }

    @Test
    public void textFromEnterTextDialogBecomeTextViewText() {
        EditText editText = (EditText) rule.getActivity().findViewById(TEXT_IDS[1]);
        String string = "Integral";

        assertNotNull(editText);
        assertNotEquals(string, editText.getText().toString());

        onView(withId(editText.getId())).perform(click());
        onView(withId(R.id.dialog_edit_text)).perform(typeText(string));
        onView(allOf(is(instanceOf(Button.class)), withText(buttonOkText))).perform(click());

        assertEquals(string, editText.getText().toString());
    }

    @Test
    public void textFromEnterTextDialogDisappearAfterCancelButtonClick() {
        EditText editText = (EditText) rule.getActivity().findViewById(TEXT_IDS[1]);
        String string = "Integral";

        assertNotNull(editText);
        assertTrue(editText.getText().toString().isEmpty());

        onView(withId(editText.getId())).perform(click());
        onView(withId(R.id.dialog_edit_text)).perform(typeText(string));
        onView(allOf(is(instanceOf(Button.class)), withText(buttonCancelText))).perform(click());

        assertTrue(editText.getText().toString().isEmpty());
    }

    @Test
    public void addingExamIsPossibleWhenAllInputsAreValid() {
        assertTrue(database.isEmpty());


        rule.getActivity().setCalendarDialog(calendarDialogHelper);

        onView(withId(TEXT_IDS[0])).perform(replaceText("Math"));
        onView(withId(TEXT_IDS[1])).perform(replaceText("Integral"));
        onView(withId(TEXT_IDS[2])).perform(replaceText("09:11"));
        onView(withId(TEXT_IDS[3])).perform(replaceText(DateHelper.getStringFromDate(calendar.getTime())));

        onView(withId(BUTTON_ID)).perform(click());

        assertFalse(database.isEmpty());
    }

    @Test
    public void cannotAddExamWithoutSubject() {
        onView(withId(TEXT_IDS[1])).perform(replaceText("Integral"));
        onView(withId(TEXT_IDS[2])).perform(replaceText("11:11"));
        onView(withId(TEXT_IDS[3])).perform(replaceText("11/09/2001"));

        assertTrue(database.isEmpty());
        onView(is(instanceOf(Button.class))).perform(click());
        assertTrue(database.isEmpty());
    }

    @Test
    public void addingExamIsPossibleWithoutExtraInfo() {
        onView(withId(TEXT_IDS[0])).perform(replaceText("Math"));
        onView(withId(TEXT_IDS[2])).perform(replaceText("11:11"));
        onView(withId(TEXT_IDS[3])).perform(replaceText("11/09/2001"));

        assertTrue(database.isEmpty());
        onView(is(instanceOf(Button.class))).perform(click());
        assertFalse(database.isEmpty());
    }

    @Test
    public void addingExamIsPossibleWithoutTimeInfo() {
        onView(withId(TEXT_IDS[0])).perform(replaceText("Math"));
        onView(withId(TEXT_IDS[1])).perform(replaceText("Integral"));
        onView(withId(TEXT_IDS[3])).perform(replaceText("11/09/2001"));

        assertTrue(database.isEmpty());
        onView(is(instanceOf(Button.class))).perform(click());
        assertFalse(database.isEmpty());
    }

    @Test
    public void addingExamIsImpossibleWithoutDateInput() {
        onView(withId(TEXT_IDS[0])).perform(replaceText("Math"));
        onView(withId(TEXT_IDS[1])).perform(replaceText("Integral"));
        onView(withId(TEXT_IDS[2])).perform(replaceText("09:11"));

        assertTrue(database.isEmpty());
        onView(is(instanceOf(Button.class))).perform(click());
        assertTrue(database.isEmpty());
    }








}
