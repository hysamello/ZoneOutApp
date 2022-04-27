package com.example.zoneout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText mailField;
    private Button changePasswordBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.Theme_loginTheme);

        setContentView(R.layout.fragment_forgetpassword);

        mailField = (EditText) findViewById(R.id.sendEmail);

        changePasswordBtn = (Button) findViewById(R.id.sendEmailBtn);

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";
                String mail = mailField.getText().toString();

                if(!mail.isEmpty()){
                    if(mail.matches(emailPattern)){
                        MainActivity.auth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Email sent with success!",Toast.LENGTH_LONG);
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                    }else{
                        mailField.setError("Invalid mail address!");
                    }
                }
            }
        });
    }
}
