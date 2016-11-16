package com.codepath.travel.fragments.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
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
    private static final String TAG = ComposeNoteDialogFragment.class.getSimpleName();
    private static final String STORY_POSITION_KEY = "storyPosition";
    private static final String PLACE_NAME_KEY = "placeName";
    private static final String MEDIA_ID_KEY = "mediaId";
    private static final String NOTE_TEXT_KEY = "noteText";

    @BindView(R.id.etBody) EditText etBody;
    @BindView(R.id.tvPlaceName) TextView tvPlaceName;
    @BindView(R.id.btnCancel) ImageButton btnCancel;
    @BindView(R.id.btnDelete) ImageButton btnDelete;
    @BindView(R.id.btnSave) Button btnSave;
    private Unbinder unbinder;

    private ComposeNoteListener listener;
    private int position;
    boolean editing;

    public interface ComposeNoteListener {
        void onComposeSave(int position, String noteText, String mediaId);
        void onComposeDelete(int position, String mediaId);
    }

    public ComposeNoteDialogFragment() {
        // empty constructor
    }

    // Todo: EDITING needs media item id, to get get the media item
    // then can extract the note text, and place name?

    public static ComposeNoteDialogFragment newInstance(int position, String placeName,
            String mediaId, String noteText) {
        ComposeNoteDialogFragment fragment = new ComposeNoteDialogFragment();
        Bundle args = new Bundle();
        args.putInt(STORY_POSITION_KEY, position);
        args.putString(PLACE_NAME_KEY, placeName);
        args.putString(MEDIA_ID_KEY, mediaId);
        args.putString(NOTE_TEXT_KEY, noteText);
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
        position = getArguments().getInt(STORY_POSITION_KEY);
        setupViews();
        return view;
    }

    private void setupViews() {
        tvPlaceName.setText(getArguments().getString(PLACE_NAME_KEY));
        // if mediaId and noteText were provided, this is an existing note being edited
        Bundle args = getArguments();
        String mediaId = args.getString(MEDIA_ID_KEY);
        String noteText = args.getString(NOTE_TEXT_KEY);
        if (mediaId != null && !TextUtils.isEmpty(mediaId)
                && noteText != null && !TextUtils.isEmpty(noteText)) {
            Log.d(TAG, String.format("populating note text for editing (mediaId: %s)", mediaId));
            etBody.setText(noteText);
            etBody.setSelection(noteText.length());
            editing = true;
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "new note");
            editing = false;
            btnDelete.setVisibility(View.INVISIBLE);
        }

        btnSave.setOnClickListener(v -> {
            listener.onComposeSave(position, etBody.getText().toString(), mediaId);
            dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            listener.onComposeDelete(position, mediaId);
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
