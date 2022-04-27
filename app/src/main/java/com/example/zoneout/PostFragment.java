package com.example.zoneout;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.zoneout.model.Post;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class PostFragment extends Fragment {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button cancel, submit, done;
    private MaterialButton save;
    private MaterialButton saveFill;
    private RatingBar ratingBar;
    private CardView postOwner;

    private String postName;
    private String owner;
    private String description;
    private ArrayList<Integer> images;
    private Bundle bundle;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view == null) {
            this.view = inflater.inflate(R.layout.fragment_post, container, false);
        }

        images = new ArrayList<>();

        setupToolBar();

        setPostInfo(view);

        ViewPager viewPager = view.findViewById(R.id.imagePost);
        PostImageAdapter adapter = new PostImageAdapter(this.getContext()/*,images*/);
        viewPager.setAdapter(adapter);

        postOwner = view.findViewById(R.id.postOwnerProfile);
        postOwner.setOnClickListener(this::goToProfile);

        done = view.findViewById(R.id.done_button);
        done.setOnClickListener(this::doneOnClick);

        save = (MaterialButton) view.findViewById(R.id.saveButton);
        saveFill = (MaterialButton) view.findViewById(R.id.saveBlackButton);


        MainActivity.reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        if(snap.getChildren().iterator().next().getValue().equals(MainActivity.auth.getCurrentUser().getEmail())){
                            for (DataSnapshot s : snap.getChildren()){
                                if (s.getKey().equals("name")){
                                    String username = s.getValue().toString();
                                    MainActivity.reference.child("Users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                for (DataSnapshot snap : snapshot.getChildren()){
                                                    if(snap.getKey().equals("trips")){
                                                        for (DataSnapshot s : snap.getChildren()){
                                                            if(s.getKey().equals(postName)){
                                                                save.setVisibility(view.GONE);
                                                                saveFill.setVisibility(view.VISIBLE);
                                                            }
                                                            else{
                                                                saveFill.setVisibility(view.GONE);
                                                                save.setVisibility(view.VISIBLE);
                                                            }
                                                        }
                                                    }else{
                                                        save.setVisibility(view.VISIBLE);
                                                        saveFill.setVisibility(view.GONE);
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
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

        save.addOnCheckedChangeListener(this::saveOnClick);
        saveFill.addOnCheckedChangeListener(this::saveFillOnClick);

        return view;
    }

    private void goToProfile(View view) {
        bundle = new Bundle();
        bundle.putString("name",owner);
        this.setArguments(bundle);
        MainActivity.reference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (snap.getChildren().iterator().next().getValue().equals(MainActivity.auth.getCurrentUser().getEmail())) {
                            for (DataSnapshot s : snap.getChildren()) {
                                if (s.getKey().equals("name")) {
                                    if (s.getValue().toString().equals(owner)) {
                                        Navigation.findNavController(view).navigate(R.id.action_navigation_post_to_navigation_Profile,bundle);
                                    }else{
                                        Navigation.findNavController(view).navigate(R.id.action_navigation_post_to_otherProfile,bundle);
                                    }
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

    private void saveFillOnClick(MaterialButton materialButton, boolean b) {
        if(b){
            MainActivity.reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for (DataSnapshot snap : snapshot.getChildren()){
                            if(snap.getChildren().iterator().next().getValue().equals(MainActivity.auth.getCurrentUser().getEmail())){
                                for (DataSnapshot s : snap.getChildren()) {
                                    if (s.getKey().equals("name")) {
                                        String username = s.getValue().toString();
                                        MainActivity.reference.child("Users").child(username).child("trips").child(postName).removeValue();
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
            saveFill.setVisibility(view.GONE);
            save.setVisibility(view.VISIBLE);
        }
    }

    private void saveOnClick(MaterialButton materialButton, boolean b) {
        if(b){
            saveFill.setVisibility(view.VISIBLE);
            MainActivity.reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for (DataSnapshot snap : snapshot.getChildren()){
                            if(snap.getChildren().iterator().next().getValue().equals(MainActivity.auth.getCurrentUser().getEmail())){
                                for (DataSnapshot s : snap.getChildren()){
                                    if (s.getKey().equals("name")){
                                        String username = s.getValue().toString();
                                        Post post = new Post(description,owner,postName);
                                        MainActivity.reference.child("Users").child(username).child("trips").child(post.getTitle()).setValue(post);
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
    }


    private void doneOnClick(View view) {

        this.dialogBuilder = new AlertDialog.Builder(this.getContext());
        final View ratePopupView = getLayoutInflater().inflate(R.layout.fragment_done_popup, null);
        ratingBar = (RatingBar) ratePopupView.findViewById(R.id.ratingBar);

        cancel = (Button) ratePopupView.findViewById(R.id.notnow);
        submit = (Button) ratePopupView.findViewById(R.id.submit);

        dialogBuilder.setView(ratePopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        MainActivity.reference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap : snapshot.getChildren()){
                        if(snap.getChildren().iterator().next().getValue().equals(MainActivity.auth.getCurrentUser().getEmail())){
                            MainActivity.reference.child("Users").child(snap.getKey()).child("ratings").child(postName).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        for (DataSnapshot snap : snapshot.getChildren()){
                                            if (snap.getKey().equals("rating")){
                                                ratingBar.setRating(Float.parseFloat(snap.getValue().toString()));
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rating = Math.round(ratingBar.getRating());
                if(rating>0){
                    MainActivity.reference.child("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for (DataSnapshot snap : snapshot.getChildren()){
                                    if(snap.getChildren().iterator().next().getValue().equals(MainActivity.auth.getCurrentUser().getEmail())){
                                        Post p = new Post(description,owner,rating,postName);
                                        MainActivity.reference.child("Users").child(snap.getKey()).child("ratings").child(postName).setValue(p);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Navigation.findNavController(view).navigate(R.id.action_navigation_post_to_navigation_home);
                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setupToolBar(){
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_navigation_post_to_postsList);
            }
        });
    }


    private void setPostInfo(View v){
        TextView ownerTextView = view.findViewById(R.id.postOwnewName);
        TextView descTextView = view.findViewById(R.id.postDescription);
        TextView postTitle = view.findViewById(R.id.postTitle);

        postName = getArguments().getString("name");
        owner = getArguments().getString("owner");
        description = getArguments().getString("description");
        images = getArguments().getIntegerArrayList("images");

        ownerTextView.setText(owner);
        descTextView.setText(description);
        postTitle.setText(postName);
    }
}