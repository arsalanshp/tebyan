package net.tebyan.filesharingapp.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by v.karimi on 4/24/2016.
 */
public class HomeFragment extends Fragment implements MainActivity.RefreshDirectory,MainActivity.ShowBarMenu,View.OnClickListener,MainActivity.ShowContextMenu{

    public View view;
    public ContextMenuRecyclerView rv;
    public FolderAdapter adapter;
    public FolderListAdapter listAdapter;
    public GetFileModel_ data;
    public String token;
    public TextView txtCount;
    public Activity activity;
    public CardView linearBarMenu;
    public FloatingActionButton fab;
    public String title;
    public GridLayoutManager manager;
    //public String currentFolder;
    public MainActivity.RefreshDirectory handler;
    boolean isPressed = true;
    public int type;
    public static boolean checkState = true;
    //public String parentFolder;
    public static Boolean changeView = false;
    public ImageView imgMore;
    public MainActivity.SelectedItems selectHandler;
    public MainActivity.deSelectedItems deSelectHandler;
    private int[] pos = new int[2];
    private String fileSearched = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_layout, container, false);
        activity = getActivity();
        initUI();
        token = Application.getToken(getActivity());
        adapter.notifyDataSetChanged();
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
        fab= (FloatingActionButton) getActivity().findViewById(R.id.fab);
        initData();
        changeView = false;
    }

    public void initUI() {
        rv = (ContextMenuRecyclerView) view.findViewById(R.id.rv);
        linearBarMenu= (CardView) view.findViewById(R.id.linear_bar_menu);
        txtCount= (TextView) view.findViewById(R.id.txt_count);
        imgMore= (ImageView) view.findViewById(R.id.img_more_menu);
        imgMore.setOnClickListener(this);
        setNewAdapter(type);

    }

    public void initData() {
        data = new GetFileModel_();
        type = getArguments().getInt("type");
        switch (type) {
            case 0: {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.home));
                getFiles("", "");
                    fab.setVisibility(View.VISIBLE);

                break;
            }
            case 1: {
                getSharedWithMe("Title");
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.sharePeople));
                fab.setVisibility(View.GONE);
                break;
            }
            case 2: {
                getDeletedFiles("Title");
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.deleted));
                fab.setVisibility(View.GONE);
                break;
            }
            case 3: {
                getStaredFiles("Title");
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.favorite_menu));
                fab.setVisibility(View.GONE);
                break;
            }

        }

    }

    public void setNewAdapter(int type) {
        listAdapter=new FolderListAdapter(getActivity(),data,type);
        adapter = new FolderAdapter(getActivity(), data, type);
        listAdapter.setBarHandler(this);
        adapter.setBarHandler(this);
        adapter.setHandler(this);
        listAdapter.setHandler(this);
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
                        default:
                            return 1;
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


        notifCount.setOnClickListener(new_icon View.OnClickListener() {
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
                getSearchedFiles(text);
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
          /*  case R.id.select_menu: {

                break;
            }*/
            case R.id.select_all_menu: {
                if (isPressed) {
                    checkState = selectHandler.getAllItems();
                    adapter.notifyDataSetChanged();
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    }
                    item.setTitle(getString(R.string.select_all));

                } else {
                    deSelectHandler.clearAllItems();
                    adapter.notifyDataSetChanged();
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    }
                    item.setTitle(getString(R.string.select_all));
                }
                isPressed = !isPressed;
                break;
            }
            case R.id.action_change_view: {
                if (changeView) {
                    changeView = false;
                } else {
                    changeView = true;
                }
                getActivity().supportInvalidateOptionsMenu();

                if (changeView) {
                    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                    listAdapter = new FolderListAdapter(getActivity(), data, type);
                    listAdapter.setHandler(this);
                    listAdapter.setBarHandler(this);
                    listAdapter.setRefreshHandler(this);
                    rv.setHasFixedSize(true);
                    rv.setAdapter(listAdapter);
                    listAdapter.notifyDataSetChanged();
                } else {
                    if (isTablet(getActivity())) {
                        rv.setLayoutManager(manager);
                        /*adapter=new_icon FolderAdapter(getActivity(),data,type);*/
                        /*adapter.setHandler(this);*/
                        adapter.setRefreshHandler(this);
                        adapter.notifyDataSetChanged();
                        rv.setAdapter(adapter);
                        rv.setHasFixedSize(true);
                    } else {
                        rv.setLayoutManager(manager);
                        /*adapter.setHandler(this);*/
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem selectAction = menu.findItem(R.id.select_all_menu);
        if (checkState) {
            selectAction.setTitle(getString(R.string.select_all));
        } else {
            selectAction.setTitle(getString(R.string.select_all));
        }

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
                int type = getArguments().getInt("type");
                switch (i) {
                    case R.id.radio_sort_by_name: {
                        ad.cancel();
                        switch (type) {
                            case 0: {
                                getFiles("Title", Application.CurrentFolder);
                                break;
                            }
                            case 1: {
                                getSharedWithMe("Title");
                                break;
                            }
                            case 2: {
                                getDeletedFiles("Title");
                                break;
                            }
                            case 3: {
                                getStaredFiles("Title");
                                break;
                            }
                        }

                        break;
                    }
                    case R.id.radio_sort_by_date: {
                        ad.cancel();
                        switch (type) {
                            case 0: {
                                getFiles("Createdate", Application.CurrentFolder);
                                break;
                            }
                            case 1: {
                                getSharedWithMe("Createdate");
                                break;
                            }
                            case 2: {
                                getDeletedFiles("Createdate");
                                break;
                            }
                            case 3: {
                                getStaredFiles("Createdate");
                                break;
                            }
                        }
                        break;
                    }
                    case R.id.radio_sort_by_size: {
                        ad.cancel();
                        switch (type) {
                            case 0: {
                                getFiles("Size", Application.CurrentFolder);
                                break;
                            }
                            case 1: {
                                getSharedWithMe("Size");
                                adapter.notifyDataSetChanged();
                                break;
                            }
                            case 2: {
                                getDeletedFiles("Size");
                                break;
                            }
                            case 3: {
                                getStaredFiles("Size");
                                break;
                            }
                        }
                        break;
                    }
                }


            }
        });


    }

    public void getFiles(String sortBy, String currentFolder) {
        if (Utils.isOnline((MainActivity)getActivity())) {
            ((MainActivity) getActivity()).progress_bar.setVisibility(View.VISIBLE);

            String url = WebserviceUrl.GetFiles + currentFolder;
            if (sortBy != null && !sortBy.isEmpty()) {
                url += "&orderBy=" + sortBy + "&sortOrder=ASC";
            }

            Ion.with(this).load(url)
                    .setHeader("userToken", Application.getToken(getActivity()))

                    .as(GetFileModel_.class)
                    .setCallback(new FutureCallback<GetFileModel_>() {
                        @Override
                        public void onCompleted(Exception e, final GetFileModel_ result) {
                            ((MainActivity) getActivity()).progress_bar.setVisibility(View.GONE);
                            if (result != null) {
                                pos = addHeaders(result);
                             if (result.Data != null && result.Error == null && e == null) {
                                    if (result.Data.Navigate.size() == 0 || result.Data.Navigate.get(0).FileID == null) {
                                        Application.CurrentFolder = "";
                                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.home));
                                        Application.ParrentFolder = null;
                                    }
                                    else {
                                        if (result.Data.Navigate.size() == 1) {
                                            Application.ParrentFolder = result.Data.Navigate.get(0).FileID;
                                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(result.Data.Navigate.get(0).Title);
                                        }if( Application.ParrentFolder == result.Data.Navigate.get(0).FileID && result.Data.Navigate.size()==1){
                                            Application.ParrentFolder="";

                                        }
                                        if(result.Data.Navigate.size() >1) {
                                            Application.ParrentFolder = result.Data.Navigate.get(1).FileID;
                                            Application.CurrentFolder = result.Data.Navigate.get(0).FileID;
                                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(result.Data.Navigate.get(0).Title);
                                        }
                                    }
                                }
                            } else
                                Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
            if (adapter != null) {
                adapter.selectedItems.clear();
            }
            if (listAdapter != null) {
                listAdapter.selectedItems.clear();
            }
        } else
            Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    private int[] addHeaders(GetFileModel_ result) {
        int[] headerPosition = {-1, -1};
        if (result != null) {
            data.Data.Files.clear();
            Boolean isHeader = true;
            if (result.Data.Files.size() > 0) {
                if (result.Data.Files.get(0).IsFolder) {
                    data.Data.Files.add(new FileData(getString(R.string.folder), true, false));
                    headerPosition[0] = 0;
                }
            }
            for (int i = 0; i < result.Data.Files.size(); i++) {

                if (!(result.Data.Files.get(i).IsFolder) && isHeader) {
                    data.Data.Files.add(new FileData(getString(R.string.file), true, false));
                    headerPosition[1] = i + 1;
                    isHeader = false;
                }
                data.Data.Files.add(result.Data.Files.get(i));
            }
            if (listAdapter != null) {
                listAdapter.data = data;
            }
            adapter.data = data;
            if(listAdapter!=null){
                listAdapter.data = data;
                listAdapter.notifyDataSetChanged();
            }
            adapter.notifyDataSetChanged();
        }
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
            linearBarMenu.setVisibility(View.GONE);
        }
        if (type == 1) {
            bottomSheetDialogFragment = new ShareMenuFragment();
            bottomSheetDialogFragment.setArguments(bundle);
            bottomSheetDialogFragment.show(((FragmentActivity) getActivity()).getSupportFragmentManager(), "shareMenuFragment");
            linearBarMenu.setVisibility(View.GONE);

        }
        if (type == 2) {
            bottomSheetDialogFragment = new DeleteMenuFragment();
            bottomSheetDialogFragment.setArguments(bundle);
            bottomSheetDialogFragment.show(((FragmentActivity) getActivity()).getSupportFragmentManager(), "deleteMenuFragment");
            linearBarMenu.setVisibility(View.GONE);
        }
        if (type == 3) {
            bottomSheetDialogFragment = new FavoriteMenuFragment();
            bottomSheetDialogFragment.setArguments(bundle);
            bottomSheetDialogFragment.show(((FragmentActivity) getActivity()).getSupportFragmentManager(), "FavoriteMenuFragment");
            linearBarMenu.setVisibility(View.GONE);
        }

    }

    @Override
    public void refreshFile(String currentFolder,String currentFolderName) {
        if (currentFolder.equals("")) {
            Application.ParrentFolder = null;
        } else {
            Application.ParrentFolder = Application.CurrentFolder;
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentFolderName);
        }
        Application.CurrentFolder = currentFolder;
        getFiles("Title", currentFolder);
        if(linearBarMenu.getVisibility()==View.VISIBLE) {
            linearBarMenu.setVisibility(View.GONE);
        }
    }

    public void getSharedWithMe(String sortBy) {
        if (Utils.isOnline(getActivity())) {
            ((MainActivity) getActivity()).progress_bar.setVisibility(View.VISIBLE);
            String url = WebserviceUrl.GetSharedFilesWithMe+Application.CurrentFolder;
            if (sortBy != null && !sortBy.isEmpty()) {
                url += "&orderBy=" + sortBy + "&order=ASC";
            }
            Ion.with(this).load("GET",url)
                    .setHeader("userToken", Application.getToken(getActivity()))
                    /*.setBodyParameter("folderId", "")
                    .setBodyParameter("pageIndex", "0")
                    .setBodyParameter("pageSize", "10")
                    .setBodyParameter("order", "DESC")
                    .setBodyParameter("orderBy", "Createdate")*/
                    .as(GetFileModel_.class)
                    .setCallback(new FutureCallback<GetFileModel_>() {
                        @Override
                        public void onCompleted(Exception e, GetFileModel_ result) {
                            ((MainActivity) getActivity()).progress_bar.setVisibility(View.GONE);
                            if (result.Data != null && result.Error == null && e == null) {
                                if (result.Data.Navigate.size() == 0 || result.Data.Navigate.get(0).FileID == null) {
                                    Application.CurrentFolder = "";
                                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.shared));
                                    Application.ParrentFolder = null;
                                }
                                else {
                                    if (result.Data.Navigate.size() == 1) {
                                        Application.ParrentFolder = result.Data.Navigate.get(0).FileID;
                                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(result.Data.Navigate.get(0).Title);
                                    }if( Application.ParrentFolder == result.Data.Navigate.get(0).FileID && result.Data.Navigate.size()==1){
                                        Application.ParrentFolder="";

                                    }
                                    if(result.Data.Navigate.size() >1) {
                                        Application.ParrentFolder = result.Data.Navigate.get(1).FileID;
                                        Application.CurrentFolder = result.Data.Navigate.get(0).FileID;
                                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(result.Data.Navigate.get(0).Title);
                                    }
                                }
                                addHeaders(result);
                                Ion.with(getActivity())
                                        .load(WebserviceUrl.RepositoryServiceUrl + "SetNoteShareReaded")
                                        .setHeader("userToken", Application.getToken(getActivity()))
                                        .setHeader("checkToken", "true")
                                        .asString()
                                        .setCallback(new FutureCallback<String>() {
                                            @Override
                                            public void onCompleted(Exception e, String result) {
                                            }
                                        });
                            } else
                                Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else
            Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public void getDeletedFiles(String sortBy) {
        if (Utils.isOnline(getActivity())) {
            ((MainActivity) getActivity()).progress_bar.setVisibility(View.VISIBLE);
            String url = WebserviceUrl.GetDeletedFiles+Application.CurrentFolder;
            if (sortBy != null && !sortBy.isEmpty()) {
                url += "&orderBy=" + sortBy + "&order=ASC";
            }
            Ion.with(this).load(url)
                    .setHeader("userToken", Application.getToken(getActivity()))
                    .as(GetFileModel_.class)
                    .setCallback(new FutureCallback<GetFileModel_>() {
                        @Override
                        public void onCompleted(Exception e, GetFileModel_ result) {
                            ((MainActivity) getActivity()).progress_bar.setVisibility(View.GONE);
                            if (result.Data != null && result.Error == null && e == null) {
                                if (result.Data.Navigate.size() == 0 || result.Data.Navigate.get(0).FileID == null) {
                                    Application.CurrentFolder = "";
                                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.deleted));
                                    Application.ParrentFolder = null;
                                }
                                else {
                                    if (result.Data.Navigate.size() == 1) {
                                        Application.ParrentFolder = result.Data.Navigate.get(0).FileID;
                                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(result.Data.Navigate.get(0).Title);
                                    }if( Application.ParrentFolder == result.Data.Navigate.get(0).FileID && result.Data.Navigate.size()==1){
                                        Application.ParrentFolder="";

                                    }
                                    if(result.Data.Navigate.size() >1) {
                                        Application.ParrentFolder = result.Data.Navigate.get(1).FileID;
                                        Application.CurrentFolder = result.Data.Navigate.get(0).FileID;
                                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(result.Data.Navigate.get(0).Title);
                                    }
                                }
                                addHeaders(result);
                            } else
                                Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else
            Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

    public void getStaredFiles(String sortBy) {
        if (Utils.isOnline(getActivity())) {
            ((MainActivity) getActivity()).progress_bar.setVisibility(View.VISIBLE);
            String url = WebserviceUrl.GetStaredFiles+Application.CurrentFolder;
            if (sortBy != null && !sortBy.isEmpty()) {
                url += "&orderBy=" + sortBy + "&sortOrder=ASC";
            }
            Ion.with(this).load(url)
                    .setHeader("userToken", Application.getToken(getActivity()))
                    .as(GetFileModel_.class)
                    .setCallback(new FutureCallback<GetFileModel_>() {
                        @Override
                        public void onCompleted(Exception e, GetFileModel_ result) {
                            ((MainActivity) getActivity()).progress_bar.setVisibility(View.GONE);
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

    public void getSearchedFiles(String str) {
        if (Utils.isOnline(getActivity())) {
            ((MainActivity) getActivity()).progress_bar.setVisibility(View.VISIBLE);
            try {
                String encodedStr = URLEncoder.encode(str, "UTF-8");

                Ion.with(this).load(WebserviceUrl.SearchFile + encodedStr)
                        .setHeader("userToken", token)
                        .as(GetFileModel_.class)
                        .setCallback(new FutureCallback<GetFileModel_>() {
                            @Override
                            public void onCompleted(Exception e, GetFileModel_ result) {
                                ((MainActivity) getActivity()).progress_bar.setVisibility(View.GONE);
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
                                if(listAdapter!=null){
                                    listAdapter.data=data;
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void initBarMenu() {
        if(adapter.getSelectedSize()>1||listAdapter.getSelectedSize()>1) {
            linearBarMenu.setVisibility(View.VISIBLE);
            int count=adapter.selectedItems.size();
            if(count==0){
                count=listAdapter.selectedItems.size();
            }
            txtCount.setText(count+" "+getString(R.string.item_selected));
        }else{
            /*txtCount.setText(adapter.selectedItems.size()+" "+getString(R.string.item_selected));*/
            linearBarMenu.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        String selectedFileId=adapter.getSelectedItems();
        String selectedFileName=adapter.getFileNames();
        switch (view.getId()){
            case R.id.img_more_menu:{
            showContextMenu(selectedFileId, selectedFileName,type);
                linearBarMenu.setVisibility(View.GONE);
                break;
            }

        }
    }

}
