package com.group11.blg439e.a2phase_auth;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SecretActivity extends AppCompatActivity {

    private static final String INTENT_CONTENT = "content";
    public static Intent getIntent(Context context, String content){
        Intent intent = new Intent(context, SecretActivity.class);
        intent.putExtra(INTENT_CONTENT, content);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);
    }

}
