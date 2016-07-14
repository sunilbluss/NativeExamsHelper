package com.grudus.nativeexamshelper.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.pojos.grades.Grades;


public class SelectGradeDialog extends DialogFragment {

    private int gradePickerValue, additionalPickerValue;
    private DialogInterface.OnClickListener listener;
    private NumberPicker picker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
// TODO: 13.07.16 change  dialog layout

        View root = inflater.inflate(R.layout.dialog_select_grade, null);
        builder.setView(root);
        picker = (NumberPicker) root.findViewById(R.id.mainGradePicker);
        picker.setMinValue(1);
        picker.setMaxValue(6);
        final NumberPicker picker1 = (NumberPicker) root.findViewById(R.id.additionalGradePicker);
        picker1.setMinValue(0);
        picker1.setMaxValue(5);
        picker1.setDisplayedValues(new String[] {"-", " ", "+", "-", " ", "+"});
        picker1.setWrapSelectorWheel(false);

        picker1.setEnabled(false);
        builder.setTitle("Wybierz ocenę");


        builder.setPositiveButton("Wybierz", listener);
        builder.setNegativeButton("Wróć", null);


        return builder.create();
    }


    public void setListener(DialogInterface.OnClickListener listener) {
        this.listener = listener;
    }

    public double getSelectedGrade() {
        return picker.getValue();
    }

}