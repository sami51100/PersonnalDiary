package com.easy.personal.diary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;

import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.ListFragment;


public class ViewNote extends ListFragment {


    public static final String ROW_ID = "row_id";
    private ListView contactListView;
    private CursorAdapter contactAdapter;
    TextView nte;
    Context context;
    private long rowID;


    public ViewNote() {

        // Constructeur public vide obligatoire
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        contactListView = getListView();
        contactListView.setOnItemClickListener(viewContactListener);

        nte = (TextView) view.findViewById(R.id.nteTextView);
        String[] from = new String[]{"subject", "note", "created_at"};
        int[] to = new int[]{R.id.subjTextView, R.id.nteTextView, R.id.timeTextView};
        contactAdapter = new SimpleCursorAdapter(
                getActivity(), R.layout.contact_list_item, null, from, to);
        setListAdapter(contactAdapter);
        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                deleteContact(id);

                return false;
            }
        });




    }


    @Override
    public void onResume() {
        super.onResume();

        // creer new GetContactsTask
        new GetContactsTask().execute((Object[]) null);
    }

    public void onStop()
    {
        Cursor cursor = contactAdapter.getCursor();

        if (cursor != null)
            cursor.deactivate();

        contactAdapter.changeCursor(null);
        super.onStop();
    }


    private class GetContactsTask extends AsyncTask<Object, Object, Cursor> {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(getActivity());

        // effectuer l'accès à la base de données
        @Override
        protected Cursor doInBackground(Object... params) {
            databaseConnector.open();


            return databaseConnector.getAllNotes();
        }


        @Override
        protected void onPostExecute(Cursor result) {
            contactAdapter.changeCursor(result);
            databaseConnector.close();
        }
    }

    AdapterView.OnItemClickListener viewContactListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            // creation d'un Intent pour lancer le ViewContact Activity
            Intent viewContact =
                    new Intent(getActivity(), ViewNoteReal.class);


            viewContact.putExtra(ROW_ID, arg3);
            startActivity(viewContact);
        } // end method onItemClick
    };





    private void deleteContact(final long position) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        builder.setTitle("Delete");
        builder.setMessage("Confirm delete");


        builder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        final DatabaseConnector databaseConnector =
                                new DatabaseConnector(getActivity());

                        databaseConnector.deleteContact(position);
                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent);

                        Toast.makeText(getActivity(),"Deleted",Toast.LENGTH_SHORT).show();

                    }
                }
        );

        builder.setNegativeButton("NO", null);
        builder.show();
    }


}





