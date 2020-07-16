package com.nibiru.creator.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    @GET
    Call<ResponseBody> getNptContent(@Url String url);

    @POST
    Call<ResponseBody> getResUrl(@Url String url, @Query("inputUrl") String inputUrl);
}
