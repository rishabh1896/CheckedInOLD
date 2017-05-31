package com.example.rishabh.checkedin;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class VivzAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Information> data= Collections.emptyList();
    private final int WITHSTATUS=0,WITHOUT=1;
    public VivzAdapter(Context context,List<Information> data) {
        this.context=context;

        this.data=data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflator=LayoutInflater.from(parent.getContext());
        switch(viewType)
        {
            case WITHOUT:
                View view1=inflator.inflate(R.layout.custom_row2,parent,false);
                viewHolder =new MyViewHolder2(view1);
                break;
            case WITHSTATUS:
                View view2=inflator.inflate(R.layout.custom_row,parent,false);
                viewHolder =new MyViewHolder(view2);
                break;
            default:
                View view=inflator.inflate(R.layout.custom_row,parent,false);
                viewHolder =new MyViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType())
        {
            case WITHOUT:
                MyViewHolder2 vh1=(MyViewHolder2) holder;
                configureViewHolder1(vh1,position);
                break;
            case WITHSTATUS:
                MyViewHolder vh2=(MyViewHolder) holder;
                configureViewHolder2(vh2,position);
                break;
            default:
                MyViewHolder vh=(MyViewHolder) holder;
                configureViewHolder2(vh,position);
                break;
        }

    }

    private void configureViewHolder1(final MyViewHolder2 vh1, int position) {
        Information current=data.get(position);
        vh1.ListText.setText(current.Title);
        vh1.userName.setText(current.activeUser);
        vh1.Adresses.setText(current.address);
        System.out.println(current.ID);
        StorageReference filepath= FirebaseStorage.getInstance().getReference().child("Users").child(current.ID).child("ProfilePic");

        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                getProfilePic( uri,vh1.profilePic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }});
    }

    private void configureViewHolder2(final MyViewHolder vh2, int position) {
        Information current=data.get(position);
        vh2.ListText.setText(current.Title);
        vh2.userName.setText(current.activeUser);
        vh2.Adresses.setText(current.address);
        System.out.println(current.ID);
        StorageReference filepath= FirebaseStorage.getInstance().getReference().child("Users").child(current.ID).child("ProfilePic");

        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                getProfilePic( uri,vh2.profilePic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }});
        vh2.Status.setText(current.status);
    }



    private void getProfilePic(Uri uri,ImageView holder) {
        Picasso.with(context).load(uri).fit().centerCrop()
                .error(R.mipmap.ic_account_circle_black_24dp)
                .placeholder(R.mipmap.ic_account_circle_black_24dp)
                .transform(new CircleTransform())
                //.resize(90,60)
                .into(holder);

    }
    @Override
    public int getItemViewType(int position)
    {
        Information current=data.get(position);
        if(current.status.equals(""))
            return WITHOUT;
        else
            return WITHSTATUS;
    }


    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ListText;
        TextView Adresses;
        TextView userName;
        ImageView profilePic;
        TextView Status;
        public MyViewHolder(View itemView) {
            super(itemView);
            ListText= (TextView) itemView.findViewById(R.id.ListText);
            Adresses= (TextView) itemView.findViewById(R.id.addresses);
            userName=(TextView)itemView.findViewById(R.id.activeUser);
            profilePic= (ImageView) itemView.findViewById(R.id.profilePic);
            Status= (TextView) itemView.findViewById(R.id.status);

        }
    }
    public class MyViewHolder2 extends RecyclerView.ViewHolder {
        TextView ListText;
        TextView Adresses;
        TextView userName;
        ImageView profilePic;
        public MyViewHolder2(View itemView) {
            super(itemView);
            ListText= (TextView) itemView.findViewById(R.id.ListText);
            Adresses= (TextView) itemView.findViewById(R.id.addresses);
            userName=(TextView)itemView.findViewById(R.id.activeUser);
            profilePic= (ImageView) itemView.findViewById(R.id.profilePic);

        }
    }
}
