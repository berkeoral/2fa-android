package com.group11.blg439e.a2phase_auth;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.fingerprint.FingerprintManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import javax.crypto.Cipher;

public class LoginActivity extends AppCompatActivity {

    private static AccountSQLHelper dbHelper;
    private EditText idEditText;
    private EditText passwordEditText;
    private FingerprintManager fpManager;
    private Cipher cipher;

    private static final int PERMISSIONS_REQUEST_FINGER_PRINT = 0;
    private static final int PERMISSIONS_REQUEST_CAMERA = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        idEditText = (EditText) findViewById(R.id.loginScreenIdEditText);
        passwordEditText = (EditText) findViewById(R.id.loginScreenPasswordEditText);
        dbHelper = new AccountSQLHelper(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_preferences_filename), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.shared_preferences_userid), idEditText.getText().toString());
                    editor.commit();
                    startActivityForResult(FaceRecognitionActivity.getIntent(this, true), 1);
                } else {
                    Toast.makeText(this, getString(R.string.toast_loginscreen_permission_denied),
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    public void loginButton(View view) {
        if (isFieldsEmpty()) {
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
        String[] selectionArgs = {idEditText.getText().toString()};
        Cursor cursor = db.query(AccountContract.Account.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor.moveToNext()) {
            String password = cursor.getString(cursor.getColumnIndex(AccountContract.Account.COLUMN_NAME_PASSWORD));
            if (password.equals(passwordEditText.getText().toString())) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_CAMERA
                    );
                } else {
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_preferences_filename), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.shared_preferences_userid), idEditText.getText().toString());
                    editor.commit();
                    startActivityForResult(FaceRecognitionActivity.getIntent(this, true), 1);
                    //startActivityForResult(SecretActivity.getIntent(this,
                    //cursor.getString(cursor.getColumnIndex(AccountContract.Account.COLUMN_NAME_CONTENT))), 1);
                }
            } else {
                Toast.makeText(this, getString(R.string.toast_loginscreen_login_error),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_loginscreen_login_error),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void signupButton(View view) {
        if (isFieldsEmpty()) {
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
        String[] selectionArgs = {idEditText.getText().toString()};
        Cursor cursor = db.query(AccountContract.Account.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor.moveToNext()) {
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
        db.insert(AccountContract.Account.TABLE_NAME, null, values);
        Toast.makeText(this, getString(R.string.toast_loginscreen_signup_accountcreated),
                Toast.LENGTH_LONG).show();
        cursor.close();
        return;
    }

    private boolean isFieldsEmpty() {
        if (idEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")) {
            return true;
        }
        return false;
    }

    public static SQLiteDatabase getReadableDB(){
        return dbHelper.getReadableDatabase();
    }

    public static SQLiteDatabase getWritableDB(){
        return dbHelper.getWritableDatabase();
    }

}
