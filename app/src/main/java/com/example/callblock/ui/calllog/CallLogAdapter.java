package com.example.callblock.ui.calllog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.callblock.R;
import com.example.callblock.utils.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.List;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

    public interface OnCallLogClickListener {
        void onBlockClick(CallLogItem item);
    }

    private List<CallLogItem> items = new ArrayList<>();
    private final OnCallLogClickListener listener;

    public CallLogAdapter(OnCallLogClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CallLogItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallLogItem item = items.get(position);
        Context context = holder.itemView.getContext();

        holder.tvPhoneNumber.setText(PhoneNumberUtils.formatDisplay(item.getPhoneNumber()));
        holder.tvCallDetail.setText(item.getDisplayDetail());

        if (item.isMissedOrSpam()) {
            holder.tvCallDetail.setTextColor(ContextCompat.getColor(context, R.color.missed_call_red));
        } else {
            holder.tvCallDetail.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        }

        holder.btnBlock.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBlockClick(item);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBlockClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPhoneNumber;
        TextView tvCallDetail;
        ImageView btnBlock;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvCallDetail = itemView.findViewById(R.id.tvCallDetail);
            btnBlock = itemView.findViewById(R.id.btnBlock);
        }
    }
}
