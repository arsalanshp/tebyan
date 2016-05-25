package net.tebyan.filesharingapp.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.adapter.FriendListAdapter;
import net.tebyan.filesharingapp.classes.Application;
import net.tebyan.filesharingapp.classes.Utils;
import net.tebyan.filesharingapp.classes.WebserviceUrl;
import net.tebyan.filesharingapp.model.GetFriendsModel;

/**
 * Created by v.karimi on 5/23/2016.
 */
public class FriendListFragment extends Fragment {
    public Context context;
    public View view;
    public RecyclerView recyclerFriends;
    public FriendListAdapter adapter;
    public GetFriendsModel data;
    public Activity activity;
    public String token;
    public ProgressBar bar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.friends_layout,container,false);
        initUI();
        initData();
        token = Application.getToken(getActivity());
        adapter.notifyDataSetChanged();
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity=getActivity();
        initData();
    }

    private void initData() {
        data=new GetFriendsModel();
        getFriends("");
    }
    public void setFriendsAdapter(){
        recyclerFriends.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter = new FriendListAdapter(getActivity(), data.getData());
        recyclerFriends.setHasFixedSize(true);
        recyclerFriends.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void initUI() {
        recyclerFriends= (RecyclerView) view.findViewById(R.id.friend_recycler);
        setFriendsAdapter();
    }

    public void getFriends(String name) {
        if (Utils.isOnline(getActivity())) {
            /*bar.setVisibility(View.VISIBLE);*/
            String url;
            if(name.equals("")) {
                url = WebserviceUrl.GetFriends;
            }else {
                url = WebserviceUrl.GetFriends+"?name="+name;
            }
            Ion.with(this).load(url)
                    .setHeader("userToken", Application.getToken(getActivity()))
                    .as(GetFriendsModel.class)
                    .setCallback(new FutureCallback<GetFriendsModel>() {
                        @Override
                        public void onCompleted(Exception e, GetFriendsModel result) {
                            /*bar.setVisibility(View.GONE);*/
                            if (result.Data != null && result.Error == null && e == null) {
                                adapter.data = result.Data;
                                adapter.notifyDataSetChanged();

                            } else
                                Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else
            Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        activity.getMenuInflater().inflate(R.menu.profile, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {
                getFriends(text);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                return false;
            }
        });

    }

}
