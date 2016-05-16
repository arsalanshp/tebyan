package net.tebyan.filesharingapp.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoSelectorActivity;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.adapter.FolderAdapter;
import net.tebyan.filesharingapp.classes.Application;
import net.tebyan.filesharingapp.classes.ContextMenuRecyclerView;
import net.tebyan.filesharingapp.classes.CropCircleTransformation;
import net.tebyan.filesharingapp.classes.TickReciever;
import net.tebyan.filesharingapp.classes.Utils;
import net.tebyan.filesharingapp.classes.WebserviceUrl;
import net.tebyan.filesharingapp.fragment.HomeFragment;
import net.tebyan.filesharingapp.fragment.UploadSheetFragment;
import net.tebyan.filesharingapp.model.FileUploadInput;
import net.tebyan.filesharingapp.model.FileUploadResultModel;
import net.tebyan.filesharingapp.model.Folder;
import net.tebyan.filesharingapp.model.GetAccountInfoModel;
import net.tebyan.filesharingapp.model.GetFileModel_;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static int mNotifCount = 0;
    public FragmentManager fragmentManager;
    public ArrayList<PhotoModel> uploadFiles;
    public int indexInPhotos;
    public GetFileModel_ data;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    String diskSpace;
    String totalSpace;
    String usedStorage;
    NavigationView navigationView;
    String phone;
    SharedPreferences prefs;
    AlertDialog alertDialog;
    ImageButton newFolder;
    MainActivity activity;
    String token;
    ArrayList<Folder> folderList = new ArrayList<>();
    ContextMenuRecyclerView rv;
    FolderAdapter adapter;
    String FileID = "";
    public ProgressBar progress_bar;
    String friendIds = "";
    RelativeLayout relFrgTab;
    TextView username_menu;
    TextView phone_menu;
    ImageView profile_menu_pic;
    FloatingActionButton fab;
    Button notifCount;
    private static int SELECT_IMAGE_CODE = 1;
    private int SELECT_FILM_CODE = 3;
    private int sizeOfPhotos;
    private boolean deletedFiles = false;
    private boolean sharedWithMe = false;
    private String fileSearched = "";
    private int PublicStatus = 0;

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String path = contentUri.getPath();
        return path;
    }

    public void setNotifCount(int count) {
        if (count == -1) {
            invalidateOptionsMenu();
        } else {
            mNotifCount = count;
            invalidateOptionsMenu();
        }
    }

    public void onClickView() {
        relFrgTab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, "Toast", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initUploadMenu();
             /* alertDialog.show();*/
            }
        });
    }

    private void initUploadMenu() {
        BottomSheetDialogFragment bottomSheetDialogFragment = new UploadSheetFragment();
        bottomSheetDialogFragment.show(getSupportFragmentManager(), "uploadFragment");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getExtras();
    }

    public void contactsToAdd() {
        prefs = this.getSharedPreferences(
                "net.tebyan.filesharingapp.activities", Context.MODE_PRIVATE);
        if (!prefs.getBoolean("firstTime", false)) {
            // run your one time code here
            Utils.addFriend(this);
            // mark first time has runned.[ek
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
        phone = prefs.getString("PHONE", "null");
    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.showOverflowMenu();
        newFolder = (ImageButton) findViewById(R.id.new_folder);
        setSupportActionBar(toolbar);
    }

    public void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FileID = extras.getString("FileID");
            deletedFiles = extras.getBoolean("deletedFiles");
            sharedWithMe = extras.getBoolean("sharedWithMe");
            fileSearched = extras.getString("fileSearched");

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setNotifCount(0);
        Utils.reloadMainActivity(Application.CurrentFolder, activity, false, true);
    }

    /*public void setAdapter() {
        isTablet(getApplicationContext());
        adapter = new FolderAdapter(activity, *//*folderList,*//* data,false);
        if (isTablet(this)) {
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new GridLayoutManager(this, 2));
        }
        registerForContextMenu(rv);
        rv.setAdapter(adapter);
    }*/

    public void setDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openRightMenuFragment("home", 0);
        scheduleAlarm();
        activity = this;
        Application.currentActivity = this;
        token = Application.getToken(activity);
        data = new GetFileModel_();
        forceRTLIfSupported();
        new MyAyncTask().execute();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1)
                getSupportFragmentManager().popBackStack();
            else {
                HomeFragment homeFragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag("home");
                if (Application.CurrentFolder != "") {
                    /*Application.CurrentFolder=Application.ParrentFolder;*/
                    homeFragment.getFiles("Title", Application.ParrentFolder);

                } else {
                    super.onBackPressed();
                    finish();
                }
            }

        }
    }

    public void scheduleAlarm() {
        Intent myIntent = new Intent(getApplicationContext(), TickReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 60); // first time
        long frequency = 60 * 1000; // in ms
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);
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
            } else if (requestCode == SELECT_FILM_CODE) {
                if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                    // For JellyBean and above
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ClipData clip = data.getClipData();

                        if (clip != null) {
                            for (int i = 0; i < clip.getItemCount(); i++) {
                                String path = getRealPathFromURI(MainActivity.this, clip.getItemAt(i).getUri());
                                uploadFiles.add(new PhotoModel(path, true));
                            }
                        }
                        // For Ice Cream Sandwich
                    } else {
                        ArrayList<String> paths = data.getStringArrayListExtra
                                (FilePickerActivity.EXTRA_PATHS);
                        if (paths != null) {
                            for (String path : paths) {
                                uploadFiles.add(new PhotoModel(path, true));
                            }
                        }
                    }
                } else {
                    uploadFiles.add(new PhotoModel(data.getData().getPath(), true));
                }
            }
            if (this.uploadFiles != null && !this.uploadFiles.isEmpty()) {
                this.sizeOfPhotos = this.uploadFiles.size();
                this.indexInPhotos = this.sizeOfPhotos - 1;
                uploadPic(this.uploadFiles, this.indexInPhotos, Application.CurrentFolder);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initNavigation() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        username_menu = (TextView) headerView.findViewById(R.id.profile_menu_username);
        phone_menu = (TextView) headerView.findViewById(R.id.profile_menu_phone);
        profile_menu_pic = (ImageView) headerView.findViewById(R.id.profile_menu_pic);
        relFrgTab = (RelativeLayout) findViewById(R.id.relFrgTab);
        rv = (ContextMenuRecyclerView) findViewById(R.id.rv);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    public void updateNavigationDrawer() {
        Menu menuNav = navigationView.getMenu();
        MenuItem nav_upgrade = menuNav.findItem(R.id.nav_upgrade);
        nav_upgrade.setTitle(usedStorage);
    }

    public void getNetworkUser() {
        if (Utils.isOnline(this)) {
            progress_bar.setVisibility(View.VISIBLE);
            Ion.with(this)
                    .load(WebserviceUrl.GetAccountInfo).setTimeout(100000000)
                    .setHeader("userToken", token)
                    .as(GetAccountInfoModel.class)
                    .setCallback(new FutureCallback<GetAccountInfoModel>() {
                                     @Override
                                     public void onCompleted(Exception e, GetAccountInfoModel accountInfo) {
                                         progress_bar.setVisibility(View.GONE);
                                         if (accountInfo != null && accountInfo.Data != null && accountInfo.Error == null && e == null) {
                                             if (accountInfo.Data.Username != null)
                                                 username_menu.setText(accountInfo.Data.Username);
                                             String phoneNum = phone;//.toString().substring(1, result.get("Data").getAsJsonObject().get("AccountID").toString().length() - 1);
                                             String imageUrl = WebserviceUrl.SiteUrl + accountInfo.Data.AvatarUrl;//.toString().substring(1, result.get("Data").getAsJsonObject().get("Avatar").toString().length() - 1);
                                             phone_menu.setText(phoneNum);
                                             Ion.with(profile_menu_pic).transform(new CropCircleTransformation()).load(imageUrl);
                                             totalSpace = accountInfo.Data.TotalSpace;//").toString();
                                             diskSpace = accountInfo.Data.DiskSpace;//").toString();
                                             usedStorage = Utils.humanReadableByteCount(Long.parseLong(diskSpace), false) + " / " + Utils.humanReadableByteCount(Long.parseLong(totalSpace), false);
                                             updateNavigationDrawer();
                                         } else {
                                             Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                                             getTokenByDevice(activity);
                                             //logout();
                                         }

                                             /*getFiles();*/
                                     }
                                 }
                    );
        } else
            Toast.makeText(this, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public void getTokenByDevice(final Activity activity) {
        Ion.with(activity)
                .load(WebserviceUrl.GetTokenByDevice + ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId().toString())
                .setTimeout(1000000000)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null && e == null) {
                            prefs.edit().putString("TOKEN", result.get("Data").toString()).commit();
                            //Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(activity, "failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void delete_File(String fileID) {
        if (Utils.isOnline(this)) {
            progress_bar.setVisibility(View.VISIBLE);
            JsonObject json = new JsonObject();
            json.addProperty("fileId", fileID);
            Ion.with(this)
                    .load(WebserviceUrl.DeleteFile)
                    .setTimeout(1000000000)
                    .setHeader("userToken", token)
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progress_bar.setVisibility(View.GONE);
                            if (e == null) {
                                Utils.reloadMainActivity(Application.CurrentFolder, activity);
                                Toast.makeText(activity, R.string.file_is_deleted, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else
            Toast.makeText(this, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }


    public void addRegisteredFriend(final Activity activity, String firstName, String lastName, String number) {
        if (Utils.isOnline(this)) {
            //progress_bar.setVisibility(View.VISIBLE);
            Ion.with(this)
                    .load(WebserviceUrl.AddFriend + firstName + WebserviceUrl.LastName + lastName + WebserviceUrl.Number + number)
                    .setTimeout(1000000000)
                    .setHeader("userToken", token)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            //progress_bar.setVisibility(View.GONE);
                            if (e == null && result.get("Error") == null) {
                                Toast.makeText(activity, result.get("Data").getAsJsonObject().get("Message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else
            Toast.makeText(this, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }


    public void createUploadAlertDialog() {
        View view = this.getLayoutInflater().inflate(R.layout.alertdialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.new_item);
        // .setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog = builder.create();
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        ImageButton alertDialogUploadImage = (ImageButton) view.findViewById(R.id.upload_image);
        ImageButton alertDialogUploadFilm = (ImageButton) view.findViewById(R.id.upload_film);
        ImageButton alertDialogUploadFile = (ImageButton) view.findViewById(R.id.upload_file);
        alertDialogUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PhotoSelectorActivity.class);
                intent.putExtra(PhotoSelectorActivity.KEY_MAX, 1);
                startActivityForResult(intent, SELECT_IMAGE_CODE);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                alertDialog.dismiss();
            }
        });

        alertDialogUploadFilm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), FilePickerActivity.class);
                AbstractFilePickerActivity.isClip = false;
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                i.putExtra(FilePickerActivity.EXTRA_TYPE, "/image");
                startActivityForResult(i, SELECT_FILM_CODE);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                alertDialog.dismiss();
            }
        });
        alertDialogUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), FilePickerActivity.class);
                AbstractFilePickerActivity.isClip = false;
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                i.putExtra(FilePickerActivity.EXTRA_TYPE, "/");
                startActivityForResult(i, SELECT_FILM_CODE);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                alertDialog.dismiss();
            }
        });
        // AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.new_item);
        // .setIcon(android.R.drawable.ic_dialog_alert);
