package com.project.swipeimages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

class ForgotPasswordActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText emailEditText;

    public void handleResetPassword(View view) {
        Log.i("RESET", "pressed!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setTitle("Insta");

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
    }
}
