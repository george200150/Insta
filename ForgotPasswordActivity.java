package com.project.swipeimages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

class ForgotPasswordActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText emailEditText;

    LinearLayout experimentalView;

    /*public void handleForgotPassword(View view){
        Log.i("FORGOT", "something happened!");
    }*/

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

        experimentalView = findViewById(R.id.experimentalView);
        //experimentalView.addView(...);

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
