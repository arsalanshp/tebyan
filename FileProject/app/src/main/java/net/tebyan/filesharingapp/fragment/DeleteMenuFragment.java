package net.tebyan.filesharingapp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.classes.Utils;

/**
 * Created by v.karimi on 5/1/2016.
 */
public class DeleteMenuFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    public String selected;
    private String fileNames;
    String friendIds = "";
    public TextView txtRestore, txtDelete, txtMove;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {


        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @Override
    public void setupDialog(Dialog dialog, int style) {
        dialog.setTitle("عملیات مورد نظر");
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.delete_menu_item_layout, null);
        txtDelete = (TextView) contentView.findViewById(R.id.txt_delete);
        txtDelete.setOnClickListener(this);
        txtRestore = (TextView) contentView.findViewById(R.id.txt_restore);
        txtRestore.setOnClickListener(this);
        txtMove = (TextView) contentView.findViewById(R.id.txt_delete_move);
        txtMove.setOnClickListener(this);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_delete_move: {
                this.dismiss();
                Toast.makeText(getContext(), R.string.cut, Toast.LENGTH_SHORT).show();
                FragmentManager fm = getFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("index", selected);
                bundle.putString("type", "cut");
                bundle.putString("tag","deleted");
                PasteDialogFragment dialogFragment = new PasteDialogFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.show(fm, "paste fragment");
                break;
            }
            case R.id.txt_restore: {
                this.dismiss();
                Utils.RestoreFile(selected.substring(0,selected.length()-1), getActivity());
                break;
            }
            case R.id.txt_delete: {
                this.dismiss();
               Utils.deleteConfirm(selected.substring(0, selected.length() - 1), getActivity());
                break;
            }
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        selected = getArguments().getString("index");
        fileNames = getArguments().getString("fileNames");
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

}
