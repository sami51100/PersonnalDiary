package com.easy.personal.diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class AddNotes extends Fragment {

    String  path_imag;

    private SimpleDateFormat dateFormat3 = new SimpleDateFormat("ddMMyy-hhmmss");

    private long rowID;
    private static final int CAMERA_REQUEST = 1888;
    AlertDialog alertDialog;
    ImageView image1;
    private static int RESULT_LOAD_IMAGE = 1;

    private static final String LEADBOLT_INTERSTITIAL_ID	= "117126176";
    // EditTexts pour les informations de contact
    private EditText subjectEditText;
    private EditText noteEditText;
    private TextView datetimetxt;
    Uri imageUri;
    Bitmap selectedBitmap;
    String currentDateString,currentTimeString,currentDayString;

    private Activity act = getActivity();



    public  AddNotes() {
        // Constructeur public vide obligatoire
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_one, container, false);
        subjectEditText = (EditText) view.findViewById(R.id.subjectEditText);
        noteEditText = (EditText) view.findViewById(R.id.noteEditText);
        image1 = (ImageView) view.findViewById(R.id.addimageView);
        datetimetxt = (TextView)view.findViewById(R.id.dateTime);
        Date now = new Date();



        Format formatter;
        formatter = new SimpleDateFormat("EEE", Locale.ENGLISH);

        currentDayString = formatter.format(now);

        formatter = new SimpleDateFormat("dd-MMM",Locale.ENGLISH);

        currentDateString = formatter.format(now);

        formatter = new SimpleDateFormat("hh:mm",Locale.ENGLISH);
        currentTimeString = formatter.format(now);
        datetimetxt.setText("  "+currentDayString + "\n\n"+ currentDateString + "\n\n" +currentTimeString);

        Bundle extras = getActivity().getIntent().getExtras();

        if (extras != null)
        {
            rowID = extras.getLong(listActivity.ROW_ID);
            subjectEditText.setText(extras.getString(getString(R.string.subject)));
            noteEditText.setText(extras.getString(getString(R.string.note)));
            noteEditText.toString().substring(0, 10);
            byte[] img = extras.getByteArray("image");
            if (img != null) {
                selectedBitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                image1.setImageBitmap(selectedBitmap);
            }
            datetimetxt.setText(extras.getString("created_at"));
        }


        Button saveButton =	(Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(saveButtonClicked);

        Button btnTakePhoto = (Button)view.findViewById(R.id.takePhoto);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Take_Image();
            }
        });


        return view;
    }



    public void Take_Image()
    {

        alertDialog = new AlertDialog.Builder(getActivity()).create();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Photo");
       // builder.setIcon(R.drawable.icon);
        builder.setMessage("Select an Option:");
        builder.setPositiveButton("Take new photo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + dateFormat3.format(new Date()) + ".jpg";
                File imageFile = new File(imageFilePath);
                imageUri = Uri.fromFile(imageFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });

        builder.setNegativeButton("Select From Gallery",new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  Toast.makeText(getActivity(),requestCode+""+CAMERA_REQUEST,4000).show();
        if (requestCode == 1888 && resultCode == -1) {
                selectedBitmap = getImage(imageUri);
            image1.setImageBitmap(selectedBitmap);
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == -1 && null != data) {
            imageUri = data.getData();
            selectedBitmap = getImage(imageUri);
           // image1.setImageBitmap(selectedBitmap);
            image1.setImageURI(imageUri);
          //  path_imag=getRealPathFromURI()

        }
    }



    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = act.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
           path_imag=cursor.getString(column_index);
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public Bitmap getImage(Uri imageUri) {
        Bitmap bmp = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inPurgeable = true;
            options.inScaled = true;
            bmp = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri), null, options);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    // répond à l'événement généré lorsque l'utilisateur clique sur le bouton "Terminé".
    View.OnClickListener saveButtonClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {


            if ((subjectEditText.getText().length() != 0)&&(noteEditText.getText().length() != 0))
            {
                AsyncTask<Object, Object, Object> saveContactTask =
                        new AsyncTask<Object, Object, Object>()
                        {
                            @Override
                            protected Object doInBackground(Object... params)
                            {
                                saveContact(); // enregistrer le contact dans la database
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object result) {

                                noteEditText.setText("");
                                subjectEditText.setText("");

                                Intent intent = getActivity().getIntent();
                                getActivity().finish();
                                startActivity(intent);}
                              //  mInterstitialAd.show();
                               // finish();

                        };


                saveContactTask.execute((Object[]) null);


            }
            else
            {
                // creer un nouveau AlertDialog Builder
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());


                builder.setTitle("NOTES UNCOMPLETED");
                builder.setMessage("Please complete the Subject and Notes");
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        }
    };

    // enregistre les informations du contact dans la database
    private void saveContact()
    {

        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

        if (getActivity().getIntent().getExtras() == null)
        {
            // thumbnail = ((BitmapDrawable)image.getDrawable()).getBitmap();

            databaseConnector.insertNote(
                    subjectEditText.getText().toString(),
                    noteEditText.getText().toString(),
                    selectedBitmap,
                    datetimetxt.getText().toString());
         //   Toast.makeText(getActivity(),"Notes Saved",Toast.LENGTH_SHORT).show();


        }
        else
        {
            databaseConnector.updateNote(rowID,
                    subjectEditText.getText().toString(),
                    noteEditText.getText().toString(),
                    selectedBitmap);

        }

    }







}
