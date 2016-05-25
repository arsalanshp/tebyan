package net.tebyan.filesharingapp.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoSelectorActivity;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.classes.Application;
import net.tebyan.filesharingapp.classes.CropCircleTransformation;
import net.tebyan.filesharingapp.classes.Utils;
import net.tebyan.filesharingapp.classes.WebserviceUrl;
import net.tebyan.filesharingapp.model.FileUploadInput;
import net.tebyan.filesharingapp.model.GetAccountInfoModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by v.karimi on 5/24/2016.
 */
public class EditProfileFragment extends Fragment implements View.OnClickListener {

    public View view;
    public Context context;
    public EditText edtFirstName,edtLastName,edtUserName;
    public TextInputLayout inputFirstName,inputLastName,inputUserName;
    public Button btnConfirm;
    private int SELECT_IMAGE_CODE = 1;
    public ImageView imgAddProfile,imgProfile;
    public ArrayList<PhotoModel> uploadFiles;
    ProgressDialog builder;
    public int indexInPhotos;
    public String guidId;
    private int sizeOfPhotos;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.profile_edit_layout,container,false);
        initUI();
        getNetworkUser(getActivity());
        return view;
    }

    private void initUI() {
        edtFirstName= (EditText) view.findViewById(R.id.edt_first_name);
        edtFirstName.addTextChangedListener(new CustomTextWatcher(edtFirstName));
        edtLastName= (EditText) view.findViewById(R.id.edt_last_name);
        edtLastName.addTextChangedListener(new CustomTextWatcher(edtLastName));
        edtUserName= (EditText) view.findViewById(R.id.edt_user_name);
        edtUserName.addTextChangedListener(new CustomTextWatcher(edtUserName));
        inputFirstName= (TextInputLayout) view.findViewById(R.id.input_layout_first_name);
        inputLastName= (TextInputLayout) view.findViewById(R.id.input_layout_last_name);
        inputUserName= (TextInputLayout) view.findViewById(R.id.input_layout_user);
        imgAddProfile= (ImageView) view.findViewById(R.id.img_add_profile);
        imgAddProfile.setOnClickListener(this);
        imgProfile= (ImageView) view.findViewById(R.id.img_profile);
        btnConfirm= (Button) view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_confirm :
               submitForm();
                break;
            case R.id.img_add_profile :
                chooseProfileImage();
                break;
            case R.id.img_profile :

                break;
        }
    }


    private void chooseProfileImage() {
        Intent intent = new Intent(getActivity(), PhotoSelectorActivity.class);
        intent.putExtra(PhotoSelectorActivity.KEY_MAX, 1);
        startActivityForResult(intent, SELECT_IMAGE_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (uploadFiles != null)
                uploadFiles.clear();
            else {
                uploadFiles = new ArrayList<PhotoModel>();
            }

            if (requestCode == SELECT_IMAGE_CODE && data != null && data.getExtras() != null) {
                this.uploadFiles = (ArrayList<PhotoModel>) data.getExtras().getSerializable("photos");
            }
            if (this.uploadFiles != null && !this.uploadFiles.isEmpty()) {
                this.sizeOfPhotos = this.uploadFiles.size();
                this.indexInPhotos = this.sizeOfPhotos - 1;
                uploadPic(this.uploadFiles, this.indexInPhotos);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void uploadPic(List<PhotoModel> files, int index) {
        FileUploadInput fileUploadInput = new FileUploadInput();
        fileUploadInput.index = index;
        fileUploadInput.url = ((PhotoModel) files.get(index)).getOriginalPath();
        Ion.with(this)
                .load(WebserviceUrl.SiteUrl + "/api/Account/UpdateAvatar")
                .setHeader("userToken", Application.getToken(getActivity()))
                .setMultipartParameter("name", "avatar")
                .setMultipartParameter("filename", "profile.jpg")
                .setMultipartFile("image", "image/jpg", new File(fileUploadInput.url))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.i("test", "test");

                        String imageUrl = WebserviceUrl.SiteUrl + "/open/" + "token_" + Application.getToken(getActivity()) + "/GetAvatar/" + guidId + "?date=" + new Date().getTime();
                        imgProfile.setImageResource(0);
                        /*Ion.getDefault(getActivity()).getBitmapCache().remove("5facbdc866bb42bda1134cbbae4eaebe");*/
                        Ion.with(imgProfile).transform(new CropCircleTransformation()).load(imageUrl);
                    }

                });
    }

    public boolean validateFristName() {
        String userName = edtFirstName.getText().toString().trim();

        if (userName.isEmpty()) {
            inputFirstName.setError(getString(R.string.empty_value));
            requestFocus(edtFirstName);
            return false;
        }  else {
            inputFirstName.setErrorEnabled(false);
                return true;
            }

        }
    public boolean validateLastName() {
        String userName = edtLastName.getText().toString().trim();

        if (userName.isEmpty()) {
            inputLastName.setError(getString(R.string.empty_value));
            requestFocus(edtLastName);
            return false;
        }  else {
            inputLastName.setErrorEnabled(false);
            return true;
        }

    }
    public boolean validateUserName() {
        String userName = edtUserName.getText().toString().trim();

        if (userName.isEmpty()) {
            inputUserName.setError(getString(R.string.empty_value));
            requestFocus(edtUserName);
            return false;
        }  else {
            inputUserName.setErrorEnabled(false);
            return true;
        }

    }
    public void submitForm() {
        if (!validateFristName()) {
            return;
        }if (!validateLastName()) {
            return;
        }if (!validateUserName()) {
            return;
        }
        editProfile(edtFirstName.getText().toString(),edtLastName.getText().toString(),edtUserName.getText().toString(),getActivity());
    }
    public void editProfile(String firstName, String lastName, String userName, final Activity activity) {
        if (Utils.isOnline(activity)) {
            Ion.with(activity)
                    .load(WebserviceUrl.UpdateProfile + firstName + WebserviceUrl.LastName + lastName + WebserviceUrl.UserName + userName)
                    .setTimeout(100000000)
                    .setHeader("userToken", Application.getToken(activity))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e == null) {
                                Toast.makeText(getActivity(), R.string.edited, Toast.LENGTH_SHORT).show();
                                getNetworkUser(getActivity());
                                builder.cancel();
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    public class CustomTextWatcher implements TextWatcher {

        private View view;

        public CustomTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edt_first_name:
                    validateFristName();
                    break;
                case R.id.edt_last_name:
                    validateLastName();
                    break;
                case R.id.edt_user_name:
                    validateUserName();
                    break;

            }
        }
    }
    public void setHint(String firstNameHint, String lastNameHint, String usernameHint) {
        edtFirstName.setText(firstNameHint);
        edtLastName.setText(lastNameHint);
        edtUserName.setText(usernameHint);
    }

    public void getNetworkUser(final Activity activity) {
        if (Utils.isOnline(getActivity())) {
            showProgressDialog();
            Ion.with(this)
                    .load(WebserviceUrl.GetAccountInfo).setTimeout(100000000)
                    .setHeader("userToken", Application.getToken(activity))
                    .as(GetAccountInfoModel.class)
                    .setCallback(new FutureCallback<GetAccountInfoModel>() {
                                     @Override
                                     public void onCompleted(Exception e, GetAccountInfoModel accountInfo) {
                                         builder.cancel();
                                         if (accountInfo != null && accountInfo.Data != null && accountInfo.Error == null && e == null) {
                                             setHint(accountInfo.Data.FirstName, accountInfo.Data.LastName, accountInfo.Data.Username);
                                             guidId=accountInfo.Data.GuidID;
                                             String imageUrl = WebserviceUrl.SiteUrl + "/open/" + "token_" + Application.getToken(getActivity()) + "/GetAvatar/" + guidId + "?date=" + new Date().getTime();
                                             Ion.with(imgProfile).transform(new CropCircleTransformation()).load(imageUrl);
                                         } else {
                                             Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                                         }
                                     }
                                 }
                    );
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }
    public void showProgressDialog() {
        builder = new ProgressDialog(getActivity());
        builder.setTitle(R.string.plz_wait);
        builder.setMessage(getString(R.string.sending));
        builder.setCancelable(false);
        builder.show();
    }
}
