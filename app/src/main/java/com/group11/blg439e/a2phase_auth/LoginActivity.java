package com.group11.blg439e.a2phase_auth;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
        if(isFieldsEmpty()){
            Toast.makeText(this, getString(R.string.toast_loginscreen_emptyfields),
                    Toast.LENGTH_LONG).show();
            return;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                AccountContract.Account._ID,
                AccountContract.Account.COLUMN_NAME_ID,
                AccountContract.Account.COLUMN_NAME_PASSWORD,
                AccountContract.Account.COLUMN_NAME_CONTENT
        };
        String selection = AccountContract.Account.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = { idEditText.getText().toString() };
        Cursor cursor = db.query(AccountContract.Account.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(cursor.moveToNext()){
            String password = cursor.getString(cursor.getColumnIndex(AccountContract.Account.COLUMN_NAME_PASSWORD));
            if(password.equals(passwordEditText.getText().toString())){
                startActivityForResult(SecretActivity.getIntent(this,
                        cursor.getString(cursor.getColumnIndex(AccountContract.Account.COLUMN_NAME_CONTENT))),1);
            }
            else{
                Toast.makeText(this, getString(R.string.toast_loginscreen_login_error),
                        Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, getString(R.string.toast_loginscreen_login_error),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void signupButton(View view){
        if(isFieldsEmpty()){
            Toast.makeText(this, getString(R.string.toast_loginscreen_emptyfields),
                    Toast.LENGTH_LONG).show();
            return;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                AccountContract.Account._ID,
                AccountContract.Account.COLUMN_NAME_ID,
                AccountContract.Account.COLUMN_NAME_PASSWORD,
                AccountContract.Account.COLUMN_NAME_CONTENT
        };
        String selection = AccountContract.Account.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = { idEditText.getText().toString() };
        Cursor cursor = db.query(AccountContract.Account.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
                );
        if(cursor.moveToNext()){
            Toast.makeText(this, getString(R.string.toast_loginscreen_signup_idexist),
                    Toast.LENGTH_LONG).show();
            cursor.close();
            return;
        }
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AccountContract.Account.COLUMN_NAME_ID, idEditText.getText().toString());
        values.put(AccountContract.Account.COLUMN_NAME_PASSWORD, passwordEditText.getText().toString());
        values.put(AccountContract.Account.COLUMN_NAME_CONTENT, "");
        db.insert(AccountContract.Account.TABLE_NAME,null, values);
        Toast.makeText(this, getString(R.string.toast_loginscreen_signup_accountcreated),
                Toast.LENGTH_LONG).show();
        cursor.close();
        return;
    }

    private boolean isFieldsEmpty(){
        if(idEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")){
            return true;
        }
        return false;
    }

}
