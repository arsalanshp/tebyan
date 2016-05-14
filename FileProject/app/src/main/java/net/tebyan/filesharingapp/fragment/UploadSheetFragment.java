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
    private String fileNames;
    public ImageView imgUpload, imgUploadMovie, imgFolder;
    public int SELECT_IMAGE_CODE = 1;
    public int SELECT_FILM_CODE = 3;
    public ArrayList<PhotoModel> uploadFiles;
    private int sizeOfPhotos;
    public int indexInPhotos;
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
        dialog.setTitle("جدید");
        View contentView = View.inflate(getContext(), R.layout.upload_sheet_layout, null);
        imgUploadMovie = (ImageView) contentView.findViewById(R.id.img_upload_film);
        imgUploadMovie.setOnClickListener(this);
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
            case R.id.img_upload_film: {
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
                getActivity().startActivityForResult(i, SELECT_FILM_CODE);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                dismiss();
                break;
            }


        }
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

    public static String getRealPathFromURI(FragmentActivity activity, Uri contentUri) {
        String path = contentUri.getPath();
        return path;
    }

    public void uploadPic(List<PhotoModel> files, int index, final FragmentActivity activity) {
        FileUploadInput fileUploadInput = new FileUploadInput();
        fileUploadInput.index = index;
        fileUploadInput.url = ((PhotoModel) files.get(index)).getOriginalPath();
        File file = new File(files.get(index).getOriginalPath());
        Ion.with(activity)
                .load(WebserviceUrl.UploadServiceUrl + "?folder=" + Application.CurrentFolder.trim())
                .setHeader("userToken", Application.getToken(activity))
                .setMultipartParameter("name", "test")
                .setMultipartParameter("filename", file.getName().trim())
                .setMultipartFile("file", "image/jpeg", file)
                .as(FileUploadResultModel.class)
                .setCallback(new FutureCallback<FileUploadResultModel>() {
                    @Override
                    public void onCompleted(Exception e, FileUploadResultModel result) {
                        if (result != null) {
                            if (result.Error == null) {
                                if (result.Data != null && !result.Data.FileID.equals(""))
                                    Toast.makeText(activity, R.string.upload_completed, Toast.LENGTH_SHORT).show();
                                else {
                                    Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(activity, "[index : " + activity.indexInPhotos + "] file NOT uploaded", Toast.LENGTH_SHORT).show();
                                }
                            } else {
/*
                            Toast.makeText(activity, "[index : " + activity.indexInPhotos + "] Error : " + result.Error.ErrorMessage, Toast.LENGTH_SHORT).show();
*/
                            }
                            if (indexInPhotos > 0) {
                                indexInPhotos--;
                                uploadPic(uploadFiles, indexInPhotos, activity);
                            } else {
                                Toast.makeText(activity, R.string.upload_completed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}
