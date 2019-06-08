package com.james.deliveryapp.Views;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.james.deliveryapp.Entities.DeliveryItem;
import com.james.deliveryapp.R;
import com.james.deliveryapp.Utils.SharedData;
import com.james.deliveryapp.viewModels.DeliveryViewModel;

// The activity for showing location of specific delivery item in google map
public class ItemActivity extends FragmentActivity implements OnMapReadyCallback, OnMarkerClickListener {
    private GoogleMap mMap;
    private LiveData<PagedList<DeliveryItem>> deliverList;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get position from the intent extra
        position = getIntent().getExtras().getInt(SharedData.POSITION);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemActivity.this.finish();
            }
        });
    }

    // draw marker
    private void setDeliveries(){
        deliverList =  DeliveryViewModel.getDeliveryItemList();
        DeliveryItem item = deliverList.getValue().get(position);
        String deliverTitle = item.getLocation().getAddress();
        LatLng deliverLatLng = new LatLng(item.getLocation().getLat(), item.getLocation().getLng());
        MarkerOptions marker = new MarkerOptions().position(deliverLatLng).title(deliverTitle);
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_marker));
        mMap.addMarker(marker).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deliverLatLng, 15));

        ImageView deliveryImage = findViewById(R.id.delivery_image);
        Glide.with(this).load(item.getImageUrl()).
                apply(new RequestOptions().centerCrop()).into(deliveryImage);
        String text = item.getDescription() + " at " + item.getLocation().getAddress();
        TextView deliveryText = findViewById(R.id.delivery_text);
        deliveryText.setText(text);
    }

    // If the map is ready, draw marker on the delivery location
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getFocusedBuilding();
        mMap.setOnMarkerClickListener(this);
        setDeliveries();
    }

    /* Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        return false;
    }
}
