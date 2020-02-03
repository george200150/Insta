package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText emailEditText;

    public void handleResetPassword(View view){
        Log.i("RESET", "pressed!");

        String user = this.usernameEditText.getText().toString();
        String email = this.emailEditText.getText().toString();

        //TODO: check if user credentials are ok (later on..)
        //TODO: reset user password on server

        DownloadPasswordTask task = new DownloadPasswordTask();// https://helloacm.com/api/random/?n=20 API FOR RANDOM PASSWORD GENERATION
        String result;

        try {
            result = task.execute("https://helloacm.com/api/random/?n=20").get();
            Log.i("Result", result);
            if(result == null){
                Toast.makeText(this, "Looks like we couldn't mail you :(", Toast.LENGTH_LONG).show();//failed to reset password
            }
            else{
                String subject = "Insta Account Recovery";
                String message = "Dear " + user + ", your password has been reseted. Your new password is: " + result + " . You can now log in!";

                String[] strings = {email, subject, message};
                MailUtil mailUtil = new MailUtil();
                mailUtil.execute(strings);

                Toast.makeText(this, "A mail has been sent to you!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {//georgeciubotariu@yahoo.com
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setTitle("Insta");

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
    }
}
