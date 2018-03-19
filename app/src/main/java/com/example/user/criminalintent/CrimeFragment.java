package com.example.user.criminalintent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Ivo Georgiev(IfChyy)
 * Crime fragment shows infomration for each particular clicked crime
 * At the top is the Title followed by crime titleField(name, person)
 * after that are the details of the crime: Date ( a button alowing to specify date
 * and a check box informing the crime specialist if the crime was solved or not
 */

public class CrimeFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_CRIME_ID = "crime_id";
    public static final String ITEM_ID = "item_id";
    private static final int DIALOG_DATE1 = 25;
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    //create a extra to store the location of the photo
    private static final int REQUEST_PHOTO = 3;

    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private Button timeButton;
    private CheckBox solvedCheckBox;

    private FragmentManager manager;
    private UUID crimeId;


    private Button reportButton;
    private Button suspectButton;
    //intent to pick a contact
    final Intent pickContact = new Intent(Intent.ACTION_PICK,
            ContactsContract.Contacts.CONTENT_URI);

    private Button callButton;
    private ImageButton photoButton;
    private ImageView photoView;

    private File photoFile;
    //intent to open the camera
    final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    //called when new instance of crimefragment is needed to be creade
    public static CrimeFragment newInstance(UUID crimeID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeID);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //oncreate added to add the menu using setHasOptionsMenu
    //and init crime with crime id gotten from serializable bundle
    //return result method to return if fragment after closing returnrs reuslt to parent CHALLENGE
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //instance of crime

        crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        crime = CrimeLab.get(getActivity()).getCrime(crimeId);
        //init the photo of thecrime
        photoFile = CrimeLab.get(getActivity()).getPhotoFile(crime);
        returnResult();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);


        //edit text title field for crime fragment
        //added text changed listener
        titleField = v.findViewById(R.id.crime_title);
        titleField.setText(crime.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                crime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //date button to pick the date of the crime
        dateButton = v.findViewById(R.id.crime_date);
        dateButton.setOnClickListener(this);

        //time button to pick the time of the crime
        timeButton = v.findViewById(R.id.crime_time);
        timeButton.setOnClickListener(this);
        //using method updateDate to setText of the buttons
        updateDate();
        updateTime();

        //solved check box and ad a listener if checked which updates crime checked state
        solvedCheckBox = v.findViewById(R.id.crime_solved);
        solvedCheckBox.setChecked(crime.isSolved());
        solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                crime.setSolved(isChecked);
            }
        });

        //report button to send report via mail or other ap
        reportButton = v.findViewById(R.id.crime_report_button);
        reportButton.setOnClickListener(this);

        //suspect button to get a contanctg info from contacts app
        suspectButton = v.findViewById(R.id.crime_suspect_button);
        suspectButton.setOnClickListener(this);

        if (crime.getSuspectName() != null) {
            suspectButton.setText(crime.getSuspectName());
        }


        //check if user phone has contacts app? if not dissable suspect button
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, packageManager.MATCH_DEFAULT_ONLY) == null) {
            suspectButton.setEnabled(false);
        }
        //call button CHALLENGE to call the suspect
        callButton = v.findViewById(R.id.call_button);
        callButton.setOnClickListener(this);

        //photo button to take a photo of the crime
        photoButton = v.findViewById(R.id.crime_camera);
        photoButton.setOnClickListener(this);
        //image view to display the image
        photoView = v.findViewById(R.id.crime_photo);


        //check if there is camera app on the device and therefore enable or disable camera button
        boolean canTakePhoto = photoFile != null && captureImage.resolveActivity(packageManager) != null;
        photoButton.setEnabled(canTakePhoto);

        //if there is camera app
        //get the uri of the photo file after image saved
        //put extra for the output of the photo file
        if(canTakePhoto){
            Uri uri = Uri.fromFile(photoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }




        return v;

    }


    //override options menu to add + add button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);

    }

    //get menu item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
              /*  CrimeLab crimeLab = CrimeLab.get(getActivity());
                for (int i = 0; i < crimeLab.getCrimes().size(); i++) {
                    if(crimeLab.getCrimes().get(i).getId().equals(crimeId)){
                        crimeLab.getCrimes().remove(i);
                        getActivity().finish();

                    }
                }*/
                CrimeLab.get(getActivity()).deleteCrime(crime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //returns a result to parent fragment/activity after completion of this one
    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, new Intent().putExtra(ITEM_ID, crime.getTitle()));
    }

    //returns intent extra with key extra_answer_shown and value false
    public static String wasAnswerShown(Intent result) {
        return result.getStringExtra(ITEM_ID);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == dateButton.getId()) {
           /* manager = getFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
            //requests information using parametar from previous fragment
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
            dialog.show(manager, DIALOG_DATE);*/

            Intent in = DatePickerActivity.newIntent(getActivity(), crime.getDate());
            // startActivity(in);
            startActivityForResult(in, DIALOG_DATE1);

        }

        if (view.getId() == timeButton.getId()) {
            manager = getFragmentManager();
            TimePickerFragment dialog = TimePickerFragment.newInstance(crime.getDate());
            //requests information using parametar from previous fragment
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
            dialog.show(manager, DIALOG_TIME);

        }

        //used for sending mail to someone for the crime report
        if (view.getId() == reportButton.getId()) {
        /*    Intent in = new Intent(Intent.ACTION_SEND);
            in.setType("text/plain");
            in.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
            in.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
            in = Intent.createChooser(in, getString(R.string.send_report));
            startActivity(in);*/

            //CHALLENGE used intentBuilder to build intent
            ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(getActivity())
                    .setType("text/plain")
                    .setSubject(getActivity().getString(R.string.crime_report_subject));
            startActivity(intentBuilder.createChooserIntent());
        }

        //get a suspect name from contancts app adn return it into the button text
        if (view.getId() == suspectButton.getId()) {

            startActivityForResult(pickContact, REQUEST_CONTACT);

        }

        if(view.getId() == callButton.getId()){
            Intent in = new Intent(Intent.ACTION_DIAL);
            in.setData(Uri.parse("tel:" + callButton.getText()));
            startActivity(in);
        }

        if(view.getId() == photoButton.getId()){
            startActivityForResult(captureImage, REQUEST_PHOTO);
        }
    }

    //get the resulted date from date picker dialog fragment in onactivity result method

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }//add or dialog date 1 for dialog beeing in activity fragment not a dialog itself
        if (requestCode == REQUEST_DATE || requestCode == DIALOG_DATE1) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setDate(date);
            updateDate();

            //request the time from the fragment dialog and return it in the button
        } else if (requestCode == REQUEST_TIME) {

            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            crime.getDate().setMinutes(time.getMinutes());
            crime.getDate().setHours(time.getHours());
            //time button
            updateTime();

            //request the name of the contact selected and return in on the button
        } else if (requestCode == REQUEST_CONTACT && data != null) {

            //request contanct name by the book

           /* Uri contactUri = data.getData();
            //specify which fields to get data for
            String[] queryFeilds = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME};

            //perform the query to get the contanct
            Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFeilds, null, null, null);

//            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            try {
                if (cursor.getCount() == 0) {
                    return;
                }
                //pull out the first column of the first row of data
                //eg suspect name
                cursor.moveToFirst();
                String suspect = cursor.getString(0);
                crime.setSuspectName(suspect);
                suspectButton.setText(suspect);

            } finally {
                cursor.close();

            }*/

            //requesto contanct name and phone number by me
            Uri contactData = data.getData();
            Cursor cursor = getActivity().getContentResolver()
                    .query(contactData, null, null, null, null);

            //if cursor.moveToFrist()
            if (cursor.moveToFirst()) {
                //get the name of the contact and place it in the text of the suspect button
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                suspectButton.setText(name);
                String suspect = cursor.getString(0);
                crime.setSuspectName(suspect);

                //resolve contanct data + display name
                ContentResolver cr = getActivity().getContentResolver();
                Cursor content_uri = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                        "DISPLAY_NAME = '" + name + "'", null, null);

                // cursor has next get contact_id
                if (content_uri.moveToFirst()) {
                    String contactId =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    //query the phone by contanct id
                    Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                    //if phone present save it in a string and display it in button
                    if (phones.moveToFirst()) {
                        String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        callButton.setText(number);
                        crime.setSuspectNumber(number);
                    }
                    //finaly close the cursors
                    cursor.close();
                    content_uri.close();
                    phones.close();
                }
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(crime);
    }

    //method to update dateButton text and time button text
    private void updateDate() {
        //date button
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
        Date date = crime.getDate();
        dateButton.setText(simpleDateFormat.format(date));

    }

    //method to update dateButton text and time button text
    private void updateTime() {
        //date button
        String hours = String.valueOf(crime.getDate().getHours());
        String minutes = String.valueOf(crime.getDate().getMinutes());
        timeButton.setText(hours + ":" + minutes);

    }


    //get crime report method
    @SuppressLint("StringFormatInvalid")
    public String getCrimeReport() {
        String solvedString = null;

        if (crime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd");
        String dateString = simpleDateFormat.format(crime.getDate());

        String suspect = crime.getSuspectName();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, crime.getTitle(), dateString, solvedString, suspect);

        return report;

    }


}
