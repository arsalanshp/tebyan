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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import net.tebyan.filesharingapp.classes.NewFolderFragment;
import net.tebyan.filesharingapp.classes.NewItemFragment;
import net.tebyan.filesharingapp.classes.Utils;
import net.tebyan.filesharingapp.classes.WebserviceUrl;
import net.tebyan.filesharingapp.fragment.FriendListFragment;
import net.tebyan.filesharingapp.model.FileUploadInput;
import net.tebyan.filesharingapp.model.GetAccountInfoModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements NewItemFragment.OnNewFolderListener {

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

    public void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
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

    public void disableEditTexts(EditText firstName, EditText lastName, EditText userName) {
        disableEditText(firstName);
        disableEditText(lastName);
        disableEditText(userName);
    }

    public void enableEditTexts(EditText firstName, EditText lastName, EditText userName) {
        enableEditText(firstName);
        enableEditText(lastName);
        enableEditText(userName);
    }

    public void onClickView() {
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       if (lockEditText) {
                                           fab.setImageResource(android.R.drawable.ic_menu_save);
                                           enableEditTexts(firstName, lastName, userName);
                                           lockEditText = false;
                                       } else {
                                           showProgressDialog();
                                           fab.setImageResource(android.R.drawable.ic_menu_edit);
                                           disableEditTexts(firstName, lastName, userName);
                                           lockEditText = true;
                                           if (firstName.getText().toString().equals("") && lastName.getText().toString().equals("") && userName.getText().toString().equals("")) {
                                               editProfile(firstName.getHint().toString(), lastName.getHint().toString(), userName.getHint().toString(), activity);
                                           } else if (firstName.getText().toString().equals("") && !lastName.getText().toString().equals("") && !userName.getText().toString().equals("")) {
                                               editProfile(firstName.getHint().toString(), lastName.getText().toString(), userName.getText().toString(), activity);
                                           } else if (!firstName.getText().toString().equals("") && lastName.getText().toString().equals("") && !userName.getText().toString().equals("")) {
                                               editProfile(firstName.getText().toString(), lastName.getHint().toString(), userName.getText().toString(), activity);
                                           } else if (!firstName.getText().toString().equals("") && !lastName.getText().toString().equals("") && userName.getText().toString().equals("")) {
                                               editProfile(firstName.getText().toString(), lastName.getText().toString(), userName.getHint().toString(), activity);
                                           } else if (firstName.getText().toString().equals("") && lastName.getText().toString().equals("") && userName.getText().toString().equals("")) {
                                               editProfile(firstName.getHint().toString(), lastName.getHint().toString(), userName.getText().toString(), activity);
                                           } else if (!firstName.getText().toString().equals("") && lastName.getText().toString().equals("") && userName.getText().toString().equals("")) {
                                               editProfile(firstName.getText().toString(), lastName.getHint().toString(), userName.getHint().toString(), activity);
                                           } else if (firstName.getText().toString().equals("") && !lastName.getText().toString().equals("") && userName.getText().toString().equals("")) {
                                               editProfile(firstName.getHint().toString(), lastName.getText().toString(), userName.getHint().toString(), activity);
                                           } else {
                                               editProfile(firstName.getText().toString(), lastName.getText().toString(), userName.getText().toString(), activity);
                                           }
                                       }
                                   }
                               }
        );

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, PhotoSelectorActivity.class);
                intent.putExtra(PhotoSelectorActivity.KEY_MAX, 1);
                startActivityForResult(intent, SELECT_IMAGE_CODE);
            }
        });
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

    private void disableEditText(EditText editText) {
        editText.setEnabled(false);
        editText.setCursorVisible(false);
    }

    private void enableEditText(EditText editText) {
        editText.setEnabled(true);
        editText.setCursorVisible(true);
    }

    public void setHint(String firstNameHint, String lastNameHint, String usernameHint) {
        firstName.setHint(firstNameHint);
        lastName.setHint(lastNameHint);
        userName.setHint(usernameHint);
    }

    public void initView() {
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        userName = (EditText) findViewById(R.id.username);
        profile_pic = (ImageView) findViewById(R.id.profile_pic);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        profile_pic = (ImageView) findViewById(R.id.profile_pic);
    }

    public void getNetworkUser(final Activity activity) {
        if (Utils.isOnline(this)) {
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
                                             String imageUrl = WebserviceUrl.SiteUrl + accountInfo.Data.AvatarUrl;
                                             Ion.with(profile_pic).transform(new CropCircleTransformation()).load(imageUrl);
                                         } else {
                                             Toast.makeText(ProfileActivity.this, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                                         }
                                     }
                                 }
                    );
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }
}
