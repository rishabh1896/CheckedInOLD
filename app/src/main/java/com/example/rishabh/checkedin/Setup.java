package com.example.rishabh.checkedin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Setup extends AppCompatActivity {
Button back;
    private EditText username,dob;
    private FirebaseAuth mAuth;
    private ImageButton proPic;
    private StorageReference mStorage;
    ProgressDialog mProgress;
private static final int Gallery_Request=2;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mStorage=FirebaseStorage.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();
        username=(EditText)findViewById(R.id.Name);
        dob=(EditText)findViewById(R.id.DOB);
        back=(Button)findViewById(R.id.Submit);
        proPic=(ImageButton)findViewById(R.id.proPic);
        proPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Request);
            }
        });
        mProgress=new ProgressDialog(this);
        mProgress.setMessage("Uploading image..");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
                String user_id=mAuth.getCurrentUser().getUid();
                DatabaseReference currentUserDb=mDatabase.child(user_id).child("Details");
                currentUserDb.child("name").setValue(username.getText().toString());
                currentUserDb.child("DOB").setValue(dob.getText().toString());
                currentUserDb.child("profile").setValue("default");
                DatabaseReference dd=mDatabase.child(user_id).child("Friends").child(username.getText().toString());
                dd.setValue(mAuth.getCurrentUser().getUid());
                Intent go=new Intent(Setup.this,MainActivity.class);
                        go.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(go);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_Request && resultCode==RESULT_OK)

        {
            mProgress.show();
               final Uri uri=data.getData();
            StorageReference filepath=mStorage.child("Users").child(mAuth.getCurrentUser().getUid()).child("ProfilePic");
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri=taskSnapshot.getDownloadUrl();
                    Picasso.with(Setup.this).load(downloadUri).fit().centerCrop().into(proPic);
                    Toast.makeText(Setup.this,"File Uploaded",Toast.LENGTH_SHORT).show();

                    mProgress.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Setup.this,"Something Went wrong..",Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();

                }
            });
        }
    }
}
