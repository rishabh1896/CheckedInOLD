package com.example.rishabh.checkedin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {
     private EditText email,password;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase,mDatabase1;
    Button submit;
    String mail;
    String passwrd;
    int b=0;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
       /* FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // NOTE: this Activity should get onpen only when the user is not signed in, otherwise
                    // the user will receive another verification email.
                    sendVerificationEmail();
                } else {
                    // User is signed out

                }
                // ...
            }
        };*/
        mProgress=new ProgressDialog(this);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase1= FirebaseDatabase.getInstance().getReference().child("Profile");


        email=(EditText)findViewById(R.id.emailadrr);
        password=(EditText)findViewById(R.id.nPassword);
        submit=(Button)findViewById(R.id.Submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });

    }


    private void startRegister() {

        mail=email.getText().toString();
         passwrd=password.getText().toString();
        if(!mail.contains("@"))
            Toast.makeText(Register.this, "Invalid Email...", Toast.LENGTH_LONG).show();
        else if((passwrd.length()<6))
        {
            Toast.makeText(Register.this, "Password should be atleast of 6 characters...", Toast.LENGTH_LONG).show();
        }
        else {
            mProgress.setMessage("Signing Up....");
            mProgress.show();
            mDatabase1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String s = mail.substring(0, mail.indexOf('@'));
                    System.out.println(s);
                    if (b == 0) {
                        b++;
                        if (dataSnapshot.hasChild(s)) {
                            Toast.makeText(Register.this, "Already Registered with this email", Toast.LENGTH_LONG).show();
                            mProgress.dismiss();
                        } else {
                            System.out.println("all ok");
                            allOk();
                        }
                    }
                    else
                    {
                        mProgress.dismiss();
                        finish();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("i'm here");
                  mProgress.dismiss();
                }
            });

        }
    }
    @Override
   protected void onStart()
    {
      b=0;
        super.onStart();
    }

    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mProgress.dismiss();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           Toast.makeText(Register.this,"A verification email is send to ur email kindly verify it and login..",Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(Register.this, login.class));
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }
    private void allOk() {
        mAuth.createUserWithEmailAndPassword(mail, passwrd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String user_id = mAuth.getCurrentUser().getUid();
                    DatabaseReference currentUserDb = mDatabase.child(user_id).child("Details");
                    String s = mail.substring(0, mail.indexOf('@'));
                    mDatabase1.child(s).setValue(user_id);
                    mProgress.dismiss();

                sendVerificationEmail();

                }
                mProgress.dismiss();

            }
        });
    }
}