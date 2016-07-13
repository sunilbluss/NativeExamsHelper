//package com.grudus.nativeexamshelper;
//
//
//import com.grudus.nativeexamshelper.helpers.DateHelper;
//import com.grudus.nativeexamshelper.helpers.ExceptionsHelper;
//import com.grudus.nativeexamshelper.pojos.Exam;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.JUnit4;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//
//import static org.junit.Assert.*;
//
//@RunWith(JUnit4.class)
//public class ExamUnitTest {
//
//
//    private Exam exam;
//    private static final String SUBJECT_TITLE = "Math";
//    private static final String SUBJECT_INFO = "Integrals";
//    private static final String DATE_STRING = "12/12/2012";
//    private static final String DATE_FORMAT = "dd/MM/yyyy";
//
//
//    @Before
//    public void init() throws ParseException {
//        exam = new Exam(SUBJECT_TITLE, SUBJECT_INFO, new SimpleDateFormat(DATE_FORMAT).parse(DATE_STRING));
//    }
//
//
//    @Test(expected = ExceptionsHelper.EmptyStringException.class)
//    public void nullInConstructorShouldThrowAnException() {
//        new Exam(null, null, null);
//    }
//
//    @Test
//    public void dateHelperCreatesDateFromString() throws ParseException {
//        Date date = DateHelper.getDateFromString(DATE_STRING);
//        assertEquals(date, exam.getDate());
//    }
//
//    @Test
//    public void dateHelperCreatesStringFromDate() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(2012, 11, 12);
//        String string = DateHelper.getStringFromDate(calendar.getTime());
//        assertEquals(string, DATE_STRING);
//    }
//
//
//    @After
//    public void destroy() {
//        exam = null;
//    }
//}
