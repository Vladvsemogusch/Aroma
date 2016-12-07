package ua.pp.oped.aromateque;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.pp.oped.aromateque.model.FilterParameterValue;


public class ActiveFilterValueAdapter extends RecyclerView.Adapter {
    private ArrayList<FilterParameterValue> activeFilterParameters;
    private LayoutInflater layoutInflater;

    public ActiveFilterValueAdapter(ArrayList<FilterParameterValue> activeFilterParameters, LayoutInflater layoutInflater) {
        this.activeFilterParameters = activeFilterParameters;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.active_filter_value, parent, false);
        return new ActiveValueViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ActiveValueViewHolder) holder).txtValueName.setText(activeFilterParameters.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return activeFilterParameters.size();
    }

    private class ActiveValueViewHolder extends RecyclerView.ViewHolder {
        TextView txtValueName;
        ImageView removeButton;

        public ActiveValueViewHolder(View itemView) {
            super(itemView);
            txtValueName = (TextView) itemView.findViewById(R.id.active_value_name);
        }
    }
}
