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

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.activities.MainActivity;
import net.tebyan.filesharingapp.adapter.FolderAdapter;
import net.tebyan.filesharingapp.adapter.FolderListAdapter;
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
    public FolderListAdapter listAdapter;
    public GetFileModel_ data;
    public String token;
    public Activity activity;
    public GridLayoutManager manager;
    public String currentFolder;
    public MainActivity.RefreshDirectory handler;
    boolean isPressed = true;
    public int type;
    public static Boolean changeView = false;
    public MainActivity.SelectedItems selectHandler;
    public MainActivity.deSelectedItems deSelectHandler;
    private ArrayList<FileData> fileDatas;
    private int[] pos = new int[2];

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
        changeView=false;
    }

    public void initUI() {
        rv = (ContextMenuRecyclerView) view.findViewById(R.id.rv);

    }

    public void initData() {
        data = new GetFileModel_();
        type = getArguments().getInt("type");
        setNewAdapter(type);
        switch (type) {
            case 0: {
                getFiles("", "");
                break;
            }
            case 1: {
                getSharedWithMe();
                break;
            }
            case 2: {
                getDeletedFiles();
                break;
            }
            case 3: {
                getStaredFiles();
                break;
            }

        }
        adapter.notifyDataSetChanged();
    }

    public void setNewAdapter(int type) {
        adapter = new FolderAdapter(getActivity(), data, type);
        ((FolderAdapter)adapter).setHandler(this);
        ((FolderAdapter)adapter).setRefreshHandler(this);
        isTablet(activity);
        if (isTablet(activity)) {
            rv.setHasFixedSize(true);
            manager = new GridLayoutManager(activity, 3);
            rv.setLayoutManager(manager);
        } else {
            rv.setHasFixedSize(true);
            manager = new GridLayoutManager(activity, 2);
        }
        if (manager != null) {
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (adapter.getItemViewType(position)) {
                        case 0: {
                            return manager.getSpanCount();
                        }
                        case 1: {

                            return 1;
                        }
                        case 2: {
                            return 1;
                        }
                        default:return 1;
                    }
                }

        });
            rv.setLayoutManager(manager);
            rv.setHasFixedSize(true);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
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
                if (isPressed) {
                    selectHandler.getAllItems();
                    adapter.notifyDataSetChanged();
                    item.setTitle(getString(R.string.clear_selection));
                } else {
                    deSelectHandler.clearAllItems();
                    adapter.notifyDataSetChanged();
                    item.setTitle(getString(R.string.select_all));
                }
                isPressed = !isPressed;
                break;
            }
            case R.id.action_change_view: {
                if(changeView){
                    changeView=false;
                }else{
                    changeView=true;
                }
                getActivity().supportInvalidateOptionsMenu();

                if (changeView) {
                    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                    listAdapter=new FolderListAdapter(getActivity(),data,type);
                    listAdapter.setHandler(this);
                    listAdapter.setRefreshHandler(this);
                    rv.setHasFixedSize(true);
                    rv.setAdapter(listAdapter);
                    adapter.notifyDataSetChanged();
                } else {
                    if (isTablet(getActivity())) {
                        rv.setLayoutManager(manager);
                        /*adapter=new FolderAdapter(getActivity(),data,type);*/
                        adapter.setHandler(this);
                        adapter.setRefreshHandler(this);
                        adapter.notifyDataSetChanged();
                        rv.setAdapter(adapter);
                        rv.setHasFixedSize(true);
                    } else {
                        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                        adapter=new FolderAdapter(getActivity(), data, type);
                       adapter.setHandler(this);
                        adapter.setRefreshHandler(this);
                        rv.setAdapter(adapter);
                        rv.setHasFixedSize(true);
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
            if(currentFolder==null){
                currentFolder="";
            }
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
                                pos = addHeaders(result);
                                if (result.Data != null && result.Error == null && e == null) {
                                    if (result.Data.Navigate.size() == 0 || result.Data.Navigate.get(0).FolderID == null)
                                        Application.ParrentFolder = "";
                                    else
                                        Application.ParrentFolder = result.Data.Navigate.get(0).FolderID;

                                    // setAdapter(type);
                                } else
                                    Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else
            Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    private int[] addHeaders(GetFileModel_ result) {
        data.Data.Files.clear();
        int[] headerPosition = {-1, -1};
        Boolean isHeader = true;
        if (result.Data.Files.size()>0 && result.Data.Files.get(0).IsFolder) {
            data.Data.Files.add(new FileData(getString(R.string.folder), true, false));
            headerPosition[0] = 0;
        }
        for (int i = 0; i < result.Data.Files.size(); i++) {

            if (!(result.Data.Files.get(i).IsFolder) && isHeader) {
                data.Data.Files.add(new FileData(getString(R.string.file), true, false));
                headerPosition[1] = i + 1;
                isHeader = false;
            }
            data.Data.Files.add(result.Data.Files.get(i));
        }
        adapter.data = data;
        adapter.notifyDataSetChanged();
        return headerPosition;
    }


    @Override
    public void showContextMenu(String fileIds, String fileNames, int type) {
        BottomSheetDialogFragment bottomSheetDialogFragment;
        Bundle bundle = new Bundle();
        bundle.putString("index", fileIds);
        bundle.putString("fileNames", fileNames);
        if (type == 0) {
            bottomSheetDialogFragment = new MenuFragment();
            bottomSheetDialogFragment.setArguments(bundle);
            bottomSheetDialogFragment.show(((FragmentActivity) getActivity()).getSupportFragmentManager(), "menuFragment");
        }
        if (type == 2) {
            bottomSheetDialogFragment = new DeleteMenuFragment();
            bottomSheetDialogFragment.setArguments(bundle);
            bottomSheetDialogFragment.show(((FragmentActivity) getActivity()).getSupportFragmentManager(), "deleteMenuFragment");
        }if(type==3){
            bottomSheetDialogFragment=new FavoriteMenuFragment();
            bottomSheetDialogFragment.setArguments(bundle);
            bottomSheetDialogFragment.show(((FragmentActivity) getActivity()).getSupportFragmentManager(), "FavoriteMenuFragment");
        }

    }


    @Override
    public void refreshFile(String currentFolder) {
        this.currentFolder = currentFolder;
        getFiles("Title", currentFolder);
    }

    public void getSharedWithMe() {
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
                                addHeaders(result);
/*                                if (result.Data != null && result.Error == null && e == null) {
                                    if (result.Data.Navigate.size() == 0 || result.Data.Navigate.get(0).FolderID == null)
                                        Application.ParrentFolder = "";
                                } else
                                    Application.ParrentFolder = result.Data.Navigate.get(0).FolderID;*/
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
                                addHeaders(result);
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
                                addHeaders(result);
                                adapter.notifyDataSetChanged();

                            } else
                                Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else
            Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }
}
