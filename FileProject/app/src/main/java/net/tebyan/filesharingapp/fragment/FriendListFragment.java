package net.tebyan.filesharingapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
    public String token;
    public ProgressBar bar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.friends_layout,container,false);
        initUI();
        token = Application.getToken(getActivity());
        adapter.notifyDataSetChanged();
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initData();
    }

    private void initData() {
        getFriends();
    }
    public void setFriendsAdapter(){
        recyclerFriends.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FriendListAdapter(getActivity(), data);
        recyclerFriends.setHasFixedSize(true);
        recyclerFriends.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void initUI() {
        recyclerFriends= (RecyclerView) view.findViewById(R.id.friend_recycler);
        setFriendsAdapter();
    }

    public void getFriends() {
        if (Utils.isOnline(getActivity())) {
            /*bar.setVisibility(View.VISIBLE);*/
            String url = WebserviceUrl.GetFriends;
            Ion.with(this).load(url)
                    .setHeader("userToken", Application.getToken(getActivity()))
                    .as(GetFriendsModel.class)
                    .setCallback(new FutureCallback<GetFriendsModel>() {
                        @Override
                        public void onCompleted(Exception e, GetFriendsModel result) {
                            /*bar.setVisibility(View.GONE);*/
                            if (result.Data != null && result.Error == null && e == null) {
                                adapter.notifyDataSetChanged();

                            } else
                                Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else
            Toast.makeText(getActivity(), R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
    }

}