//        alertDialog = builder.create();
//        alertDialog.setView(view, 0, 0, 0, 0);
//        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
//        alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }


    public void searchFile(String str) {
        if (Utils.isOnline(this)) {
            progress_bar.setVisibility(View.VISIBLE);
            Ion.with(this).load(WebserviceUrl.SearchFile + str)
                    .setHeader("userToken", token)
                    .as(GetFileModel_.class)
                    .setCallback(new FutureCallback<GetFileModel_>() {
                        @Override
                        public void onCompleted(Exception e, GetFileModel_ result) {
                            progress_bar.setVisibility(View.GONE);
                            if (result.Data != null && result.Error == null && e == null) {
                                if (result.Data.Navigate.size() == 0 || result.Data.Navigate.get(0).FolderID == null)
                                    Application.ParrentFolder = "";
                                else
                                    Application.ParrentFolder = result.Data.Navigate.get(0).FolderID;
                            } else
                                Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                            fileSearched = "";
                            data = result;
                            adapter.data = result;
                            adapter.notifyDataSetChanged();
                        }
                    });
        } else
            Toast.makeText(this, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public void getFiles() {
        if (Utils.isOnline(this)) {
            progress_bar.setVisibility(View.VISIBLE);
            Ion.with(this).load(WebserviceUrl.GetFiles + Application.CurrentFolder)
                    .setHeader("userToken", token)
                    .as(GetFileModel_.class)
                    .setCallback(new FutureCallback<GetFileModel_>() {
                        @Override
                        public void onCompleted(Exception e, GetFileModel_ result) {
                            progress_bar.setVisibility(View.GONE);
                            if (result != null) {
                                if (result.Data != null && result.Error == null && e == null) {
                                    if (result.Data.Navigate.size() == 0 || result.Data.Navigate.get(0).FolderID == null)
                                        Application.ParrentFolder = "";
                                    else
                                        Application.ParrentFolder = result.Data.Navigate.get(0).FolderID;
                                    data = result;
                                    adapter.data = result;
                                    adapter.notifyDataSetChanged();
                                } else
                                    Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else
            Toast.makeText(this, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("TOKEN");
        editor.apply();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        finish();
    }

    public void uploadPic(List<PhotoModel> files, int index, final String currentFolder) {
        FileUploadInput fileUploadInput = new FileUploadInput();
        fileUploadInput.index = index;
        fileUploadInput.url = ((PhotoModel) files.get(index)).getOriginalPath();
        /*new DataProvider.UploadFileTask(activity).execute(new FileUploadInput[]{fileUploadInput});*/
        File file = new File(files.get(index).getOriginalPath());
        Ion.with(this)
                .load(WebserviceUrl.UploadServiceUrl + "?folder=" + currentFolder.trim())
                .setHeader("userToken", Application.getToken(this))
                .setMultipartParameter("name", "test")
                .setMultipartParameter("filename", file.getName().trim())
                .setMultipartFile("image", "image/*", file)
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
                            if (activity.indexInPhotos > 0) {
                                activity.indexInPhotos--;
                                uploadPic(uploadFiles, indexInPhotos, currentFolder);
                            } else {
                                Toast.makeText(activity, R.string.upload_completed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           /* case R.id.action_new_folder: {
                NewFolderFragment.showDialog(getSupportFragmentManager(), MainActivity.this, 0, activity, Application.fileIdClicked, "");
                return true;
            }*/
            case R.id.action_add_account: {
                /*NewFolderFragment.showDialog(getSupportFragmentManager(), MainActivity.this, 4, activity, Application.fileIdClicked, "");*/
                return true;
            }
           /* case R.id.action_back: {
                if (fileSearched != null && fileSearched.equals("") && !deletedFiles && !sharedWithMe)
                    Utils.reloadMainActivity(Application.ParrentFolder, activity);
                else if (deletedFiles || sharedWithMe)
                    Utils.reloadMainActivity("", activity, false, false, "");
                else Utils.reloadMainActivity("", activity, deletedFiles, sharedWithMe, "");
                return true;
            }*/
            case R.id.action_account: {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }

            case R.id.action_paste: {
                pasteFile();
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home: {
                // Utils.reloadMainActivity("", activity, false, false);
                Utils.reloadMainActivity("", activity, false, false, "");
                break;
            }
            case R.id.nav_favorite: {
                openRightMenuFragment("favorite", 3);
                break;
            }
            case R.id.favorite: {
                // Utils.reloadMainActivity("", activity, false, false);
                Utils.reloadMainActivity("", activity, false, false, "");
                break;
            }
            case R.id.nav_manage: {
                break;
            }
            case R.id.shared: {
                /*Utils.reloadMainActivity("", activity, false, true, "");*/
                openRightMenuFragment("shareWithMe", 1);
                break;
            }
            case R.id.deleted: {
                openRightMenuFragment("deleted", 2);
                /*Utils.reloadMainActivity("", activity, true, false, "");*/
                break;
            }
            case R.id.exit: {
                ((MainActivity) activity).logout();
                break;
            }
            case R.id.nav_upgrade_web: {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebserviceUrl.BuyStorage));
                startActivity(intent);
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        if (sharedWithMe) menuInflater.inflate(R.menu.context_menu_shared_files, menu);
        else if (!deletedFiles) {
            menuInflater.inflate(R.menu.context_menu, menu);
            ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) menuInfo;
            if (data.Data.Files.get(info.position).IsFolder) {
                menu.getItem(0).setVisible(false);
            }
        } else {
            menuInflater.inflate(R.menu.context_menu_deleted_files, menu);
        }


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        Application.fileIdToPase = data.Data.Files.get(info.position).FileID;

        switch (item.getItemId()) {
            case R.id.download: {
                if (!data.Data.Files.get(info.position).IsFolder) {
                    Utils.downloadFile(data.Data.Files.get(info.position).Title, data.Data.Files.get(info.position).FileID, activity);
                }
                break;
            }
            /*case R.id.delete: {
                if (Application.fileIdSelected.equals("")) {
                    if (!deletedFiles)
                        delete_File(data.Data.Files.get(info.position).FileID);
                    else
                        delete_File_(data.Data.Files.get(info.position).FileID);
                } else {
                    if (!deletedFiles)
                        delete_File(Application.fileIdSelected);
                    else
                        delete_File_(Application.fileIdSelected);
                }
                Application.fileIdSelected = "";
                break;
            }*/
            case R.id.cut: {
                if (!data.Data.Files.get(info.position).IsFolder) {
                    Application.fileIdClicked = data.Data.Files.get(info.position).FileID;
                    Application.fileMoved = true;
                    Application.fileCopied = false;
                    invalidateOptionsMenu();
                    Toast.makeText(activity, R.string.moved, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(activity, R.string.copy_not_possible, Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.copy: {
                if (!data.Data.Files.get(info.position).IsFolder) {
                    Application.fileIdClicked = data.Data.Files.get(info.position).FileID;
                    Application.fileMoved = false;
                    Application.fileCopied = true;
                    invalidateOptionsMenu();
                    Toast.makeText(activity, R.string.copied, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(activity, R.string.copy_not_possible, Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.paste: {
                if (Application.fileCopied) {
                    Application.fileCopied = false;
                    if (Application.fileIdSelected.equals(""))
                        Utils.copy_file(Application.fileIdClicked, activity, Application.fileIdToPase, progress_bar);
                    else
                        Utils.copy_file(Application.fileIdSelected.substring(0, Application.fileIdSelected.length() - 1), activity, Application.fileIdToPase, progress_bar);
                } else {
                    Application.fileMoved = false;
                    if (Application.fileIdSelected.equals(""))
                        Utils.cut_file(Application.fileIdClicked, activity, Application.fileIdToPase, progress_bar);
                    else
                        Utils.cut_file(Application.fileIdSelected.substring(0, Application.fileIdSelected.length() - 1), activity, Application.fileIdToPase, progress_bar);
                }
                Application.fileIdClicked = "";
                Application.fileIdSelected = "";
                invalidateOptionsMenu();
                break;
            }
            case R.id.info: {
                Application.fileIdClicked = data.Data.Files.get(info.position).FileID;
                /*NewFolderFragment.showDialog(getSupportFragmentManager(), MainActivity.this, 2, activity, Application.fileIdClicked, "");*/
                break;
            }
            case R.id.access: {
                Application.fileIdClicked = data.Data.Files.get(info.position).FileID;
                PublicStatus = data.Data.Files.get(info.position).PublicStatus;
               /* NewFolderFragment.showDialog(getSupportFragmentManager(),
                        MainActivity.this, 3, activity, Application.fileIdClicked, PublicStatus);*/
                break;
            }
            case R.id.zip: {

            }
            case R.id.rename: {
                Application.fileIdClicked = data.Data.Files.get(info.position).FileID;
                String text = data.Data.Files.get(info.position).Title;
                /*NewFolderFragment.showDialog(getSupportFragmentManager(),
                        MainActivity.this, 1, activity, Application.fileIdClicked, text);*/
                break;
            }
            case R.id.favorite: {
                break;
            }

            case R.id.sharePeople: {
                Application.fileIdClicked = data.Data.Files.get(info.position).FileID;
                /*getFriendsForShareFile(Application.fileIdClicked);*/
                break;
            }

            case R.id.shareOtherSocialNetwork: {
                Application.fileIdClicked = data.Data.Files.get(info.position).FileID;
                Utils.shareFileUrl(Application.fileIdClicked, activity);
                break;
            }
        }
        return false;
    }
    public void pasteFile() {
        if (Application.fileCopied) {
            Application.fileCopied = false;
            if (Application.fileIdSelected.equals("")) {
                Utils.copy_file(Application.fileIdClicked, activity, Application.CurrentFolder, progress_bar);
            } else {
                Utils.copy_file(Application.fileIdSelected.substring(0, Application.fileIdSelected.length() - 1), activity, Application.CurrentFolder, progress_bar);
            }
        } else {
            Application.fileMoved = false;
            if (Application.fileIdSelected.equals(""))
                Utils.cut_file(Application.fileIdClicked, activity, Application.CurrentFolder, progress_bar);
            else
                Utils.cut_file(Application.fileIdSelected.substring(0, Application.fileIdSelected.length() - 1), activity, Application.CurrentFolder, progress_bar);
        }
        Application.fileIdClicked = "";
        invalidateOptionsMenu();
    }


    public class MyAyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            initToolbar();
            initNavigation();
            onClickView();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getNetworkUser();
            createUploadAlertDialog();
//            setAdapter();
            setDrawer();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {
            contactsToAdd();
            getExtras();
            return null;
        }
    }

    private void openRightMenuFragment(String tag, int type) {
        Fragment fragment = new HomeFragment();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
        //android.R.animator.fade_out);
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putBoolean("pasteMode", false);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frame, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public void startPhotoSelector() {
        Intent intent = new Intent(this, PhotoSelectorActivity.class);
        intent.putExtra(PhotoSelectorActivity.KEY_MAX, 1);
        startActivityForResult(intent, SELECT_IMAGE_CODE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    public interface RefreshDirectory {
        void refreshFile(String currentFolder,String currentFolderName);
    }

    public interface PasteConfirm {
        void pasteConfirm();
    }

    public interface CutConfirm {
        void cutConfirm(String tag);
    }

    public interface DismissPasteDialog {
        void dismissPasteDialog();
    }

    public interface SelectedItems {
        boolean getAllItems();
    }

    public interface deSelectedItems {
        void clearAllItems();
    }
}
