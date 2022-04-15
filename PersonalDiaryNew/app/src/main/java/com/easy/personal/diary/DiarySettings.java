package com.easy.personal.diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class DiarySettings extends Activity {

    TextView text;
    String password;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Password = "Passwrd";
    public String check;


    private Activity act = this;

    SharedPreferences sharedpreferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        text = (TextView) findViewById(R.id.textView1);
        TextView text_remove = (TextView) findViewById(R.id.textView2);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        text.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                set_password();
            }
        });


        text_remove.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                Remove_pass();
            }
        });


    }


    public void set_password() {
        // Création d'un alertdialog avec un bouton
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DiarySettings.this);


        alertDialog.setTitle("SET PASSWORD");



        final EditText input = new EditText(DiarySettings.this);
        final EditText input1 = new EditText(DiarySettings.this);

        input.setSingleLine(true);
        input1.setSingleLine(true);


        input.setHint("Enter Password");
        input1.setHint("Confirm Password");

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(input);
        ll.addView(input1);


        alertDialog.setView(ll);

        alertDialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        password = input.getText().toString();
                        String password1 = input1.getText().toString();


                        if (password.equals(password1)) {
                            SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("Passwrd", password);
                            editor.putString("check", "truestring");
                            editor.putBoolean("pass_check", true);
                            editor.commit();

                            Toast.makeText(getApplicationContext(), "Password Changed!!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong Password!", Toast.LENGTH_LONG).show();
                        }
                    }


                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "No Changes Made!!", Toast.LENGTH_LONG).show();
                    }
                });



        // Affichage du message d'alerte
        alertDialog.show();

    }


    public void Remove_pass() {

        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        final SharedPreferences.Editor editor = settings.edit();
        final String existing_pass = settings.getString("Passwrd", "do_no");



        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DiarySettings.this);


        alertDialog.setTitle("REMOVE PASSWORD");



        final EditText input = new EditText(DiarySettings.this);

        input.setSingleLine(true);

        input.setHint("Enter The Password");
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(input);


        alertDialog.setView(ll);

        alertDialog.setPositiveButton("REMOVE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        password = input.getText().toString();


                        if (password.equals(existing_pass)) {


                            editor.putBoolean("pass_check", false);
                            editor.commit();
                            Toast.makeText(DiarySettings.this, "Password Removed", Toast.LENGTH_SHORT).show();
                            //  Toast.makeText(getApplicationContext(), "Password Changed!!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to Remove! Wrong Password Given", Toast.LENGTH_LONG).show();
                        }
                    }


                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "No Changes Made!!", Toast.LENGTH_LONG).show();
                    }
                });




        alertDialog.show();

    }


}



