package com.example.brgyplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class Signup extends AppCompatActivity implements View.OnClickListener {

    private TextView signin, register;
    private EditText userFirstName, userLastName, userEmail, userPassword, confirmPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        signin = (TextView) findViewById(R.id.to_sign_in);
        signin.setOnClickListener(this);

        register = (Button) findViewById(R.id.register_button);
        register.setOnClickListener(this);

        userFirstName = (EditText) findViewById(R.id.first_name);
        userLastName = (EditText) findViewById(R.id.last_name);
        userEmail = (EditText) findViewById(R.id.email);
        userPassword = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.to_sign_in:
                startActivity(new Intent(this, SignIn.class));
                break;
            case R.id.register_button:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String firstname = userFirstName.getText().toString().trim();
        String lastname = userLastName.getText().toString().trim();
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        if(firstname.isEmpty()){
            userFirstName.setError("First Name is required!");
            userFirstName.requestFocus();
            return;
        }

        if(lastname.isEmpty()){
            userLastName.setError("Last Name is required!");
            userLastName.requestFocus();
            return;
        }

        if(email.isEmpty()){
            userEmail.setError("Email is required!");
            userEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userEmail.setError("Please provide a valid email!");
            userEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            userPassword.setError("Password is required!");
            userPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            userPassword.setError("Min password length should be 6 characters!");
            userPassword.requestFocus();
            return;
        }

        if(confirmPass.isEmpty()){
            confirmPassword.setError("Password is required!");
            confirmPassword.requestFocus();
            return;
        }

        if(!password.equals(confirmPass)){
            userPassword.setError("Passwords does not match!");
            userPassword.requestFocus();
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User( firstname, lastname, email );

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(Signup.this, "User registration successful!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                        // redirect to login layout!
                                    }else{
                                        Toast.makeText(Signup.this, "Failed to register. Try again!" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(Signup.this, "Failed to register." + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}