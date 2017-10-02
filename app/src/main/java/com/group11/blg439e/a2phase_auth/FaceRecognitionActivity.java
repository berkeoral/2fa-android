package com.group11.blg439e.a2phase_auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FaceRecognitionActivity extends AppCompatActivity {


    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int PHOTO_COMPRESS_QUALITY = 50;
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

    /*
    *From file path obtains bitmap and compresses it
    * Makes API call sending compressed image file
    * >If verify flag is true makes api call for verifying user
    * >else makes api call for enrolling user
    * Returns int code to LoginActivity
    * >int code represents servers response
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, options);
                OutputStream fOut = new FileOutputStream(new File(currentPhotoPath));
                bitmap.compress(Bitmap.CompressFormat.JPEG, PHOTO_COMPRESS_QUALITY, fOut);
                fOut.close();
            }
            catch (Exception e){
                Log.d("Exception: ", "Failed to compress file");
            }
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
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra(getString(R.string.forresult_intent_responsecode)
                                        ,getResources().getInteger(R.integer.facerecog_login_verified));
                                setResult(getResources().getInteger(R.integer.facerecog_result_code), returnIntent);
                                finish();
                            }
                            else{
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra(getString(R.string.forresult_intent_responsecode)
                                        ,getResources().getInteger(R.integer.facerecog_login_error_notrecog));
                                setResult(getResources().getInteger(R.integer.facerecog_result_code), returnIntent);
                                finish();
                            }
                        }
                        else{
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(getString(R.string.forresult_intent_responsecode)
                                    ,getResources().getInteger(R.integer.facerecog_login_error_noface));
                            setResult(getResources().getInteger(R.integer.facerecog_result_code), returnIntent);
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(Call<KairosResponse> call, Throwable t) {
                        File dFile = new File(currentPhotoPath);
                        dFile.delete();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(getString(R.string.forresult_intent_responsecode)
                                ,getResources().getInteger(R.integer.facerecog_login_error_connection));
                        setResult(getResources().getInteger(R.integer.facerecog_result_code), returnIntent);
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
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra(getString(R.string.forresult_intent_responsecode)
                                        ,getResources().getInteger(R.integer.facerecog_signup_enrolled));
                                setResult(getResources().getInteger(R.integer.facerecog_result_code), returnIntent);
                                finish();
                            }
                            else{
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra(getString(R.string.forresult_intent_responsecode)
                                        ,getResources().getInteger(R.integer.facerecog_signup_error_poorquality));
                                setResult(getResources().getInteger(R.integer.facerecog_result_code), returnIntent);
                                finish();
                            }
                        }
                        else{
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(getString(R.string.forresult_intent_responsecode)
                                    ,getResources().getInteger(R.integer.facerecog_signup_error_noface));
                            setResult(getResources().getInteger(R.integer.facerecog_result_code), returnIntent);
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(Call<KairosResponse> call, Throwable t) {
                        File dFile = new File(currentPhotoPath);
                        dFile.delete();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(getString(R.string.forresult_intent_responsecode)
                                ,getResources().getInteger(R.integer.facerecog_signup_error_connection));
                        setResult(getResources().getInteger(R.integer.facerecog_result_code), returnIntent);
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
