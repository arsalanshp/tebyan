package net.tebyan.filesharingapp.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.classes.Application;
import net.tebyan.filesharingapp.classes.NewFolderFragment;
import net.tebyan.filesharingapp.classes.Utils;
import net.tebyan.filesharingapp.classes.WebserviceUrl;
import net.tebyan.filesharingapp.model.FriendData;
import net.tebyan.filesharingapp.model.GetFriendsModel;

import java.util.ArrayList;

/**
 * Created by v.karimi on 5/1/2016.
 */
public class FavoriteMenuFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    public String selected;
    private String fileNames;
    String friendIds = "";
    public TextView txtDownload, txtShareLink, txtSendFile, txtAddPeople, txtRemove,txtUnStar;
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
        View contentView = View.inflate(getContext(), R.layout.favorite_menu_item_layout, null);
        txtDownload = (TextView) contentView.findViewById(R.id.txt_download);
        txtDownload.setOnClickListener(this);
        txtShareLink = (TextView) contentView.findViewById(R.id.txt_share_link);
        txtShareLink.setOnClickListener(this);
        txtSendFile = (TextView) contentView.findViewById(R.id.txt_send_file);
        txtSendFile.setOnClickListener(this);
        txtAddPeople = (TextView) contentView.findViewById(R.id.txt_add_people);
        txtAddPeople.setOnClickListener(this);
        txtUnStar = (TextView) contentView.findViewById(R.id.txt_un_star);
        txtUnStar.setOnClickListener(this);
        txtRemove = (TextView) contentView.findViewById(R.id.txt_remove);
        txtRemove.setOnClickListener(this);
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
            case R.id.txt_un_star: {
                this.dismiss();
             Utils.unStar(selected,getActivity());
                break;
            }
            case R.id.txt_download: {
                this.dismiss();
                Utils.downloadFile(fileNames, selected.substring(0, selected.length() - 1), getActivity());
                break;
            }
            case R.id.txt_share_link: {
                this.dismiss();
                String url= WebserviceUrl.DownloadFile + Application.getToken(getActivity()) + WebserviceUrl.FileIdDownload +selected.substring(0,selected.length()-1);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "آدرس دانلود");
                i.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(i, "Share URL"));
                break;
            }
            case R.id.txt_send_file:{
                this.dismiss();
                getFriendsForShareFile(selected.substring(0, selected.length() - 1));
                break;
            }
            case R.id.txt_remove: {
                this.dismiss();
                Utils.deleteFile(selected, getActivity(), "favorite");
                break;
            }
        }
    }

    private void renameFile() {
        NewFolderFragment.showDialog(getActivity().getSupportFragmentManager(), null, 1, getActivity());
    }


    public interface ShowMenu {
        void showContextMenu(String fileIds, String fileNames, int type);
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
        if (Utils.isOnline(getActivity())) {
            /*progress_bar.setVisibility(View.VISIBLE);*/
            Ion.with(getActivity())
                    .load(WebserviceUrl.GetFriendsForShareFile + fileIdClicked)
                    .setTimeout(100000000)
                    .setHeader("userToken", Application.getToken(getActivity()))
                    .as(GetFriendsModel.class)
                    .setCallback(new FutureCallback<GetFriendsModel>() {
                        @Override
                        public void onCompleted(Exception e, GetFriendsModel result) {
                            /*progress_bar.setVisibility(View.GONE);*/
                            if (e == null) {
                                showContactsToShare(result.Data);
                            }
                        }
                    });
        } else
            Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public void showContactsToShare(final ArrayList<FriendData> friends) {
        CharSequence[] name = new CharSequence[friends.size()];
        final boolean bl[] = new boolean[friends.size()];
        for (int i = 0; i < friends.size(); i++) {
            name[i] = friends.get(i).FirstName + " " + friends.get(i).LastName;
        }
        AlertDialog.Builder user = new AlertDialog.Builder(getActivity());
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
                    shareWith("false", "2",selected.substring(0,selected.length()-1), friendIds.substring(0, friendIds.length() - 1));
                dialog.cancel();
            }
        });
        user.show();
    }
    public void shareWith(String canEdit, String ps, String fileID, String friendIds) {
        if (Utils.isOnline(getActivity())) {
            /*progress_bar.setVisibility(View.VISIBLE);*/
            Ion.with(getActivity())
                    .load(WebserviceUrl.FullShareFile)
                    .setTimeout(1000000000)
                    .setHeader("userToken", Application.getToken(getActivity()))
                    .setBodyParameter("canEdit", canEdit)
                    .setBodyParameter("ps",ps)
                    .setBodyParameter("friendIds",friendIds)
                    .setBodyParameter("fileid",fileID)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            /*progress_bar.setVisibility(View.GONE);*/
                            if (e == null && result.get("Error").toString().equals("null")) {
                                Toast.makeText(getActivity(), result.get("Data").getAsJsonObject().get("Message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else
            Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }
}
