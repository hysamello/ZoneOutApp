package com.example.zoneout.bottomnavigationbar;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zoneout.MainActivity;
import com.example.zoneout.R;
import com.example.zoneout.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment{

    private TextView usernameView, postCountView;
    private ImageView settingsView;
    private int postCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        Button editProfile = (Button) v.findViewById(R.id.editProfileBtn);

        Button reviewsBtn = (Button) v.findViewById(R.id.reviewsbutton);

        usernameView = (TextView) v.findViewById(R.id.nomeutilizadorprofile);

        settingsView = (ImageView) v.findViewById(R.id.profileSettings);

        postCountView = (TextView) v.findViewById(R.id.postCount);

        MainActivity.reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        if(snap.getChildren().iterator().next().getValue().equals(MainActivity.auth.getCurrentUser().getEmail())){
                            for (DataSnapshot s : snap.getChildren()){
                                if (s.getKey().equals("name")){
                                    usernameView.setText(s.getValue().toString());
                                    countPosts();
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

        settingsView.setOnClickListener(this::goToSettings);

        editProfile.setOnClickListener(this::goToEditProfile);

        reviewsBtn.setOnClickListener(this::goToReviews);

        return v;
    }

    public void goToEditProfile(View view){
        Navigation.findNavController(view).navigate(R.id.action_navigation_Profile_to_editProfileFragment);
    }

    public void goToSettings(View view){
        Navigation.findNavController(view).navigate(R.id.action_navigation_Profile_to_settingsFragment);
    }

    public void goToReviews(View view){
        Navigation.findNavController(view).navigate(R.id.action_navigation_Profile_to_profileReviewsFragment);
    }

    public void countPosts(){
        String username = usernameView.getText().toString();
        MainActivity.reference.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        for(DataSnapshot s : snap.getChildren()){
                            if(s.getKey().equals("owner") && s.getValue().toString().equals(username)){
                                postCount++;
                            }
                        }
                    }
                    postCountView.setText(String.valueOf(postCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
}