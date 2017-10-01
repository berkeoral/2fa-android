package com.group11.blg439e.a2phase_auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FaceRecognitionActivity extends AppCompatActivity {

    private static Context context;
    private static SQLiteDatabase db;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String INTENT_ACTIVITY_PURPOSE = "purpose";
    private Boolean verify;
    private String currentPhotoPath;
    private Uri photoURI;
    private Retrofit retrofit;
    private Gson gson;
    private KairosService kairosService;
    private AccountSQLHelper dbHelper;

    public static Intent getIntent(Context context, Boolean verify){
        Intent intent = new Intent(context, FaceRecognitionActivity.class);
        intent.putExtra(INTENT_ACTIVITY_PURPOSE, verify);
        return intent;
    }

    public static Context getContext(){
        return FaceRecognitionActivity.context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
        verify = getIntent().getExtras().getBoolean(INTENT_ACTIVITY_PURPOSE);
        gson = new GsonBuilder()
                .setLenient()
                .create();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.kairos_url_base))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        kairosService = retrofit.create(KairosService.class);
        dispatchTakePictureIntent();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File file = new File(currentPhotoPath);
            SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.shared_preferences_filename), Context.MODE_PRIVATE);
            final RequestBody user_id = RequestBody.create(
                    MediaType.parse("text/plain")
                    , sharedPreferences.getString(getString(R.string.shared_preferences_userid), ""));
            //
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
            String app_id = getString(R.string.karios_app_id);
            String app_key = getString(R.string.karios_app_key);
            if (verify) {
                Call<KairosResponse> call = kairosService.verify(app_id, app_key, body, user_id, user_id);
                call.enqueue(new Callback<KairosResponse>() {
                    @Override
                    public void onResponse(Call<KairosResponse> call, Response<KairosResponse> response) {
                        File dFile = new File(currentPhotoPath);
                        dFile.delete();
                        if(response.body().getErrors() == null){
                            if(Double.parseDouble(response.body().getImages()[0].getTransaction().getConfidence()) > 0.5) {
                                db = LoginActivity.getReadableDB();
                                String[] projection = {
                                        AccountContract.Account._ID,
                                        AccountContract.Account.COLUMN_NAME_ID,
                                        AccountContract.Account.COLUMN_NAME_PASSWORD,
                                        AccountContract.Account.COLUMN_NAME_CONTENT
                                };
                                String selection = AccountContract.Account.COLUMN_NAME_ID + " = ?";
                                String[] selectionArgs = {user_id.toString()}; // check if anything is wrong with user_id
                                Cursor cursor = db.query(AccountContract.Account.TABLE_NAME,
                                        projection,
                                        selection,
                                        selectionArgs,
                                        null,
                                        null,
                                        null
                                );
                                String content = "";
                                if (cursor.moveToNext()) {
                                    content = cursor.getString(cursor.getColumnIndex(AccountContract.Account.COLUMN_NAME_CONTENT));
                                }
                                startActivityForResult(SecretActivity
                                        .getIntent(FaceRecognitionActivity.getContext(),content), 1);
                            }
                            else{
                                Toast.makeText(FaceRecognitionActivity.getContext()
                                        ,getString(R.string.toast_facerecognition_nomatch)
                                        ,Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(FaceRecognitionActivity.getContext()
                                    ,getString(R.string.toast_facerecognition_nomatch)
                                    ,Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<KairosResponse> call, Throwable t) {
                        File dFile = new File(currentPhotoPath);
                        dFile.delete();
                        Toast.makeText(FaceRecognitionActivity.getContext()
                                ,getString(R.string.toast_facerecognition_error)
                                ,Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
            else {
                Call<KairosResponse> call = kairosService.enroll(app_id, app_key, body, user_id, user_id);
                call.enqueue(new Callback<KairosResponse>() {
                    @Override
                    public void onResponse(Call<KairosResponse> call, Response<KairosResponse> response) {
                        File dFile = new File(currentPhotoPath);
                        dFile.delete();
                        if(response.body().getErrors() == null){
                            if(Double.parseDouble(response.body().getImages()[0].getTransaction().getConfidence()) > 0.5) {
                                finish();
                            }
                            else{
                                Toast.makeText(FaceRecognitionActivity.getContext()
                                        ,getString(R.string.toast_facerecognition_error_poorphoto)
                                        ,Toast.LENGTH_LONG).show();
                                db = LoginActivity.getWritableDB();
                                db.delete(AccountContract.Account.TABLE_NAME
                                        , AccountContract.Account.COLUMN_NAME_ID + "=?"
                                        , new String[]{user_id.toString()});
                                finish();
                            }
                        }
                        else{
                            Toast.makeText(FaceRecognitionActivity.getContext()
                                    ,getString(R.string.toast_facerecognition_error_noface)
                                    ,Toast.LENGTH_LONG).show();
                            db = LoginActivity.getWritableDB();
                            db.delete(AccountContract.Account.TABLE_NAME
                                    , AccountContract.Account.COLUMN_NAME_ID + "=?"
                                    , new String[]{user_id.toString()});
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(Call<KairosResponse> call, Throwable t) {
                        File dFile = new File(currentPhotoPath);
                        dFile.delete();
                        Toast.makeText(FaceRecognitionActivity.getContext()
                                ,getString(R.string.toast_facerecognition_error)
                                ,Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("ERROR: ", "dispatchTakePictureIntent: Error while creating photo");
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.group11.blg439e.a2phase_auth",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
}
