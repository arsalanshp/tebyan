package net.tebyan.filesharingapp.model;

import java.util.ArrayList;

/**
 * Created by F.piri on 2/16/2016.
 */
public class GetFriendsModel extends MainModel {
    public ArrayList<FriendData> Data;

    public ArrayList<FriendData> getData() {
        return Data;
    }

    public void setData(ArrayList<FriendData> data) {
        Data = data;
    }
}
