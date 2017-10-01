package com.group11.blg439e.a2phase_auth;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by berke on 10/1/2017.
 */
public interface KairosService {

    @Multipart
    @POST
    Call<KairosEnrollResponse> enroll(
            @Part KairosSendImageData data,
            @Part MultipartBody.Part file
            );

}
