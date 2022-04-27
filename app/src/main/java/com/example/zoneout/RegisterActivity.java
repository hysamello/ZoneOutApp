package com.example.zoneout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zoneout.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText username;
    private EditText email;
    private EditText userPassword;
    private EditText userPasswordConfirm;
    private Button signinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.Theme_loginTheme);

        setContentView(R.layout.register);

        username = (EditText) findViewById(R.id.username_register);
        email = (EditText) findViewById(R.id.email_register);
        userPassword = (EditText) findViewById(R.id.password_register);
        userPasswordConfirm = (EditText) findViewById(R.id.passwordConfirm_register);
        signinBtn = (Button) findViewById(R.id.signinBtn_register);

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regUsername = username.getText().toString();
                String regEmail = email.getText().toString();
                String password = userPassword.getText().toString();
                String passwordConfirm = userPasswordConfirm.getText().toString();

                if(checkFields(regUsername,regEmail,password,passwordConfirm)) {
                    DatabaseReference ref = MainActivity.db.getReference().child("Users").child(regUsername);

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                MainActivity.auth.createUserWithEmailAndPassword(regEmail, password).addOnCompleteListener(task -> {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Invalid Email or password", Toast.LENGTH_LONG).show();
                                    } else {
                                        MainActivity.user = new User(regUsername, regEmail);
                                        MainActivity.reference.child("Users").child(MainActivity.user.getName()).setValue(MainActivity.user);

                                        Toast.makeText(RegisterActivity.this, "Register successful!", Toast.LENGTH_LONG);
                                        Intent login = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(login);
                                        finish();
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, "Username already exists!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        username.addTextChangedListener(watcher);
        email.addTextChangedListener(watcher);
        userPassword.addTextChangedListener(watcher);
        userPasswordConfirm.addTextChangedListener(watcher);

    }

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (checkAllFilled()) {
                signinBtn.setEnabled(true);
            }else{
                signinBtn.setEnabled(false);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private boolean checkAllFilled(){
        if(!username.getText().toString().trim().isEmpty() &&
                !email.getText().toString().trim().isEmpty() &&
                !userPassword.getText().toString().trim().isEmpty() &&
                !userPasswordConfirm.getText().toString().trim().isEmpty()){
            return true;
        }
        return false;
    }

    private boolean checkFields(String usernameStr, String emailStr, String passwordStr, String confirmPasswordStr){
        String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";
        String whiteSpaces = "\\A\\w{4,20}\\z";
        if(usernameStr.length() >= 15){
            username.setError("Username too long!");
            return false;
        }
        if(!(usernameStr.matches(whiteSpaces))){
            username.setError("White spaces not allowed!");
            return false;
        }
        if(!emailStr.matches(emailPattern)){
            email.setError("Invalid email address");
            return false;
        }
        if(!(passwordStr.length() >= 6)){
            userPassword.setError("Password must have at least 6 characters!");
            return false;
        }
        if(!(confirmPasswordStr.length() >= 6)){
            userPasswordConfirm.setError("Password must have at least 6 characters!");
            return false;
        }
        if(!passwordStr.equals(confirmPasswordStr)){
            Toast.makeText(RegisterActivity.this, "Passwords must match!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
