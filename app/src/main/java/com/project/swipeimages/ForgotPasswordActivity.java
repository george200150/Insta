package com.project.swipeimages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText emailEditText;


    public void handleResetPassword(View view) {
        Log.i("RESET", "pressed!");
        Toast.makeText(this, "SERVER ERROR. WILL BE FIXED SOON", Toast.LENGTH_LONG).show();
        //TODO: to get hands on the password I should use server Masterkey, which is not secure !!!
        //idk how else should I do it...
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setTitle("Insta");

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);



        //TODO: local cached repo for the bitmaps ( store kvp<userId, superclass{Bitmap}>  -> find out a way to encapsulate ImageView (Bitmap) into other view)
        //experimentalView.
                //TODO: find out how to add programatically views inside other views

        /*LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.your_layout, null);

// fill in any details dynamically here
        TextView textView = (TextView) v.findViewById(R.id.a_text_view);
        textView.setText("your text");

// insert into main view
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.insert_point);
        insertPoint.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));*/

    }
}



    /*LinearLayout myLayout = findViewById(R.id.main);

    Button myButton = new Button(this);
myButton.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT));

        myLayout.addView(myButton);*/
