package com.group11.blg439e.a2phase_auth;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText idEditText;
    private EditText passwordEditText;
    private AccountSQLHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        idEditText = (EditText) findViewById(R.id.loginScreenIdEditText);
        passwordEditText = (EditText)findViewById(R.id.loginScreenPasswordEditText);
        dbHelper = new AccountSQLHelper(this); // ??? which context is neeeded?
    }

    public void loginButton(View view) {

        System.out.println("Login");
    }

    public void signupButton(View view){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AccountContract.Account.COLUMN_NAME_ID, idEditText.getText().toString());
        values.put(AccountContract.Account.COLUMN_NAME_PASSWORD, passwordEditText.getText().toString());
        values.put(AccountContract.Account.COLUMN_NAME_CONTENT, "");
    }
}
