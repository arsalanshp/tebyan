package net.tebyan.filesharingapp.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.activities.MainActivity;
import net.tebyan.filesharingapp.model.FriendData;
import net.tebyan.filesharingapp.model.GetFriendsModel;

/**
 * Created by F.piri on 1/25/2016.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.CustomViewHolder> {
    public GetFriendsModel data;
    FragmentActivity activity;
    MainActivity.ShowContextMenu handler;
    public MainActivity.ShowBarMenu barHandler;
    public int type;
    MainActivity.RefreshDirectory refreshHandler;
    public SparseBooleanArray selectedItems;

    public FriendListAdapter(FragmentActivity context, GetFriendsModel data) {
        super();
        activity = context;
        this.data = data;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        CustomViewHolder viewHolder;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_row, viewGroup, false);
        viewHolder = new CustomViewHolder(view);
        viewHolder.itemView.setLongClickable(true);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {

        FriendData friendData = data.Data.get(i);
        customViewHolder.txtFriendName.setText(friendData.FirstName + " " + friendData.LastName);
    }

    @Override
    public int getItemCount() {
        return (null != data.Data ? data.Data.size() : 0);
    }

    @Override
    public long getItemId(int position) {

        return super.getItemId(position);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView txtFriendName;
        public ImageView imgdelete;

        public CustomViewHolder(View view) {
            super(view);
            this.txtFriendName = (TextView) view.findViewById(R.id.txt_friends_name);
            this.imgdelete = (ImageView) view.findViewById(R.id.delete_friends);
            imgdelete.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {


        }
    }
}
