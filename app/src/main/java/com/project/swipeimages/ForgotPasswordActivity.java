package com.swipeimages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class ForgotPasswordActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText emailEditText;


    /**
     * this is supposed to make a reset password request to the server, but there must be set a
     * mailing server so that everything works fine...
     */
    public void handleResetPassword(View view) {
        Log.i("RESET", "pressed!");
        Toast.makeText(this, "SERVER ERROR. WILL BE FIXED SOON", Toast.LENGTH_LONG).show();
        //to get hands on the password I should use server Masterkey, which is not secure !!!
        //idk how else should I do it...
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
