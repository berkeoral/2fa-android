package com.group11.blg439e.a2phase_auth;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by berke on 10/1/2017.
 */
public interface KairosService {

    @Multipart
    @POST("/enroll")
    Call<KairosResponse> enroll(
            @Header("app_id") String app_id,
            @Header("app_key") String app_key,
            @Part MultipartBody.Part  imageFile,
            @Part("gallery_name") RequestBody gallery_name,
            @Part("subject_id") RequestBody  subject_id
            );

    @Multipart
    @POST("/verify")
    Call<KairosResponse> verify(
            @Header("app_id") String app_id,
            @Header("app_key") String app_key,
            @Part MultipartBody.Part  imageFile,
            @Part("gallery_name") RequestBody gallery_name,
            @Part("subject_id") RequestBody  subject_id
    );

}
