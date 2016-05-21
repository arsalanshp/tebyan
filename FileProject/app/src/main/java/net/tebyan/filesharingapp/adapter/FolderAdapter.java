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
import net.tebyan.filesharingapp.model.FileData;
import net.tebyan.filesharingapp.model.GetFileModel_;

import java.util.ArrayList;

/**
 * Created by F.piri on 1/25/2016.
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.CustomViewHolder> {
    public GetFileModel_ data;
    FragmentActivity activity;
    ArrayList<FileData> fileSelected;
    MainActivity.ShowContextMenu handler;
    public MainActivity.ShowBarMenu barHandler;
    public int type;
    MainActivity.RefreshDirectory refreshHandler;
    public SparseBooleanArray selectedItems;

    public FolderAdapter(FragmentActivity context, GetFileModel_ data,int type) {
        super();
        activity = context;
        this.data = data;
        fileSelected = new ArrayList<>();
        selectedItems = new SparseBooleanArray();
        this.type=type;
    }

    public void setHandler(MainActivity.ShowContextMenu handler) {
        this.handler = handler;
    }
    public void setBarHandler(MainActivity.ShowBarMenu handler) {
        this.barHandler = handler;
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
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);
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
                    .load(WebserviceUrl.SiteUrl + data.Data.Files.get(i).FileTypeIcon);
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

    public int getSelectedSize(){
        return selectedItems.size();
    }
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
            HomeFragment favoriteFragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag("favorite");
            HomeFragment deletedFragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag("deleted");
            if(shareFragment!=null) {
                shareFragment.setHandler(this);
                shareFragment.setDeSelectHandler(this);
            }
            if(favoriteFragment!=null) {
                favoriteFragment.setHandler(this);
                favoriteFragment.setDeSelectHandler(this);
            }
            if(deletedFragment!=null) {
                deletedFragment.setHandler(this);
                deletedFragment.setDeSelectHandler(this);
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
        public boolean getAllItems() {
            for (int i = 0; i < data.Data.Files.size(); i++) {
               /* View view=this;
                imgTick= (ImageView) view.findViewById(R.id.img_tick);*/
                if (imgTick != null) {
                    selectedItems.put(i, true);
                    imgTick.setSelected(true);
                    imgTick.setVisibility(View.VISIBLE);
                }
            }
            if(selectedItems.size()>0){
                return false;
            }else
                return true;
        }
       /* public void showContextMenu(String fileIds, String fileNames, int type) {
            Bundle bundle = new Bundle();
            bundle.putString("index", fileIds);
            bundle.putString("fileNames", fileNames);
            if (type == 0) {
                MenuFragment bottomSheetDialogFragment = new MenuFragment();
                bottomSheetDialogFragment.setHandler(this);
                bottomSheetDialogFragment.setArguments(bundle);
                bottomSheetDialogFragment.show(((FragmentActivity) activity).getSupportFragmentManager(), "menuFragment");
            }
            if (type == 1) {
                ShareMenuFragment bottomSheetDialogFragment = new ShareMenuFragment();
                bottomSheetDialogFragment.setArguments(bundle);
                bottomSheetDialogFragment.show(((FragmentActivity) activity).getSupportFragmentManager(), "shareMenuFragment");
            }
            if (type == 2) {
                DeleteMenuFragment bottomSheetDialogFragment = new DeleteMenuFragment();
                bottomSheetDialogFragment.setArguments(bundle);
                bottomSheetDialogFragment.show(((FragmentActivity) activity).getSupportFragmentManager(), "deleteMenuFragment");
            }
            if (type == 3) {
                FavoriteMenuFragment bottomSheetDialogFragment = new FavoriteMenuFragment();
                bottomSheetDialogFragment.setArguments(bundle);
                bottomSheetDialogFragment.show(((FragmentActivity) activity).getSupportFragmentManager(), "FavoriteMenuFragment");
            }

        }*/
        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.img_more) {
                //View menuView = activity.getLayoutInflater().inflate(R.layout.menu_item_layout, null);
                selectedItems.put(getAdapterPosition(), true);
                imgTick.setSelected(true);
                imgTick.setVisibility(View.VISIBLE);
                handler.showContextMenu(getSelectedItems(), getFileNames(), type);
                /*showContextMenu(getSelectedItems(), getFileNames(),type);*/
            } else {
                if (!data.Data.Files.get(getAdapterPosition()).IsFolder) {
                    if (imgTick != null) {
                        if (selectedItems.get(getAdapterPosition(), false)) {
                            selectedItems.delete(getAdapterPosition());
                            imgTick.setSelected(false);
                                barHandler.initBarMenu();
                        } else {
                            selectedItems.put(getAdapterPosition(), true);
                            imgTick.setSelected(true);
                            imgTick.setVisibility(View.VISIBLE);
                            barHandler.initBarMenu();
                        }
                    }
                } else {
                    if (view.getId() == R.id.img_tick) {
                        if (imgTick != null) {
                            if (selectedItems.get(getAdapterPosition(), false)) {
                                selectedItems.delete(getAdapterPosition());
                                imgTick.setSelected(false);
                                barHandler.initBarMenu();

                            } else {
                                selectedItems.put(getAdapterPosition(), true);
                                imgTick.setSelected(true);
                                imgTick.setVisibility(View.VISIBLE);
                                barHandler.initBarMenu();
                            }

                        }
                    } else {

                        refreshHandler.refreshFile(data.Data.Files.get(getAdapterPosition()).FileID,data.Data.Files.get(getAdapterPosition()).Title);
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
                    notifyDataSetChanged();
                    /*imgTick.setVisibility(View.
                    GONE);*/
                }
            }
        }
    }



    public String getFileNames() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < selectedItems.size(); i++) {
            if (selectedItems.valueAt(i)) {
                builder.append(data.Data.Files.get(selectedItems.keyAt(i)).Title);
                builder.append(",");
            }
        }
        return builder.toString();
    }

    public String getSelectedItems() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < selectedItems.size(); i++) {
            if (selectedItems.valueAt(i)) {
                if(data.Data.Files.get(selectedItems.keyAt(i)).FileID!=null) {
                    builder.append(data.Data.Files.get(selectedItems.keyAt(i)).FileID);
                    builder.append(",");
                }
            }


        }

        return builder.toString();
    }




}
