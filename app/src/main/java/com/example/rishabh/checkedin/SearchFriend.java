package com.example.rishabh.checkedin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchFriend extends AppCompatActivity {
   private EditText username;
    private Button Search;
    ProgressDialog mProgress;
    private DatabaseReference mDatabase,mDatabase1;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        mProgress=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();

        username= (EditText) findViewById(R.id.uniqueName);
        Search= (Button) findViewById(R.id.SearchFriend);
        final  String name=username.getText().toString();
        System.out.println(name+"bbbbb");
       // mDatabase= FirebaseDatabase.getInstance().getReference().child("Profile");
        //mDatabase1= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid().toString()).child("Friends");

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase= FirebaseDatabase.getInstance().getReference().child("Profile");
                mDatabase1= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid().toString()).child("Friends");
                System.out.println("gauri"+username.getText().toString()+"clicked");
                mProgress.setMessage("Searching...");
                mProgress.show();
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(username.getText().toString())) {
                            System.out.println("gauri"+username.getText().toString());
                            //DatabaseReference m1=mDatabase.child(name);
                            mDatabase1.child(username.getText().toString()).setValue(dataSnapshot.child(username.getText().toString()).getValue());
                            Toast.makeText(getApplicationContext(),"Added successfully",Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();
                            Intent loginIntent = new Intent(SearchFriend.this, MainActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(loginIntent);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Sorry no user found.!!",Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }
}
