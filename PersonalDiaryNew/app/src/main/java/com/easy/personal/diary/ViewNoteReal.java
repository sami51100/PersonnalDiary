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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


public class ViewNoteReal extends Activity {
    protected static final int PICK_FROM_FILE = 0;
    private long rowID;
    private TextView subjectTextView;
    private TextView noteTextView;
    int i = 0;
    ImageView image1;
    AlertDialog alertDialog;

    private static final int CAMERA_REQUEST = 1888;
    private static int RESULT_LOAD_IMAGE = 1;
    Uri imageUri;
    Bitmap selectedBitmap;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_contact);



        subjectTextView = (TextView) findViewById(R.id.subjectTextView);
        noteTextView = (TextView) findViewById(R.id.noteTextView);
        image1 = (ImageView) findViewById(R.id.imageView1);


        Bundle extras = getIntent().getExtras();
        rowID = extras.getLong(listActivity.ROW_ID);

        loadData();


    }


	/*@Override
    public void onBackPressed() {
	startAppAd.onBackPressed();
	super.onBackPressed();
	}
	@Override
	public void onPause() {
	super.onPause();
	startAppAd.onPause();
	}



	@Override
	protected void onResume()
	{
		super.onResume();
		loadData();
	}
*/

//
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        super.onCreateOptionsMenu(menu);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.view_contact_menu, menu);
//        return true;
//    }


    //  @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        switch (item.getItemId())
//        {
//            case R.id.editItem:
//
//                Intent addEditContact =	new Intent(this, AddEditNote.class);
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                byte[] img = null;
//                if (selectedBitmap != null) {
//                    selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    img = baos.toByteArray();
//                }
//
//                addEditContact.putExtra(Diary.ROW_ID, rowID);
//                addEditContact.putExtra("subject", subjectTextView.getText());
//                addEditContact.putExtra("note", noteTextView.getText());
//                addEditContact.putExtra("image", img);
//                startActivity(addEditContact);
//                return true;
//            case R.id.deleteItem:
//                deleteContact();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            selectedBitmap = getImage(imageUri);
            image1.setImageBitmap(selectedBitmap);
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            imageUri = data.getData();
            selectedBitmap = getImage(imageUri);
            image1.setImageBitmap(selectedBitmap);
        }
    }

    public Bitmap getImage(Uri imageUri) {
        Bitmap bmp = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inPurgeable = true;
            options.inScaled = true;
            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    private void loadData() {
        DatabaseConnector databaseConnector = new DatabaseConnector(ViewNoteReal.this);
        databaseConnector.open();
        Cursor result = databaseConnector.getOneContact(rowID);
        result.moveToFirst(); // passer au premiere element


        int nameIndex = result.getColumnIndex("subject");
        int phoneIndex = result.getColumnIndex("note");
        int imageIndex = result.getColumnIndex("image");

        // remplir les TextViews avec les données récupérées
        subjectTextView.setText(result.getString(nameIndex));
        noteTextView.setText(result.getString(phoneIndex));
        byte[] img = result.getBlob(imageIndex);
        if (img != null) {
            selectedBitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            if (selectedBitmap != null) {
                image1.setImageBitmap(selectedBitmap);
            }
        }
        result.close();
        databaseConnector.close();
    }


    private void deleteContact() {
        // creation d'un nouveau AlertDialog Builder
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ViewNoteReal.this);

        builder.setTitle("delete");
        builder.setMessage("confirm delete");


        builder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        final DatabaseConnector databaseConnector =
                                new DatabaseConnector(ViewNoteReal.this);


                        AsyncTask<Long, Object, Object> deleteTask =
                                new AsyncTask<Long, Object, Object>() {
                                    @Override
                                    protected Object doInBackground(Long... params) {
                                        databaseConnector.deleteContact(params[0]);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Object result) {
                                        finish();
                                    }
                                };


                        deleteTask.execute(new Long[]{rowID});
                    } // end method onClick
                }
        );

        builder.setNegativeButton("no", null);
        builder.show();
    }




 }
