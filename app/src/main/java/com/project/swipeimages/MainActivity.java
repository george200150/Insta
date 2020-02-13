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

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;


/**
 * SignUpActivity complement, the rather LogInActivity
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    TextView loginTextView;
    EditText usernameEditText;
    EditText passwordEditText;

    /**
     * This method creates a new Intent and sends us from the sign up menu to the core menu.
     */
    public void showUserList() {
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }


    /**
     * @param i - KeyCode of the pressed key
     * @param keyEvent - the event itself
     * @return false, because it does not matter what the event does, it is important to execute the
     * logInClicked function. (if the "LOGIN" Button is clicked or the ENTER Key is pressed)
     */
    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            logInClicked(view);
        }

        return false;
    }


    /**
     * Method that manages the clicks on the screen. If we click on the "SIGNUP" blue text, we are
     * redirected to the SignUp menu. If we click on the background of the View, then we get its
     * focus, meaning that if we are typing something, the virtual keyboard will disappear.
     */
    @Override
    public void onClick(View view) {//NOTA BENE: THE "SIGNUP" TEXT VIEW DOES NOT HAVE A SEPARATE "ONCLICK" FUNCTION !!!
        if (view.getId() == R.id.loginTextView) {
            //intent to signup
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            this.finish();
            startActivity(intent);
        } else if (view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    /**
     * Method that redirects the user to the ForgotPasswordActivity, in order to request a
     * password recovery. (not implemented)
     */
    public void handleForgotPassword(View view) {
        // intent to new window
        Log.i("FORGOT", "clicked on forgot password");
        Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(intent);

    }


    /**
     * Method that managed the data input and suggests any changes if one of the fields are empty,
     * username was not found, or log in failed by any means.
     */
    public void logInClicked(View view) {

        if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
            Toast.makeText(this, "A username and a password are required.", Toast.LENGTH_SHORT).show();

        } else {
            // Login
            ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Log.i("Login", "ok!");
                        showUserList();
                    } else {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    /**
     * Method that gathers all the necessary data and sets listeners of the important View in the
     * Activity layout. Also, if there is an existent session (a user is already connected
     * (e.g. just started the app again)), then we will be redirected to the UserListActivity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Insta");

        loginTextView = findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(this);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
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