package com.group11.blg439e.a2phase_auth;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class LoginActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int REQUEST_CODE_FACE_RECOGNITION = 1;
    private static final int REQUEST_CODE_SECRET_ACTIVITY = 1;
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static AccountSQLHelper dbHelper;
    private static Boolean verify = true;
    private TextInputLayout idTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private String currentUserId;
    private Encryptor encryptor;
    private Decryptor decryptor;
    private KeyStore keyStore;

    /*
    * If keystore doenst contains key for alies this indicates applications first launch.
    * If so creates secret key for encryption
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        idTextInputLayout = (TextInputLayout) findViewById(R.id.facerecognition_textinputlayout_id);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.facerecognition_textinputlayout_password);
        dbHelper = new AccountSQLHelper(this);
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
            if(!keyStore.containsAlias(getString(R.string.keystore_key_alias))){
                getSecretKey(getString(R.string.keystore_key_alias));
            }
            decryptor = new Decryptor();
            encryptor = new Encryptor();
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException
                | InvalidAlgorithmParameterException | NoSuchProviderException e){
            e.printStackTrace();
            finish();
        }
    }

    /*
    * Handles users permission responses
    * Depending on where permission asked function starts login or signup processes if permission given
    */
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
                    verify = true;
                    Toast.makeText(this, getString(R.string.toast_loginscreen_permission_denied),
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /*
    * FaceRecognitionActivity's result intent contains responseCode specifying servers API call response
    * > If response code indicates error, function toasts error message
    * > If response code indicates successful face validation, function starts SecretActivity
    * > If response code indicates successful face enrollment, function encrypts password and stores credentials in SQL db
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_FACE_RECOGNITION || requestCode == REQUEST_CODE_FACE_RECOGNITION) {
            if (resultCode == getResources().getInteger(R.integer.facerecog_result_code)) {
                int responsecode = data.getIntExtra(getString(R.string.forresult_intent_responsecode), 0);
                if (responsecode == getResources().getInteger(R.integer.facerecog_canceled)){
                        currentUserId ="";
                }
                else if(responsecode == getResources().getInteger(R.integer.facerecog_login_verified)){
                    startActivityForResult(SecretActivity
                            .getIntent(this), REQUEST_CODE_SECRET_ACTIVITY);
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
                    try{
                        String rawPassword = passwordTextInputLayout.getEditText().getText().toString();
                        String encryptedPassword = encryptor.encryptText(getString(R.string.keystore_key_alias)
                                ,rawPassword);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(AccountContract.Account.COLUMN_NAME_ID
                                , idTextInputLayout.getEditText().getText().toString().toLowerCase());
                        values.put(AccountContract.Account.COLUMN_NAME_PASSWORD
                                , encryptedPassword);
                        values.put(AccountContract.Account.COLUMN_NAME_IV
                                , Base64.encodeToString(encryptor.getIv(), Base64.DEFAULT));
                        db.insert(AccountContract.Account.TABLE_NAME, null, values);
                        Toast.makeText(this, getString(R.string.toast_loginscreen_signup_accountcreated),
                                Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
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
                else if(responsecode == getResources().getInteger(R.integer.facerecog_signup_error_toomanyfaces)){
                    Toast.makeText(this
                            ,getString(R.string.toast_facerecognition_error_toomanyfaces)
                            ,Toast.LENGTH_LONG).show();
                }
                else if(responsecode == getResources().getInteger(R.integer.facerecog_signup_error_connection)){
                    Toast.makeText(this
                            ,getString(R.string.toast_facerecognition_error_connection)
                            ,Toast.LENGTH_LONG).show();
                }
            }
            else if(resultCode == getResources().getInteger(R.integer.secretactivity_result_code)){
                currentUserId ="";
            }
        }
    }

    /*
    * Checks if given id - password pair exists in database
    * Handles necessary encryption operations
    * > If exists and application has permission to use camera, starts FaceRecognitionActivity
    * > If exists and application doesn't have permission to use camera, asks for permission
     */
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
                AccountContract.Account.COLUMN_NAME_IV
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
            String encryptedPassword = cursor.getString(cursor.getColumnIndex(AccountContract.Account.COLUMN_NAME_PASSWORD));
            String iv = cursor.getString(cursor.getColumnIndex(AccountContract.Account.COLUMN_NAME_IV));
            try{
                String password = decryptor.decryptData(getString(R.string.keystore_key_alias)
                        , encryptedPassword
                        , Base64.decode(iv, Base64.DEFAULT));
                if (password.equals(passwordTextInputLayout.getEditText().getText().toString())) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA},
                                PERMISSIONS_REQUEST_CAMERA
                        );
                    } else {
                        SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_preferences_filename)
                                , Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(getString(R.string.shared_preferences_userid)
                                , idTextInputLayout.getEditText().getText().toString().toLowerCase());
                        editor.apply();
                        currentUserId = idTextInputLayout.getEditText().getText().toString().toLowerCase();
                        startActivityForResult(FaceRecognitionActivity.getIntent(this, true)
                                , REQUEST_CODE_FACE_RECOGNITION);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.toast_loginscreen_login_error),
                            Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_loginscreen_login_error),
                    Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Checks if given id exists in database
    * > If not exists and application has permission to use camera, starts FaceRecognitionActivity
    * > If not exists and application doesn't have permission to use camera, asks user for permission
     */
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
            verify = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA
            );
        }
        else {
            SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_preferences_filename), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getString(R.string.shared_preferences_userid), idTextInputLayout.getEditText().getText().toString().toLowerCase());
            editor.commit();
            currentUserId = idTextInputLayout.getEditText().getText().toString().toLowerCase();
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

    /*
    * Creates AES secret key for encryption
    * Secret key stored in android keystore
    */
    @NonNull
    private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException {
        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

        keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_SIGN)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false)
                .build());
        return keyGenerator.generateKey();
    }
}
