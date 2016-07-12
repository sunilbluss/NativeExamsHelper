package com.grudus.nativeexamshelper.helpers;


import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.grudus.nativeexamshelper.pojos.OldExam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeStatisticsCalculator {
    private final double MINIMAL_GRADE_TO_PASS;

    private Map<Double, Integer> gradesOccurrences;
    private int maxGradeOccurrences = 0;
    private ArrayList<Double> dominants;
    private double median, sum = 0;
    private int passed = 0, numberOfGrades = 0;

    private Cursor sortedGradesCursor;
    private int gradesColumnDatabaseIndex;


    public GradeStatisticsCalculator(double minimalGradeToPass) {
        gradesOccurrences = new HashMap<>();
        dominants = new ArrayList<>();
        MINIMAL_GRADE_TO_PASS = minimalGradeToPass;
    }

    public void setUpDatabaseData(@NonNull Cursor sortedGradesCursor, int gradesColumnIndex) {
        this.sortedGradesCursor = sortedGradesCursor;
        this.gradesColumnDatabaseIndex = gradesColumnIndex;
    }

    public void calculateFromDatabase() {
        if (sortedGradesCursor == null) throw new IllegalStateException("You have to set up database");
        sortedGradesCursor.moveToFirst();

        double tempGrade;
        int numberOfOccurrencesOfSingleGrade = 0;
        double previousGrade = -1;

        numberOfGrades = sortedGradesCursor.getCount();

        do {
            tempGrade = sortedGradesCursor.getDouble(gradesColumnDatabaseIndex);

            if (previousGrade == -1) previousGrade = tempGrade;

            if (tempGrade > previousGrade) {
                gradesOccurrences.put(previousGrade, numberOfOccurrencesOfSingleGrade);
                previousGrade = tempGrade;
                checkIfMaxOccurrencesWasReached(numberOfOccurrencesOfSingleGrade);
                numberOfOccurrencesOfSingleGrade = 1;
            }

            else numberOfOccurrencesOfSingleGrade++;

            sum += tempGrade;


            if (tempGrade > MINIMAL_GRADE_TO_PASS) {
                passed++;
            }


        } while (sortedGradesCursor.moveToNext());

        gradesOccurrences.put(tempGrade, numberOfOccurrencesOfSingleGrade);
        checkIfMaxOccurrencesWasReached(numberOfOccurrencesOfSingleGrade);
        calculateMedianFromDatabase();
        calculateDominants();
        closeDatabase();
    }

    private void checkIfMaxOccurrencesWasReached(int numberOfOccurrencesOfSingleGrade) {
        if (numberOfOccurrencesOfSingleGrade > maxGradeOccurrences)
            maxGradeOccurrences = numberOfOccurrencesOfSingleGrade;
    }


    private void calculateMedianFromDatabase() {
        if (sortedGradesCursor == null) throw new IllegalStateException("You have to set up database");

        sortedGradesCursor.moveToPosition(numberOfGrades / 2);

        double middleGrade = sortedGradesCursor.getDouble(gradesColumnDatabaseIndex);

        // if number is even, then median is an average of two 'middle' values
        if (numberOfGrades % 2 == 0) {
            sortedGradesCursor.moveToPosition(numberOfGrades / 2 - 1);
            double secondMiddle = sortedGradesCursor.getDouble(gradesColumnDatabaseIndex);
            median = (middleGrade + secondMiddle) / 2;
        }
        else median = middleGrade;

    }

    private void closeDatabase() {
        sortedGradesCursor.close();
        sortedGradesCursor = null;
    }

    public double getAverage() {
        return  sum / numberOfGrades;
    }


    public double getMedian() {
        return median;
    }

    @Nullable
    public ArrayList<Double> getDominants() {
        return dominants.size() == gradesOccurrences.size() ? null : dominants;
    }

    private void calculateDominants() {
        Log.d("@@@", "calculateDominants: " + gradesOccurrences.toString());
        for (Double d : gradesOccurrences.keySet()) {
            if (gradesOccurrences.get(d) == maxGradeOccurrences)
                dominants.add(d);
        }
    }


    public int getNumberOfPassedExams() {
        return passed;
    }

    public int getNumberOfFailedExams() {
        return numberOfGrades - passed;
    }

    public double getPercentOfPassedExams() {
        return ((double) passed) / numberOfGrades;
    }
    
    

}
