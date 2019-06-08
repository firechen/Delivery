package com.james.deliveryapp.Views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.james.deliveryapp.Utils.NetworkState;
import com.james.deliveryapp.Entities.DeliveryItem;
import com.james.deliveryapp.Utils.RxBus;
import com.james.deliveryapp.Utils.SharedData;
import com.james.deliveryapp.viewModels.DeliveryViewModel;
import com.james.deliveryapp.R;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

// The main activty for showing the delivery item list
public class MainActivity extends AppCompatActivity implements DeliveryAdapter.ItemClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DeliveryViewModel deliveryViewModel;
    private RecyclerView mRecyclerView;
    private DeliveryAdapter adapter;
    private ProgressBar mainProgressBar;
    private TextView errorText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // recyclerView
        mRecyclerView = findViewById(R.id.list);

        // adapter for binding recyclerView and pagedList
        adapter = new DeliveryAdapter(this);
        adapter.setItemClickListener(this);
        // view model
        DeliveryViewModel.setMyContext(this);
        deliveryViewModel = ViewModelProviders.of(this).get(DeliveryViewModel.class);
        deliveryViewModel.getDeliveryItemList().observe(this, new Observer<PagedList<DeliveryItem>>() {
            @Override
            public void onChanged(@Nullable PagedList<DeliveryItem> items) {
                Log.d(TAG, "onChanged: "+items.size());
                adapter.submitList(items);
            }
        });
        deliveryViewModel.getNetworkStateLiveData().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                Log.d(TAG, "onChanged: network state changed");
                adapter.setNetworkState(networkState);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(adapter);

        mainProgressBar = findViewById(R.id.main_progress);
        errorText = findViewById(R.id.error_text);
        setUpRxListener();
    }

    private void setUpRxListener() {
        RxBus.getInstance().getSubject().subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                switch (s) {
                    case SharedData.HIDE_PROGRESS_BAR:
                        mainProgressBar.setVisibility(View.GONE);
                        errorText.setVisibility(View.GONE);
                        break;
                    case SharedData.SHOW_ERROR_LAYOUT:
                        mainProgressBar.setVisibility(View.GONE);
                        errorText.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    // The callback for RecyclerView item click
    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "The position is " + position);
        Intent intent = new Intent(MainActivity.this, ItemActivity.class);
        intent.putExtra(SharedData.POSITION, position);
        startActivity(intent);
    }
}
