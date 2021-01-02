package id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.R;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.receiver.ConnectivityReceiver;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.service.RecordingService;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.ui.main.MainFragment;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.view.fragment.FileBrowserFragment;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.view.fragment.RecordFragment;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.viewmodel.RecordingViewModel;
import util.PermissionUtil;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_DANGEROUS_PERMISSIONS = 0;
    private final boolean marshmallowOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

    private RecordingViewModel recordingViewModel;

    private ConnectivityReceiver connectivityReceiver;

    private boolean firstCallback = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectivityReceiver = new ConnectivityReceiver();
        registerConnectivityBroadcast();

        checkUserPermission();

        recordingViewModel = ViewModelProviders.of(this).get(RecordingViewModel.class);
        recordingViewModel.init(getApplication());

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new AppFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class AppFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[] { "Record", "Saved Recordings"};
        private Context context;

        public AppFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:{
                    return RecordFragment.newInstance(position);
                }
                case 1:{
                    return FileBrowserFragment.newInstance(position, recordingViewModel);
                }
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

    private void registerConnectivityBroadcast() {
        if (marshmallowOrLater) {
            registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    private void checkUserPermission() {
        if (!marshmallowOrLater) {
            startStopRecording();
            return;
        }

        String[] permissions = {
                (Manifest.permission.RECORD_AUDIO),
                (Manifest.permission.READ_EXTERNAL_STORAGE),
                (Manifest.permission.WRITE_EXTERNAL_STORAGE),
                (Manifest.permission.ACCESS_FINE_LOCATION),
                (Manifest.permission.ACCESS_COARSE_LOCATION)
        };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permissions")
                    .setMessage("Jokecorder needs some permissions in order to work properly.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    permissions, REQUEST_DANGEROUS_PERMISSIONS);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
            return;
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        permissions, REQUEST_DANGEROUS_PERMISSIONS);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_DANGEROUS_PERMISSIONS) {
            if (grantResults.length >= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_LONG).show();
            } else startStopRecording();
        }
    }

    private void startStopRecording() {
        firstCallback = false;
        if (!recordingViewModel.serviceRecording.get()) { // start recording
            recordingViewModel.startRecording();
        } else { //stop recording
            recordingViewModel.stopRecording();
        }
    }

    // Connection with local Service through the view model.
    @Override
    protected void onStart() {
        super.onStart();

        recordingViewModel.connectService(RecordingService.makeIntent(this, true));
    }

    // Disconnection from local Service.
    @Override
    protected void onStop() {
        super.onStop();

        recordingViewModel.disconnectAndStopService(new Intent(this, RecordingService.class));
    }

//    protected void unregisterConnectivityBroadcast() {
//        try {
//            unregisterReceiver(connectivityReceiver);
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        }
//    }
}