package com.example.callblock.ui.brand;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.callblock.R;
import com.example.callblock.data.entity.BrandWithNumbers;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder> {

    public interface OnBrandToggleListener {
        void onToggle(BrandWithNumbers brandWithNumbers, boolean isEnabled);
    }

    public interface OnBrandClickListener {
        void onBrandClick(BrandWithNumbers brandWithNumbers);
    }

    private List<BrandWithNumbers> items = new ArrayList<>();
    private final OnBrandToggleListener toggleListener;
    private final OnBrandClickListener clickListener;

    public BrandAdapter(OnBrandToggleListener toggleListener, OnBrandClickListener clickListener) {
        this.toggleListener = toggleListener;
        this.clickListener = clickListener;
    }

    public void setItems(List<BrandWithNumbers> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BrandWithNumbers item = items.get(position);

        holder.tvBrandName.setText(item.brand.getName());

        int numCount = item.numbers != null ? item.numbers.size() : 0;
        String statusText = item.brand.isEnabled() ? "Đã kích hoạt" : "Tắt";
        String subText = numCount + " số điện thoại • " + statusText;
        holder.tvBrandSub.setText(subText);

        holder.switchEnabled.setOnCheckedChangeListener(null);
        holder.switchEnabled.setChecked(item.brand.isEnabled());

        holder.switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (toggleListener != null) {
                toggleListener.onToggle(item, isChecked);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onBrandClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBrandName;
        TextView tvBrandSub;
        SwitchMaterial switchEnabled;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
            tvBrandSub = itemView.findViewById(R.id.tvBrandSub);
            switchEnabled = itemView.findViewById(R.id.switchEnabled);
        }
    }
}
