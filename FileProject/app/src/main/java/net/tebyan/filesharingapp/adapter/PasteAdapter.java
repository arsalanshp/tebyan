package net.tebyan.filesharingapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.activities.MainActivity;
import net.tebyan.filesharingapp.classes.WebserviceUrl;
import net.tebyan.filesharingapp.model.FileData;
import net.tebyan.filesharingapp.model.GetFileModel_;

import java.util.ArrayList;

/**
 * Created by v.karimi on 5/3/2016.
 */
public class PasteAdapter extends RecyclerView.Adapter<PasteAdapter.CustomViewHolder> {
    public GetFileModel_ data;
    Activity activity;
    ArrayList<FileData> fileSelected;
    public MainActivity.RefreshDirectory handler;
    private SparseBooleanArray selectedItems;

    public PasteAdapter(Activity context, GetFileModel_ data) {
        super();
        activity = context;
        this.data = data;
        fileSelected = new ArrayList<>();
        selectedItems = new SparseBooleanArray();
    }

    public void setHandler(MainActivity.RefreshDirectory handler)
    {
        this.handler = handler;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        CustomViewHolder viewHolder;
        if (viewType == 0) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_layout, viewGroup, false);
        } else if (viewType == 1) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.list_row_layout , viewGroup, false);
        } else if (viewType == 2) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_folder_row, viewGroup, false);
        }
        viewHolder = new CustomViewHolder(view);
        viewHolder.itemView.setLongClickable(true);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (!(data.Data.Files.get(position).IsFolder)) {
            if (data.Data.Files.get(position).isHeader) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 2;
        }

    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        if(customViewHolder.imgMore!=null) {
            customViewHolder.imgMore.setVisibility(View.GONE);
        }
        if (data.Data.Files.get(i).isHeader) {
            customViewHolder.txtHeader.setText(data.Data.Files.get(i).Title);
        } else if (!(data.Data.Files.get(i).IsFolder)) {
            customViewHolder.txtTitle.setText(data.Data.Files.get(i).Title);
            Ion.with(customViewHolder.imgIconType)
                    .load(WebserviceUrl.SiteUrl +data.Data.Files.get(i).FileTypeIcon);
            if (customViewHolder.txtDate != null) {
                customViewHolder.txtDate.setText(data.Data.Files.get(i).MonthStr);
            }

            if (data.Data.Files.get(i).Stared == null && customViewHolder.imgFavorite != null) {
                customViewHolder.imgFavorite.setVisibility(View.GONE);
            }
            if (data.Data.Files.get(i).Sharedate == null && customViewHolder.imgShared != null) {
                customViewHolder.imgShared.setVisibility(View.GONE);
            }
        } else if (customViewHolder.txtFolderTitle != null && data.Data.Files.get(i).IsFolder) {
            customViewHolder.txtFolderTitle.setText(data.Data.Files.get(i).Title);
        }

    }

    @Override
    public int getItemCount() {
        return (null != data.Data ? data.Data.Files.size() : 0);
    }

    @Override
    public long getItemId(int position) {

        return super.getItemId(position);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView txtTitle, txtDate, txtHeader, txtFolderTitle;
        protected ImageView imgIconType, imgShared, imgFavorite,imgMore;

        FrameLayout container;

        public CustomViewHolder(View view) {
            super(view);
            container = (FrameLayout) itemView.findViewById(R.id.item_layout_container);
            this.txtTitle = (TextView) view.findViewById(R.id.txt_title);
            this.txtFolderTitle = (TextView) view.findViewById(R.id.txt_folder_title);
            txtHeader = (TextView) itemView.findViewById(R.id.txt_header);
            this.imgMore = (ImageView) view.findViewById(R.id.img_more);
            this.txtDate = (TextView) view.findViewById(R.id.txt_date);
            this.imgFavorite = (ImageView) view.findViewById(R.id.img_favorite);
            this.imgIconType = (ImageView) view.findViewById(R.id.img_type);
            this.imgShared = (ImageView) view.findViewById(R.id.img_share);
            view.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            if (data.Data.Files.get(getLayoutPosition()).IsFolder) {
                /*Utils.reloadMainActivity(data.Data.Files.get(getLayoutPosition()).FileID, activity);*/
                handler.refreshFile(data.Data.Files.get(getLayoutPosition()).FileID,data.Data.Files.get(getAdapterPosition()).Title);
            }
        }
    }
}
