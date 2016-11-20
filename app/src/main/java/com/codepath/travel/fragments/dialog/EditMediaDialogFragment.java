package com.codepath.travel.fragments.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.helper.ImageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Modal overlay for editing a Media item.
 */
public class EditMediaDialogFragment extends DialogFragment {
    private static final String TAG = EditMediaDialogFragment.class.getSimpleName();
    private static final String STORY_POSITION_KEY = "storyPosition";
    private static final String PLACE_NAME_KEY = "placeName";
    private static final String MEDIA_ID_KEY = "mediaId";
    private static final String CAPTION_KEY = "caption";
    private static final String DATA_KEY = "data";

    @BindView(R.id.btnCancel) ImageButton btnCancel;
    @BindView(R.id.tvPlaceName) TextView tvPlaceName;
    @BindView(R.id.btnDelete) ImageButton btnDelete;
    @BindView(R.id.llPhotoView) LinearLayout llPhotoView;
    @BindView(R.id.ivPhoto) ImageView ivPhoto;
    @BindView(R.id.btnSetStoryPlaceCover) Button btnSetStoryPlaceCover;
    @BindView(R.id.btnSetTripCover) Button btnSetTripCover;
    @BindView(R.id.btnSetUserCover) Button btnSetUserCover;
    @BindView(R.id.etCaption) EditText etCaption;
    @BindView(R.id.btnSave) Button btnSave;
    private Unbinder unbinder;

    private EditMediaListener listener;
    private int position;

    public interface EditMediaListener {
        void onSaveCaption(int position, String caption, String mediaId);
        void onDelete(int position, String mediaId);
        void onSetStoryPlaceCoverPhoto(int position, String coverUrl);
        void onSetTripCoverPhoto(String coverUrl);
        void onSetUserCoverPhoto(String coverUrl);
    }

    public EditMediaDialogFragment() {
        // empty constructor
    }

    public static EditMediaDialogFragment newInstance(int position, String placeName,
            String mediaId, String caption, String data) {
        EditMediaDialogFragment fragment = new EditMediaDialogFragment();
        Bundle args = new Bundle();
        args.putInt(STORY_POSITION_KEY, position);
        args.putString(PLACE_NAME_KEY, placeName);
        args.putString(MEDIA_ID_KEY, mediaId);
        args.putString(CAPTION_KEY, caption);
        args.putString(DATA_KEY, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = (EditMediaListener) getActivity();
        position = getArguments().getInt(STORY_POSITION_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_media, container);
        unbinder = ButterKnife.bind(this, view);
        setupViews();
        return view;
    }

    protected void setupViews() {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Bundle args = getArguments();
        tvPlaceName.setText(args.getString(PLACE_NAME_KEY));

        String mediaId = args.getString(MEDIA_ID_KEY);
        if (mediaId != null) {
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.INVISIBLE);
        }

        String caption = args.getString(CAPTION_KEY);
        if (caption != null && !TextUtils.isEmpty(caption)) {
            etCaption.setText(caption);
            etCaption.setSelection(caption.length());
        }

        btnCancel.setOnClickListener(v -> {
            dismiss();
        });

        btnSave.setOnClickListener(v -> {
            listener.onSaveCaption(position, etCaption.getText().toString(), mediaId);
            dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            listener.onDelete(position, mediaId);
            dismiss();
        });

        // photo-specific
        String data = args.getString(DATA_KEY);
        if (data != null && !TextUtils.isEmpty(data)) {
            ImageUtils.loadImage(ivPhoto, data, android.R.drawable.gallery_thumb, null);
            btnSetStoryPlaceCover.setOnClickListener(v -> {
                listener.onSetStoryPlaceCoverPhoto(position, data);
            });
            btnSetTripCover.setOnClickListener(v -> {
                listener.onSetTripCoverPhoto(data);
            });
            btnSetUserCover.setOnClickListener(v -> {
                listener.onSetUserCoverPhoto(data);
            });
        } else {
            llPhotoView.setVisibility(View.GONE);
        }
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
