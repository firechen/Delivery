package com.james.deliveryapp.Entities;

import android.support.v7.util.DiffUtil;
import com.google.gson.annotations.SerializedName;

// data class for deliver item
public class DeliveryItem {
    private int id;
    private String description;
    @SerializedName("imageUrl")
    private String imageUrl;
    private Location location;

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Location getLocation() {
        return location;
    }

    public static final DiffUtil.ItemCallback<DeliveryItem> DIFF_CALL = new DiffUtil.ItemCallback<DeliveryItem>() {
        @Override
        public boolean areItemsTheSame(DeliveryItem oldItem, DeliveryItem newItem) {
            return  oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(DeliveryItem oldItem, DeliveryItem newItem) {
            return  oldItem.id == newItem.id;
        }
    };
}
