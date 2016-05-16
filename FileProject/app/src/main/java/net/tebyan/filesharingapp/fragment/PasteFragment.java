package net.tebyan.filesharingapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.activities.MainActivity;
import net.tebyan.filesharingapp.adapter.PasteAdapter;
import net.tebyan.filesharingapp.classes.Application;
import net.tebyan.filesharingapp.classes.ContextMenuRecyclerView;
import net.tebyan.filesharingapp.classes.Utils;
import net.tebyan.filesharingapp.classes.WebserviceUrl;
import net.tebyan.filesharingapp.model.FileData;
import net.tebyan.filesharingapp.model.GetFileModel_;

/**
 * Created by v.karimi on 5/3/2016.
 */
public class PasteFragment extends Fragment implements MainActivity.RefreshDirectory, MainActivity.PasteConfirm, MainActivity.CutConfirm {

    public View view;
    public ContextMenuRecyclerView rv;
    public PasteAdapter adapter;
    public ProgressBar progress_bar;
    public GetFileModel_ data;
    public String selected;
    public String token;
    public String currentFolder;
    public FragmentActivity activity;
    public int type;
    public MainActivity.DismissPasteDialog handler;
    public PasteDialogFragment dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_layout, container, false);
        activity = getActivity();
        selected = getArguments().getString("index");
        initUI();
        initData();
        token = Application.getToken(getActivity());
        return view;
    }

    public void setHandler(MainActivity.DismissPasteDialog handler) {
        this.handler = handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void initUI() {
        rv = (ContextMenuRecyclerView) view.findViewById(R.id.rv);
        dialog.setHandler(this);
        dialog.txtPaste.setEnabled(true);
        dialog.setPasteHandler(this);

    }

    public void initData() {
        data = new GetFileModel_();
        setAdapter();

    }

    public void setAdapter() {
        getFiles("", "");
        adapter = new PasteAdapter(getActivity(), data);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.setHandler(this);
        rv.setAdapter(adapter);


    }

    public void getFiles(String sortBy, String currentFolder) {
        if (Utils.isOnline(activity)) {
            /*progress_bar.setVisibility(View.VISIBLE);*/
            String url = WebserviceUrl.GetFiles + currentFolder;
            if (sortBy != null && !sortBy.isEmpty()) {
                url += "&orderBy=" + sortBy;
            }

            Ion.with(this).load(url)
                    .setHeader("userToken", Application.getToken(getActivity()))
                    .as(GetFileModel_.class)
                    .setCallback(new FutureCallback<GetFileModel_>() {
                        @Override
                        public void onCompleted(Exception e, final GetFileModel_ result) {
                            /*progress_bar.setVisibility(View.GONE);*/
                            if (result != null) {
                                if (result.Data != null && result.Error == null && e == null) {
                                    if (result.Data.Navigate.size() == 0 || result.Data.Navigate.get(0).FolderID == null)
                                        Application.ParrentFolder = "";
                                    else
                                        Application.ParrentFolder = result.Data.Navigate.get(0).FolderID;
                                    int headerPos = 0;
                                    Boolean isHeader = true;
                                    final GetFileModel_ data = new GetFileModel_();
                                    if(result.Data.Files.size()>0) {
                                        if (result.Data.Files.get(0).IsFolder) {
                                            data.Data.Files.add(new FileData(getString(R.string.folder), true,false));
                                        }
                                        for (int i = 0; i < result.Data.Files.size(); i++) {

                                            if (!(result.Data.Files.get(i).IsFolder) && isHeader) {
                                                data.Data.Files.add(new FileData(getString(R.string.file), true,false));
                                                headerPos = i + 1;
                                                isHeader = false;
                                            }
                                            data.Data.Files.add(result.Data.Files.get(i));
                                        }
                                    }
                                    adapter.data = data;
                                    adapter.notifyDataSetChanged();
                                    final int finalHeaderPos = headerPos;
                                } else
                                    Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refreshFile(String currentFolder,String currentFolderTitle) {
        this.currentFolder = currentFolder;
        getFiles("Title", currentFolder);
        initCopy(selected,currentFolder);
    }
    private void initCopy(String selected,String currentFolder) {
        String [] arraySelected=selected.split(",");
        for (int i=0;i<arraySelected.length;i++){
            if(arraySelected[i].toString().equals(currentFolder)){
                dialog.txtPaste.setEnabled(false);
            }
        }
    }

    @Override
    public void pasteConfirm() {
        handler.dismissPasteDialog();
        if (Utils.isOnline(activity)) {
            Ion.with(activity)
                    .load(WebserviceUrl.CopyFile + selected.substring(0, selected.length() - 1) + WebserviceUrl.FolderId + currentFolder)
                    .setTimeout(1000000)
                    .setHeader("userToken", Application.getToken(activity)).asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e == null) {
                                if (!result.get("Data").toString().equals("null") || result.get("Error").toString().equals("null")) {

                                    Toast.makeText(activity, R.string.un_zip, Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(activity, result.get("Error").getAsJsonObject().get("ErrorMessage").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cutConfirm(final String tag) {
        handler.dismissPasteDialog();
        if (Utils.isOnline(activity)) {
            if(currentFolder==null){
                currentFolder="";
            }
            Ion.with(activity)
                    .load(WebserviceUrl.MoveFile + selected.substring(0, selected.length() - 1) + WebserviceUrl.FolderId + currentFolder)
                    .setHeader("userToken", Application.getToken(activity)).asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e == null) {
                                if (!result.get("Data").toString().equals("null") || result.get("Error").toString().equals("null")) {
                                    if (tag == "home") {
                                        HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag(tag);
                                        fragment.getFiles("Title",currentFolder );
                                    }
                                    if (tag == "favorite") {
                                        HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag(tag);
                                        fragment.getStaredFiles("Title");
                                    }
                                    if (tag == "deleted") {
                                        HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag(tag);
                                        fragment.getDeletedFiles("Title");

                                    }
                                    Toast.makeText(activity, R.string.pasted, Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(activity, result.get("Error").getAsJsonObject().get("ErrorMessage").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }
}
