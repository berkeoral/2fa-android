package com.group11.blg439e.a2phase_auth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by berke on 10/1/2017.
 */
public interface TeknikUploadService {
    @Headers("izukoi: 123456")
    @POST("/Upload")
    Call<WebImageInformation> postImage(@Body LocalImage localImage);
}
