package com.example.zoneout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class OtherProfile extends Fragment {

    private TextView postCountView,tv;
    private String username;
    private Bundle bundle;
    private int postCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_other_profile, container, false);

        Button reviewsBtn = (Button) v.findViewById(R.id.reviewsbutton);

        postCountView = (TextView) v.findViewById(R.id.otherProfilePostCount);

        tv = v.findViewById(R.id.nomeutilizadorOtherprofile);

        username = getArguments().get("name").toString();

        countPosts();

        reviewsBtn.setOnClickListener(this::goToOtherProfileReviews);


        TextView tv = v.findViewById(R.id.nomeutilizadorOtherprofile);
        tv.setText(getArguments().get("name").toString());

        return v;
    }

    public void goToOtherProfileReviews(View v){
        this.bundle = new Bundle();

        bundle.putString("username",username);

        this.setArguments(bundle);

        Navigation.findNavController(v).navigate(R.id.action_otherProfile_to_otherProfileReviewsFragment,bundle);
    }

    public void countPosts(){
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
                    tv.setText(username);
                    System.out.println("o texto é " + tv.getText().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
}