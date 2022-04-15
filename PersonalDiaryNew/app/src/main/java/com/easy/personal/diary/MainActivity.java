package com.easy.personal.diary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;




public class MainActivity extends AppCompatActivity {


    Context context;

    static String rec = "";
    String recovery_email, password;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        context = this;

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.icon);


        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(-1);


        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        recovery_email = settings.getString("email", "personal diary");
        password = settings.getString("Passwrd", "no pass");
        rec = recovery_email;
        boolean pass = settings.getBoolean("pass_check", false);
        if (pass) {

            a();
        }

    }




    public void a() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);

        builder.setMessage(R.string.enter_pass);
        final EditText input = new EditText(MainActivity.this);

        input.setHint(R.string.enter_pass);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(input);
        // ll.addView(input1);
        builder.setView(ll);
        builder.setPositiveButton(R.string.submit,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.setNeutralButton(R.string.close_app, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;


                final SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
                final String pass1 = settings.getString("Passwrd", "n/a");

                String pass = input.getText().toString();
                if (pass1.equals(pass)) {


                    Toast.makeText(getApplicationContext(), R.string.toast_here_go, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.wrong_pass, Toast.LENGTH_LONG).show();


                }



            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {

            Intent i = new Intent(MainActivity.this, DiarySettings.class);
            startActivity(i);


            return true;
        }




        return super.onOptionsItemSelected(item);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AddNotes(), getString(R.string.add_notes));
        adapter.addFragment(new ViewNote(), getString(R.string.view_notes));

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    Boolean isBack = false;


    public void onBackPressed() {
        if (isBack) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {

            Toast.makeText(this, "  Press back again to exit  ", Toast.LENGTH_LONG).show();

            isBack = true;


            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isBack = false;

                }
            });
            t.start();
        }
    }


}




