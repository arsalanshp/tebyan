package net.tebyan.filesharingapp.adapter;

import android.support.v4.app.FragmentActivity;
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
import net.tebyan.filesharingapp.fragment.HomeFragment;
import net.tebyan.filesharingapp.fragment.MenuFragment;
import net.tebyan.filesharingapp.model.FileData;
import net.tebyan.filesharingapp.model.GetFileModel_;

import java.util.ArrayList;

/**
 * Created by F.piri on 1/25/2016.
 */

public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.CustomViewHolder> {
    public GetFileModel_ data;
    FragmentActivity activity;
    ArrayList<FileData> fileSelected;
    MenuFragment.ShowMenu handler;
    public int type;
    MainActivity.RefreshDirectory refreshHandler;
    private SparseBooleanArray selectedItems;

    public FolderListAdapter(FragmentActivity context, GetFileModel_ data ,int type) {
        super();
        activity = context;
        this.data = data;
        fileSelected = new ArrayList<>();
        selectedItems = new SparseBooleanArray();
        this.type=type;
    }

    public void setHandler(MenuFragment.ShowMenu handler) {
        this.handler = handler;
    }

    public void setRefreshHandler(MainActivity.RefreshDirectory handler) {
        this.refreshHandler = handler;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        CustomViewHolder viewHolder;
        if (viewType == 0) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_layout, viewGroup, false);
        } else if (viewType == 1) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_layout, viewGroup, false);
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
            if (data.Data.Files.get(i).SharedBy == null && customViewHolder.imgShared != null) {
                customViewHolder.imgShared.setVisibility(View.GONE);
            }
            if (customViewHolder.imgThumbnail != null) {
                Ion.with(customViewHolder.imgThumbnail)
                        .load(WebserviceUrl.SiteUrl + data.Data.Files.get(i).Thumb);
            }
        } else if (customViewHolder.txtFolderTitle != null && data.Data.Files.get(i).IsFolder) {
            customViewHolder.txtFolderTitle.setText(data.Data.Files.get(i).Title);
            Ion.with(customViewHolder.imgIconType)
                    .load(WebserviceUrl.SiteUrl +data.Data.Files.get(i).FileTypeIcon);
        }
        if (customViewHolder.imgMore != null) {
            customViewHolder.imgMore.setTag(i);

        }
        if (customViewHolder.imgTick != null) {

            /*customViewHolder.imgTick.setSelected(selectedItems.get(i, false));*/
            if (selectedItems.get(i)) {
                customViewHolder.imgTick.setSelected(true);
            } else {
                customViewHolder.imgTick.setSelected(false);
            }
        }
    }
 /*   public void getAllItems() {

        for (int i = 0; i < data.Data.Files.size(); i++) {
            View view=this;
            ImageView imgT= (ImageView) view.findViewById(R.id.img_tick);
            if (imgTick != null) {
                selectedItems.put(i, true);
                imgTick.setSelected(true);
                imgTick.setVisibility(View.VISIBLE);
            }
        }
    }*/
    @Override
    public int getItemCount() {
        return (null != data.Data ? data.Data.Files.size() : 0);
    }

    @Override
    public long getItemId(int position) {

        return super.getItemId(position);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, MainActivity.SelectedItems,MainActivity.deSelectedItems {
        protected TextView txtTitle, txtDate, txtHeader, txtFolderTitle;
        public ImageView imgIconType, imgShared, imgFavorite, imgThumbnail, imgMore, imgTick;

        FrameLayout container;

        public CustomViewHolder(View view) {
            super(view);
            container = (FrameLayout) itemView.findViewById(R.id.item_layout_container);
            this.txtTitle = (TextView) view.findViewById(R.id.txt_title);
            this.txtFolderTitle = (TextView) view.findViewById(R.id.txt_folder_title);
            txtHeader = (TextView) itemView.findViewById(R.id.txt_header);
            this.txtDate = (TextView) view.findViewById(R.id.txt_date);
            this.imgFavorite = (ImageView) view.findViewById(R.id.img_favorite);
            this.imgMore = (ImageView) view.findViewById(R.id.img_more);
            this.imgTick = (ImageView) view.findViewById(R.id.img_tick);
            this.imgIconType = (ImageView) view.findViewById(R.id.img_type);
            HomeFragment homeFragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag("home");
            homeFragment.setHandler(this);
            homeFragment.setDeSelectHandler(this);
            HomeFragment shareFragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag("shareWithMe");
            if(shareFragment!=null) {
                shareFragment.setHandler(this);
            }
            this.imgShared = (ImageView) view.findViewById(R.id.img_share);
            this.imgThumbnail = (ImageView) view.findViewById(R.id.img_thumbnail);
            if (imgMore != null) {
                imgMore.setOnClickListener(this);
            }
            if (imgTick != null) {
                imgTick.setOnClickListener(this);
            }
            view.setOnClickListener(this);

        }

        @Override
        public void getAllItems() {
            for (int i = 0; i < data.Data.Files.size(); i++) {
               /* View view=this;
                imgTick= (ImageView) view.findViewById(R.id.img_tick);*/
                if (imgTick != null) {
                    selectedItems.put(i, true);
                    imgTick.setSelected(true);
                    imgTick.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.img_more) {
                //View menuView = activity.getLayoutInflater().inflate(R.layout.menu_item_layout, null);
                selectedItems.put(getAdapterPosition(), true);
                imgTick.setSelected(true);
                imgTick.setVisibility(View.VISIBLE);
                handler.showContextMenu(getSelectedItems(), getFileNames(),type);
            } else {
                if (!data.Data.Files.get(getAdapterPosition()).IsFolder) {
                    if (imgTick != null) {
                        if (selectedItems.get(getAdapterPosition(), false)) {
                            selectedItems.delete(getAdapterPosition());
                            imgTick.setSelected(false);

                        } else {
                            selectedItems.put(getAdapterPosition(), true);
                            imgTick.setSelected(true);
                            imgTick.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (view.getId() == R.id.img_tick) {
                        if (imgTick != null) {
                            if (selectedItems.get(getAdapterPosition(), false)) {
                                selectedItems.delete(getAdapterPosition());
                                imgTick.setSelected(false);

                            } else {
                                selectedItems.put(getAdapterPosition(), true);
                                imgTick.setSelected(true);
                                imgTick.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {

                        refreshHandler.refreshFile(data.Data.Files.get(getAdapterPosition()).FileID);
                    }
                }
            }
             /* else {
                if (data.Data.Files.get(getLayoutPosition()).IsFolder)
                    Utils.reloadMainActivity(data.Data.Files.get(getLayoutPosition()).FileID, activity);
                else {
                    if (!fileSelected.contains(data.Data.Files.get(getLayoutPosition()))) {
                        fileSelected.add(data.Data.Files.get(getLayoutPosition()));
                        this.container.setBackgroundColor(Color.parseColor("#80000000"));
                        Application.fileIdSelected += data.Data.Files.get(getLayoutPosition()).FileID + ",";
                    } else {
                        fileSelected.remove(data.Data.Files.get(getLayoutPosition()));
                        this.container.setBackgroundColor(Color.TRANSPARENT);
                        String[] array = data.Data.Files.get(getLayoutPosition()).FileID.split(",");
                        String stringToRemove;
                        if (array.length == 1) {
                            stringToRemove = data.Data.Files.get(getLayoutPosition()).FileID + ",";
                            String currentFileIdSelected = Application.fileIdSelected.replace(stringToRemove, "");
                            Application.fileIdSelected = currentFileIdSelected;
                        } else {
                            stringToRemove = "," + data.Data.Files.get(getLayoutPosition()).FileID + ",";
                            String currentFileIdSelected = Application.fileIdSelected.replace(stringToRemove, "");
                            Application.fileIdSelected = currentFileIdSelected + ",";
                        }

                    }
                }

            }*/
        }


        @Override
        public void clearAllItems() {
            selectedItems.clear();
            for (int i = 0; i < data.Data.Files.size(); i++) {
                if (imgTick != null) {
                    imgTick.setSelected(false);
                    /*imgTick.setVisibility(View.GONE);*/
                }
            }
        }
    }


    private String getFileNames() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < selectedItems.size() + 1; i++) {
            if (selectedItems.valueAt(i)) {
                builder.append(data.Data.Files.get(selectedItems.keyAt(i)).Title);
                builder.append(",");
            }
        }
        return builder.toString();
    }

    private String getSelectedItems() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < selectedItems.size() + 1; i++) {
            if (selectedItems.valueAt(i)) {
                builder.append(data.Data.Files.get(selectedItems.keyAt(i)).FileID);
                builder.append(",");
            }
        }
        return builder.toString();
    }

}
