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
    private NumberPicker picker, picker1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
// TODO: 13.07.16 change  dialog layout

        View root = inflater.inflate(R.layout.dialog_select_grade, null);
        builder.setView(root);

        builder.setTitle(getString(R.string.dialog_select_grade_title));
        setUpPickers(root);

        builder.setPositiveButton(getString(R.string.dialog_select_grade_posbut), listener);
        builder.setNegativeButton(getString(R.string.dialog_negbut), null);


        return builder.create();
    }

    private void setUpPickers(View root) {
        picker = (NumberPicker) root.findViewById(R.id.mainGradePicker);
        picker.setMinValue(1);
        picker.setMaxValue(6);
        picker1 = (NumberPicker) root.findViewById(R.id.additionalGradePicker);
        picker1.setMinValue(0);
        picker1.setMaxValue(5);
        picker1.setDisplayedValues(new String[] {"-", " ", "+", "-", " ", "+"});
        picker1.setWrapSelectorWheel(true);

    }


    public void setListener(DialogInterface.OnClickListener listener) {
        this.listener = listener;
    }

    public double getSelectedGrade() {
        switch (picker1.getValue() % 3) {
            case 0: return picker.getValue() - 0.25;
            default:
            case 1: return picker.getValue();
            case 2: return picker.getValue() + 0.25;
        }
    }

}
