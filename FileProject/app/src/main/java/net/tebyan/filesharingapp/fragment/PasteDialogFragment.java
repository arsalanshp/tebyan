package net.tebyan.filesharingapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.activities.MainActivity;
import net.tebyan.filesharingapp.classes.Application;

/**
 * Created by v.karimi on 5/2/2016.
 */
public class PasteDialogFragment extends DialogFragment implements View.OnClickListener, MainActivity.DismissPasteDialog {

    public View view;
    public Context context;
    public FrameLayout frame;
    public String selected;
    public TextView txtPaste, txtCancel;
    public MainActivity.PasteConfirm handler;
    public MainActivity.CutConfirm cutHandler;
    public MainActivity.ShowPasteDirectory pasteHandler;
    public String type;
    public  PasteFragment fragment;
    public ImageView imgBack;
    public FragmentActivity activity;
    public String tag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.paste_layout, container, false);
        type = getArguments().getString("type");
        frame = (FrameLayout) view.findViewById(R.id.frame_layout);
        txtPaste = (TextView) view.findViewById(R.id.txt_paste_confirm);
        txtPaste.setOnClickListener(this);
        imgBack = (ImageView) view.findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        txtPaste.setEnabled(true);
        txtCancel = (TextView) view.findViewById(R.id.txt_paste_cancel);
        selected = getArguments().getString("index");
        txtCancel.setOnClickListener(this);
        tag = getArguments().getString("tag");
        showHomeFragment();
        /*getDialog().setTitle(getString(R.string.paste_dialog_header));*/
        return view;
    }
    public void setPasteHandler(MainActivity.ShowPasteDirectory handler) {
        this.pasteHandler = handler;
    }

    public void setHandler(MainActivity.PasteConfirm handler) {

        this.handler = handler;
    }

    public void setPasteHandler(MainActivity.CutConfirm handler) {

        this.cutHandler = handler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    private void showHomeFragment() {
        fragment = new PasteFragment();
        fragment.setHandler(this);
        fragment.dialog = this;
        android.support.v4.app.FragmentManager fragmentManager = getChildFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("index", selected);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(this.frame.getId(), fragment, "tag");
        fragmentTransaction.addToBackStack("tag");
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_paste_confirm: {
                if (type == "copy") {
                    handler.pasteConfirm();
                } else {
                    cutHandler.cutConfirm(tag);
                }
            }
            break;
            case R.id.txt_paste_cancel: {
                this.dismiss();
                break;
            }
            case R.id.img_back: {
                if (Application.PasteFolder != "") {
                    Application.PasteFolder = Application.PasteParentFolder;
                    fragment.getFiles("Title", Application.PasteParentFolder);
                } else {
                    dismiss();
                }
                break;
            }
        }
    }

    @Override
    public void dismissPasteDialog() {
        this.dismiss();
    }

}
