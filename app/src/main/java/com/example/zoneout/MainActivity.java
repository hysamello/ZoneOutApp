package com.example.zoneout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.zoneout.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {


    private EditText usernameField;
    private EditText userPasswordField;
    private Button loginBtn;

    private Button tryAgain, register;
    private TextView forgetText;

    public static User user;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    public static FirebaseDatabase db = FirebaseDatabase.getInstance("https://zoneoutfinal-default-rtdb.europe-west1.firebasedatabase.app/");
    public static FirebaseStorage storage = FirebaseStorage.getInstance();

    public static StorageReference storageReference = storage.getReference();
    public static DatabaseReference reference = db.getReference();
    public static FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser user = auth.getCurrentUser();

        setTheme(R.style.SplashScreen);

        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(3000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        if(user != null) {
            Intent home = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(home);
        }else {
            setTheme(null);
            setTheme(R.style.Theme_loginTheme);
            setContentView(R.layout.login);
            setButtonActions();
        }

    }

    private void setButtonActions(){
        TextView noAccount = (TextView) findViewById(R.id.noAccountBtn);
        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });

        usernameField = (EditText) findViewById(R.id.emailField);
        userPasswordField = (EditText) findViewById(R.id.userPassword);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        forgetText = (TextView) findViewById(R.id.textView);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                String password = userPasswordField.getText().toString();
                if(!(username.isEmpty() && password.isEmpty())) {
                    auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                loginErrorPopup();
                            } else {
                                Intent home = new Intent(MainActivity.this, HomeActivity.class);
                                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(home);
                                finish();
                            }
                        }
                    });
                }
            }
        });

        forgetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forget = new Intent(getApplicationContext(),ForgetPasswordActivity.class);
                forget.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(forget);
                finish();
            }
        });

        usernameField.addTextChangedListener(watcher);
        userPasswordField.addTextChangedListener(watcher);
    }

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!usernameField.getText().toString().trim().isEmpty() && !userPasswordField.getText().toString().trim().isEmpty()) {
                        loginBtn.setEnabled(true);
            }else{
                loginBtn.setEnabled(false);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void loginErrorPopup(){
        this.dialogBuilder = new AlertDialog.Builder(this);
        final View loginErrorView = getLayoutInflater().inflate(R.layout.login_error, null);

        tryAgain = (Button) loginErrorView.findViewById(R.id.tryAgain_btn);
        register = (Button) loginErrorView.findViewById(R.id.register_btn);

        dialogBuilder.setView(loginErrorView);
        dialog = dialogBuilder.create();
        dialog.show();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPasswordField.setText("");
                dialog.dismiss();
            }
        });
    }

    public void goToRegister(){
        Intent registerActivity = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(registerActivity);
    }
}