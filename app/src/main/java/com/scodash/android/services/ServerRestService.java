package com.scodash.android.services;

import com.scodash.android.dto.Dashboard;
import com.scodash.android.services.impl.ScodashService;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServerRestService {

    String BASE_URL = "http://" + ScodashService.HOSTNAME;

    @GET("rest/dashboard/{hash}")
    Call<Dashboard> getRemoteDashboardByHash(@Path("hash") String hash);

    @POST("rest/dashboard")
    Call<Dashboard> createDashboard(@Body Dashboard dashboard);
}
