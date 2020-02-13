package com.swipeimages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;



public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    TextView loginTextView;
    EditText usernameEditText;
    EditText emailEditText;
    //EditText bDayEditText; - maybe one other time
    EditText passwordEditText;

    public void showUserList() {
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            signUpClicked(view);
        }

        return false;
    }

    @Override
    public void onClick(View view) {//NOTA BENE: THE "LOGIN" TEXT VIEW DOES NOT HAVE A SEPARATE "ONCLICK" FUNCTION !!!
        if (view.getId() == R.id.loginTextView) {
            //intent to login
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            this.finish();
            startActivity(intent);

        } else if (view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }


    public void signUpClicked(View view) {

        if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
            Toast.makeText(this, "A username and a password are required.", Toast.LENGTH_SHORT).show();

        } else {
            ParseUser user = new ParseUser();
            user.setUsername(usernameEditText.getText().toString());
            user.setPassword(passwordEditText.getText().toString());
            user.setEmail(emailEditText.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("Signup", "Success");
                        showUserList();
                    } else {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Insta");

        loginTextView = findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(this);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        emailEditText = findViewById(R.id.emailEditText);
        //bDayEditText = findViewById(R.id.bDayEditText);

        ImageView logoImageView = findViewById(R.id.logoImageView);
        RelativeLayout backgroundLayout = findViewById(R.id.backgroundLayout);
        logoImageView.setOnClickListener(this);
        backgroundLayout.setOnClickListener(this);
        passwordEditText.setOnKeyListener(this);

        if (ParseUser.getCurrentUser() != null) {
            showUserList();
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

}