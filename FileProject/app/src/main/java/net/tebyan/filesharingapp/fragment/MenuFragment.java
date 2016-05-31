package net.tebyan.filesharingapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.activities.MainActivity;
import net.tebyan.filesharingapp.classes.Application;
import net.tebyan.filesharingapp.classes.NewFolderFragment;
import net.tebyan.filesharingapp.classes.NewItemFragment;
import net.tebyan.filesharingapp.classes.Utils;
import net.tebyan.filesharingapp.classes.WebserviceUrl;
import net.tebyan.filesharingapp.model.FriendData;
import net.tebyan.filesharingapp.model.GetFriendsModel;

import java.util.ArrayList;

/**
 * Created by v.karimi on 5/1/2016.
 */
public class MenuFragment extends BottomSheetDialogFragment implements View.OnClickListener, NewItemFragment.OnNewFolderListener
        , MainActivity.ShowPasteDirectory {
    public String selected;
    private String fileNames;
    public FragmentActivity activity;
    public LinearLayout shareLayout, renameLayout;
    public View.OnClickListener snackClickListener;
    public MainActivity.deSelectedItems handler;
    public View contentView;
    String friendIds = "";
    public TextView txtDownload, txtShareLink, txtSendFile, txtAddPeople, txtMove, txtRename, txtInfo, txtRemove, txtCopy, txtFavorite, txtZip;
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
    }

    public void setHandler(MainActivity.deSelectedItems handler) {
        this.handler = handler;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {

        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.menu_item_layout, null);
        txtDownload = (TextView) contentView.findViewById(R.id.txt_download);
        txtDownload.setOnClickListener(this);
        txtFavorite = (TextView) contentView.findViewById(R.id.txt_favorite);
        txtFavorite.setOnClickListener(this);
        txtShareLink = (TextView) contentView.findViewById(R.id.txt_share_link);
        txtShareLink.setOnClickListener(this);
        txtInfo = (TextView) contentView.findViewById(R.id.txt_info);
        txtInfo.setOnClickListener(this);
        txtSendFile = (TextView) contentView.findViewById(R.id.txt_send_file);
        txtSendFile.setOnClickListener(this);
        txtAddPeople = (TextView) contentView.findViewById(R.id.txt_add_people);
        txtAddPeople.setOnClickListener(this);
        txtZip = (TextView) contentView.findViewById(R.id.txt_zip);
        txtZip.setOnClickListener(this);
        txtMove = (TextView) contentView.findViewById(R.id.txt_move);
        txtMove.setOnClickListener(this);
        txtRename = (TextView) contentView.findViewById(R.id.txt_rename);
        txtRename.setOnClickListener(this);
        txtRemove = (TextView) contentView.findViewById(R.id.txt_remove);
        txtRemove.setOnClickListener(this);
        txtCopy = (TextView) contentView.findViewById(R.id.txt_copy);
        txtCopy.setOnClickListener(this);
        shareLayout = (LinearLayout) contentView.findViewById(R.id.share_file_layout);
        renameLayout = (LinearLayout) contentView.findViewById(R.id.rename_layout);
        initMultiSelectedMenu();
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    private void initMultiSelectedMenu() {
        String[] selectArray = selected.split(",");
        if (selectArray.length > 1) {
            shareLayout.setVisibility(View.GONE);
            renameLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        HomeFragment fragment = (HomeFragment) getActivity().getSupportFragmentManager().findFragmentByTag("home");
        switch (view.getId()) {
            case R.id.txt_copy: {
                this.dismiss();
                //Toast.makeText(getContext(), R.string.copied, Toast.LENGTH_SHORT).show();
                FragmentManager fm = getFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("type", "copy");
                bundle.putString("index", selected);
                PasteDialogFragment dialogFragment = new PasteDialogFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.show(fm, "paste fragment");

                fragment.deSelectHandler.clearAllItems();
                break;
            }
            case R.id.txt_download: {
                this.dismiss();
                Utils.downloadFile(fileNames, selected, activity);
                fragment.deSelectHandler.clearAllItems();
                break;
            }
            case R.id.txt_share_link: {
                this.dismiss();
                Utils.shareLink(selected, activity);
                fragment.deSelectHandler.clearAllItems();
                break;
            }
            case R.id.txt_rename: {
                this.dismiss();
                renameFile();
                fragment.deSelectHandler.clearAllItems();
                break;
            }
            case R.id.txt_move: {
                this.dismiss();
                FragmentManager fm = getFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("type", "cut");
                bundle.putString("tag", "home");
                bundle.putString("index", selected);
                PasteDialogFragment dialogFragment = new PasteDialogFragment();
                dialogFragment.setPasteHandler(this);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(fm, "paste fragment");
                fragment.deSelectHandler.clearAllItems();
                break;
            }
            case R.id.txt_send_file: {
                this.dismiss();
                Utils.shareFile(fileNames, selected, activity);
                fragment.deSelectHandler.clearAllItems();
                break;
            }

            case R.id.txt_info: {
                this.dismiss();
                String selectArray[] = selected.split(",");
                if (selectArray.length > 1) {
                    Toast.makeText(activity, getString(R.string.info_error), Toast.LENGTH_LONG).show();
                } else {
                    NewFolderFragment.showDialog(((FragmentActivity) activity).getSupportFragmentManager(), this, 2, activity, selected, "");
                }
                fragment.deSelectHandler.clearAllItems();
                break;
            }
            case R.id.txt_add_people: {
                this.dismiss();
                getFriendsForShareFile(selected.substring(0, selected.length() - 1));
                fragment.deSelectHandler.clearAllItems();
                break;
            }
            case R.id.txt_zip: {
                this.dismiss();
                String extension = fileNames.substring(fileNames.length() - 4);
                String arrZip[] = fileNames.split(",");
                if (extension.trim().equals("zip,")) {
                    if (arrZip.length > 1) {
                        Utils.zipFile(selected.substring(0, selected.length() - 1), (FragmentActivity) activity, "home");
                    } else {
                        Utils.unZipFile(selected.substring(0, selected.length() - 1), (FragmentActivity) activity, "home");
                    }
                } else {
                    Utils.zipFile(selected.substring(0, selected.length() - 1), (FragmentActivity) activity, "home");
                }
                fragment.deSelectHandler.clearAllItems();
                break;
            }
            case R.id.txt_remove: {
                this.dismiss();
                Utils.deleteFile(selected, (FragmentActivity) activity, "home");
                fragment.deSelectHandler.clearAllItems();
                break;
            }
            case R.id.txt_favorite: {
                this.dismiss();
                Utils.favoriteFile(selected, (FragmentActivity) activity);
                fragment.deSelectHandler.clearAllItems();
                break;
            }

        }
    }

    private void renameFile() {

        NewFolderFragment.showDialog(((FragmentActivity) activity).getSupportFragmentManager(), this, 1, activity, selected, fileNames.substring(0, fileNames.length() - 1));
    }

    @Override
    public void onNewFolder(String name) {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        selected = getArguments().getString("index");
        fileNames = getArguments().getString("fileNames");
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void getFriendsForShareFile(String fileIdClicked) {
        if (Utils.isOnline(activity)) {
            /*progress_bar.setVisibility(View.VISIBLE);*/
            Ion.with(activity)
                    .load(WebserviceUrl.GetFriendsForShareFile + fileIdClicked)
                    .setTimeout(100000000)
                    .setHeader("userToken", Application.getToken(activity))
                    .as(GetFriendsModel.class)
                    .setCallback(new FutureCallback<GetFriendsModel>() {
                        @Override
                        public void onCompleted(Exception e, GetFriendsModel result) {
                            /*progress_bar.setVisibility(View.GONE);*/
                            if (e == null) {
                                if (result.Data.size() == 0) {
                                    Toast.makeText(activity, getString(R.string.no_friend_error), Toast.LENGTH_LONG).show();
                                } else {
                                    showContactsToShare(result.Data);
                                }
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public void showContactsToShare(final ArrayList<FriendData> friends) {
        CharSequence[] name = new CharSequence[friends.size()];
        final boolean bl[] = new boolean[friends.size()];
        for (int i = 0; i < friends.size(); i++) {
            name[i] = friends.get(i).FirstName + " " + friends.get(i).LastName;
        }
        AlertDialog.Builder user = new AlertDialog.Builder(activity);
        user.setTitle(R.string.contacts);
        user.setMultiChoiceItems(name, bl, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
            }
        });
        user.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < friends.size(); i++) {
                    if (bl[i] == true) {
                        friendIds += friends.get(i).FriendID + ",";
                    }
                }
                if (friendIds.length() > 0)
                    shareWith("false", "2", selected.substring(0, selected.length() - 1), friendIds.substring(0, friendIds.length() - 1));
                dialog.cancel();
            }
        });
        user.show();
    }

    public void shareWith(String canEdit, String ps, String fileID, String friendIds) {
        if (Utils.isOnline(activity)) {
            /*progress_bar.setVisibility(View.VISIBLE);*/
            Ion.with(activity)
                    .load(WebserviceUrl.FullShareFile)
                    .setTimeout(1000000000)
                    .setHeader("userToken", Application.getToken(activity))
                    .setBodyParameter("canEdit", canEdit)
                    .setBodyParameter("ps", ps)
                    .setBodyParameter("friendId", friendIds)
                    .setBodyParameter("fileid", fileID)
                    .setBodyParameter("shareText", "پیغام")
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            /*progress_bar.setVisibility(View.GONE);*/
                            if (e == null && result.get("Error").toString().equals("null")) {
                                Toast.makeText(activity, result.get("Data").getAsJsonObject().get("Message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPasteDirectory(final String pasteFolder) {


    }
}
