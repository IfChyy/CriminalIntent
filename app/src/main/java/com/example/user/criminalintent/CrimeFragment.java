package com.example.user.criminalintent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Ivo Georgiev(IfChyy)
 * Crime fragment shows infomration for each particular clicked crime
 * At the top is the Title followed by crime titleField(name, person)
 * after that are the details of the crime: Date ( a button alowing to specify date
 * and a check box informing the crime specialist if the crime was solved or not
 * <p>
 * added crime photo and crime photo button to take a photo of the crime
 */

public class CrimeFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_CRIME_ID = "crime_id";
    public static final String ITEM_ID = "item_id";
    private static final int DIALOG_DATE1 = 25;
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    //create a extra to store the location of the photo
    private static final int REQUEST_PHOTO = 3;
    //initial fields
    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private Button timeButton;
    private CheckBox solvedCheckBox;
    //used fragment manager to fire the fragment
    private FragmentManager manager;
    private UUID crimeId;
    //added report and suspect buttons
    private Button reportButton;
    private Button suspectButton;
    //intent to pick a contact
    final Intent pickContact = new Intent(Intent.ACTION_PICK,
            ContactsContract.Contacts.CONTENT_URI);
    //add callbutoon and photo buttons
    private Button callButton;
    private ImageButton photoButton;
    private ImageView photoView;
    //add photo file to store the image location
    private File photoFile;
    //intent to open the camera
    final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    //width and height gotten after viewtree observer listening for layout pass
    private int photoViewWidth, photoViewHeight;
    //callbacks for calling methods to atach and detach fragments in tablet mode
    private Callbacks callbacks;

    //oncreate added to add the menu using setHasOptionsMenu
    //and init crime with crime id gotten from serializable bundle
    //return result method to return if fragment after closing returnrs reuslt to parent CHALLENGE
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //instance of crime

        //initilised the crimeid from arguments passed by intent/fragment / reuslt
        crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        //gotten crime from crimlab with id
        crime = CrimeLab.get(getActivity()).getCrime(crimeId);
        //init the photo of thecrime
        photoFile = CrimeLab.get(getActivity()).getPhotoFile(crime, getActivity());
        //CHALLENGE return a result from child to parent fragment
        returnResult();
    }

    //creates the view fragment of CRIMEFRAGMNET
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
                //updates the title of the crime with the most recent change using method updatecrime
                //from callback interface
                updateCrime();
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
                updateCrime();
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
        ViewTreeObserver viewTreeObserver = photoView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                photoViewWidth = photoView.getWidth();
                photoViewHeight = photoView.getHeight();

            }
        });
        photoView.setOnClickListener(this);

        //use runnable thread with hanlder to delay the loading of the image into the photoview
        //used for performance issue with slow loading of fragment while waiting for
        //view tree observer to load and pass the dimensions of the imageview we neeed
        //this way we delay by 80 milliseconds the loading of the suspect image into the imageview
        //the result is flawless loading of fragment and instant loading of image with no lag in the app
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updatePhotoView(photoViewWidth, photoViewHeight);
            }
        }, 80);

        //check if there is camera app on the device and therefore enable or disable camera button
        boolean canTakePhoto = photoFile != null && captureImage.resolveActivity(packageManager) != null;
        photoButton.setEnabled(canTakePhoto);

        //if there is camera app
        //get the uri of the photo file after image saved
        //put extra for the output of the photo file
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(photoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        return v;

    }

    //method to create the options menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);

    }

    //method to check wich item from the options menu was clicked to perform action
    //return true after each click to stop the listener for item cliecked
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
                //delete the current crime and close the activity
                CrimeLab.get(getActivity()).deleteCrime(crime);
                getActivity().finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //onclick method to check which button was clicked and performa actions
    @Override
    public void onClick(View view) {

        //date button clicked
        if (view.getId() == dateButton.getId()) {
            //check if tablet or phone is used depending on one of the layouts used in tablet mode
            if (getActivity().findViewById(R.id.detail_fragment_container) != null) {
                //if tablet use dialog to display date picker dialog
                manager = getFragmentManager();
                //init the dialog
                DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
                //requests information using parametar from previous fragment using extra date
                //set where the information should be returned from child fragment to parent fragment
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            } else {
                Intent in = DatePickerActivity.newIntent(getActivity(), crime.getDate());
                startActivityForResult(in, DIALOG_DATE1);
            }

        }
        //time picker button exactly the same as date picker above
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
            //start intent to open contantas and return result with extra REQUEST_CONTANCTS
            //in onActivity result get the extra and perform UI changes
            startActivityForResult(pickContact, REQUEST_CONTACT);

        }
        //callback button to call the suspect on the phone
        if (view.getId() == callButton.getId()) {
            Intent in = new Intent(Intent.ACTION_DIAL);
            in.setData(Uri.parse("tel:" + callButton.getText()));
            startActivity(in);
        }

        //photo button to take a photo  with the camera
        if (view.getId() == photoButton.getId()) {
            //used to bypass camera app to build intent
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            startActivityForResult(captureImage, REQUEST_PHOTO);

        }
        //photo view pressed opens a dialog to show the image in bigger view
        if (view.getId() == photoView.getId()) {
            manager = getFragmentManager();

         //   startWithTransition(getActivity(),PicturePopupDialog.newInstance(crimeId),  photoView);

            PicturePopupDialog popupDialog = PicturePopupDialog.newInstance(crimeId);
            popupDialog.show(manager, null);

        }
    }

    //get the result date from data/time picker dialogs, contact result for picked suspecct
    //and photo requested to update hte photoview
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //check if result ok if true continue
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        // check if request code eqaul Extra REQUEST FROM FRAGMENT OR REQUEST FROM ACTIVITY
        if (requestCode == REQUEST_DATE || requestCode == DIALOG_DATE1) {
            //get the date from the extra passed
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            //set crime's new date
            crime.setDate(date);
            //update the crime if in tablet mode
            updateCrime();
            //update date on listFragment
            updateDate();


            //request the time from the fragment dialog and return it in the button
        } else if (requestCode == REQUEST_TIME) {
            //same as above date request
            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            crime.getDate().setMinutes(time.getMinutes());
            crime.getDate().setHours(time.getHours());
            //time button
            updateTime();
            updateCrime();

            //request the name of the contact selected and return in on the button
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            //----------------------CODE SAVED FOR FUTURE REFFERENCE
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
            //----------------------CODE SAVED FOR FUTURE REFFERENCE


            //uri gettting the data passed by the intent picking the contact
            Uri contactData = data.getData();
            //cursor to get the informtaion from the query about the contanct
            Cursor cursor = getActivity().getContentResolver()
                    .query(contactData, null, null, null, null);

            //of cursror is not empty go to first element
            if (cursor.moveToFirst()) {
                //get the name of the contact and place it in the text of the suspect button
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                suspectButton.setText(name);
                String suspect = cursor.getString(0);
                crime.setSuspectName(suspect);

                //uses content resolver to get the data from contacts app first query
                ContentResolver cr = getActivity().getContentResolver();
                //create new cursor with the contnetresolver and query the contnte uri looging for
                //DISPLAY_NAME equal to our name of the person selected from contacts app
                Cursor content_uri = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                        "DISPLAY_NAME = '" + name + "'", null, null);

                // if the names match from the new cursror go to the first element
                if (content_uri.moveToFirst()) {
                    //get the contact id
                    String contactId =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    //then create another query as above one, to get the information contanct id from the cursor
                    Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                    //if there is contncat_id go to its first position element to get the number
                    if (phones.moveToFirst()) {
                        //here we get the number froum our query and set it to the crime call button and in Crime data
                        String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        callButton.setText(number);
                        crime.setSuspectNumber(number);
                        updateCrime();
                    }
                    //finaly close the cursors for security issues and LOW MEMORY issues
                    cursor.close();
                    content_uri.close();
                    phones.close();
                }
            }
            //if request code equal to photo
        } else if (requestCode == REQUEST_PHOTO) {
            //update the crime
            updateCrime();
            //update photo view with the image and dimensions of the photoview imageview
            updatePhotoView(photoViewWidth, photoViewHeight);
        }
    }

    //if activity paused, update the crime
    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(crime);
    }

    //get crime report method to send the infromation to a user using mail
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

    //called to instantiate a new fragment CrimeFragment with arguments for the crime id
    //using bundle and returning results to previous fragment
    public static CrimeFragment newInstance(UUID crimeID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeID);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //method to update dateButton text in format specified
    private void updateDate() {
        //date button
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
        Date date = crime.getDate();
        dateButton.setText(simpleDateFormat.format(date));

    }

    //method to update time button text
    private void updateTime() {
        //date button
        String hours = String.valueOf(crime.getDate().getHours());
        String minutes = String.valueOf(crime.getDate().getMinutes());
        timeButton.setText(hours + ":" + minutes);

    }

    //CHALLENGE return a result from child fragment to parent fragment
    //returns a result with crime title from child to parent
    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, new Intent().putExtra(ITEM_ID, crime.getTitle()));
    }

    //returns intent extra with key extra_answer_shown and value false
    public static String wasAnswerShown(Intent result) {
        return result.getStringExtra(ITEM_ID);
    }

    //update the photoview after getting right dimensions
    public void updatePhotoView(int widht, int height) {
        //check if photoview is not null and file exist
        if (photoView == null || !photoFile.exists()) {
            //if not set photoview not clickable
            photoView.setEnabled(false);
        } else {
            //if true create a new bitmap with the image taken from our camera
            Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), widht, height);
            //set the photo inside our imageview and rotate 90 degree while enableing the photo clicked status
            photoView.setImageBitmap(bitmap);
            photoView.setRotation(90);
            photoView.setEnabled(true);
        }
    }


    //------------------CALLBACKS INTERFACE
    //uses crimeUpdate method to update the list of crimes on tablets in real time
    public interface Callbacks {
        void onCrimeUpdate(Crime crme);
    }

    //used to atach fragment
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (Callbacks) activity;
    }

    //used to detach fragment
    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    //callback from interface to use interface method onCrmeUpdate(crime)
    //updates the list with the most recent change in a crime
    private void updateCrime() {
        //updates the database with the new eddited crime
        CrimeLab.get(getActivity()).updateCrime(crime);
        //updates the ListFragment view with the new information about the crime
        callbacks.onCrimeUpdate(crime);
    }


    //CHAPTER 33 METHOD TO START THE IMAGEVIEW DIALOG WITH TRANSITIN

    public static void startWithTransition(Activity activity, Intent intent, View sourceView) {
        ViewCompat.setTransitionName(sourceView, "image_view_test_proba");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, sourceView, "image_view_test_proba");

        activity.startActivity(intent, optionsCompat.toBundle());
    }

}
