package com.example.zoneout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.zoneout.bottomnavigationbar.ProfileFragment;
import com.example.zoneout.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class EditProfileFragment extends Fragment {

    private View view;
    private EditText usernameField,emailField;
    private Button editProfilePhoto;
    private User oldUser;
    private String oldUsername,oldEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        this.view = inflater.inflate(R.layout.fragment_editprofile, container, false);

        usernameField = (EditText) view.findViewById(R.id.editUsernameField);
        editProfilePhoto = (Button) view.findViewById(R.id.editProfilePhoto);

        editProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
            }
        });

        emailField = (EditText) view.findViewById(R.id.editEmailField);

        DatabaseReference ref = MainActivity.db.getReference().child("Users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap : snapshot.getChildren()){
                        if(snap.getChildren().iterator().next().getValue().equals(MainActivity.auth.getCurrentUser().getEmail())){
                            for (DataSnapshot s : snap.getChildren()){
                                if(s.getKey().equals("email")){
                                    emailField.setText(s.getValue().toString());
                                    oldEmail = s.getValue().toString();
                                }
                                if(s.getKey().equals("name")){
                                    usernameField.setText(s.getValue().toString());
                                    oldUsername = s.getValue().toString();
                                }
                            }
                        }
                    }
                    oldUser = new User(oldUsername,oldEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ImageView imgView = (ImageView) view.findViewById(R.id.editProfileConfirm);

        imgView.setOnClickListener(this::sendDataToDB);

        return view;
    }

    public void sendDataToDB(View view){
        String username = usernameField.getText().toString();
        String email = emailField.getText().toString();
        String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";
        String whiteSpaces = "\\A\\w{4,20}\\z";
        User newUser = new User(username,email);

        if((username.matches(whiteSpaces))){
            if(email.matches(emailPattern)){
                if(!username.equals(oldUser.getName())){
                    MainActivity.reference.child("Users").child(oldUser.getName()).removeValue();
                }
                if(!email.equals(oldUser.getEmail())){
                    MainActivity.auth.getCurrentUser().updateEmail(email);
                }
                MainActivity.reference.child("Users").child(newUser.getName()).setValue(newUser);

                MainActivity.reference.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for (DataSnapshot snap : snapshot.getChildren()){
                                for (DataSnapshot s : snap.getChildren()){
                                    if(s.getKey().equals("owner") && s.getValue().toString().equals(oldUser.getName())){
                                        MainActivity.reference.child("Posts").child(snap.getKey()).child(s.getKey()).setValue(newUser.getName());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Navigation.findNavController(view).navigate(R.id.action_editProfileFragment_to_navigation_Profile);
            }else{
                emailField.setError("Invalid email address");
            }
        }else{
            usernameField.setError("White spaces not allowed");
        }
    }
}
