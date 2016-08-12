package com.grudus.nativeexamshelper.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.grudus.nativeexamshelper.R;


public class EnterTextDialog extends DialogFragment {

    private View root;
    private EditText editText;
    private OnTextReceivedListener listener;

    private String title;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        initViews(builder);
        initOnGlobalLayoutListener();

        builder.setTitle(
                title == null ? getString(R.string.dialog_enter_text_title) : title);

        builder.setView(root);
        return builder.create();
    }

    private void initViews(AlertDialog.Builder builder) {
        root = getActivity().getLayoutInflater().inflate(R.layout.dialog_enter_text, null);
        editText = (EditText) root.findViewById(R.id.dialog_edit_text);

        builder.setPositiveButton(getString(R.string.button_text_save), null);
        builder.setNegativeButton(getString(R.string.button_text_back), null);
    }

    private void initOnGlobalLayoutListener() {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setUpButtonColors();
                setUpButtonListeners();
            }
        });
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void setUpButtonListeners() {
        AlertDialog dialog = (AlertDialog) getDialog();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (listener != null)
                listener.onCorrectInput(editText.getText().toString());
            hideKeyboard();
            dismiss();
        });

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(v -> {
            hideKeyboard();
            dismiss();
        });
    }

    private void setUpButtonColors() {
        TypedValue typedValue = new TypedValue();
        getActivity().getTheme()
                .resolveAttribute(R.attr.colorAccent, typedValue, true);
        final int color = typedValue.data;

        final AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(color);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(color);
    }

    public EnterTextDialog addListener(OnTextReceivedListener listener) {
        this.listener = listener;
        return this;
    }

    public EnterTextDialog addTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public void onResume() {
        super.onResume();
        showKeyboard();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
        dismiss();
    }

    public interface OnTextReceivedListener {
        void onCorrectInput(String text);
    }
}
