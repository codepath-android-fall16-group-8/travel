package com.codepath.travel.fragments.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.codepath.travel.R;

/**
 * {@link AlertDialog} to confirm trip deletion.
 */
public class ConfirmDeleteTripDialogFragment extends DialogFragment {

    private DeleteTripListener listener;

    public interface DeleteTripListener {
        void onDeleteTrip();
    }

    public ConfirmDeleteTripDialogFragment() {
    }

    public static ConfirmDeleteTripDialogFragment newInstance() {
        ConfirmDeleteTripDialogFragment fragment = new ConfirmDeleteTripDialogFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.listener = (DeleteTripListener) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogTheme);
        builder.setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.delete_trip, (dialog, id) -> {
                    listener.onDeleteTrip();
                    dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dismiss());
        return builder.create();
    }
}
