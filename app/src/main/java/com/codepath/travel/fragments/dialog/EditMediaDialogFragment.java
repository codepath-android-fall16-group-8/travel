package com.codepath.travel.fragments.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.models.parse.Media;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Modal overlay for editing a Media item.
 */
public class EditMediaDialogFragment extends DialogFragment {
    private static final String TAG = EditMediaDialogFragment.class.getSimpleName();
    private static final String POSITION_KEY = "mediaPosition";
    private static final String PLACE_NAME_KEY = "placeName";
    private static final String CAPTION_KEY = "caption";
    private static final String DATA_KEY = "data";
    private static final String IS_OWNER_KEY = "isOwner";
    private static final String IS_NEW_ITEM_KEY = "isNewItem";

    @BindView(R.id.btnCancel) ImageButton btnCancel;
    @BindView(R.id.tvPlaceName) TextView tvPlaceName;
    @BindView(R.id.btnDelete) ImageButton btnDelete;
    @BindView(R.id.layoutPhotoView) RelativeLayout layoutPhotoView;
    @BindView(R.id.ivPhoto) ImageView ivPhoto;
    @BindView(R.id.tvSetPhoto) TextView tvSetPhoto;
    @BindView(R.id.spSetPhoto) Spinner spSetPhoto;
    @BindView(R.id.etCaption) EditText etCaption;
    @BindView(R.id.tvCaption) TextView tvCaption;
    @BindView(R.id.btnSave) Button btnSave;
    private Unbinder unbinder;

    private EditMediaListener listener;

    public interface EditMediaListener {
        void onSaveCaption(int position, String caption);
        void onDeleteMedia(int position);
        void onSetPhoto(int position, int type);
    }

    public EditMediaDialogFragment() {
        // empty constructor
    }

    public static EditMediaDialogFragment newInstance(String placeName, int position,
            Media mediaItem, boolean isOwner) {

        EditMediaDialogFragment fragment = new EditMediaDialogFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION_KEY, position);
        args.putString(PLACE_NAME_KEY, placeName);
        args.putBoolean(IS_OWNER_KEY, isOwner);
        if (mediaItem == null) {
            args.putBoolean(IS_NEW_ITEM_KEY, true);
        } else {
            args.putBoolean(IS_NEW_ITEM_KEY, false);
            args.putString(CAPTION_KEY, mediaItem.getCaption());
            args.putString(DATA_KEY, mediaItem.getDataUrl());
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = (EditMediaListener) getActivity();
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

        boolean newItem = args.getBoolean(IS_NEW_ITEM_KEY, false);
        int position = args.getInt(POSITION_KEY, -1);
        boolean isOwner = args.getBoolean(IS_OWNER_KEY, false);
        String caption = args.getString(CAPTION_KEY);
        String data = args.getString(DATA_KEY);
        boolean isPhoto = data != null && !TextUtils.isEmpty(data);

        // cancel button
        btnCancel.setOnClickListener(v -> dismiss());

        // delete button for existing items
        if (!newItem && isOwner) {
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.INVISIBLE);
        }

        if (isOwner) {
            // caption edit text
            tvCaption.setVisibility(View.GONE);
            if (caption != null && !TextUtils.isEmpty(caption)) {
                etCaption.setText(caption);
                etCaption.setSelection(caption.length());
            }

            // show save and delete buttons
            btnSave.setOnClickListener(v -> {
                listener.onSaveCaption(position, etCaption.getText().toString());
                int setPhoto = spSetPhoto.getSelectedItemPosition();
                if (isPhoto && setPhoto > 0) {
                    listener.onSetPhoto(position, setPhoto);
                }
                dismiss();
            });
            btnDelete.setOnClickListener(v -> {
                listener.onDeleteMedia(position);
                dismiss();
            });
        } else {
            // caption text view
            etCaption.setVisibility(View.GONE);
            if (caption != null && !TextUtils.isEmpty(caption)) {
                tvCaption.setText(caption);
            }

            // hide save and delete buttons
            btnSave.setVisibility(View.GONE);
            btnDelete.setVisibility(View.INVISIBLE);
        }

        // photo-specific
        if (isPhoto) {
            ImageUtils.loadImage(ivPhoto, data);
            if (isOwner) {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.set_photo_options_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spSetPhoto.setAdapter(adapter);
                spSetPhoto.setSelection(0);
            } else {
                tvSetPhoto.setVisibility(View.GONE);
                spSetPhoto.setVisibility(View.GONE);
            }
        } else {
            layoutPhotoView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
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
