package net.tebyan.filesharingapp.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.activities.MainActivity;
import net.tebyan.filesharingapp.classes.Application;
import net.tebyan.filesharingapp.classes.CropCircleTransformation;
import net.tebyan.filesharingapp.classes.WebserviceUrl;
import net.tebyan.filesharingapp.model.FriendData;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by F.piri on 1/25/2016.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.CustomViewHolder> {
    public ArrayList<FriendData> data;
    FragmentActivity activity;
    MainActivity.ShowContextMenu handler;
    public MainActivity.ShowBarMenu barHandler;
    public int type;
    MainActivity.RefreshDirectory refreshHandler;
    public SparseBooleanArray selectedItems;

    public FriendListAdapter(FragmentActivity context, ArrayList<FriendData>  data) {
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

        FriendData friendData = data.get(i);
        customViewHolder.txtFriendName.setText(friendData.FirstName + " " + friendData.LastName);
        String imageUrl = WebserviceUrl.SiteUrl+"/open/"+"token_"+ Application.getToken(activity)+"/GetAvatar/" + friendData.GuidID+ "?date=" + new Date().getTime();
        Ion.with(customViewHolder.imgAvatar).transform(new CropCircleTransformation()).load(imageUrl);

    }

    @Override
    public int getItemCount() {
        return (null != data ? data.size() : 0);
    }

    @Override
    public long getItemId(int position) {

        return super.getItemId(position);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView txtFriendName;
        public ImageView imgdelete,imgNewFriend,imgAvatar;

        public CustomViewHolder(View view) {
            super(view);
            this.txtFriendName = (TextView) view.findViewById(R.id.txt_friends_name);
            this.imgdelete = (ImageView) view.findViewById(R.id.img_delete_friends);
            this.imgAvatar = (ImageView) view.findViewById(R.id.img_avatar);
            this.imgNewFriend = (ImageView) view.findViewById(R.id.img_new);
            imgdelete.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {


        }
    }
}
