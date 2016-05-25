package net.tebyan.filesharingapp.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.photoselector.model.PhotoModel;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.classes.Application;
import net.tebyan.filesharingapp.classes.NewFolderFragment;
import net.tebyan.filesharingapp.classes.NewItemFragment;
import net.tebyan.filesharingapp.classes.Utils;
import net.tebyan.filesharingapp.classes.WebserviceUrl;
import net.tebyan.filesharingapp.fragment.EditProfileFragment;
import net.tebyan.filesharingapp.fragment.FriendListFragment;
import net.tebyan.filesharingapp.model.FileUploadInput;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements NewItemFragment.OnNewFolderListener,View.OnClickListener {

    public ArrayList<PhotoModel> uploadFiles;
    public int indexInPhotos;
    EditText firstName;
    EditText lastName;
    EditText userName;
    FloatingActionButton fab;
    ImageView profile_pic;
    boolean lockEditText = true;
    ProgressDialog builder;
    ProfileActivity activity;
    public FragmentManager fragmentManager;
    private int SELECT_IMAGE_CODE = 1;
    private int sizeOfPhotos;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    @Override
    public void onNewFolder(String name) {
        //...
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
                                Toast.makeText(ProfileActivity.this, R.string.edited, Toast.LENGTH_SHORT).show();
                                builder.cancel();
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        if (item.getItemId() == R.id.action_add_friend) {
            NewFolderFragment.showDialog(getSupportFragmentManager(),
                    ProfileActivity.this, 4, activity);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStack();
        else {
            super.onBackPressed();
            finish();
        }
    }

    public void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Application.currentActivity = this;
        activity = this;
        setToolbar();
        forceRTLIfSupported();
        initFragment("friendList");
        initView();

       /* onClickView();
        getNetworkUser(this);
        disableEditTexts(firstName, lastName, userName);*/
    }

    private void initFragment(String tag) {
        Fragment fragment = new FriendListFragment();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
        //android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.frame_profile, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();

    }

    public void showProgressDialog() {
        builder = new ProgressDialog(this);
        builder.setTitle(R.string.plz_wait);
        builder.setMessage(getString(R.string.sending));
        builder.setCancelable(false);
        builder.show();
    }
    public void onClickView() {
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       initEditProfile("editProfile");
                                   }
                               }
        );

    }

    private void initEditProfile(String tag) {
        Fragment fragment = new EditProfileFragment();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
        //android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.frame_profile, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        /*new_icon DataProvider.UploadFileTask(activity).execute(new_icon FileUploadInput[]{fileUploadInput});*/
        Ion.with(this)
                .load(WebserviceUrl.SiteUrl + "/api/Account/UpdateAvatar")
                .setHeader("userToken", Application.getToken(this))
                .setMultipartParameter("name", "noop")
                .setMultipartParameter("filename", "ahmad.jpg")
                .setMultipartFile("image", "image/jpg", new File(fileUploadInput.url))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        //Log.i("test",e.getMessage());
                        //Log.i("test",result);
                    }
                });
    }

    public void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab :{
                initEditProfile("editProfile");
            }
        }
    }
}
