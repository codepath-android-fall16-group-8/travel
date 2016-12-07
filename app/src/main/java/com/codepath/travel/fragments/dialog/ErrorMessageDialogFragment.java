package com.codepath.travel.fragments.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.codepath.travel.R;

/**
 * {@link AlertDialog} to show a basic error message.
 */
public class ErrorMessageDialogFragment extends DialogFragment {
    private static final String ERROR_MESSAGE_KEY = "errorMessage";

    public ErrorMessageDialogFragment() {}

    public static ErrorMessageDialogFragment newInstance(String errorMessage) {
        ErrorMessageDialogFragment fragment = new ErrorMessageDialogFragment();
        Bundle args = new Bundle();
        args.putString(ERROR_MESSAGE_KEY, errorMessage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogTheme);
        builder.setMessage(getArguments().getString(ERROR_MESSAGE_KEY))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> dismiss());
        return builder.create();
    }
}
