package hn.techcom.com.hncitipages.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Objects;

import hn.techcom.com.hncitipages.Fragments.HomeFragment;
import hn.techcom.com.hncitipages.Fragments.SharePostBottomSheetFragment;
import hn.techcom.com.hncitipages.Fragments.SupportSectionFragment;
import hn.techcom.com.hncitipages.Fragments.VisitSectionFragment;
import hn.techcom.com.hncitipages.Models.Post;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.SupporterProfile;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.BottomSheetFragment;
import hn.techcom.com.hncitipages.Utils.Utils;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    static ArrayList<SupporterProfile> userSupportedProfiles;
    static ArrayList<Post> globalPosts = new ArrayList<>();
    static ArrayList<Post> userSupportedProfilePosts = new ArrayList<>();
    private static final int PERMISSION_REQUEST_CODE = 200;

    //currently its hard coded but later on it will taken from local db based on currently logged in user's username
    private String currentUserUsername = "redviper";

    private static final String TAG = "MainActivity";

    private Utils myUtils;

    private final int UPDATE_REQUEST_CODE = 1612;

    @Override
    protected void onStart() {
        super.onStart();

        myUtils = new Utils();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Profile localUser = myUtils.getNewUserFromSharedPreference(this);

        //Check if user is logged in and profile is locally stored
        if(user == null || localUser.getProfileImg() == null){
            startActivity(new Intent(this, SignInActivity.class));
        }
        else {
            if (checkPermission()) {
                Fragment fragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).commit();
            }else{
                requestPermission();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callInAppUpdate();

        //hooks
        BottomAppBar bottomAppBar = findViewById(R.id.bottomappbar_home);
        FloatingActionButton newPostFab = findViewById(R.id.fab_post);

        //set bottomAppBar
        setSupportActionBar(bottomAppBar);

        newPostFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharePostBottomSheetFragment shareSheetFragment = new SharePostBottomSheetFragment();
                shareSheetFragment.show(getSupportFragmentManager(), shareSheetFragment.getTag());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        callInAppUpdate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottomappbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment fragmentSelected = null;
        switch(item.getItemId()){
            case R.id.navigation_supportedsection:
                fragmentSelected = new SupportSectionFragment();
                break;
            case R.id.navigation_home:
                fragmentSelected = new HomeFragment();
                break;
            case R.id.navigation_visitsection:
                fragmentSelected = new VisitSectionFragment();
                break;
            case android.R.id.home:
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                break;
        }
        // Begin the transaction
        if(fragmentSelected != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragmentSelected)).commit();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean recordAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted && recordAccepted){
                        Fragment fragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).commit();
                    }
                    else
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                            showMessageOKCancel(
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA, RECORD_AUDIO},
                                                    PERMISSION_REQUEST_CODE);
                                        }
                                    });
                            return;
                        }
                    }
                }else
                    finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (requestCode == UPDATE_REQUEST_CODE) {
                Toast.makeText(this, "Downloading start", Toast.LENGTH_SHORT).show();
                if (resultCode != RESULT_OK) {
                    Log.d(TAG,"onActivityResult: Update flow failed! Result code: " + resultCode);
                    // If the update is cancelled or fails,
                    // you can request to start the update again.
                }
            }
        }
    }

    //Custom methods
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this, CAMERA);
        int result2 = ContextCompat.checkSelfPermission(this, RECORD_AUDIO);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, CAMERA, RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("You need to allow access to all of these permission in order to continue with this app.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        onDestroy();
                    }
                })
                .create()
                .show();
    }

    private void callInAppUpdate(){
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.

                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,
                            MainActivity.this,
                            UPDATE_REQUEST_CODE);
                }
                catch (IntentSender.SendIntentException exception){
                    Log.d(TAG,"callInAppUpdate: "+exception.getMessage());
                }
            }
        });
    }
}