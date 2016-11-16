package com.codepath.travel.fragments.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codepath.travel.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Modal overlay for composing notes.
 */
public class ComposeNoteDialogFragment extends DialogFragment {
    private static final String POSITION_KEY = "position";
    private static final String PLACE_NAME_KEY = "placeName";

    @BindView(R.id.etBody) EditText etBody;
    @BindView(R.id.tvPlaceName) TextView tvPlaceName;
    @BindView(R.id.btnCancel) ImageButton btnCancel;
    @BindView(R.id.btnSave) Button btnSave;
    private Unbinder unbinder;

    private ComposeNoteListener listener;
    private int position;

    public interface ComposeNoteListener {
        void onComposeSave(int position, String noteText);
    }

    public ComposeNoteDialogFragment() {
        // empty constructor
    }

    public static ComposeNoteDialogFragment newInstance(int position, String placeName) {
        ComposeNoteDialogFragment fragment = new ComposeNoteDialogFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION_KEY, position);
        args.putString(PLACE_NAME_KEY, placeName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose_note, container);
        unbinder = ButterKnife.bind(this, view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        listener = (ComposeNoteListener) getActivity();
        position = getArguments().getInt(POSITION_KEY);
        setupViews();
        return view;
    }

    private void setupViews() {
        tvPlaceName.setText(getArguments().getString(PLACE_NAME_KEY));
        btnSave.setOnClickListener(v -> {
            listener.onComposeSave(position, etBody.getText().toString());
            dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
