package net.tebyan.filesharingapp.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.system.ErrnoException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.photoselector.model.PhotoModel;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.activities.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by v.karimi on 3/2/2016.
 */
public class CameraFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    View view;
    Context context;
    private CropImageView cropProfileImage;
    private Uri cropImageUri;
    public String name;
    private ImageView imgConfirmProfile;
    public String path;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.capture_image_layout,container,false);
        context=container.getContext();
        initUI();
        onLoadImage();
        return view;
    }
    public void initUI(){
        cropProfileImage = (CropImageView) view.findViewById(R.id.crop_image);
        imgConfirmProfile= (ImageView) view.findViewById(R.id.img_confirm_profile);
        imgConfirmProfile.setOnClickListener(this);
    }
    public void onLoadImage() {

        startActivityForResult(getPickImageChooserIntent(), 200);
    }
    public void onCropImageClick() throws IOException {
        Bitmap cropped = cropProfileImage.getCroppedImage(500, 500);
        if (cropped != null){
            cropProfileImage.setImageBitmap(cropped);
            path=saveUploadImage(cropped);
           /* getActivity().getSupportFragmentManager().popBackStack();*/
            onDestroy();
            Date date =new Date();
            File file=new File(saveUploadImage(cropped),name);
            PhotoModel model=new PhotoModel(file.getPath(),true);
            ArrayList<PhotoModel> photoList=new ArrayList<>();
            photoList.add(model);
            ((MainActivity) getActivity()).uploadPic(photoList, 0);
        }
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);
            boolean requirePermissions = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    isUriRequiresPermissions(imageUri)) {
                requirePermissions = true;
                cropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }

            if (!requirePermissions) {
                cropProfileImage.setImageUriAsync(imageUri);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (cropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cropProfileImage.setImageUriAsync(cropImageUri);
        } else {

        }
    }
    public Intent getPickImageChooserIntent() {
        Uri outputFileUri = getCaptureImageOutputUri();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

      /*  Intent galleryIntent = new_icon Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image*//*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new_icon Intent(galleryIntent);
            intent.setComponent(new_icon ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            *//*allIntents.add(intent);*//*
        }*/
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {

            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "v.jpeg"));
        }
        return outputFileUri;
    }
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = context.getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_confirm_profile:
                try {
                    onCropImageClick();
                    imgConfirmProfile.setClickable(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }

    }
    private String saveUploadImage(Bitmap bitmapImage) throws IOException {
        ContextWrapper contextWrapper = new ContextWrapper(context.getApplicationContext());
        File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
        name = new Date().toString().replaceAll("\\s+","")+".jpg";
        File path=new File(directory,name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        return directory.getAbsolutePath();
    }
    public interface ShowImageProfile{
        void showImageProfile(String path);
    }

}
