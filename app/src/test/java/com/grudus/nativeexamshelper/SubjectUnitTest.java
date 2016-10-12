package com.grudus.nativeexamshelper;

import com.grudus.nativeexamshelper.pojos.Subject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class SubjectUnitTest {

    private Subject subject;
    private static final String TITLE = "Math";
    private static final String COLOR = "#123456";

    @Before
    public void init() {
        subject = Subject.subjectWithoutId(TITLE, COLOR);
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
        Subject newOne = Subject.subjectWithoutId(TITLE, COLOR);
        assertEquals(newOne, subject);
    }

    @Test
    public void emptySubjectIsEmpty() {
        Subject empty = Subject.empty();
        assertTrue(empty.isEmpty());
    }

    @Test
    public void theSameSubjectButNotCopiedShouldHasTheSameHashCode() {
        Subject newOne = Subject.subjectWithoutId(TITLE, COLOR);
        assertEquals(newOne.hashCode(), subject.hashCode());
    }


    @Test(expected = Exception.class)
    public void newInstanceWithNullShouldThrowAnException() {
        new Subject(null, null, null);
    }

    @Test(expected = Exception.class)
    public void setTitleWithNullShouldThrowAnException() {
        subject.setTitle(null);
    }

    @Test(expected = Exception.class)
    public void setColorWithNullShouldThrowAnException() {
        subject.setColor(null);
    }




    @After
    public void destroy() {
        subject = null;
    }
}