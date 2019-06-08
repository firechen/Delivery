package com.james.deliveryapp.DataSource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;
import com.james.deliveryapp.Utils.JSONParser;
import com.james.deliveryapp.Utils.NetworkState;
import com.james.deliveryapp.Utils.RxBus;
import com.james.deliveryapp.Utils.ServiceGenerator;
import com.james.deliveryapp.Entities.DeliveryItem;
import com.james.deliveryapp.Utils.SharedData;
import com.james.deliveryapp.dataModels.DeliveryService;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// The data source for delivery items
public class DeliveryDataSource extends PageKeyedDataSource<Long, DeliveryItem> {
    private static final String TAG = DeliveryDataSource.class.getSimpleName();
    private DeliveryService deliveryService;
    private MutableLiveData<NetworkState> networkState;
    private MutableLiveData<NetworkState> initialLoading;
    private Executor retryExecutor;

    public DeliveryDataSource(Executor retryExecutor,DeliveryService deliveryService1) {
        deliveryService = deliveryService1;
        networkState = new MutableLiveData<>();
        initialLoading = new MutableLiveData<>();
        this.retryExecutor = retryExecutor;
    }


    // network state for showing delivery item or progress item
    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public MutableLiveData getInitialLoading() {

        return initialLoading;
    }

    // load the first 20 delivery items, namely first page
    @Override
    public void loadInitial(@NonNull final LoadInitialParams<Long> params, @NonNull final LoadInitialCallback<Long, DeliveryItem> callback) {
        Log.d(TAG, "loadInitial: ");
        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);
        deliveryService.getDeliveries(ServiceGenerator.DEFAULT_OFFSET, ServiceGenerator.LIMIT).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString;
                List<DeliveryItem> deliveryItemList;
                if (response.isSuccessful() && response.code() ==200) {
                        try {
                            initialLoading.postValue(NetworkState.LOADING);
                            networkState.postValue(NetworkState.LOADED);
                            responseString = response.body().string();
                            deliveryItemList = JSONParser.getDeliveryItemList(responseString);
                            RxBus.getInstance().sendEvent(SharedData.HIDE_PROGRESS_BAR);
                            callback.onResult(deliveryItemList,null, (long) 1);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                }else {
                    Log.e(TAG, "onResponse error "+response.message());
                    initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED,response.message()));
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED,response.message()));
                    RxBus.getInstance().sendEvent(SharedData.SHOW_ERROR_LAYOUT);
                    Log.e(TAG, "load init again");
                    loadInitial(params, callback);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMessage = t.getMessage();
                Log.e(TAG, "onFailure: "+errorMessage );
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED,errorMessage));
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, DeliveryItem> callback) {

    }

    // load other pages
    @Override
    public void loadAfter(@NonNull final LoadParams<Long> params, @NonNull final LoadCallback<Long, DeliveryItem> callback) {
        networkState.postValue(NetworkState.LOADING);
        deliveryService.getDeliveries(params.key  * ServiceGenerator.LIMIT, ServiceGenerator.LIMIT).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString;
                List<DeliveryItem> deliveryItemList;
                Long nextKey;
                Log.e(TAG, "onResponse called, with key " + params.key);
                if (response.isSuccessful() && response.code() ==200) {
                    try {
                        initialLoading.postValue(NetworkState.LOADING);
                        networkState.postValue(NetworkState.LOADED);

                        responseString = response.body().string();
                        deliveryItemList = JSONParser.getDeliveryItemList(responseString);
                        nextKey = params.key+1;

                        callback.onResult(deliveryItemList, nextKey);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Log.e(TAG, "onResponse error "+response.message());
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED,response.message()));
                    Log.e(TAG, "call load after again");
                    loadAfter(params, callback);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMessage = t.getMessage();
                Log.e(TAG, "onFailure: "+errorMessage );
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED,errorMessage));
            }
        });
    }



}

