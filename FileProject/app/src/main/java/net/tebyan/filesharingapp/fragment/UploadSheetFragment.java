package net.tebyan.filesharingapp.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.FilePickerActivity;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.activities.MainActivity;
import net.tebyan.filesharingapp.classes.NewFolderFragment;

/**
 * Created by v.karimi on 5/4/2016.
 */
public class UploadSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    public String selected;
    private String fileNames;
    public ImageView imgUpload, imgUploadPic, imgFolder, imgTakePhoto;
    public int SELECT_IMAGE_CODE = 1;
    public int SELECT_FILE_CODE = 3;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {


        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @Override
    public void setupDialog(Dialog dialog, int style) {

        super.setupDialog(dialog, style);
        dialog.setTitle(R.string.new_item);
        View contentView = View.inflate(getContext(), R.layout.upload_sheet_layout, null);
        imgUploadPic = (ImageView) contentView.findViewById(R.id.img_upload_pic);
        imgUploadPic.setOnClickListener(this);
        imgFolder = (ImageView) contentView.findViewById(R.id.img_new_folder);
        imgFolder.setOnClickListener(this);
        imgTakePhoto = (ImageView) contentView.findViewById(R.id.img_take_photo);
        imgTakePhoto.setOnClickListener(this);
        imgUpload = (ImageView) contentView.findViewById(R.id.img_upload_file);
        imgUpload.setOnClickListener(this);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_new_folder: {
                NewFolderFragment.showDialog(getActivity().getSupportFragmentManager(), null, 0, getActivity());
                dismiss();
                break;
            }
            case R.id.img_upload_pic: {
                ((MainActivity) getActivity()).startPhotoSelector();
                dismiss();
                break;
            }

            case R.id.img_upload_file: {
                Intent i = new Intent(getActivity(), FilePickerActivity.class);
                AbstractFilePickerActivity.isClip = false;
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                i.putExtra(FilePickerActivity.EXTRA_TYPE, "/");
                getActivity().startActivityForResult(i, SELECT_FILE_CODE);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                dismiss();
                break;
            }
            case R.id.img_take_photo: {
                openCameraFragment();
                break;
            }


        }
    }

    private void openCameraFragment() {
        Fragment fragment = new CameraFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "camera");
        fragmentTransaction.addToBackStack("camera");
        fragmentTransaction.commit();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    }


