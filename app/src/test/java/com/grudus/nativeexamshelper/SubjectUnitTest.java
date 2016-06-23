package com.grudus.nativeexamshelper;

import com.grudus.nativeexamshelper.pojos.Subject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class SubjectUnitTest {

    private Subject subject;
    private static final String TITLE = "Math";
    private static final String COLOR = "#123456";

    @Before
    public void init() {
        subject = new Subject(TITLE, COLOR);
    }

    @Test
    public void shouldNotBeEmpty() {
        assertFalse(subject.isEmpty());
    }

    @Test
    public void copiedSubjectShouldBeEqual() {
        Subject newOne = subject.copy();
        assertEquals(newOne, subject);
    }

    @Test
    public void copiedSubjectShouldHasTheSameHashCode() {
        Subject newOne = subject.copy();
        assertEquals(newOne.hashCode(), subject.hashCode());
    }

    @Test
    public void theSameSubjectButNotCopiedShouldBeEqual() {
        Subject newOne = new Subject(TITLE, COLOR);
        assertEquals(newOne, subject);
    }

    @Test
    public void emptySubjectIsEmpty() {
        Subject empty = Subject.empty();
        assertTrue(empty.isEmpty());
    }

    @Test
    public void theSameSubjectButNotCopiedShouldHasTheSameHashCode() {
        Subject newOne = new Subject(TITLE, COLOR);
        assertEquals(newOne.hashCode(), subject.hashCode());
    }


    @Test(expected = ExceptionsHelper.EmptyStringException.class)
    public void newInstanceWithNullShouldThrowAnException() {
        new Subject(null, null);
    }

    @Test(expected = ExceptionsHelper.EmptyStringException.class)
    public void setTitleWithNullShouldThrowAnException() {
        subject.setTitle(null);
    }

    @Test(expected = ExceptionsHelper.EmptyStringException.class)
    public void setColorWithNullShouldThrowAnException() {
        subject.setColor(null);
    }




    @After
    public void destroy() {
        subject = null;
    }
}