package com.example.electricitybillestimator; // Replace with your package name

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;

public class CustomListAdapter extends ArrayAdapter<BillEntry> {

    private Context mContext;
    private int mResource;

    public CustomListAdapter(@NonNull Context context, int resource, @NonNull List<BillEntry> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
        }

        TextView tvMonth = convertView.findViewById(R.id.tvMonthItem);
        TextView tvFinalCost = convertView.findViewById(R.id.tvFinalCostItem);

        BillEntry billEntry = getItem(position);

        if (billEntry != null) {
            tvMonth.setText(billEntry.getMonth());
            tvFinalCost.setText(String.format(Locale.getDefault(), "RM %.2f", billEntry.getFinalCost()));
        }

        return convertView;
    }
}