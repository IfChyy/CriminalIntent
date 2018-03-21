package com.example.user.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by Ivo Georgiev(IfChyy)
 * TimePickerFragment allows the user to pick a time when the crime happened
 */

public class TimePickerFragment extends DialogFragment implements DialogInterface.OnClickListener {
    //extra to get from parent
    private static final String ARG_TIME = "time";
    public static final String EXTRA_TIME = "com.example.user.criminalintent.time";
    private TimePicker timePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //init the dialog with the date provided from parent
        Calendar calendar = Calendar.getInstance();
        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        calendar.setTime(date);
        int hour = date.getHours();
        int minutes = date.getMinutes();
        //init the layout
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        //init the time picker and set the provided time
        timePicker = v.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minutes);
        //build the dialog with ok button
        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.time_picker_title).setPositiveButton(android.R.string.ok, this).create();
    }
    //get when ok button clicked
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        int hour = timePicker.getCurrentHour();
        int minutes = timePicker.getCurrentMinute();
        //get new date selected and send it back to parent fragment
        Date date = new Date();
        date.setHours(hour);
        date.setMinutes(minutes);
        sendResult(Activity.RESULT_OK, date);
    }

    //pass date argument from other fragmentsto this one with containing date element
    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(args);

        return timePickerFragment;
    }

    //METHOD TO send result from dialog fragment(Date picker) to fragment (Crime) shown for the user
    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
