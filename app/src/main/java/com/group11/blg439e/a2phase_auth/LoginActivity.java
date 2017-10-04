package com.group11.blg439e.a2phase_auth;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int REQUEST_CODE_FACE_RECOGNITION = 1;
    private static final int REQUEST_CODE_SECRET_ACTIVITY = 2;
    private static AccountSQLHelper dbHelper;
    private static Boolean verify = true;
    private TextInputLayout idTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        idTextInputLayout = (TextInputLayout) findViewById(R.id.facerecognition_textinputlayout_id);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.facerecognition_textinputlayout_password);
        dbHelper = new AccountSQLHelper(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_preferences_filename), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.shared_preferences_userid), idTextInputLayout.getEditText().getText().toString().toLowerCase());
                    editor.commit();
                    currentUserId = idTextInputLayout.getEditText().getText().toString().toLowerCase();
                    if(verify) {
                        startActivityForResult(FaceRecognitionActivity.getIntent(this, true), REQUEST_CODE_FACE_RECOGNITION);
                    }
                    else{
                        verify = true;
                        startActivityForResult(FaceRecognitionActivity.getIntent(this, false), REQUEST_CODE_FACE_RECOGNITION);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.toast_loginscreen_permission_denied),
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FACE_RECOGNITION) {
            if (resultCode == getResources().getInteger(R.integer.facerecog_result_code)) {
                int responsecode = data.getIntExtra(getString(R.string.forresult_intent_responsecode), 0);
                if (responsecode == getResources().getInteger(R.integer.facerecog_canceled)){
                    currentUserId ="";
                }
                else if(responsecode == getResources().getInteger(R.integer.facerecog_login_verified)){
                    startActivityForResult(SecretActivity
                            .getIntent(this), 1);
                }
                else if(responsecode == getResources().getInteger(R.integer.facerecog_login_error_notrecog)){
                    Toast.makeText(this
                            ,getString(R.string.toast_facerecognition_nomatch)
                            ,Toast.LENGTH_LONG).show();
                    currentUserId ="";
                }
                else if(responsecode == getResources().getInteger(R.integer.facerecog_login_error_noface)){
                    Toast.makeText(this
                            ,getString(R.string.toast_facerecognition_nomatch)
                            ,Toast.LENGTH_LONG).show();
                    currentUserId ="";
                }
                else if(responsecode == getResources().getInteger(R.integer.facerecog_login_error_connection)){
                    Toast.makeText(this
                            ,getString(R.string.toast_facerecognition_error_connection)
                            ,Toast.LENGTH_LONG).show();
                    currentUserId ="";
                }
                else if(responsecode == getResources().getInteger(R.integer.facerecog_signup_enrolled)){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(AccountContract.Account.COLUMN_NAME_ID,
                            idTextInputLayout.getEditText().getText().toString().toLowerCase());
                    values.put(AccountContract.Account.COLUMN_NAME_PASSWORD,
                            passwordTextInputLayout.getEditText().getText().toString());
                    db.insert(AccountContract.Account.TABLE_NAME, null, values);
                    Toast.makeText(this, getString(R.string.toast_loginscreen_signup_accountcreated),
                            Toast.LENGTH_LONG).show();
                }
                else if(responsecode == getResources().getInteger(R.integer.facerecog_signup_error_poorquality)){
                    Toast.makeText(this
                            ,getString(R.string.toast_facerecognition_error_poorquality)
                            ,Toast.LENGTH_LONG).show();
                }
                else if(responsecode == getResources().getInteger(R.integer.facerecog_signup_error_noface)){
                    Toast.makeText(this
                            ,getString(R.string.toast_facerecognition_error_noface)
                            ,Toast.LENGTH_LONG).show();
                }
                else if(responsecode == getResources().getInteger(R.integer.facerecog_signup_error_connection)){
                    Toast.makeText(this
                            ,getString(R.string.toast_facerecognition_error_connection)
                            ,Toast.LENGTH_LONG).show();
                }
            }
        }
        else if(requestCode == REQUEST_CODE_SECRET_ACTIVITY){
            currentUserId ="";
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
                AccountContract.Account.COLUMN_NAME_PASSWORD
        };
        String selection = AccountContract.Account.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {idTextInputLayout.getEditText().getText().toString().toLowerCase()};
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
            if (password.equals(passwordTextInputLayout.getEditText().getText().toString())) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_CAMERA
                    );
                } else {
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_preferences_filename), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.shared_preferences_userid), idTextInputLayout.getEditText().getText().toString().toLowerCase());
                    editor.commit();
                    currentUserId = idTextInputLayout.getEditText().getText().toString().toLowerCase();
                    startActivityForResult(FaceRecognitionActivity.getIntent(this, true), REQUEST_CODE_FACE_RECOGNITION);
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
                AccountContract.Account.COLUMN_NAME_PASSWORD
        };
        String selection = AccountContract.Account.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {idTextInputLayout.getEditText().getText().toString().toLowerCase()};
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
        cursor.close();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA
            );
        }
        else {
            startActivityForResult(FaceRecognitionActivity.getIntent(this, false)
                    , REQUEST_CODE_FACE_RECOGNITION);
        }
    }

    private boolean isFieldsEmpty() {
        if (idTextInputLayout.getEditText().getText().toString().equals("")
                || passwordTextInputLayout.getEditText().getText().toString().equals("")) {
            return true;
        }
        return false;
    }

}
