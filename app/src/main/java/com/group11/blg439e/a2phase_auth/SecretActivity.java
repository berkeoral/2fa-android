package com.group11.blg439e.a2phase_auth;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SecretActivity extends AppCompatActivity {

    /*
    * Default intent generator for SecretActivity
     */
    public static Intent getIntent(Context context){
        return new Intent(context, SecretActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);
    }

    /*
    * Returns to LoginActivity
     */
    public void exitButton(View view){
        finish();
    }

}
