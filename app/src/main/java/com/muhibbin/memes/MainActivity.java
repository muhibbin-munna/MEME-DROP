package com.muhibbin.memes;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.muhibbin.memes.broadcastReceiver.AlarmReceiver;
import com.muhibbin.memes.model.AddActivity;
import com.muhibbin.memes.model.GamificationActivity;
import com.muhibbin.memes.photoEditorMainClasses.PhotoEditorActivity;
import com.muhibbin.memes.ui.main.SectionsPagerAdapter;

import java.util.Calendar;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;
import static com.google.android.play.core.install.model.InstallStatus.DOWNLOADED;
import static com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    boolean doubleBackToExitPressedOnce = false;
    Toolbar toolbar;
    FloatingActionButton upload, create;
    public FloatingActionsMenu floatingActionsMenu;
    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    TabLayout tabs;
    public String categoryName = "All";

    DrawerLayout drawerLayout;
    public NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFE23B"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Meme Drop");
        viewPager = findViewById(R.id.view_pager);
        tabs = findViewById(R.id.tabs);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 1){
                    floatingActionsMenu.setVisibility(View.GONE);
                    navigationView.setCheckedItem(R.id.newFragment);
                }
                else if(tab.getPosition() == 0){
                    floatingActionsMenu.setVisibility(View.VISIBLE);
                    navigationView.setCheckedItem(R.id.newFragment);
                }else if(tab.getPosition() == 2){
                    floatingActionsMenu.setVisibility(View.VISIBLE);
                    navigationView.setCheckedItem(R.id.trendingFragment);
                }else if(tab.getPosition() == 3){
                    floatingActionsMenu.setVisibility(View.VISIBLE);
                    navigationView.setCheckedItem(R.id.leadersFragment);
                }else if(tab.getPosition() == 4){
                    floatingActionsMenu.setVisibility(View.VISIBLE);
                    navigationView.setCheckedItem(R.id.favoritesFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        drawerLayout = findViewById(R.id.drawerLayoutId);
        navigationView = findViewById(R.id.nav_view);
        floatingActionsMenu= findViewById(R.id.customfab);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new
                ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.newFragment);

        FirebaseMessaging.getInstance().subscribeToTopic("all");
        //Daily Notification Set
        regesterAlarm();

        sectionsPagerAdapter = new SectionsPagerAdapter(MainActivity.this, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);

        create = findViewById(R.id.createFabButton);
        upload = findViewById(R.id.uploadFabButton);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                    builder.setTitle("Choose an action");
                    String[] choose = {"Choose from template", "Upload from file"};
                    builder.setItems(choose, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent intent = new Intent(MainActivity.this, TemplateActivity.class);
                                    startActivity(intent);
                                    break;
                                case 1:
                                    openFileChooser();
                                    break;
                            }
                        }

                        private void openFileChooser() {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, PICK_IMAGE_REQUEST);
                        }

                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 111);
                    }
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    startActivity(intent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 111);
                    }
                }

            }
        });
    }




    private void regesterAlarm() {
        // Set the alarm to start at approximately 7:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 19);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private boolean checkPermission() {
        int write = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.filter:
                filterCategory();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void filterCategory() {
        Query categoryDatabaseRef;
        categoryDatabaseRef = FirebaseDatabase.getInstance().getReference("category").orderByValue();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
        builderSingle.setTitle("Filter By Category");

        final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.category_layout);
        categoryDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryAdapter.clear();
                categoryAdapter.add("All");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String areaName = postSnapshot.getValue(String.class);
                    categoryAdapter.add(areaName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(categoryAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categoryName = categoryAdapter.getItem(which);
//                sectionsPagerAdapter.setmCategoryName(categoryName);
                sectionsPagerAdapter.notifyDataSetChanged();
            }
        });
        builderSingle.show();
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "press again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            String ext = getFileExtension(data.getData());
            if (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("png")) {
                mImageUri = data.getData();
                Intent intent = new Intent(MainActivity.this, PhotoEditorActivity.class);
                intent.putExtra("mImageUri", mImageUri.toString());
                intent.putExtra("option", 2);
                startActivity(intent);
            } else {
                Toast.makeText(this, "enter a jpg/jpeg/png", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_gamification:
                Intent gamificationIntent = new Intent(MainActivity.this, GamificationActivity.class);
                startActivity(gamificationIntent);
                break;
            case R.id.newFragment:
                tabs.getTabAt(0).select();
                break;
            case R.id.trendingFragment:
               tabs.getTabAt(2).select();
                break;
            case R.id.leadersFragment:
                tabs.getTabAt(3).select();
                break;
            case R.id.favoritesFragment:
                tabs.getTabAt(4).select();
                break;
            case R.id.nav_share:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}