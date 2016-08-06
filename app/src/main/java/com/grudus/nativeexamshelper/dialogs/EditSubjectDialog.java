package com.grudus.nativeexamshelper.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.pojos.Subject;

public class EditSubjectDialog extends DialogFragment {

    private final String TAG = "@@@" + this.getClass().getSimpleName();

    private final int[] seekBarsIds = {R.id.seekbar_red, R.id.seekbar_green, R.id.seekbar_blue};
    private static final int MAX_RGB_VALUE = 255;

    private int[] colors;

    private Subject editedSubject = Subject.empty();
    protected OnSaveListener onSaveListener;

    private Rect rect;
    private int seekBarLeft;

    private SeekBar[] seekBarsRGB;
    private TextView[] seekBarsTextViews;
    private EditText subjectInput;
    private View root;
    private View colorView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        root = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_subject, null);

        initColors();
        initViews();
        initListeners();
        builder.setPositiveButton(getString(R.string.button_text_save), null);
        builder.setNegativeButton(getString(R.string.button_text_back), null);

        if (!editedSubject.isEmpty()) {
            subjectInput.setText(editedSubject.getTitle());
        }



        root.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setWidthTo90percentOfScreenWidth();
                setUpSeekBars();
                setUpButtonsListener(builder);
                setButtonsColor();
            }
        });


        builder.setView(root);
        return builder.create();
    }


    private void initColors() {
        colorView = root.findViewById(R.id.dialog_edit_subject_colorview);
        colorView.setBackgroundColor(Color.parseColor(editedSubject.getColor()));
        colors = new int[3];
        final int color = Color.parseColor(editedSubject.getColor());

        colors[0] = Color.red(color);
        colors[1] = Color.green(color);
        colors[2] = Color.blue(color);
    }

    private void initViews() {
        final int length = seekBarsIds.length;
        seekBarsRGB = new SeekBar[length];
        seekBarsTextViews = new TextView[length];
        subjectInput = (EditText) root.findViewById(R.id.edit_subject_title);
        rect = new Rect();

        for (int i = 0; i < length; i++) {
            final ViewGroup viewGroup = (ViewGroup) root.findViewById(seekBarsIds[i]);
            seekBarsRGB[i] = (SeekBar) viewGroup.getChildAt(1);
            seekBarsRGB[i].setProgress(colors[i]);
            seekBarsTextViews[i] = (TextView) viewGroup.getChildAt(0);

            seekBarsTextViews[i].setText(String.valueOf(colors[i]));
        }

        seekBarLeft = seekBarsRGB[0].getPaddingLeft();
    }


    private void initListeners() {
        for (int i = 0; i < seekBarsRGB.length; i++) {
            final int temp = i;
            seekBarsRGB[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    seekBarsTextViews[temp].setText(progress < 10 ? "  " + String.valueOf(progress) : (progress < 100 ? " " + progress : progress + ""));

                    setSeekBarsTextViewsPosition(temp, progress);

                    colors[temp] = progress;
                    updateColorView();
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }

    private void setSeekBarsTextViewsPosition(int whichOne, int progress) {
        seekBarsRGB[whichOne].setProgress(progress);
        rect = seekBarsRGB[whichOne].getThumb().getBounds();
        seekBarsTextViews[whichOne].setX(rect.left /*+ seekBarLeft*/);

    }


    protected void setUpButtonsListener(AlertDialog.Builder builder) {
        ((AlertDialog)getDialog()).getButton(DialogInterface.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    if (inputIsEmpty()) {
                        Toast.makeText(getActivity(), getString(R.string.warning_add_subject_empty), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!inputIsCorrect()) {
                        Toast.makeText(getActivity(), getString(R.string.warning_add_subject_badchar), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updateSubject();
                    if (onSaveListener != null)
                        onSaveListener.onCorrectInputs(editedSubject);
                    this.dismiss();
                });

        builder.setNegativeButton(getString(R.string.button_text_back), ((dialog, which) -> this.dismiss()));
    }

    protected boolean inputIsEmpty() {
        return subjectInput.getText().toString().replaceAll("\\s+", "").isEmpty();
    }

    protected boolean inputIsCorrect() {
        return Character.isLetter(subjectInput.getText().toString().charAt(0));
    }

    private void setWidthTo90percentOfScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = (int) (metrics.widthPixels * 0.9);
        getDialog().getWindow().setLayout(params.width, params.height);
    }

    private void setUpSeekBars() {
        for (int i = 0; i < seekBarsTextViews.length; i++) {
            setSeekBarsTextViewsPosition(i, colors[i]);


            seekBarsRGB[0].getThumb()
                    .setColorFilter(ContextCompat.getColor(getActivity(), R.color.seekBarRed), PorterDuff.Mode.SRC_IN);

            seekBarsRGB[0].setProgressDrawable(
                    ContextCompat.getDrawable(getActivity(), R.drawable.red_seekbar));

            seekBarsRGB[1].getThumb()
                    .setColorFilter(ContextCompat.getColor(getActivity(), R.color.seekBarGreen), PorterDuff.Mode.SRC_IN);

            seekBarsRGB[1].setProgressDrawable(
                    ContextCompat.getDrawable(getActivity(), R.drawable.green_seekbar));

            seekBarsRGB[2].getThumb()
                    .setColorFilter(ContextCompat.getColor(getActivity(), R.color.seekBarBlue), PorterDuff.Mode.SRC_IN);

            seekBarsRGB[2].setProgressDrawable(
                    ContextCompat.getDrawable(getActivity(), R.drawable.blue_seekbar));

        }
    }

    private void setButtonsColor() {
        TypedValue typedValue = new TypedValue();
        getActivity().getTheme()
                .resolveAttribute(R.attr.colorAccent, typedValue, true);
        final int color = typedValue.data;

        ((AlertDialog)getDialog()).getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(color);
        ((AlertDialog)getDialog()).getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(color);
    }

    private void updateColorView() {
        final int color = Color.rgb(colors[0], colors[1], colors[2]);
        colorView.setBackgroundColor(color);
    }

    private void updateSubject() {
        final int color = Color.rgb(
                seekBarsRGB[0].getProgress(),
                seekBarsRGB[1].getProgress(),
                seekBarsRGB[2].getProgress()
        );
        final String hex = String.format("#%06X", (0xFFFFFF & color));
        editedSubject.setColor(hex);
        editedSubject.setTitle(subjectInput.getText().toString());
    }

    public EditSubjectDialog addSubject(Subject subject) {
        this.editedSubject = subject.copy();
        return this;
    }

    public EditSubjectDialog addListener(OnSaveListener listener) {
        this.onSaveListener = listener;
        return this;
    }

    public interface OnSaveListener {
        void onCorrectInputs(Subject editedSubject);
    }

}
