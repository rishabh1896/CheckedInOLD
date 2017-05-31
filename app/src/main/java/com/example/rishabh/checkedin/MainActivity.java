package com.example.rishabh.checkedin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Location Error";
    private static final int REQUEST_CHECK_SETTINGS = 0;
    final int a = 0;
    private GPSTracker gps;
    static double lat = 0.0;
    static double lon = 0.0;
    private RecyclerView recyclerView;
    public static String ss;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    String addr;
    public static String pname;
    private VivzAdapter adapter;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    private FirebaseAuth.AuthStateListener mAuthListener;
    static boolean called = false;
    LocationSettingsRequest.Builder builder;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (!called || !login.call) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            called = true;
            login.call = true;
        }
        super.onCreate(savedInstanceState);
        initialise();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, a);
            return;
        }
        callToapis();
        checkForLogin();
        setContentView(R.layout.activity_main);
        mProgress = new ProgressDialog(this);

        if (mAuth.getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference().child("Profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ch : dataSnapshot.getChildren()) {
                        if (ch.getValue().equals(mAuth.getCurrentUser().getUid())) {
                            ss = ch.getKey();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        review();
    }

    private void initialise() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(MainActivity.this, login.class);

                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };
    }

    private void callToapis() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        LocationRequest mLocationRequestHighAccuracy = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);
        LocationRequest mLocationRequestBalancedPowerAccuracy = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);
        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestHighAccuracy)
                .addLocationRequest(mLocationRequestBalancedPowerAccuracy);
        requestLocation();
    }

    private void checkForLogin() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            System.out.println("rishabh2222");
            Intent loginIntent = new Intent(MainActivity.this, login.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
        }
    }

    public void geo() {

        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
//            String address = addresses.get(0).getAddressLine(0);
            addr = addresses.get(0).getSubAdminArea();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(22)
    private void requestLocation() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {


            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        getCoordinates();
                        System.out.println(lat + lon);
                        geo();
                        // requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        break;
                }
            }
        });
    }


    private void review() {
        System.out.println("i am called");
        recyclerView = (RecyclerView) findViewById(R.id.drawerList);
        adapter = new VivzAdapter(this, getData());
        recyclerView.setAdapter(adapter);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public List<Information> getData() {
        if (mAuth.getCurrentUser() != null) {
            DatabaseReference ddm = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("FriendsPlace");
            final List<Information> data = new ArrayList<>();

            ddm.limitToLast(5).addValueEventListener(new ValueEventListener() {
                int b = -1;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    data.clear();
                    b++;
                    if (b % 4 == 0) {

                        for (DataSnapshot ch : dataSnapshot.getChildren()) {
                            //System.out.println(ch.getPriority().toString());
                            for (DataSnapshot t1 : ch.getChildren()) {
                                for (DataSnapshot t2 : t1.getChildren()) {
                                    final Information current = new Information();
                                    current.activeUser = t2.getKey();
                                    current.profilePic = "";
                                    //  System.out.println("Setting uri"+current.profilePic);
                                    pname = t2.getKey();
                                    for (DataSnapshot t3 : t2.getChildren()) {

                                        if (t3.getKey().equals("Address")) {
                                            current.Title = t3.getValue().toString();

                                        }
                                        if (t3.getKey().equals("Place")) {
                                            current.address = t3.getValue().toString();

                                        }
                                        if (t3.getKey().equals("status")) {
                                            current.status = t3.getValue().toString();

                                        }
                                        if (t3.getKey().equals("ID")) {
                                            current.ID = t3.getValue().toString();
                                        }
                                        //current.profilePic=(ImageView)findViewById(R.mipmap.ic_account_circle_black_24dp);
                                        // current.iconTitle=icons[i++];

                                    }
                                    data.add(0, current);
                                    System.out.println(data.get(0).address);
                                  adapter.notifyDataSetChanged();
                                }

                            }


                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return data;
        }

        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("rishabh1123");
        mAuth.addAuthStateListener(mAuthListener);
      //  geo();
        checkUserExist();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // checkUserExist();
    }

    private void checkUserExist() {

        if ((!(FirebaseAuth.getInstance().getCurrentUser() == null)) && ((mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Details").child("name")).toString() != null)) {
            final String user_id = mAuth.getCurrentUser().getUid();
            System.out.println(mDatabase.getKey() + " " + user_id + mAuth.getCurrentUser().getDisplayName());
            FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                        //System.out.println("rishabh");
                        Intent loginIntent = new Intent(MainActivity.this, Setup.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {

            Intent loginIntent = new Intent(MainActivity.this, login.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
        }
    }

    public void getCoordinates()

    {

        gps = new GPSTracker(MainActivity.this);
        if (gps.canGetLocation()) {
            lat = gps.getLatitude();
            lon = gps.getLongitude();
        } else {
            gps.showSettingAlert();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent loginIntent = new Intent(MainActivity.this, Post.class);
            loginIntent.putExtra("addr", addr);
            final int result = 1;
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(loginIntent, result);
        }
        if (item.getItemId() == R.id.AddFriends) {
            Intent loginIntent = new Intent(MainActivity.this, SearchFriend.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
        }
        if (item.getItemId() == R.id.signMeOut) {
            mAuth.signOut();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                }
                break;


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)

    {
        switch (requestCode) {
            case a:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCoordinates();
                }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
