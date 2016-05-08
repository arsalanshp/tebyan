package net.tebyan.filesharingapp.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.activities.MainActivity;
import net.tebyan.filesharingapp.adapter.FolderAdapter;
import net.tebyan.filesharingapp.classes.Application;
import net.tebyan.filesharingapp.classes.ContextMenuRecyclerView;
import net.tebyan.filesharingapp.classes.Utils;
import net.tebyan.filesharingapp.classes.WebserviceUrl;
import net.tebyan.filesharingapp.model.FileData;
import net.tebyan.filesharingapp.model.GetFileModel_;

import java.util.ArrayList;

/**
 * Created by v.karimi on 4/24/2016.
 */
public class HomeFragment extends Fragment implements MainActivity.RefreshDirectory, MenuFragment.ShowMenu {

    public View view;
    public ContextMenuRecyclerView rv;
    public FolderAdapter adapter;
    public GetFileModel_ data;
    public String token;
    public Activity activity;
    public GridLayoutManager manager;
    public String currentFolder;
    public MainActivity.RefreshDirectory handler;
    boolean isPressed = true;
    public int type;
    public Boolean changeView = false;
    private MultiSelector mMultiSelector = new MultiSelector();
    public MainActivity.SelectedItems selectHandler;
    public MainActivity.deSelectedItems deSelectHandler;
    private ArrayList<FileData> fileDatas;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_layout, container, false);
        activity = getActivity();
        initUI();
        initData();
        token = Application.getToken(getActivity());
        return view;
    }

    public void setHandler(MainActivity.SelectedItems handler) {
        this.selectHandler = handler;
    }
    public void setDeSelectHandler(MainActivity.deSelectedItems handler) {
        this.deSelectHandler = handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void initUI() {
        rv = (ContextMenuRecyclerView) view.findViewById(R.id.rv);

    }

    public void initData() {
        data = new GetFileModel_();
        type = getArguments().getInt("type");
        switch (type) {
            case 0: {
                getFiles("", "");
                setAdapter(type);
                return;
            }
            case 1: {
                getSharedWithMe(currentFolder);
                setAdapter(type);
                return;
            }
            case 2: {
                getDeletedFiles();
                setAdapter(type);
                return;
            }
            case 3: {
                getStaredFiles();
                setAdapter(type);
                return;
            }
        }
    }

    public void setAdapter(int type) {
        adapter = new FolderAdapter(getActivity(), data, changeView,type);
        adapter.setHandler(this);
        adapter.setRefreshHandler(this);
        isTablet(activity);
        if (isTablet(activity)) {
            rv.setHasFixedSize(true);
            manager = new GridLayoutManager(activity, 3);
            rv.setLayoutManager(manager);
        } else {
            rv.setHasFixedSize(true);
            manager = new GridLayoutManager(activity, 2);
            rv.setLayoutManager(manager);
        }


        rv.setAdapter(adapter);

    }

    public static Boolean isTablet(Context context) {

        if ((context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {

            return true;
        }
        return false;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        activity.getMenuInflater().inflate(R.menu.main, menu);

        MenuItem eventsMI = menu.findItem(R.id.action_events);

        MenuItemCompat.setActionView(eventsMI, R.layout.notif);
        /*View count = eventsMI.getActionView();

        notifCount = (Button) count.findViewById(R.id.notif_count);

        notifCount.setText(String.valueOf(mNotifCount));
        notifCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, 10);


        notifCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setNotifCount(0);
                Utils.reloadMainActivity(Application.CurrentFolder, activity, false, true);
            }
        });*/

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {
                searchView.setSearchableInfo(searchManager
                        .getSearchableInfo(activity.getComponentName()));
                Utils.reloadMainActivity("", activity, false, false, text);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sort_menu: {
                sortDialog();
                return true;
            }
            case R.id.select_menu: {

                break;
            }
            case R.id.select_all_menu: {
                if(isPressed) {
                    selectHandler.getAllItems();
                    adapter.notifyDataSetChanged();
                    item.setTitle(getString(R.string.clear_selection));
                }else{
                    deSelectHandler.clearAllItems();
                    adapter.notifyDataSetChanged();
                    item.setTitle(getString(R.string.select_all));
                }
                isPressed=!isPressed;
                break;
            }
            case R.id.action_change_view: {
                changeView = !changeView;
                adapter.changeView = !adapter.changeView;
                getActivity().supportInvalidateOptionsMenu();
                if (adapter.changeView) {
                    rv.setHasFixedSize(true);
                    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                    adapter.notifyDataSetChanged();
                    rv.setAdapter(adapter);

                } else {
                    if (isTablet(getActivity())) {
                        setSpan(adapter.data);
                        rv.setHasFixedSize(true);
                        rv.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                        rv.setAdapter(adapter);
                    } else {
                        setSpan(adapter.data);
                        rv.setHasFixedSize(true);
                        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                        rv.setAdapter(adapter);
                    }
                    item.setIcon(R.drawable.grid_1);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void sortDialog() {
        final AlertDialog.Builder sortDialogBuilder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.sort_view, null, false);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radiog_sort);
        sortDialogBuilder.setView(view);
        final AlertDialog ad = sortDialogBuilder.create();
        ad.show();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_sort_by_name: {
                        ad.cancel();
                        getFiles("Title", currentFolder);
                        break;
                    }
                    case R.id.radio_sort_by_date: {
                        ad.cancel();
                        getFiles("Createdate", currentFolder);
                        break;
                    }
                }


            }
        });


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

                                    setSpan(result);
                                } else
                                    Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    private void setSpan(GetFileModel_ result) {
        int headerPos = 0;
        Boolean isHeader = true;
        final GetFileModel_ data = new GetFileModel_();
        if (result.Data.Files.get(0).IsFolder) {
            data.Data.Files.add(new FileData(getString(R.string.folder), true));
        }
        for (int i = 0; i < result.Data.Files.size(); i++) {

            if (!(result.Data.Files.get(i).IsFolder) && isHeader) {
                data.Data.Files.add(new FileData(getString(R.string.file), true));
                headerPos = i + 1;
                isHeader = false;
            }
            data.Data.Files.add(result.Data.Files.get(i));
        }
        final int finalHeaderPos = headerPos;
        if (manager != null) {
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position == 0 || (data.Data.Files.get(1).IsFolder && position == finalHeaderPos)) {
                        if (isTablet(getActivity())) {
                            return 3;
                        } else {
                            return 2;
                        }
                    } else {
                        return 1;
                    }
                }
            });
            rv.setLayoutManager(manager);

        }
        adapter.data = data;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showContextMenu(String fileIds, String fileNames,int type) {
        BottomSheetDialogFragment bottomSheetDialogFragment;
        Bundle bundle = new Bundle();
        bundle.putString("index", fileIds);
        bundle.putString("fileNames", fileNames);
        if(type==0){
            bottomSheetDialogFragment=new MenuFragment();
            bottomSheetDialogFragment.setArguments(bundle);
            bottomSheetDialogFragment.show(((FragmentActivity) getActivity()).getSupportFragmentManager(), "menuFragment");
        } if(type==2){
            bottomSheetDialogFragment=new DeleteMenuFragment();
            bottomSheetDialogFragment.setArguments(bundle);
            bottomSheetDialogFragment.show(((FragmentActivity) getActivity()).getSupportFragmentManager(), "deleteMenuFragment");
        }

    }


    @Override
    public void refreshFile(String currentFolder) {
        this.currentFolder = currentFolder;
        getFiles("Title", currentFolder);
    }

    public void getSharedWithMe(String current) {
        if (Utils.isOnline(getActivity())) {
//            progress_bar.setVisibility(View.VISIBLE);
            Ion.with(this).load(WebserviceUrl.GetSharedFilesWithMe)
                    .setHeader("userToken", Application.getToken(activity))
                    .as(GetFileModel_.class)
                    .setCallback(new FutureCallback<GetFileModel_>() {
                        @Override
                        public void onCompleted(Exception e, GetFileModel_ result) {
                            /*progress_bar.setVisibility(View.GONE);*/
                            if (result.Data != null && result.Error == null && e == null) {
                                if (result.Data.Navigate.size() == 0 || result.Data.Navigate.get(0).FolderID == null)
                                    Application.ParrentFolder = "";
                                else
                                    Application.ParrentFolder = result.Data.Navigate.get(0).FolderID;
                                data = result;
                                adapter.data = result;
                                adapter.notifyDataSetChanged();
                                if (result.Data != null && result.Error == null && e == null) {
                                    if (result.Data.Navigate.size() == 0 || result.Data.Navigate.get(0).FolderID == null)
                                        Application.ParrentFolder = "";
                                } else
                                    Application.ParrentFolder = result.Data.Navigate.get(0).FolderID;
                                setSpan(result);
                            } else
                                Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else
            Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public void getDeletedFiles() {
        if (Utils.isOnline(getActivity())) {
            /*progress_bar.setVisibility(View.VISIBLE);*/
            Ion.with(this).load(WebserviceUrl.GetDeletedFiles)
                    .setHeader("userToken", Application.getToken(getActivity()))
                    .as(GetFileModel_.class)
                    .setCallback(new FutureCallback<GetFileModel_>() {
                        @Override
                        public void onCompleted(Exception e, GetFileModel_ result) {
                            /*progress_bar.setVisibility(View.GONE);*/
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
                        }
                    });
        } else
            Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }
    public void getStaredFiles() {
        if (Utils.isOnline(getActivity())) {
            /*progress_bar.setVisibility(View.VISIBLE);*/
            Ion.with(this).load(WebserviceUrl.GetStaredFiles)
                    .setHeader("userToken", Application.getToken(getActivity()))
                    .as(GetFileModel_.class)
                    .setCallback(new FutureCallback<GetFileModel_>() {
                        @Override
                        public void onCompleted(Exception e, GetFileModel_ result) {
                            /*progress_bar.setVisibility(View.GONE);*/
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
                        }
                    });
        } else
            Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }
}
