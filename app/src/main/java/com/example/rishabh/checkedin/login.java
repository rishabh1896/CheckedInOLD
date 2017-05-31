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

public class login extends AppCompatActivity {
    EditText email,password;
    Button loginbtn,register;
    private FirebaseAuth mAuth;
    public static String mAuth1;
    private DatabaseReference mDatabase;
    ProgressDialog mProgress;
    public static boolean call=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(!MainActivity.called||!call) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.called = true;
            call=true;
        }
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        loginbtn=(Button) findViewById(R.id.SignIn);
        mProgress=new ProgressDialog(this);
        register=(Button) findViewById(R.id.Register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent= new Intent(login.this,Register.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);


            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.setMessage("Checking Login....");
                mProgress.show();
                startSignIn();

            }
        });
    }



    private void startSignIn() {
        String getEmail = email.getText().toString();
        String getpassword = password.getText().toString();
        if (TextUtils.isEmpty(getEmail) || TextUtils.isEmpty(getpassword)) {
            Toast.makeText(login.this,"Fields Are Empty",Toast.LENGTH_LONG).show();
        } else {

            mAuth.signInWithEmailAndPassword(getEmail, getpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        mProgress.dismiss();
                        Toast.makeText(login.this, "SignIn problem", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        mProgress.dismiss();
                        checkIfEmailVerified();

                    }

                }


            });
        }
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            // user is verified, so you can finish this activity or send user to activity which you want.
            finish();
            checkUserExist();
            Intent loginIntent = new Intent(login.this, MainActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
        }
        else
        {
            Toast.makeText(login.this,"Account not verified...",Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();

            //restart this activity

        }
    }

    private void checkUserExist() {
        if ((!(FirebaseAuth.getInstance().getCurrentUser() == null))&&((mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Details").child("name")).toString()!=null)) {
            final String user_id = mAuth.getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                       mAuth1 =mAuth.getCurrentUser().getUid();
                        Intent loginIntent = new Intent(login.this, MainActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);
                    }
               else
                {
                    Intent loginIntent = new Intent(login.this, Setup.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
