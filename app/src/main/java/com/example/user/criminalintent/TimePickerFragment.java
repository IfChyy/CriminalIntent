package com.example.user.criminalintent;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Ivo Georgiev(IfChyy)
 * TimePickerFragment allows the user to pick a time when the crime happened
 */

public class TimePickerFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_TIME = "time";
    public static final String EXTRA_TIME = "com.example.user.criminalintent.time";
    private TimePicker timePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Time time = (Time) getArguments().getSerializable(ARG_TIME);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.time_picker_title).setPositiveButton(android.R.string.ok, this).create();
    }

    //pass date argument from other fragmentsto this one with containing date element
    public static TimePickerFragment newInstance(Time time) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, time);

        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(args);

        return timePickerFragment;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
}
