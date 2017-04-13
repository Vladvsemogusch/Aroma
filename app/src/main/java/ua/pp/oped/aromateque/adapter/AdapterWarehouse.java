package ua.pp.oped.aromateque.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import timber.log.Timber;
import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.api.WarehouseResponse;

public class AdapterWarehouse extends RecyclerView.Adapter {
    private List<WarehouseResponse.WarehouseItem> warehouseItems;

    public AdapterWarehouse(Context context, List<WarehouseResponse.WarehouseItem> warehouseItems) {
        this.warehouseItems = warehouseItems;
        Timber.d("AdapterWarehouse created");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.warehouse_item, viewGroup, false);
        return new WarehouseItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        final WarehouseItemViewHolder warehouseItemViewHolder = (WarehouseItemViewHolder) viewHolder;
        final WarehouseResponse.WarehouseItem warehouseItem = warehouseItems.get(position);
        String fullDescription = warehouseItem.getDescriptionRu();
        String[] splitFullDescription = fullDescription.split(": ", 2);
        if (splitFullDescription.length == 2) {
            String warehouseNumber = splitFullDescription[0];
            String warehouseAddress = splitFullDescription[1];
            warehouseItemViewHolder.number.setText(warehouseNumber);
            warehouseItemViewHolder.address.setText(warehouseAddress);
        } else {
            warehouseItemViewHolder.number.setText(fullDescription);
            warehouseItemViewHolder.address.setText("");
        }
        warehouseItemViewHolder.lytWarehouse.setTag(warehouseItem);
//        warehouseItemViewHolder.city.setText(cartProductItem.getBrand());
    }

    @Override
    public int getItemCount() {
        return warehouseItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }


    private class WarehouseItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout lytWarehouse;
        private TextView number;
        private TextView address;


        WarehouseItemViewHolder(View itemView) {
            super(itemView);
            lytWarehouse = (LinearLayout) itemView.findViewById(R.id.lyt_warehouse);
            number = (TextView) itemView.findViewById(R.id.tv_number);
            address = (TextView) itemView.findViewById(R.id.tv_description);

        }
    }

}


