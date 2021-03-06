package net.tebyan.filesharingapp.classes;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.fragment.HomeFragment;
import net.tebyan.filesharingapp.fragment.MenuFragment;
import net.tebyan.filesharingapp.model.GetFileModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by F.piri on 1/20/2016.
 */
public class Utils {
    public static long id = 0;

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static void reloadMainActivity(String currentFolderId, Activity activity, boolean deletedFiles, boolean sharedWithMe) {
        if (!currentFolderId.equals(""))
            Application.CurrentFolder = currentFolderId;
        Intent intent = activity.getIntent();
        intent.putExtra("FileID", currentFolderId);
        intent.putExtra("deletedFiles", deletedFiles);
        intent.putExtra("sharedWithMe", sharedWithMe);
        activity.finish();
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void reloadMainActivity(String currentFolderId, Activity activity) {
        Application.CurrentFolder = currentFolderId;
        Intent intent = activity.getIntent();
        intent.putExtra("FileID", currentFolderId);
        activity.finish();
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void reloadMainActivity(String currentFolderId, Activity activity, boolean deletedFiles, boolean sharedWithMe, String fileSearched) {
        Application.CurrentFolder = currentFolderId;
        Intent intent = activity.getIntent();
        intent.putExtra("FileID", currentFolderId);
        intent.putExtra("deletedFiles", deletedFiles);
        intent.putExtra("sharedWithMe", sharedWithMe);
        intent.putExtra("fileSearched", fileSearched);
        activity.finish();
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    public static void setFilePublic(final Activity activity, String canEdit, String ps, String fileID /*,String friendIds*/) {
        if (Utils.isOnline(activity)) {
            Ion.with(activity)
                    .load(WebserviceUrl.FullShareFile + canEdit + WebserviceUrl.ps + ps + WebserviceUrl.FriendId + "" /*friendIds*/ + WebserviceUrl.FileId + fileID + "&shareText=")
                    .setTimeout(1000000000)
                    .setHeader("userToken", Application.getToken(activity))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e == null && result.get("Error").toString().equals("null")) {
                                Toast.makeText(activity, result.get("Data").getAsJsonObject().get("Message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public static void setFilePrivate(final Activity activity, String canEdit, String ps, String fileID /*,String friendIds*/) {
        if (Utils.isOnline(activity)) {
            Ion.with(activity)
                    .load(WebserviceUrl.FullShareFile + canEdit + WebserviceUrl.ps + ps + WebserviceUrl.FriendId + "" /*friendIds*/ + WebserviceUrl.FileId + fileID)
                    .setTimeout(1000000000)
                    .setHeader("userToken", Application.getToken(activity))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e == null && result.get("Error").toString().equals("null")) {
                                Toast.makeText(activity, result.get("Data").getAsJsonObject().get("Message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }


    public static void downloadFile(final String filename, String fileId, final Activity activity) {
        final String[] filenames = filename.split(",");
        final String[] fileIds = fileId.split(",");
        for (int i = 0; i < fileIds.length; i++) {
            Uri downloadUri = Uri.parse(WebserviceUrl.DownloadFile + Application.getToken(activity) + WebserviceUrl.FileIdDownload + fileIds[i]);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/TebyanFiles/", filenames[i]);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            request.setAllowedOverRoaming(false);
            request.setTitle(filenames[i]);
            request.setDescription(activity.getString(R.string.downloading));
            String servicestring = Context.DOWNLOAD_SERVICE;
            DownloadManager downloadManager = (DownloadManager) activity.getSystemService(servicestring);
            id = downloadManager.enqueue(request);
        }
        /*final ProgressDialog mProgressDialog;
        mProgressDialog = new_icon ProgressDialog(activity);
        mProgressDialog.setTitle(activity.getString(R.string.downloading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
        Ion.with(activity)
                .load(WebserviceUrl.DownloadFile + Application.getToken(activity) + WebserviceUrl.FileIdDownload + fileId)
                .progressDialog(mProgressDialog)
                .progress(new_icon ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        float percent = (float) downloaded / total;
                        mProgressDialog.setProgress((int) percent);
                    }
                })
                .write(new_icon File(folder + "/" + filename))
                .setCallback(new_icon FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        if (e == null) {
                            Toast.makeText(activity, "فایل با موفقیت دانلود شد", Toast.LENGTH_SHORT).show();
                            mProgressDialog.cancel();
                            openFile(file, activity);
                        } else
                            Toast.makeText(activity, "اشکال در عملیات دانلود", Toast.LENGTH_SHORT).show();
                    }
                });*/
    }

    public static void shareFile(final String filename1, String fileId1, final Activity activity) {
        final String filename = filename1.split(",")[0];
        final String fileId = fileId1.split(",")[0];
        final File folder = new File(Environment.getExternalStorageDirectory() + "/TebyanFiles/");
        if (!folder.exists())
            folder.mkdir();
        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setTitle(activity.getString(R.string.downloading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
        Ion.with(activity)
                .load(WebserviceUrl.DownloadFile + Application.getToken(activity) + WebserviceUrl.FileIdDownload + fileId)
                .progressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        mProgressDialog.setProgress((int) ((float) downloaded / (float) total) * 100);
                    }
                })
                .write(new File(folder + "/" + filename))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        if (e == null) {
                            Toast.makeText(activity, "فایل با موفقیت دانلود شد", Toast.LENGTH_SHORT).show();
                            mProgressDialog.cancel();
                            shareFileToNetwork(file, activity);
                        } else
                            Toast.makeText(activity, "اشکال در عملیات اشتراک", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void shareFileToNetwork(File file, Activity activity) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType("*/*");
        activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getText(R.string.share_file)));
    }

    public static void openFile(File file, Activity activity) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.fromFile(file);
        intent.setDataAndType(data, getMimeType(file));
        activity.startActivity(intent);
    }

    public static String getMimeType(File file) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
        String type = map.getMimeTypeFromExtension(ext);
        if (type == null)
            type = "*/*";
        return type;
    }


    public static void rename_File(String fileID1, String newName, final FragmentActivity activity) {
        if (Utils.isOnline(activity)) {
            MenuFragment fragment = (MenuFragment) activity.getSupportFragmentManager().findFragmentByTag("menuFragment");
            String fileID = fileID1.substring(0, fileID1.length() - 1);
            Ion.with(activity)
                    .load(WebserviceUrl.RenameFile + fileID + WebserviceUrl.NewName + newName)
                    .setTimeout(100000000)
                    .setHeader("userToken", Application.getToken(activity)).asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e == null) {
                                HomeFragment fragment= (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag("home");
                                fragment.getFiles("Title",Application.CurrentFolder,0);
                                Toast.makeText(activity, R.string.renamed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }
    public static void shareLink(String fileSelected,Activity activity){

        StringBuilder url = new StringBuilder();
        String selectedArray[]=fileSelected.split(",");
        if(selectedArray.length>1){
            for(int i=0;i<selectedArray.length;i++){
                url.append(activity.getString(R.string.link));
                url.append("\n");
                url.append(WebserviceUrl.DownloadFile + Application.getToken(activity) + WebserviceUrl.FileIdDownload +selectedArray[i]);
                url.append("\n");
            }
        }else{
            url.append(WebserviceUrl.DownloadFile + Application.getToken(activity) + WebserviceUrl.FileIdDownload +selectedArray[0]);
        }

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "آدرس دانلود");
        i.putExtra(Intent.EXTRA_TEXT, url.toString());
         activity.startActivity(Intent.createChooser(i, "Share URL"));

    }

    public static void onNewFolder(String name, final FragmentActivity activity) {
        final HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag("home");
        if (Utils.isOnline(activity)) {
            /*progress_bar.setVisibility(View.VISIBLE);*/
            Ion.with(activity).load(WebserviceUrl.NewFolderRequest + name + "&folder=" + Application.CurrentFolder)
                    .setHeader("userToken", Application.getToken(activity))
                    .as(GetFileModel.class)
                    .setCallback(new FutureCallback<GetFileModel>() {
                        @Override
                        public void onCompleted(Exception e, GetFileModel result) {
                            /*progress_bar.setVisibility(View.GONE);*/
                            if (result.Data != null && result.Error == null && e == null) {
                                fragment.getFiles("Title", Application.CurrentFolder,0);
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }


    public static void deleteFile(String fileID1, final FragmentActivity activity, final String tag) {
        if (Utils.isOnline(activity)) {
            String fileID = fileID1.substring(0, fileID1.length() - 1);
            final ProgressDialog mProgressDialog;
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setTitle(activity.getString(R.string.delete));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
            JsonObject json = new JsonObject();
            json.addProperty("fileId", fileID);
            Ion.with(activity)
                    .load(WebserviceUrl.DeleteFile)
                    .setTimeout(1000000000)
                    .setHeader("userToken", Application.getToken(activity))
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            mProgressDialog.dismiss();
                            if (e == null) {
                                if (!result.get("Data").toString().equals("null") || result.get("Error").toString().equals("null")) {
                                    if (tag == "home") {
                                        HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag(tag);
                                        fragment.getFiles("Title", Application.CurrentFolder,0);
                                    }
                                    if (tag == "favorite") {
                                        HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag(tag);
                                        fragment.getStaredFiles("Title");
                                    }
                                    if (tag == "deleted") {
                                        HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag(tag);
                                        fragment.getDeletedFiles("Title");
                                    }
                                    Toast.makeText(activity, R.string.file_is_deleted, Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(activity, result.get("Error").getAsJsonObject().get("ErrorMessage").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public static void copy_file(String fileID, final Activity activity, String folderId, final ProgressBar progressBar) {
        if (Utils.isOnline(activity)) {
            progressBar.setVisibility(View.VISIBLE);
            Ion.with(activity)
                    .load(WebserviceUrl.CopyFile + fileID + WebserviceUrl.FolderId + folderId)
                    .setTimeout(1000000099)
                    .setHeader("userToken", Application.getToken(activity)).asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressBar.setVisibility(View.GONE);
                            if (e == null) {
                                Utils.reloadMainActivity(Application.CurrentFolder, activity);
                                Toast.makeText(activity, R.string.pasted, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
        Application.fileIdSelected = "";
    }

    public static void cut_file(final String fileID, final Activity activity, String folderId, final ProgressBar progress_bar) {
        if (Utils.isOnline(activity)) {
            progress_bar.setVisibility(View.VISIBLE);
            Ion.with(activity)
                    .load(WebserviceUrl.MoveFile + fileID + WebserviceUrl.FolderId + folderId)
                    .setTimeout(100000000)
                    .setHeader("userToken", Application.getToken(activity)).asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progress_bar.setVisibility(View.GONE);

                            if (e == null) {
                                if (!result.get("Data").toString().equals("null") || result.get("Error").toString().equals("null")) {
                                    Utils.reloadMainActivity(Application.CurrentFolder, activity);
                                    Toast.makeText(activity, R.string.moved, Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(activity, result.get("Error").getAsJsonObject().get("ErrorMessage").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
        Application.fileIdSelected = "";
    }

    public static void addFriend(String firstName, String lastName, String number, final Activity activity) {
        if (Utils.isOnline(activity)) {
            JsonArray jsonArray = new JsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("FirstName", firstName);
            jsonObject.addProperty("LastName", lastName);
            jsonObject.addProperty("Mobile", number);
            jsonArray.add(jsonObject);
            Ion.with(activity)
                    .load(WebserviceUrl.AddFriend)
                    .setTimeout(100000000)
                    .setHeader("userToken", Application.getToken(activity))
                    .setJsonArrayBody(jsonArray)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e == null) {
                                Toast.makeText(activity, result.get("Data").getAsJsonObject().get("Message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public static void zipFile(String fileID, final FragmentActivity activity, final String tag) {
        if (Utils.isOnline(activity)) {
            final ProgressDialog mProgressDialog;
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setTitle("در حال فشرده سازی");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
            JsonObject json = new JsonObject();
            json.addProperty("fileId", fileID);
            Ion.with(activity)
                    .load(WebserviceUrl.ZipFile)
                    .setTimeout(1000000000)
                    .setHeader("userToken", Application.getToken(activity))
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            mProgressDialog.dismiss();
                            if (e == null) {
                                if (!result.get("Data").toString().equals("null") || result.get("Error").toString().equals("null")) {
                                    if (tag == "home") {
                                        HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag(tag);
                                        fragment.getFiles("Title", Application.CurrentFolder,0);
                                    }
                                    if (tag == "favorite") {
                                        HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag(tag);
                                        fragment.getStaredFiles("Title");
                                    }
                                    Toast.makeText(activity, R.string.un_zip, Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(activity, result.get("Error").getAsJsonObject().get("ErrorMessage").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public static void unStar(String fileID1, final FragmentActivity activity) {
        if (Utils.isOnline(activity)) {
            String fileID = fileID1.substring(0, fileID1.length() - 1);
            final ProgressDialog mProgressDialog;
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setTitle(activity.getString(R.string.delete));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
            JsonObject json = new JsonObject();
            json.addProperty("fileId", fileID);
            Ion.with(activity)
                    .load(WebserviceUrl.UnStar)
                    .setTimeout(1000000000)
                    .setHeader("userToken", Application.getToken(activity))
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            mProgressDialog.dismiss();
                            if (e == null) {
                                if (!result.get("Data").toString().equals("null") || result.get("Error").toString().equals("null")) {
                                    HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag("favorite");
                                    fragment.getStaredFiles("Title");
                                    Toast.makeText(activity, R.string.file_is_deleted, Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(activity, result.get("Error").getAsJsonObject().get("ErrorMessage").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public static void addFriend(final Context context) {
        if (Utils.isOnline(context)) {
            JsonArray jsonArray = new JsonArray();
            ArrayList<Contact> contacts = getContacts(context);
            int size = contacts.size();
            for (int i = 0; i < size; i++) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("FirstName", contacts.get(i).getFirstName());
                jsonObject.addProperty("LastName", contacts.get(i).getLastName());
                jsonObject.addProperty("Mobile", contacts.get(i).getNumber());
                jsonArray.add(jsonObject);
            }
            Ion.with(context)
                    .load(WebserviceUrl.AddFriends)
                    .setTimeout(1000000000)
                    .setHeader("userToken", Application.getToken(context))
                    .setJsonArrayBody(jsonArray)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e == null) {
                                String msg = result.get("Data").getAsJsonObject().get("Message").toString();
                                Toast.makeText(context, msg.substring(1, msg.length() - 1), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    public static ArrayList<Contact> getContacts(Context activity) {
        String[] splited = new String[2];
        Contact contact;
        ContentResolver cr = activity.getContentResolver();
        ArrayList<Contact> allContacts = new ArrayList<Contact>();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        if (contactName.contains(" ")) {
                            splited = contactName.split("\\s+");
                            contact = new Contact(splited[0], splited[1], contactNumber);
                        } else contact = new Contact(contactName, "", contactNumber);
                        allContacts.add(contact);
                        break;
                    }
                    pCur.close();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return allContacts;
    }


    public static String getDeviceId(Activity activity) {
        String android_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static void shareFileUrl(String fileId, Activity activity) {
        String url = WebserviceUrl.DownloadFile + Application.getToken(activity) + WebserviceUrl.FileIdDownload + fileId;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        //intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this site!");
        activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share)));
    }

    public static void favoriteFile(String fileId1, final FragmentActivity activity) {
        if (Utils.isOnline(activity)) {
            final ProgressDialog mProgressDialog;
            String fileId = fileId1.substring(0, fileId1.length() - 1);
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setTitle(activity.getString(R.string.favorite));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
            JsonObject json = new JsonObject();

            Ion.with(activity)
                    .load(WebserviceUrl.staredFiles)
                    .setTimeout(1000000000)
                    .setHeader("userToken", Application.getToken(activity))
                    .setHeader("Content-Type", "application/x-www-form-urlencoded")
                    .setBodyParameter("fileId", fileId)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            mProgressDialog.dismiss();
                            if (e == null) {
                                /*Utils.reloadMainActivity(Application.CurrentFolder, activity);*/
                                HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag("home");
                                fragment.getFiles("Title", Application.CurrentFolder,0);
                                //Toast.makeText(activity, "success!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public static void RestoreFile(String fileID, final FragmentActivity activity) {
        if (Utils.isOnline(activity)) {
            final ProgressDialog mProgressDialog;
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setTitle("در حال بازیابی...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
            JsonObject json = new JsonObject();
            json.addProperty("fileId", fileID);
            Ion.with(activity)
                    .load(WebserviceUrl.RestoreFile)
                    .setTimeout(1000000000)
                    .setHeader("userToken", Application.getToken(activity))
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            mProgressDialog.dismiss();
                            if (e == null) {
                                Toast.makeText(activity, result.get("Data").getAsJsonObject().get("Message").toString(), Toast.LENGTH_SHORT).show();
                                HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag("deleted");
                                fragment.getDeletedFiles("Title");
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public static void deleteConfirm(String fileID, final FragmentActivity activity) {
        if (Utils.isOnline(activity)) {
            final ProgressDialog mProgressDialog;
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setTitle("در حال حذف...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
            JsonObject json = new JsonObject();
            json.addProperty("fileId", fileID);
            Ion.with(activity)
                    .load(WebserviceUrl.Delete)
                    .setTimeout(1000000000)
                    .setHeader("userToken", Application.getToken(activity))
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            mProgressDialog.dismiss();
                            if (e == null) {
                                Toast.makeText(activity, result.get("Data").getAsJsonObject().get("Message").toString(), Toast.LENGTH_SHORT).show();
                                HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag("deleted");
                                fragment.getDeletedFiles("Title");
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public static void unZipFile(String fileID, final FragmentActivity activity, final String tag) {
        if (Utils.isOnline(activity)) {
            final ProgressDialog mProgressDialog;
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setTitle("در حال فشرده سازی");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
            JsonObject json = new JsonObject();
            json.addProperty("fileId", fileID);
            Ion.with(activity)
                    .load(WebserviceUrl.UnZipFile)
                    .setTimeout(1000000000)
                    .setHeader("userToken", Application.getToken(activity))
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            mProgressDialog.dismiss();
                            if (e == null) {
                                if (!result.get("Data").toString().equals("null") || result.get("Error").toString().equals("null")) {
                                    if (tag == "home") {
                                        HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag(tag);
                                        fragment.getFiles("Title", Application.CurrentFolder,0);
                                    }
                                    if (tag == "favorite") {
                                        HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag(tag);
                                        fragment.getStaredFiles("Title");
                                    }
                                    Toast.makeText(activity, R.string.un_zip, Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(activity, result.get("Error").getAsJsonObject().get("ErrorMessage").toString(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }
}
