package com.easy.personal.diary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class DatabaseConnector {

    private static final String DATABASE_NAME = "UserDiary.db";
    private SQLiteDatabase database; // objet database
    private DatabaseOpenHelper databaseOpenHelper; // database helper

    // constructeur pour le DatabaseConnector
    public DatabaseConnector(Context context) {
        // creation d'un nouveau DatabaseOpenHelper
        databaseOpenHelper =
                new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    // ouverture de la database connection
    public void open() throws SQLException
    {
        // créer ou ouvrir une base de données en lecture/écriture
        database = databaseOpenHelper.getWritableDatabase();
    }

    // fermeture de la database connection
    public void close()
    {
        if (database != null)
            database.close(); // fermeture de la database connection
    }

    // inserer un nouveau contact dans la database
    public void insertNote(String subject, String note, Bitmap image, String date)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] img = null;
        if (image != null) {
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            img = baos.toByteArray();
        }
        ContentValues newNote = new ContentValues();
        newNote.put("note", note);
        newNote.put("subject", subject);
        newNote.put("image", img);
        newNote.put("created_at",date);
        open();
        database.insert("notes", null, newNote);
        close();
    }

    // inserer un nouveau contact dans la base de données
    public void updateNote(long id, String subject, String note, Bitmap image)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] img = null;
        if (image != null) {
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            img = baos.toByteArray();
        }
        ContentValues editNote = new ContentValues();
        editNote.put("subject", subject);
        editNote.put("note", note);
        editNote.put("image", img);

        open();
        database.update("notes", editNote, "_id=" + id, null);
        close();
    }

    // retourne un curseur avec toutes les informations de contact dans la base de données
    public Cursor getAllNotes()
    {
        return database.query("notes", new String[] {"_id", "subject", "note","created_at"},
                null, null, null, null, "subject");
    }

    // obtenir un curseur contenant toutes les informations sur le contact spécifié
    // par l'identifiant donné

    public Cursor getOneContact(long id)
    {
        return database.query(
                "notes", null, "_id=" + id, null, null, null, null);
    }

    // supprimer le contact spécifié
    public void deleteContact(long id)
    {
        open();

        database.delete("notes", "_id=" + id, null);
        close();
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper
    {
        // public constructeur
        public DatabaseOpenHelper(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        }

        // crée la table des contacts
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            // requête pour créer une nouvelle table
            String createQuery = "CREATE TABLE notes" +
                    "(_id integer primary key autoincrement," +
                    "subject TEXT,  note TEXT ," + " image BLOB,"+"created_at date );";
			/* String imageQuery = "CREATE TABLE image" + "(imageid integer primary key autoincrement," +
            "_id integer," + "image TEXT," + "FORIEGN KEY(_id) REFERNCES(notes));";*/

            db.execSQL(createQuery);
            //db.execSQL(imageQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
        }
    }
}
