package com.example.zoneout.bottomnavigationbar;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.zoneout.HomeActivity;
import com.example.zoneout.MainActivity;
import com.example.zoneout.R;
import com.example.zoneout.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AddFragment extends Fragment {

    EditText postTitle,description;

    ImageView submitPost;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_add, container, false);

        postTitle = (EditText) view.findViewById(R.id.editTextTextPersonName6);

        description = (EditText) view.findViewById(R.id.editTextTextMultiLine);

        submitPost = (ImageView) view.findViewById(R.id.submitPost);

        submitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.reference.child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for (DataSnapshot snap : snapshot.getChildren()){
                                if(snap.getChildren().iterator().next().getValue().equals(MainActivity.auth.getCurrentUser().getEmail())){
                                    for (DataSnapshot s : snap.getChildren()){
                                        if (s.getKey().equals("name")){
                                            String username = s.getValue().toString();
                                            MainActivity.reference.child("Posts").child(postTitle.getText().toString()).setValue(new Post(description.getText().toString(),username,postTitle.getText().toString()));
                                            Intent intent = new Intent(getContext(), HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        return view;
    }
}