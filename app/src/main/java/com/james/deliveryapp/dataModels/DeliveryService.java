package com.james.deliveryapp.dataModels;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DeliveryService {
    /**
     * Get list of delivery items
     */
    @GET("deliveries")
    Call<ResponseBody> getDeliveries(@Query("offset") long offset, @Query("limit") long limit);
}
