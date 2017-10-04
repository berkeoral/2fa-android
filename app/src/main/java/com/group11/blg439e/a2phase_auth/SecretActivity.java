package com.group11.blg439e.a2phase_auth;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SecretActivity extends AppCompatActivity {
    public static Intent getIntent(Context context){
        return new Intent(context, SecretActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);
    }

    public void exitButton(View view){
        finish();
    }

}
