package com.example.rishabh.checkedin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Post extends AppCompatActivity {
    private TextView username, placeName, address;
    EditText status;
    ImageView displayPic;
   // ImageView img;
    String addr;
    Button post, checkIn;
    DatabaseReference mDatabase;
    String ss,s;
    ProgressDialog mProgress;
    Place place;
    int c=0;
    private static final int Place_picker_Request = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post);

        mProgress = new ProgressDialog(this);
        final ImageView img= (ImageView) findViewById(R.id.displayPic);
        username = (TextView) findViewById(R.id.usr1);
        username.setText(MainActivity.pname);
        StorageReference rf= FirebaseStorage.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ProfilePic");
        rf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri).fit().centerCrop()
                        .error(R.mipmap.ic_account_circle_black_24dp)
                        .placeholder(R.mipmap.ic_account_circle_black_24dp)
                        .transform(new CircleTransform())
                        //.resize(90,60)
                        .into(img);

            }
        });
        placeName = (TextView) findViewById(R.id.placeName);
        address = (TextView) findViewById(R.id.address);
        status = (EditText) findViewById(R.id.status);
        post = (Button) findViewById(R.id.Post);
        //img.setImageResource(R.mipmap.ic_account_circle_black_24dp);
        checkIn = (Button) findViewById(R.id.checkPlace);
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ch : dataSnapshot.getChildren()) {
                    if (ch.getKey().equals("name")) {
                        ss = ch.getValue().toString();
                        System.out.println("heyy "+ss);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        checkIn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           pickup();
                                       }
                                   }
        );
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (c==0) {
                    Toast.makeText(Post.this, "Please Pick a Place First..!!", Toast.LENGTH_LONG).show();
                } else {
                    addValues();
                    finish();
                }
            }
        });
    }

    private void pickup() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this),Place_picker_Request);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Place_picker_Request:
                if (resultCode == RESULT_OK) {
                    Intent sender=getIntent();
                    addr=sender.getExtras().getString("addr");
                    place = PlacePicker.getPlace(this, data);
                    c=1;
                    placeName.setText(place.getName().toString());
                    address.setText(place.getAddress().toString());
                    System.out.println("Hello");
                }
        }
        }

    private void addValues() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference mDatabase1 = mDatabase.child("Places");

        String name = place.getName().toString();
        System.out.println("rishabhqqq" + name + addr);
        if (addr == null) {
            Toast.makeText(Post.this,"Something went wrong check your Connections..!!",Toast.LENGTH_LONG).show();
        }
        else
        {
            mDatabase1.child(addr).child(name).child("Name").setValue(place.getName().toString());
            mDatabase1.child(addr).child(name).child("Address").setValue(place.getAddress().toString());
            mDatabase1.child(addr).child(name).child("Phone number").setValue(place.getPhoneNumber().toString());
            DatabaseReference mDatabase2 = mDatabase.child("Friends");
            mDatabase2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                    String dateTime = df.format(new Date());
                    TimeZone tz = TimeZone.getDefault();
                    Calendar cal = Calendar.getInstance(tz);
                    Date clt = cal.getTime();
                    DateFormat date = new SimpleDateFormat("HHmmss");
                    date.setTimeZone(tz);
                    String lt = date.format(clt);
                    for (DataSnapshot ch : dataSnapshot.getChildren()) {
                        //System.out.println(ss);
                        //username.setText(ss);
                        DatabaseReference dd = FirebaseDatabase.getInstance().getReference().child("Users").child(ch.getValue().toString()).child("FriendsPlace").child(dateTime).child(lt).child(MainActivity.ss);
                        dd.child("Place").setValue(place.getName().toString());
                        dd.child("Address").setValue(place.getAddress().toString());
                        dd.child("ID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        s = status.getText().toString();
                        dd.child("status").setValue(s);
                        //status.setText("");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}