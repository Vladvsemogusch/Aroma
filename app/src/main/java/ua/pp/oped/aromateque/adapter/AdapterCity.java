package ua.pp.oped.aromateque.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.api.SearchSettlementResponse;

public class AdapterCity extends RecyclerView.Adapter {
    private List<SearchSettlementResponse.SearchSettlementItem> searchSettlementItems;

    public AdapterCity(Context context, List<SearchSettlementResponse.SearchSettlementItem> searchSettlementItems) {
        this.searchSettlementItems = searchSettlementItems;
        Log.d("ADAPTER", "AdapterCity created");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.city_item, viewGroup, false);
        return new CityItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        final CityItemViewHolder cityItemViewHolder = (CityItemViewHolder) viewHolder;
        final SearchSettlementResponse.SearchSettlementItem searchSettlementItem = searchSettlementItems.get(position);
        cityItemViewHolder.city.setText(searchSettlementItem.getMainDescription());
        if (searchSettlementItem.getRegion().equals("")) {
            cityItemViewHolder.region.setVisibility(View.GONE);
        } else {
            cityItemViewHolder.region.setText(searchSettlementItem.getRegion());
        }
        if (searchSettlementItem.getArea().equals("")) {
            cityItemViewHolder.area.setVisibility(View.GONE);
        } else {
            cityItemViewHolder.area.setText(searchSettlementItem.getArea());
        }
        cityItemViewHolder.lytCity.setTag(searchSettlementItem);
//        cityItemViewHolder.city.setText(cartProductItem.getBrand());


    }

    @Override
    public int getItemCount() {
        return searchSettlementItems.size();
    } //TODO

    @Override
    public int getItemViewType(int position) {
        return 0;
    }


    private class CityItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout lytCity;
        private TextView city;
        private TextView region;
        private TextView area;

        CityItemViewHolder(View itemView) {
            super(itemView);
            lytCity = (LinearLayout) itemView.findViewById(R.id.lyt_warehouse);
            city = (TextView) itemView.findViewById(R.id.t_city);
            region = (TextView) itemView.findViewById(R.id.t_region);
            area = (TextView) itemView.findViewById(R.id.t_area);
        }
    }

}


