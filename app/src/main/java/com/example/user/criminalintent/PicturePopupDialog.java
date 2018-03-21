package com.example.user.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.UUID;


/**
 * Created by Ivo Georgiev (IfChyy)
 * Class to represent the captured suspect image
 * in a pop up dialog on press of the image
 */

public class PicturePopupDialog extends DialogFragment {

    //extra to get info from parent fragment
    private static final String ARG_PICTURE = "com.example.user.criminalintent.bitmap";

    private ImageView photoView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //create dialog builder and inflater
        LayoutInflater inflater = getActivity().getLayoutInflater() ;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //get the layout
        View view = inflater.inflate(R.layout.picture_pop_up, null);
        //init the image view for the image
        photoView = view.findViewById(R.id.picture_pop_up);

        //update the image view with the photo from parent fragment
        updatePhotoView();

        return builder.setView(view).create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //remove white space in layout
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    //pass date argument from other fragmentsto this one with containing date element
    public static PicturePopupDialog newInstance(UUID id) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PICTURE, id);

        PicturePopupDialog popupDialog = new PicturePopupDialog();
        popupDialog.setArguments(args);

        return popupDialog;
    }

    //update the picture view with image and rotate 90 degree
    private void updatePhotoView(){
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_PICTURE);
        Crime crime = CrimeLab.get(getActivity()).getCrime(crimeId);
        //init the photo of thecrime
        File photoFile = CrimeLab.get(getActivity()).getPhotoFile(crime, getActivity());

        Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
        photoView.setImageBitmap(bitmap);
        photoView.setRotation(90);
    }



}
