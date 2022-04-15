package com.easy.personal.diary;

import android.app.ListActivity;
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


public class listActivity extends ListActivity {


        public static final String ROW_ID = "row_id";
        private ListView contactListView;
        private CursorAdapter contactAdapter;
        TextView nte;
        @SuppressWarnings("deprecation")
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            contactListView = getListView(); // obtenir le ListView intégré
            contactListView.setOnItemClickListener(viewContactListener);
           // contactListView.setBackgroundResource(R.drawable.bg);
//            contactListView.setCacheColorHint(android.R.color.transparent);
            nte= (TextView)findViewById(R.id.nteTextView);
            String[] from = new String[] { "subject","note","created_at"};
            int[] to = new int[] { R.id.subjTextView,R.id.nteTextView,R.id.timeTextView };
            contactAdapter = new SimpleCursorAdapter(
                    listActivity.this, R.layout.contact_list_item,null, from, to);
            setListAdapter(contactAdapter);

        }
        @Override
        protected void onResume()
        {
            super.onResume();


            new GetContactsTask().execute((Object[]) null);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onStop()
        {
            Cursor cursor = contactAdapter.getCursor();

            if (cursor != null)
                cursor.deactivate();

            contactAdapter.changeCursor(null);
            super.onStop();
        }



        // effectue une requête de base de données
        private class GetContactsTask extends AsyncTask<Object, Object, Cursor>
        {
            DatabaseConnector databaseConnector =
                    new DatabaseConnector(listActivity.this);

            // effectuer l'accès à la base de données
            @Override
            protected Cursor doInBackground(Object... params)
            {
                databaseConnector.open();


                return databaseConnector.getAllNotes();
            }

            // utiliser le curseur renvoyé par la méthode doInBackground.
            @Override
            protected void onPostExecute(Cursor result)
            {
                contactAdapter.changeCursor(result);
                databaseConnector.close();
            }
        }
        AdapterView.OnItemClickListener viewContactListener = new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3)
            {
                // créer un Intent pour lancer l'activité ViewNote
                Intent viewContact =
                        new Intent(listActivity.this, ViewNote.class);


                viewContact.putExtra(ROW_ID, arg3);
                startActivity(viewContact);
            }
        };
    }

