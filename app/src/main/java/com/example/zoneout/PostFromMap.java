package com.example.zoneout;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.zoneout.model.Post;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class PostFromMap extends Fragment {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button cancel, submit, done;
    private MaterialButton save;
    private MaterialButton saveFill;
    private RatingBar ratingBar;
    private CardView postOwner;
    private TextView ownerTextView, descTextView, title;

    private String postName;
    private String owner;
    private int id;
    private String description;

    private ArrayList<Integer> images;
    private Bundle bundle;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view == null) {
            this.view = inflater.inflate(R.layout.fragment_post_from_map, container, false);
        }

        ownerTextView = view.findViewById(R.id.postOwnewName);
        descTextView = view.findViewById(R.id.postDescription);
        title = view.findViewById(R.id.postTitle);

        id = (Integer) getArguments().get("postId");

        images = new ArrayList<>();

        setupToolBar();

        setPostInfo(view);




        //coisas da imagem
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
                                                            System.out.println("A key : " + s.getKey() + " e o value " + postName);
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
        System.out.println("owneeeeeeeerrrrrrr############"+owner);
        this.setArguments(bundle);
        Navigation.findNavController(view).navigate(R.id.action_navigation_postfrommap_to_otherProfile,bundle);
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Ver como que salva a informação
                System.out.println(ratingBar.getRating());
                if(ratingBar.getRating()>0){

                    done.setVisibility(v.GONE);
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
        toolbar.setTitle(postName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void setPostInfo(View v){
        MainActivity.reference.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){

                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                        Map<String,Object> post = snap.getValue(genericTypeIndicator);

                        int dbId = Math.toIntExact((long)post.get("id"));

                        if(dbId == id){
                            postName = (String) post.get("title");
                            owner = (String) post.get("owner");
                            description = (String) post.get("description");
                            postName = (String) post.get("title");

                            System.out.println(owner);
                            System.out.println(description);

                            ownerTextView.setText(owner);
                            descTextView.setText(description);
                            title.setText(postName);
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