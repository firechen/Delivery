package com.james.deliveryapp.viewModels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;
import android.util.Log;

import com.james.deliveryapp.Utils.NetworkState;
import com.james.deliveryapp.Utils.ServiceGenerator;
import com.james.deliveryapp.DataSource.DeliveryDataSource;
import com.james.deliveryapp.DataSource.DeliveryDataSourceFactory;
import com.james.deliveryapp.Entities.DeliveryItem;
import com.james.deliveryapp.dataModels.DeliveryService;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


// view model for MVVM
public class DeliveryViewModel extends ViewModel {
    private static final String TAG = DeliveryViewModel.class.getSimpleName();
    // DeliveryList
    private static LiveData<PagedList<DeliveryItem>> deliveryItemList;
    // network state
    private LiveData<NetworkState> networkStateLiveData;
    private Executor executor; // ?
    // dataSource
    private LiveData<DeliveryDataSource> dataSource;
    private static Context myContext;


    public DeliveryViewModel() {
        Log.d(TAG, "MoviesInTheaterViewModel: ");
        // provide a thread pool with 5 threads
        executor = Executors.newFixedThreadPool(5);
        DeliveryService deliveryService = ServiceGenerator.createService(DeliveryService.class, myContext);
        DeliveryDataSourceFactory factory = new DeliveryDataSourceFactory(executor,deliveryService);
        dataSource =  factory.getMutableLiveData();
        
        networkStateLiveData = Transformations.switchMap(factory.getMutableLiveData(), new Function<DeliveryDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(DeliveryDataSource source) {
                Log.d(TAG, "apply: network change");
                return source.getNetworkState();
            }
        });

        // define the pagination spec
        PagedList.Config pageConfig = (new PagedList.Config.Builder())
                                                .setEnablePlaceholders(true)
                                                .setInitialLoadSizeHint(10)
                                                .setPageSize(ServiceGenerator.LIMIT).build();

        // the delivery data list
        deliveryItemList = (new LivePagedListBuilder<Long,DeliveryItem>(factory,pageConfig))
                                                    .setBackgroundThreadExecutor(executor)
                                                    .build();
    }

    public static LiveData<PagedList<DeliveryItem>> getDeliveryItemList() {
        Log.d(TAG, "getDeliveryItemList: ");
        return deliveryItemList;
    }

    public static void setMyContext(Context context) {
        myContext = context;
    }

    public LiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }
}

