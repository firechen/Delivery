package com.james.deliveryapp.DataSource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.util.Log;

import com.james.deliveryapp.dataModels.DeliveryService;

import java.util.concurrent.Executor;

// factory for connecting retrofit service and data source
public class DeliveryDataSourceFactory extends DataSource.Factory {
    private static final String TAG = DeliveryDataSourceFactory.class.getSimpleName();
    DeliveryDataSource deliveryDataSource;
    MutableLiveData<DeliveryDataSource> mutableLiveData;
    Executor executor;
    DeliveryService deliveryService;

    public DeliveryDataSourceFactory(Executor executor, DeliveryService deliveryService) {
        
      this.executor = executor;
      this.deliveryService = deliveryService;
      mutableLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource create() {
        Log.d(TAG, "create: ");
                deliveryDataSource = new DeliveryDataSource(executor,deliveryService);
                mutableLiveData.postValue(deliveryDataSource);
                return deliveryDataSource;
    }

    public MutableLiveData<DeliveryDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
