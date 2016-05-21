package net.tebyan.filesharingapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoSelectorActivity;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.activities.MainActivity;
import net.tebyan.filesharingapp.classes.Application;
import net.tebyan.filesharingapp.classes.NewFolderFragment;
import net.tebyan.filesharingapp.classes.WebserviceUrl;
import net.tebyan.filesharingapp.model.FileUploadInput;
import net.tebyan.filesharingapp.model.FileUploadResultModel;
import net.tebyan.filesharingapp.model.MainModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v.karimi on 5/4/2016.
 */
public class UploadSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    public String selected;
    public ImageView imgUpload, imgUploadPic, imgFolder;
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
                ((MainActivity)getActivity()).startPhotoSelector();
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


        }
    }

}
