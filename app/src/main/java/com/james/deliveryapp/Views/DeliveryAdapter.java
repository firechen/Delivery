package com.james.deliveryapp.Views;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.james.deliveryapp.Utils.NetworkState;
import com.james.deliveryapp.Entities.DeliveryItem;
import com.james.deliveryapp.R;


// the adapter for RecyclerView
public class DeliveryAdapter extends PagedListAdapter<DeliveryItem,RecyclerView.ViewHolder> {
    private static final String TAG = DeliveryAdapter.class.getSimpleName();
    // two types of items
    // delivery items and load items
    public static final int DELIVERY_ITEM_VIEW_TYPE = 1;
    public static final int LOAD_ITEM_VIEW_TYPE  = 0;
    private Context mContext;
    // check for network state
    private NetworkState mNetworkState;
    private static ItemClickListener _listener;

    public DeliveryAdapter(Context context) {
        super(DeliveryItem.DIFF_CALL);
        mContext = context;
    }


    @Override
    public int getItemViewType(int position) {
        // check for load item type
        return ( isLoadingData() && position == getItemCount()-1 )  ? LOAD_ITEM_VIEW_TYPE : DELIVERY_ITEM_VIEW_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == DELIVERY_ITEM_VIEW_TYPE) {
            view = inflater.inflate(R.layout.delivery_item, parent, false);
            return  new DeliveryViewHolder(view);
        } else{
            view = inflater.inflate(R.layout.load_progress_item, parent, false);
            return new ProgressViewHolder(view);
         }

    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if( holder instanceof DeliveryViewHolder){
            DeliveryViewHolder movieViewHolder = (DeliveryViewHolder) holder;
            DeliveryItem item = getItem(position);
            movieViewHolder.bind(item,mContext);
        }
    }

    public void setNetworkState(NetworkState networkState) {
        NetworkState prevState = networkState;
        boolean wasLoading = isLoadingData();
        mNetworkState = networkState;
        boolean willLoad =  isLoadingData();
        if(wasLoading != willLoad){
            if (wasLoading) notifyItemRemoved(getItemCount()); else  notifyItemInserted(getItemCount());
        }
    }

    public boolean isLoadingData(){
        return  ( mNetworkState != null && mNetworkState != NetworkState.LOADED);
    }

    // delivery item view holder
    private static class DeliveryViewHolder extends RecyclerView.ViewHolder{
        ImageView deliveryImage;
        TextView deliveryText;
        public DeliveryViewHolder(View itemView) {
            super(itemView);
            deliveryImage = itemView.findViewById(R.id.delivery_image);
            deliveryText = itemView.findViewById(R.id.delivery_text);

            itemView.findViewById(R.id.delivery_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (_listener != null) {
                        _listener.onItemClick(view, getAdapterPosition());
                    }
                }
            });
        }
        public void bind(DeliveryItem item,Context context){
            Glide.with(context).load(item.getImageUrl()).
                    apply(new RequestOptions().centerCrop()).into(deliveryImage);
            String text = item.getDescription() + " at " + item.getLocation().getAddress();
            deliveryText.setText(text);
        }
    }

    // progress view holder
    private static class ProgressViewHolder extends RecyclerView.ViewHolder{

        public ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // Set listener for RecyclerView
    public static void setItemClickListener(ItemClickListener listener) {
        _listener = listener;
    }
}
